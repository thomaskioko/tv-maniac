import DesignSystem
import SwiftUI

public struct Toast: Equatable {
    public var type: ToastStyle
    public var title: String
    public var message: String
    public var duration: Double?
    public var persistent: Bool
    public var loading: Bool

    public init(
        type: ToastStyle,
        title: String = "",
        message: String,
        duration: Double? = 10.0,
        persistent: Bool = false,
        loading: Bool = false
    ) {
        self.type = type
        self.title = title
        self.message = message
        self.duration = persistent ? nil : duration
        self.persistent = persistent
        self.loading = loading
    }
}

public enum ToastStyle {
    case error
    case warning
    case success
    case info
}

extension ToastStyle {
    var themeColor: Color {
        switch self {
        case .error: Color.red
        case .warning: Color.orange
        case .info: Color.blue
        case .success: Color.green
        }
    }

    var iconFileName: String {
        switch self {
        case .info: "info.circle.fill"
        case .warning: "exclamationmark.triangle.fill"
        case .success: "checkmark.circle.fill"
        case .error: "xmark.circle.fill"
        }
    }
}
