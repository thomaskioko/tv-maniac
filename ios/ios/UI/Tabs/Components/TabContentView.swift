import SwiftUI
import SwiftUIComponents
import TvManiac

public struct TabContentView<Child: HomePresenterChild, Content: View>: View {
    private let child: Child?
    private let tab: NavigationTab
    private let avatarUrl: String?
    @ViewBuilder let content: (Child) -> Content

    public init(
        child: Child?,
        tab: NavigationTab,
        avatarUrl: String? = nil,
        @ViewBuilder content: @escaping (Child) -> Content
    ) {
        self.child = child
        self.tab = tab
        self.avatarUrl = avatarUrl
        self.content = content
    }

    public var body: some View {
        ZStack {
            if let child {
                NavigationView {
                    content(child)
                        .id(ObjectIdentifier(child))
                }
            }
        }
        .tag(tab)
        .tabItem {
            if let avatarUrl, !avatarUrl.isEmpty {
                Label {
                    Text(tab.title)
                } icon: {
                    AvatarView(avatarUrl: avatarUrl, size: 24)
                }
            } else {
                Label(tab.title, systemImage: tab.icon)
            }
        }
    }
}
