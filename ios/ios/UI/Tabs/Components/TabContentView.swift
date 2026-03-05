import SwiftUI
import SwiftUIComponents
import TvManiac

public struct TabContentView<Child: HomePresenterChild, Content: View>: View {
    private let child: Child?
    private let tab: NavigationTab
    private let avatarImage: UIImage?
    @ViewBuilder let content: (Child) -> Content

    public init(
        child: Child?,
        tab: NavigationTab,
        avatarImage: UIImage? = nil,
        @ViewBuilder content: @escaping (Child) -> Content
    ) {
        self.child = child
        self.tab = tab
        self.avatarImage = avatarImage
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
            if let avatarImage {
                Image(uiImage: avatarImage.withRenderingMode(.alwaysOriginal))
                Text(tab.title)
            } else {
                Label(tab.title, systemImage: tab.icon)
            }
        }
    }
}
