import SwiftUI
import TvManiac

public struct TabContentView<Child: HomePresenterChild, Content: View>: View {
  private let child: Child?
  private let tab: NavigationTab
  @ViewBuilder let content: (Child) -> Content

  public init(
    child: Child?,
    tab: NavigationTab,
    @ViewBuilder content: @escaping (Child) -> Content
  ) {
    self.child = child
    self.tab = tab
    self.content = content
  }

  public var body: some View {
    ZStack {
      if let child = child {
        NavigationView {
          content(child)
            .id(ObjectIdentifier(child))
        }
      }
    }
    .tag(tab)
    .tabItem {
      Label(tab.title, systemImage: tab.icon)
    }
  }
}
