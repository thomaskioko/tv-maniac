import SwiftUI
import SwiftUIComponents
import TvManiac

public struct TabContentView<Content: View>: View {
    private let child: TabChild<AnyObject>?
    private let tab: NavigationTab
    private let avatarImage: UIImage?
    @ViewBuilder let content: (TabChild<AnyObject>) -> Content

    public init(
        child: TabChild<AnyObject>?,
        tab: NavigationTab,
        avatarImage: UIImage? = nil,
        @ViewBuilder content: @escaping (TabChild<AnyObject>) -> Content
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
