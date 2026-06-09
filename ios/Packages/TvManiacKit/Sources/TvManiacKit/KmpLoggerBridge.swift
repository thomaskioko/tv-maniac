import CoreKit
import TvManiac

final class KmpLoggerBridge: CoreLogger, @unchecked Sendable {
    private let kmpLogger: TvManiac.Logger

    init(_ logger: TvManiac.Logger) {
        kmpLogger = logger
    }

    func debug(tag: String, message: String) {
        kmpLogger.debug(tag: tag, message: message)
    }

    func warning(tag: String, message: String) {
        kmpLogger.warning(tag: tag, message: message)
    }

    func error(tag: String, message: String) {
        kmpLogger.error(tag: tag, message: message)
    }
}
