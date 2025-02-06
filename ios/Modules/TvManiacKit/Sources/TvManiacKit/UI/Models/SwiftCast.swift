
import Foundation

public struct SwiftCast: Identifiable {
    public let id = UUID()
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
