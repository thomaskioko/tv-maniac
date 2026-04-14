import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

public struct TabBarView: View {
    @Theme private var theme

    private let presenter: HomePresenter
    @StateValue private var stack: ChildStack<AnyObject, TabChild<AnyObject>>
    @StateValue private var profileAvatar: ProfileAvatar
    @State private var selectedTab: NavigationTab = .discover
    @State private var avatarImage: UIImage?
    @State private var downloadedAvatar: UIImage?
    @EnvironmentObject private var appDelegate: AppDelegate

    init(presenter: HomePresenter) {
        self.presenter = presenter
        _stack = .init(presenter.homeChildStackValue)
        _profileAvatar = .init(presenter.profileAvatarUrlValue)
    }

    public var body: some View {
        TabView(selection: $selectedTab) {
            ForEach(NavigationTab.allCases, id: \.self) { tab in
                TabContentView(
                    child: stack.items.first(where: { tabForChild($0.instance) == tab })?.instance,
                    tab: tab,
                    avatarImage: tab == .profile ? avatarImage : nil
                ) { child in
                    switch child.presenter {
                    case let presenter as DiscoverShowsPresenter:
                        DiscoverTab(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as ProgressPresenter:
                        ProgressTab(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as ProfilePresenter:
                        ProfileTab(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as LibraryPresenter:
                        LibraryTab(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    default:
                        EmptyView()
                    }
                }
            }
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

    private var activeTab: NavigationTab {
        tabForChild(stack.active.instance)
    }

    private func tabForChild(_ child: TabChild<AnyObject>) -> NavigationTab {
        switch child.presenter {
        case is DiscoverShowsPresenter: .discover
        case is ProgressPresenter: .progress
        case is ProfilePresenter: .profile
        case is LibraryPresenter: .library
        default: .discover
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
