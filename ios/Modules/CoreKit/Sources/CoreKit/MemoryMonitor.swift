import Foundation

enum MemoryPressureError: LocalizedError {
    case critical(event: String, usage: String)

    var errorDescription: String? {
        switch self {
        case let .critical(event, usage):
            "Critical memory pressure at \(event): \(usage)"
        }
    }
}

public final class MemoryMonitor: @unchecked Sendable {
    public static let shared = MemoryMonitor()

    private var timer: Timer?
    private var _warningCount: Int = 0
    private let lock = NSLock()
    private var _logger: CoreLogger?
    private var _diagnosticLogger: DiagnosticLogger?

    public var warningCount: Int {
        lock.lock()
        defer { lock.unlock() }
        return _warningCount
    }

    private init() {}

    public func setLogger(_ logger: CoreLogger) {
        lock.lock()
        _logger = logger
        lock.unlock()
    }

    public func setDiagnosticLogger(_ logger: DiagnosticLogger) {
        lock.lock()
        _diagnosticLogger = logger
        lock.unlock()
    }

    public func start() {
        #if DEBUG
            stop()
            let newTimer = Timer(timeInterval: 30.0, repeats: true) { [weak self] _ in
                self?.logCurrentState()
            }
            RunLoop.main.add(newTimer, forMode: .common)
            timer = newTimer

            lock.lock()
            let log = _logger
            lock.unlock()

            log?.debug(tag: "Memory", message: "Monitor started")
            logCurrentState()
        #endif
    }

    public func stop() {
        #if DEBUG
            timer?.invalidate()
            timer = nil
        #endif
    }

    public func logMemoryState(event: String) {
        let description = SystemMemory.memoryUsageDescription
        let pressure = SystemMemory.pressureLevel
        let pressureLabel = switch pressure {
        case .normal: "normal"
        case .warning: "WARNING"
        case .critical: "CRITICAL"
        }

        lock.lock()
        let log = _logger
        let reporter = _diagnosticLogger
        lock.unlock()

        log?.debug(tag: "Memory", message: "\(event) — \(description) (pressure: \(pressureLabel))")
        reporter?.logBreadcrumb(category: "memory", message: "\(event) — \(description)")

        if pressure == .critical {
            log?.warning(tag: "Memory", message: "CRITICAL pressure at \(event) — \(description)")
            reporter?.recordError(
                MemoryPressureError.critical(event: event, usage: description),
                context: ["pressure": pressureLabel, "event": event]
            )
        }
    }

    public func recordMemoryWarning() -> Int {
        lock.lock()
        _warningCount += 1
        let count = _warningCount
        let reporter = _diagnosticLogger
        let log = _logger
        lock.unlock()

        let description = SystemMemory.memoryUsageDescription

        log?.warning(
            tag: "Memory",
            message: "Warning #\(count) — \(description) — device: \(SystemMemory.isLowMemoryDevice ? "low-mem" : "standard")"
        )
        reporter?.recordMemoryWarning(level: count, memoryUsage: description)
        return count
    }

    public func resetWarningEscalation() {
        lock.lock()
        let previous = _warningCount
        _warningCount = 0
        let log = _logger
        lock.unlock()

        if previous > 0 {
            log?.debug(tag: "Memory", message: "Escalation reset (was \(previous) warnings)")
        }
    }

    private func logCurrentState() {
        let description = SystemMemory.memoryUsageDescription
        let pressure = SystemMemory.pressureLevel

        lock.lock()
        let log = _logger
        lock.unlock()

        switch pressure {
        case .normal:
            log?.debug(tag: "Memory", message: "Periodic: \(description)")
        case .warning:
            log?.warning(tag: "Memory", message: "Periodic: \(description) — pressure: WARNING")
        case .critical:
            log?.warning(tag: "Memory", message: "Periodic: \(description) — pressure: CRITICAL")
        }
    }
}
