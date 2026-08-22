import DesignSystem
import SwiftUI
import TvManiac
import UIKit
import UniformTypeIdentifiers

extension View {
    func settingsBackupFolderPicker(
        uiState: SettingsState,
        presenter: SettingsPresenter,
        isPresented: Binding<Bool>
    ) -> some View {
        onChange(of: uiState.backup.awaitingDestination) { _, awaitingDestination in
            isPresented.wrappedValue = awaitingDestination
        }
        .fileImporter(isPresented: isPresented, allowedContentTypes: [.folder]) { result in
            switch result {
            case let .success(url):
                guard let location = backupFolderBookmark(for: url) else {
                    presenter.dispatch(action: BackupDestinationCancelled())
                    return
                }
                presenter.dispatch(action: BackupDestinationSelected(location: location))
            case .failure:
                presenter.dispatch(action: BackupDestinationCancelled())
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
                    accountLabel: "Restore and sync with Trakt",
                    deviceLabel: "Restore locally"
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

/// iOS has no security-scoped bookmarks, only macOS does. A plain bookmark taken while access is
/// held is what keeps a chosen folder writable after a relaunch.
private func backupFolderBookmark(for url: URL) -> String? {
    let accessing = url.startAccessingSecurityScopedResource()
    defer { if accessing { url.stopAccessingSecurityScopedResource() } }
    guard let data = try? url.bookmarkData() else { return nil }
    return "bookmark:\(data.base64EncodedString())"
}

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
