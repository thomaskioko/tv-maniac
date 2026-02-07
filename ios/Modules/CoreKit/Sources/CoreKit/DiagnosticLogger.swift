import Foundation

public protocol DiagnosticLogger: Sendable {
    func recordMemoryWarning(level: Int, memoryUsage: String)
    func logBreadcrumb(category: String, message: String)
    func recordError(_ error: Error, context: [String: String])
    func setUserContext(userId: String?)
    func lastMemoryWarningInterval() -> TimeInterval?
}
