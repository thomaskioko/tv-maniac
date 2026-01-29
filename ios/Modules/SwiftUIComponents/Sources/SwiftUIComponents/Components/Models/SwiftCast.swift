import Foundation

public struct SwiftCast: Identifiable, Equatable {
    public var id: Int64 { castId }
    public let castId: Int64
    public let name: String
    public let characterName: String
    public let profileUrl: String?

    public init(
        castId: Int64,
        name: String,
        characterName: String,
        profileUrl: String?
    ) {
        self.castId = castId
        self.name = name
        self.characterName = characterName
        self.profileUrl = profileUrl
    }
}
