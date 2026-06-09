import AppAuth
import Foundation
import UIKit

public final class OAuthClient {
    private let configuration: OAuthConfiguration
    private var currentAuthorizationFlow: OIDExternalUserAgentSession?

    public init(configuration: OAuthConfiguration) {
        self.configuration = configuration
    }

    @MainActor
    public func authorize(
        presentingViewController: UIViewController
    ) async throws -> OAuthCredential {
        let coordinator = AppAuthCoordinator(configuration: configuration)
        return try await coordinator.performAuthorizationFlow(
            presentingViewController: presentingViewController
        )
    }
}
