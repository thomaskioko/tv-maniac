import SwiftUI

public struct BottomNavigation: View {
    private let actions: [BottomTabAction]

    public init(actions: [BottomTabAction]) {
        self.actions = actions
    }

    public var body: some View {
        VStack(spacing: 0) {
            Divider()
                .background(Color.gray.opacity(0.3))

            HStack(spacing: 0) {
                ForEach(actions) { action in
                    BottomTabItem(
                        title: action.title,
                        systemImage: action.systemImage,
                        isActive: action.isActive,
                        action: action.action
                    )
                }
            }
            .frame(height: 54)
            .padding(.bottom, 28)
            .padding(.top, 2)
        }
        .background(TransparentBlurView(style: .systemThinMaterial))
        .edgesIgnoringSafeArea(.bottom)
    }
}

public struct BottomTabAction: Identifiable {
    public let id = UUID()
    public let title: String
    public let systemImage: String
    public let isActive: Bool
    public let action: () -> Void

    public init(
        title: String,
        systemImage: String,
        isActive: Bool,
        action: @escaping () -> Void
    ) {
        self.title = title
        self.systemImage = systemImage
        self.isActive = isActive
        self.action = action
    }
}

#Preview {
    BottomNavigation(
        actions: [
            BottomTabAction(
                title: "Discover",
                systemImage: "tv",
                isActive: true,
                action: {}
            ),
            BottomTabAction(
                title: "Search",
                systemImage: "magnifyingglass",
                isActive: false,
                action: {}
            ),
            BottomTabAction(
                title: "Library",
                systemImage: "list.bullet.below.rectangle",
                isActive: false,
                action: {}
            ),
            BottomTabAction(
                title: "Settings",
                systemImage: "gearshape",
                isActive: false,
                action: {}
            )
        ]
    )
}
