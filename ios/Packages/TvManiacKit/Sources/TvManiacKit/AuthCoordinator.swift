import Foundation

/// Provider-neutral entry point for launching a sign-in OAuth flow. Each provider ships a coordinator
/// configured per provider, that the composition root wires to the matching `AuthManager`.
public protocol AuthCoordinator: AnyObject {
    @MainActor func initiateAuthorization()
}
