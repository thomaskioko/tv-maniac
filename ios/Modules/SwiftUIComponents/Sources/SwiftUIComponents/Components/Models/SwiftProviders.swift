import Foundation

public struct SwiftProviders: Identifiable, Equatable {
    public var id: Int64 {
        providerId
    }

    public let providerId: Int64
    public let logoUrl: String?

    public init(providerId: Int64, logoUrl: String?) {
        self.logoUrl = logoUrl
        self.providerId = providerId
    }
}
