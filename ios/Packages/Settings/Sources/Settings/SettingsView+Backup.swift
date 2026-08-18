import SwiftUI
import TvManiac
import UIKit

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
            guard wasExporting, !isExporting, uiState.message == nil, pendingURL.wrappedValue != nil else { return }
            isPresented.wrappedValue = true
        }
        .sheet(isPresented: isPresented, onDismiss: { pendingURL.wrappedValue = nil }) {
            if let url = pendingURL.wrappedValue {
                BackupDocumentExporter(url: url, isPresented: isPresented)
            }
        }
    }
}
