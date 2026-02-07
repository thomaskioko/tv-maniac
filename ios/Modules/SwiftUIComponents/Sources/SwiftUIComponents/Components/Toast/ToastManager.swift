import SwiftUI

@MainActor
@Observable
public final class ToastManager {
    public private(set) var toast: Toast?
    private var dismissalTask: Task<Void, Never>?

    public init() {}

    public func show(_ toast: Toast) {
        dismissalTask?.cancel()
        self.toast = toast

        if toast.duration > 0 {
            dismissalTask = Task { @MainActor [weak self] in
                try? await Task.sleep(for: .seconds(toast.duration))
                guard !Task.isCancelled, let self else { return }
                dismiss()
            }
        }
    }

    public func show(
        type: ToastStyle,
        title: String,
        message: String,
        duration: Double = 3.5
    ) {
        show(Toast(type: type, title: title, message: message, duration: duration))
    }

    public func dismiss() {
        dismissalTask?.cancel()
        dismissalTask = nil
        toast = nil
    }

    public func showError(title: String, message: String) {
        show(type: .error, title: title, message: message)
    }

    public func showSuccess(title: String, message: String) {
        show(type: .success, title: title, message: message)
    }

    public func showInfo(title: String, message: String) {
        show(type: .info, title: title, message: message)
    }

    public func showWarning(title: String, message: String) {
        show(type: .warning, title: title, message: message)
    }

    public func showTask<T: Sendable>(
        loadingMessage: String,
        task: @escaping @Sendable () async throws -> T,
        onSuccess: ((T) -> Toast)? = nil,
        onFailure: ((Error) -> Toast)? = nil
    ) {
        show(type: .info, title: "", message: loadingMessage, duration: 0)

        Task { @MainActor [weak self] in
            do {
                let result = try await task()
                guard !Task.isCancelled, let self else { return }
                if let successToast = onSuccess?(result) {
                    show(successToast)
                } else {
                    dismiss()
                }
            } catch {
                guard !Task.isCancelled, let self else { return }
                if let failureToast = onFailure?(error) {
                    show(failureToast)
                } else {
                    showError(title: "Error", message: error.localizedDescription)
                }
            }
        }
    }
}
