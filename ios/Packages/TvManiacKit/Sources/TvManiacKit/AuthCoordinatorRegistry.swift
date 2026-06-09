import Foundation
import TvManiac

/// Builds and retains a sign-in coordinator per connected provider, pointing each provider's
/// `AuthManager` at the coordinator built from its matching `AuthClientConfig`. Keeps the app entry point
/// free of provider-auth setup — a new provider is picked up from the graph with no change here.
@MainActor
public final class AuthCoordinatorRegistry {
    private var coordinators: [AuthCoordinator] = []

    public init() {}

    public func register(presenterGraph: IosViewPresenterGraph, appGraph: IosApplicationGraph) {
        guard coordinators.isEmpty else { return }
        let configs = appGraph.authClientConfigs
        let repositories = appGraph.accountAuthRepositories

        coordinators = presenterGraph.authManagers.compactMap { key, value in
            guard let manager = value as? AuthManager,
                  let config = configs[key] as? AuthClientConfig,
                  let repository = repositories[key] as? AccountAuthRepository
            else {
                return nil
            }
            let coordinator = AuthCoordinatorFactory.create(
                authRepository: repository,
                config: config,
                logger: appGraph.logger
            )
            manager.setAuthCallback(callback: { [weak coordinator] in
                coordinator?.initiateAuthorization()
            })
            return coordinator
        }
    }
}
