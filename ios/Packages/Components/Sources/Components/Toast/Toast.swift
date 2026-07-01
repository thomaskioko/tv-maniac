import DesignSystem
import SwiftUI

public struct Toast: Equatable {
    public var type: ToastStyle
    public var title: String
    public var message: String
    public var duration: Double?
    public var persistent: Bool
    public var loading: Bool
    public var onDismiss: (() -> Void)?

    public init(
        type: ToastStyle,
        title: String = "",
        message: String,
        duration: Double? = 10.0,
        persistent: Bool = false,
        loading: Bool = false,
        onDismiss: (() -> Void)? = nil
    ) {
        self.type = type
        self.title = title
        self.message = message
        self.duration = persistent ? nil : duration
        self.persistent = persistent
        self.loading = loading
        self.onDismiss = onDismiss
    }

    public static func == (lhs: Toast, rhs: Toast) -> Bool {
        lhs.type == rhs.type &&
            lhs.title == rhs.title &&
            lhs.message == rhs.message &&
            lhs.duration == rhs.duration &&
            lhs.persistent == rhs.persistent &&
            lhs.loading == rhs.loading
    }
}

public enum ToastStyle {
    case error
    case warning
    case success
    case info
    case syncing
}

public extension ToastStyle {
    var iconFileName: String {
        switch self {
        case .info: "info.circle.fill"
        case .warning: "exclamationmark.triangle.fill"
        case .success: "checkmark.circle.fill"
        case .error: "xmark.circle.fill"
        case .syncing: "arrow.triangle.2.circlepath"
        }
    }
}
