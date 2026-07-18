import DesignSystem
import SwiftUI

public struct NavigationTopBar: View {
    @Environment(\.appTheme) private var theme

    private let topBarTitle: String?
    private let imageName: String
    private let onBackClicked: () -> Void
    private let width: CGFloat
    private let height: CGFloat

    @State private var isButtonPressed: Bool

    public init(
        topBarTitle: String? = nil,
        imageName: String = "arrow.backward",
        width: CGFloat = 40,
        height: CGFloat = 40,
        onBackClicked: @escaping () -> Void,
        isButtonPressed: Bool = false
    ) {
        self.topBarTitle = topBarTitle
        self.onBackClicked = onBackClicked
        self.imageName = imageName
        self.width = width
        self.height = height
        self.isButtonPressed = isButtonPressed
    }

    public var body: some View {
        ZStack {
            theme.colors.background
                .appShadow(theme.shadows.medium, color: theme.colors.surfaceVariant)

            VStack {
                HStack {
                    CircularButton(
                        iconName: imageName,
                        width: width,
                        height: height,
                        action: {
                            isButtonPressed = true
                            onBackClicked()
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                                isButtonPressed = false
                            }
                        },
                        isPressed: isButtonPressed
                    )
                    .padding(.leading, theme.spacing.medium)

                    Spacer()

                    if let title = topBarTitle {
                        Text(title)
                            .textStyle(theme.typography.titleSmall)
                            .foregroundStyle(.appOnSurface)

                        Spacer()
                    }

                    Spacer()
                        .frame(width: 40)
                }
                .padding(.bottom, theme.spacing.xSmall)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.top, 60)
        }
        .frame(height: 100)
        .edgesIgnoringSafeArea(.top)
    }
}

#Preview {
    VStack {
        NavigationTopBar(
            topBarTitle: "Upcoming",
            imageName: "arrow.backward",
            onBackClicked: {}
        )

        Spacer()
    }
}
