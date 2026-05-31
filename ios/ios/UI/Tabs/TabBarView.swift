import Components
import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

public struct TabBarView: View {
    @Environment(\.appTheme) private var theme

    private let presenter: HomePresenter
    private let navigator: Navigator
    private let registry: ScreenRegistry
    @StateValue private var activeRoot: NavRoot
    @StateValue private var profileAvatar: ProfileAvatar
    @State private var selectedTab: NavigationTab = .discover
    @State private var avatarImage: UIImage?
    @State private var downloadedAvatar: UIImage?
    @EnvironmentObject private var appDelegate: AppDelegate

    init(presenter: HomePresenter, navigator: Navigator, registry: ScreenRegistry) {
        self.presenter = presenter
        self.navigator = navigator
        self.registry = registry
        _activeRoot = .init(presenter.activeRootValue)
        _profileAvatar = .init(presenter.profileAvatarUrlValue)
    }

    public var body: some View {
        TabView(selection: $selectedTab) {
            tabContent(.discover, stack: presenter.discoverChildStackValue)
            tabContent(.progress, stack: presenter.progressChildStackValue)
            tabContent(.myShows, stack: presenter.myShowsChildStackValue)
            tabContent(.profile, stack: presenter.profileChildStackValue, avatarImage: avatarImage)
        }
        .tint(theme.colors.accent)
        .toolbarBackground(theme.colors.surface, for: .tabBar)
        .toolbarBackground(.visible, for: .tabBar)
        .task(id: profileAvatar.url) {
            await loadAvatar()
        }
        .onChange(of: selectedTab) { _, newTab in
            if let circular = downloadedAvatar {
                avatarImage = buildAvatarImage(circular: circular, showRing: newTab == .profile)
            }
            switch newTab {
            case .discover: presenter.onDiscoverClicked()
            case .progress: presenter.onProgressClicked()
            case .profile: presenter.onProfileClicked()
            case .myShows: presenter.onMyShowsClicked()
            }
        }
        .onChange(of: tabFor(activeRoot)) { _, newTab in
            if selectedTab != newTab {
                selectedTab = newTab
            }
        }
    }

    private func tabContent(
        _ tab: NavigationTab,
        stack: Value<ChildStack<AnyObject, RootChild>>,
        avatarImage: UIImage? = nil
    ) -> some View {
        DecomposeNavigationStack(
            stack: stack,
            onBack: navigator.popTo
        ) { child in
            renderChild(child)
        }
        .tag(tab)
        .tabItem {
            if let avatarImage {
                Image(uiImage: avatarImage.withRenderingMode(.alwaysOriginal))
                Text(tab.title)
            } else {
                Label(tab.title, systemImage: tab.icon)
            }
        }
    }

    @ViewBuilder
    private func renderChild(_ child: RootChild) -> some View {
        if let tabChild = child as? TabChild<AnyObject> {
            switch tabChild.presenter {
            case let p as DiscoverShowsPresenter:
                DiscoverTab(presenter: p).id(NavigationTab.discover.rawValue)
            case let p as ProgressPresenter:
                ProgressTab(presenter: p).id(NavigationTab.progress.rawValue)
            case let p as MyShowsPresenter:
                MyShowsTab(presenter: p).id(NavigationTab.myShows.rawValue)
            case let p as ProfilePresenter:
                ProfileTab(presenter: p).id(NavigationTab.profile.rawValue)
            default:
                EmptyView()
            }
        } else {
            registry.view(for: child)
                .toolbar(.hidden, for: .tabBar)
        }
    }

    private func tabFor(_ root: NavRoot) -> NavigationTab {
        switch root {
        case is DiscoverRoot: .discover
        case is ProgressRoot: .progress
        case is MyShowsRoot: .myShows
        case is ProfileRoot: .profile
        default: .discover
        }
    }

    private func loadAvatar() async {
        guard let avatarUrl = profileAvatar.url, !avatarUrl.isEmpty, let url = URL(string: avatarUrl) else {
            downloadedAvatar = nil
            avatarImage = nil
            return
        }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            guard let downloaded = UIImage(data: data) else { return }
            let pointSize = CGSize(width: 25, height: 25)
            let renderer = UIGraphicsImageRenderer(size: pointSize)
            let circular = renderer.image { _ in
                let rect = CGRect(origin: .zero, size: pointSize)
                UIBezierPath(ovalIn: rect).addClip()
                downloaded.draw(in: rect)
            }
            downloadedAvatar = circular
            avatarImage = buildAvatarImage(circular: circular, showRing: selectedTab == .profile)
        } catch {
            downloadedAvatar = nil
            avatarImage = nil
        }
    }

    private func buildAvatarImage(circular: UIImage, showRing: Bool) -> UIImage {
        guard showRing else { return circular }
        let strokeWidth: CGFloat = 2
        let padding: CGFloat = 1
        let totalSize = CGSize(
            width: circular.size.width + (strokeWidth + padding) * 2,
            height: circular.size.height + (strokeWidth + padding) * 2
        )
        let accentUIColor = UIColor(theme.colors.accent)
        let renderer = UIGraphicsImageRenderer(size: totalSize)
        return renderer.image { _ in
            let ringRect = CGRect(origin: .zero, size: totalSize)
                .insetBy(dx: strokeWidth / 2, dy: strokeWidth / 2)
            accentUIColor.setStroke()
            let ringPath = UIBezierPath(ovalIn: ringRect)
            ringPath.lineWidth = strokeWidth
            ringPath.stroke()

            let imageOrigin = CGPoint(x: strokeWidth + padding, y: strokeWidth + padding)
            circular.draw(at: imageOrigin)
        }
    }
}

// MARK: - Tab Item

public enum NavigationTab: String, CaseIterable {
    case discover
    case progress
    case myShows
    case profile

    var title: String {
        switch self {
        case .discover: String(\.label_tab_discover)
        case .progress: String(\.menu_item_progress)
        case .myShows: String(\.label_tab_my_shows)
        case .profile: String(\.menu_item_profile)
        }
    }

    var icon: String {
        switch self {
        case .discover: "tv"
        case .progress: "play.circle"
        case .myShows: "play.square.stack.fill"
        case .profile: "person.circle"
        }
    }
}
