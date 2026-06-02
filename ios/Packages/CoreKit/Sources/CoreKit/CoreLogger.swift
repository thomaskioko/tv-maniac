import Foundation

public protocol CoreLogger: Sendable {
    func debug(tag: String, message: String)
    func warning(tag: String, message: String)
    func error(tag: String, message: String)
}
