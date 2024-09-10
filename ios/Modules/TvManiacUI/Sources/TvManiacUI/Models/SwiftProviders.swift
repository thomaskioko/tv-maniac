import Foundation

public struct SwiftProviders: Identifiable {
    public let id: UUID = .init()
    public let providerId: Int64
    public let logoUrl: String?

    public init(providerId: Int64, logoUrl: String?) {
        self.logoUrl = logoUrl
        self.providerId = providerId
    }
}
