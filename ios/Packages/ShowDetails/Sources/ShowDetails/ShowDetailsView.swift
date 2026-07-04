import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct ShowDetailsView: View {
    private let presenter: ShowDetailsPresenter
    @StateValue private var hostState: ShowDetailsState
    @StateValue private var headerState: ShowDetailsHeaderState
    @StateValue private var seasonsEpisodesState: ShowDetailsSeasonsEpisodesState
    @State private var toast: Toast?

    public init(presenter: ShowDetailsPresenter) {
        self.presenter = presenter
        _hostState = .init(presenter.stateValue)
        _headerState = .init(presenter.headerPresenter.stateValue)
        _seasonsEpisodesState = .init(presenter.seasonsEpisodesPresenter.stateValue)
    }

    public var body: some View {
        ShowDetailsScreen(
            state: ShowDetailsScreen.State(
                title: headerState.title,
                overview: headerState.overview,
                backdropImageUrl: headerState.backdropImageUrl,
                status: headerState.status,
                year: headerState.year,
                language: headerState.language,
                communityRating: headerState.communityRating as? Double,
                communityVotes: headerState.communityVotes as? Int64,
                userRating: headerState.userRating as? Int,
                numberOfSeasons: Int(seasonsEpisodesState.numberOfSeasons),
                isRefreshing: hostState.isRefreshing
            ),
            toast: $toast,
            seasonCountFormat: { count in String(\.season_count, quantity: count) },
            onBack: { presenter.dispatch(action: ShowDetailsBackClicked()) },
            onRefresh: { presenter.dispatch(action: ShowDetailsReload()) }
        ) {
            ShowDetailsContentSections(
                presenter: presenter,
                showStatus: headerState.status
            )
        }
        .onChange(of: hostState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: "Error", message: message.message)
                presenter.dispatch(action: ShowDetailsMessageShown(id: message.id))
            }
        }
    }
}

private struct ShowDetailsContentSections: View {
    @Environment(\.appTheme) private var theme
    let presenter: ShowDetailsPresenter
    let showStatus: String?

    var body: some View {
        VStack(spacing: theme.spacing.medium) {
            ShowDetailsHeaderSection(presenter: presenter.headerPresenter)
            ShowDetailsSeasonEpisodesSection(
                presenter: presenter.seasonsEpisodesPresenter,
                showStatus: showStatus
            )
            ShowDetailsProvidersSection(presenter: presenter.providersPresenter)
            ShowDetailsTrailersSection(presenter: presenter.trailersPresenter)
            ShowDetailsCastSection(presenter: presenter.castPresenter)
            ShowDetailsSimilarSection(presenter: presenter.similarPresenter)
        }
        .padding(.top, theme.spacing.medium)
    }
}
