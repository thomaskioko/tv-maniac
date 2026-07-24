import Foundation

public final class DefaultDiagnosticLogger: DiagnosticLogger, @unchecked Sendable {
    private struct Breadcrumb {
        let timestamp: Date
        let category: String
        let message: String
    }

    public static let shared = DefaultDiagnosticLogger()

    private let lock = NSLock()
    private var _logger: CoreLogger?
    private var _breadcrumbs: [Breadcrumb] = []
    private let maxBreadcrumbs = 50

    private init() {}

    public func setLogger(_ logger: CoreLogger) {
        lock.lock()
        _logger = logger
        lock.unlock()
    }

    public func recordMemoryWarning(level: Int, memoryUsage: String) {
        lock.lock()
        appendBreadcrumbLocked(
            category: "memory",
            message: "Warning #\(level) — \(memoryUsage)"
        )
        let log = _logger
        lock.unlock()

        log?.warning(tag: "DiagnosticLogger", message: "Memory warning #\(level) — \(memoryUsage)")
    }

    public func logBreadcrumb(category: String, message: String) {
        lock.lock()
        appendBreadcrumbLocked(category: category, message: message)
        let log = _logger
        lock.unlock()

        log?.debug(tag: "DiagnosticLogger", message: "[\(category)] \(message)")
    }

    public func recordError(_ error: Error, context: [String: String]) {
        let contextDescription = context.map { "\($0.key)=\($0.value)" }.joined(separator: ", ")

        lock.lock()
        appendBreadcrumbLocked(
            category: "error",
            message: "\(error.localizedDescription) [\(contextDescription)]"
        )
        let log = _logger
        lock.unlock()

        log?.error(
            tag: "DiagnosticLogger",
            message: "Error: \(error.localizedDescription) context: [\(contextDescription)]"
        )
    }

    private func appendBreadcrumbLocked(category: String, message: String) {
        _breadcrumbs.append(Breadcrumb(timestamp: Date(), category: category, message: message))
        if _breadcrumbs.count > maxBreadcrumbs {
            _breadcrumbs.removeFirst(_breadcrumbs.count - maxBreadcrumbs)
        }
    }
}
