import SwiftUI
import SwiftUIComponents
import TvManiacKit

public struct TabBarView: View {
    @Theme private var theme

    private let presenter: HomePresenter
    @StateObject @KotlinStateFlow private var stack: ChildStack<AnyObject, HomePresenterChild>
    @StateObject @KotlinOptionalStateFlow private var avatarUrl: String?
    @State private var selectedTab: NavigationTab = .discover
    @State private var avatarImage: UIImage?
    @EnvironmentObject private var appDelegate: AppDelegate

    init(presenter: HomePresenter) {
        self.presenter = presenter
        _stack = .init(presenter.homeChildStack)
        _avatarUrl = .init(presenter.profileAvatarUrl)
    }

    public var body: some View {
        TabView(selection: $selectedTab) {
            ForEach(NavigationTab.allCases, id: \.self) { tab in
                TabContentView(
                    child: stack.items.first(where: { tabForChild($0.instance) == tab })?.instance,
                    tab: tab,
                    avatarImage: tab == .profile ? avatarImage : nil
                ) { child in
                    switch onEnum(of: child) {
                    case let .discover(screen):
                        DiscoverTab(presenter: screen.presenter)
                            .id(ObjectIdentifier(screen))
                    case let .progress(screen):
                        ProgressTab(presenter: screen.presenter)
                            .id(ObjectIdentifier(screen))
                    case let .profile(screen):
                        ProfileTab(presenter: screen.presenter)
                            .id(ObjectIdentifier(screen))
                    case let .library(screen):
                        LibraryTab(presenter: screen.presenter)
                            .id(ObjectIdentifier(screen))
                    }
                }
            }
        }
        .tint(theme.colors.accent)
        .toolbarBackground(theme.colors.surface, for: .tabBar)
        .toolbarBackground(.visible, for: .tabBar)
        .task(id: avatarUrl) {
            await loadAvatar()
        }
        .onChange(of: selectedTab) { _, newTab in
            switch newTab {
            case .discover: presenter.onDiscoverClicked()
            case .progress: presenter.onProgressClicked()
            case .profile: presenter.onProfileClicked()
            case .library: presenter.onLibraryClicked()
            }
        }
        .onChange(of: activeTab) { _, newTab in
            if selectedTab != newTab {
                selectedTab = newTab
            }
        }
    }

    private func loadAvatar() async {
        guard let avatarUrl, !avatarUrl.isEmpty, let url = URL(string: avatarUrl) else {
            avatarImage = nil
            return
        }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            guard let downloaded = UIImage(data: data) else { return }
            let pointSize = CGSize(width: 25, height: 25)
            let renderer = UIGraphicsImageRenderer(size: pointSize)
            avatarImage = renderer.image { _ in
                let rect = CGRect(origin: .zero, size: pointSize)
                UIBezierPath(ovalIn: rect).addClip()
                downloaded.draw(in: rect)
            }
        } catch {
            avatarImage = nil
        }
    }

    private var activeTab: NavigationTab {
        tabForChild(stack.active.instance)
    }

    private func tabForChild(_ child: HomePresenterChild) -> NavigationTab {
        switch onEnum(of: child) {
        case .discover: .discover
        case .progress: .progress
        case .profile: .profile
        case .library: .library
        }
    }
}

// MARK: - Tab Item

public enum NavigationTab: String, CaseIterable {
    case discover
    case progress
    case library
    case profile

    var title: String {
        switch self {
        case .discover: String(\.label_tab_discover)
        case .progress: String(\.menu_item_progress)
        case .library: String(\.menu_item_library)
        case .profile: String(\.menu_item_profile)
        }
    }

    var icon: String {
        switch self {
        case .discover: "tv"
        case .progress: "play.circle"
        case .library: "square.stack"
        case .profile: "person.circle"
        }
    }
}
