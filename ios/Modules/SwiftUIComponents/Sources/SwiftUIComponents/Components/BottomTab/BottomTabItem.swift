import SwiftUI

struct BottomTabItem: View {
  private let title: String
  private let systemImage: String
  private let isActive: Bool
  private let action: () -> Void

  public init(
    title: String,
    systemImage: String,
    isActive: Bool, action:
    @escaping () -> Void
  ) {
    self.title = title
    self.systemImage = systemImage
    self.isActive = isActive
    self.action = action
  }

  public var body: some View {
    Button(action: action) {
      VStack(spacing: 4) {
        Image(systemName: systemImage)
          .resizable()
          .aspectRatio(contentMode: .fit)
          .frame(width: 24, height: 24)

        Text(title)
          .font(.system(size: 10, weight: .medium))
      }
      .foregroundColor(isActive ? Color.iosBlue : .textColor)
      .frame(maxWidth: .infinity)
    }
    .buttonStyle(PlainButtonStyle())
  }
}

#Preview {
  BottomTabItem(
    title: "Discover",
    systemImage: "tv",
    isActive: true,
    action: {}
  )
}
