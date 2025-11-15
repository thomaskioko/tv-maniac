import AppAuth
import Foundation
import UIKit

public final class TraktOAuthClient {
    private let configuration: TraktAuthConfiguration
    private var currentAuthorizationFlow: OIDExternalUserAgentSession?

    public init(configuration: TraktAuthConfiguration) {
        self.configuration = configuration
    }

    @MainActor
    public func authorize(
        presentingViewController: UIViewController
    ) async throws -> TraktCredential {
        let coordinator = AppAuthCoordinator(configuration: configuration)
        return try await coordinator.performAuthorizationFlow(
            presentingViewController: presentingViewController
        )
    }
}
