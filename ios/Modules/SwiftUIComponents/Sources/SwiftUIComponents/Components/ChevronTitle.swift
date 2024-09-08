import SwiftUI

public struct ChevronTitle: View {
  private let title: String
  private let subtitle: String?
  private let showChevron: Bool

  public init(
    title: String,
    subtitle: String? = nil,
    showChevron: Bool = false
  ) {
    self.title = title
    self.subtitle = subtitle
    self.showChevron = showChevron
  }

  public var body: some View {
    HStack {
      VStack(alignment: .leading) {
        Text(title)
          .padding([.top, .leading])
          .fontWeight(.semibold)
          .font(.title3)

        if let subtitle {
          Text(subtitle)
            .foregroundColor(.secondary)
            .padding(.leading)
            .font(.callout)
        }
      }

      Spacer()

      if showChevron {
        Image(systemName: "chevron.right")
          .font(.callout)
          .fontWeight(.regular)
          .foregroundColor(.secondary)
          .padding([.top, .trailing])
          .accessibilityHidden(true)
      }
    }
    .accessibilityElement(children: .combine)
  }
}

#Preview {
  VStack {
    ChevronTitle(
      title: "Coming Soon"
    )
    ChevronTitle(
      title: "Coming Soon",
      showChevron: true
    )
    ChevronTitle(
      title: "Coming Soon",
      subtitle: "From Watchlist"
    )
    ChevronTitle(
      title: "Coming Soon",
      subtitle: "From Watchlist",
      showChevron: true
    )
  }
}
