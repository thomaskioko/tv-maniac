import SwiftUI

public struct ChevronTitle: View {
  private let title: String
  private let subtitle: String?
  private let chevronStyle: ChevronStyle
  private let action: () -> Void

  public init(
    title: String,
    subtitle: String? = nil,
    chevronStyle: ChevronStyle = .none,
    action: @escaping () -> Void = {}
  ) {
    self.title = title
    self.subtitle = subtitle
    self.chevronStyle = chevronStyle
    self.action = action
  }

  public var body: some View {
    HStack {
      titleSubtitleView
      Spacer()
      chevronView
    }
    .accessibilityElement(children: .combine)
    .padding([.vertical], 8)
  }

  private var titleSubtitleView: some View {
    VStack(alignment: .leading, spacing: 4) {
      Text(title)
        .font(.title3)
        .fontWeight(.semibold)

      if let subtitle = subtitle {
        Text(subtitle)
          .font(.callout)
          .foregroundColor(.secondary)
      }
    }
    .padding(.leading)
  }

  private var chevronView: some View {
    Group {
      switch chevronStyle {
      case .none:
        EmptyView()
      case .chevronOnly:
        chevronButton(title: nil)
      case .withTitle(let title):
        chevronButton(title: title)
      }
    }
  }

  private func chevronButton(title: String?) -> some View {
    Button(action: action) {
      HStack(spacing: 4) {
        if let title = title {
          Text(title)
            .font(.callout)
        }
        Image(systemName: "chevron.right")
          .font(.callout)
      }
    }
    .foregroundColor(Color.accent.opacity(0.8))
    .padding(.trailing)
  }
}

public enum ChevronStyle {
  case none
  case chevronOnly
  case withTitle(String)
}

#Preview {
  VStack(spacing: 20) {
    ChevronTitle(title: "Coming Soon")
    ChevronTitle(title: "Coming Soon", chevronStyle: .chevronOnly)
    ChevronTitle(title: "Coming Soon", chevronStyle: .withTitle("More"))
    ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist")
    ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist", chevronStyle: .chevronOnly)
  }
  .padding()
}
