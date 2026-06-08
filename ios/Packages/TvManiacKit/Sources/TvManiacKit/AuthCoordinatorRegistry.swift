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
        let configs = appGraph.authClientConfigs.compactMap { $0 as? AuthClientConfig }
        let repositories = appGraph.accountAuthRepositories.compactMap { $0 as? AccountAuthRepository }

        coordinators = presenterGraph.authManagers.compactMap { element in
            guard let manager = element as? AuthManager,
                  let config = configs.first(where: { $0.provider == manager.provider }),
                  let repository = repositories.first(where: { $0.provider == manager.provider })
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
