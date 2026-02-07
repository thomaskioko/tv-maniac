import Foundation

public final class DefaultDiagnosticLogger: DiagnosticLogger, @unchecked Sendable {
    public static let shared = DefaultDiagnosticLogger()

    private let lock = NSLock()
    private var _logger: CoreLogger?
    private var _lastMemoryWarningDate: Date?
    private var _breadcrumbs: [(timestamp: Date, category: String, message: String)] = []
    private let maxBreadcrumbs = 50

    private init() {}

    public func setLogger(_ logger: CoreLogger) {
        lock.lock()
        _logger = logger
        lock.unlock()
    }

    public func recordMemoryWarning(level: Int, memoryUsage: String) {
        lock.lock()
        _lastMemoryWarningDate = Date()
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

    public func setUserContext(userId: String?) {
        lock.lock()
        let log = _logger
        lock.unlock()

        if let userId {
            log?.debug(tag: "DiagnosticLogger", message: "User context set: \(userId)")
        } else {
            log?.debug(tag: "DiagnosticLogger", message: "User context cleared")
        }
    }

    public func lastMemoryWarningInterval() -> TimeInterval? {
        lock.lock()
        defer { lock.unlock() }
        guard let date = _lastMemoryWarningDate else { return nil }
        return Date().timeIntervalSince(date)
    }

    private func appendBreadcrumbLocked(category: String, message: String) {
        _breadcrumbs.append((timestamp: Date(), category: category, message: message))
        if _breadcrumbs.count > maxBreadcrumbs {
            _breadcrumbs.removeFirst(_breadcrumbs.count - maxBreadcrumbs)
        }
    }
}
