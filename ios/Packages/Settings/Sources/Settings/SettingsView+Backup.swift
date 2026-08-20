import DesignSystem
import SwiftUI
import TvManiac
import UIKit
import UniformTypeIdentifiers

private func makeTemporaryBackupURL() -> URL {
    let formatter = DateFormatter()
    formatter.dateFormat = "yyyyMMdd-HHmmss"
    let filename = "tvmaniac-backup-\(formatter.string(from: Date())).json"
    return FileManager.default.temporaryDirectory.appendingPathComponent(filename)
}

struct BackupDocumentExporter: UIViewControllerRepresentable {
    let url: URL
    @Binding var isPresented: Bool

    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        let controller = UIDocumentPickerViewController(forExporting: [url], asCopy: true)
        controller.delegate = context.coordinator
        return controller
    }

    func updateUIViewController(_: UIDocumentPickerViewController, context _: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(sourceURL: url, isPresented: $isPresented)
    }

    final class Coordinator: NSObject, UIDocumentPickerDelegate {
        private let sourceURL: URL
        private let isPresented: Binding<Bool>

        init(sourceURL: URL, isPresented: Binding<Bool>) {
            self.sourceURL = sourceURL
            self.isPresented = isPresented
        }

        func documentPicker(_: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            for pickedURL in urls {
                let isAccessing = pickedURL.startAccessingSecurityScopedResource()
                defer { if isAccessing { pickedURL.stopAccessingSecurityScopedResource() } }
            }
            finish()
        }

        func documentPickerWasCancelled(_: UIDocumentPickerViewController) {
            finish()
        }

        private func finish() {
            try? FileManager.default.removeItem(at: sourceURL)
            isPresented.wrappedValue = false
        }
    }
}

extension View {
    func settingsBackupExporter(
        uiState: SettingsState,
        presenter: SettingsPresenter,
        pendingURL: Binding<URL?>,
        isPresented: Binding<Bool>
    ) -> some View {
        onChange(of: uiState.backup.awaitingDestination) { _, awaitingDestination in
            guard awaitingDestination else { return }
            let url = makeTemporaryBackupURL()
            pendingURL.wrappedValue = url
            presenter.dispatch(action: BackupDestinationSelected(location: url.path))
        }
        .onChange(of: uiState.backup.isExporting) { wasExporting, isExporting in
            guard wasExporting, !isExporting, uiState.message?.type != .error, pendingURL.wrappedValue != nil else { return }
            isPresented.wrappedValue = true
        }
        .sheet(isPresented: isPresented, onDismiss: { pendingURL.wrappedValue = nil }) {
            if let url = pendingURL.wrappedValue {
                BackupDocumentExporter(url: url, isPresented: isPresented)
            }
        }
    }

    func settingsBackupImporter(
        uiState: SettingsState,
        presenter: SettingsPresenter,
        showingConfirm: Binding<Bool>,
        showingSource: Binding<Bool>
    ) -> some View {
        onChange(of: uiState.backup.confirm != nil) { _, isConfirming in
            showingConfirm.wrappedValue = isConfirming
        }
        .onChange(of: uiState.backup.awaitingSource) { _, awaitingSource in
            showingSource.wrappedValue = awaitingSource
        }
        .alert(
            uiState.backup.confirm?.title ?? "",
            isPresented: showingConfirm,
            actions: {
                BackupRestoreConfirmationActions(
                    confirm: uiState.backup.confirm,
                    onConfirmAccount: { presenter.dispatch(action: BackupImportConfirmedWithAccount()) },
                    onConfirmDevice: { presenter.dispatch(action: BackupImportConfirmed()) },
                    onCancel: { presenter.dispatch(action: BackupImportCancelled()) }
                )
            },
            message: {
                if let message = uiState.backup.confirm?.message {
                    Text(message)
                }
            }
        )
        .fileImporter(isPresented: showingSource, allowedContentTypes: [.json]) { result in
            switch result {
            case let .success(url):
                presenter.dispatch(action: BackupSourceSelected(location: importedBackupPath(from: url)))
            case .failure:
                presenter.dispatch(action: BackupSourceCancelled())
            }
        }
    }
}

public struct BackupRestoreConfirmationActions: View {
    let confirm: BackupRestoreConfirmationDialog?
    let onConfirmAccount: () -> Void
    let onConfirmDevice: () -> Void
    let onCancel: () -> Void

    public init(
        confirm: BackupRestoreConfirmationDialog?,
        onConfirmAccount: @escaping () -> Void,
        onConfirmDevice: @escaping () -> Void,
        onCancel: @escaping () -> Void
    ) {
        self.confirm = confirm
        self.onConfirmAccount = onConfirmAccount
        self.onConfirmDevice = onConfirmDevice
        self.onCancel = onCancel
    }

    public var body: some View {
        if let local = confirm as? BackupRestoreConfirmationDialogLocal {
            Button(local.confirmLabel, action: onConfirmDevice)
        }
        if let connected = confirm as? BackupRestoreConfirmationDialogConnected {
            Button(connected.accountLabel, action: onConfirmAccount)
            Button(connected.deviceLabel, action: onConfirmDevice)
        }
        if let confirm {
            Button(confirm.cancelLabel, role: .cancel, action: onCancel)
        }
    }
}

#if DEBUG
    #Preview("Restore Confirm - Local") {
        VStack(alignment: .leading, spacing: 12) {
            BackupRestoreConfirmationActions(
                confirm: BackupRestoreConfirmationDialogLocal(
                    title: "Restore this backup?",
                    message: "This replaces the shows and watch history on this device. A copy of your current data is saved first.",
                    cancelLabel: "Cancel",
                    confirmLabel: "Restore"
                ),
                onConfirmAccount: {},
                onConfirmDevice: {},
                onCancel: {}
            )
        }
        .padding()
        .appPreview()
    }

    #Preview("Restore Confirm - Connected") {
        VStack(alignment: .leading, spacing: 12) {
            BackupRestoreConfirmationActions(
                confirm: BackupRestoreConfirmationDialogConnected(
                    title: "Restore this backup?",
                    message: "This replaces the shows and watch history on this device, and a copy of your current data is saved first. You are signed in to Trakt, so shows you do not add to it are removed at the next sync.",
                    cancelLabel: "Cancel",
                    accountLabel: "Add to Trakt",
                    deviceLabel: "This device only"
                ),
                onConfirmAccount: {},
                onConfirmDevice: {},
                onCancel: {}
            )
        }
        .padding()
        .appPreview()
    }
#endif

private func importedBackupPath(from url: URL) -> String {
    let isAccessing = url.startAccessingSecurityScopedResource()
    defer { if isAccessing { url.stopAccessingSecurityScopedResource() } }
    let destination = FileManager.default.temporaryDirectory
        .appendingPathComponent("tvmaniac-restore-\(UUID().uuidString).json")
    do {
        try FileManager.default.copyItem(at: url, to: destination)
        return destination.path
    } catch {
        return url.path
    }
}
