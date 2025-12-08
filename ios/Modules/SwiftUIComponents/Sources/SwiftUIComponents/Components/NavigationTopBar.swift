import SwiftUI

public struct NavigationTopBar: View {
    @Theme private var theme

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
                .shadow(color: theme.colors.surfaceVariant, radius: 10, x: 0, y: 5)

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
                            .textStyle(theme.typography.titleLarge)
                            .bold()
                            .foregroundColor(theme.colors.onSurface)

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
