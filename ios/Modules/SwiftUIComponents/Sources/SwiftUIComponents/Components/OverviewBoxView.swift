import SwiftUI

public struct OverviewBoxView: View {
    @Theme private var theme
    @State private var showFullText = false
    @State private var isTruncated = false

    private let overview: String?
    private let lineLimit: Int

    public init(
        overview: String?,
        lineLimit: Int = 4,
        showFullText: Bool = false,
        isTruncated: Bool = false
    ) {
        self.overview = overview
        self.lineLimit = lineLimit
        self.showFullText = showFullText
        self.isTruncated = isTruncated
    }

    public var body: some View {
        if let overview, !overview.isEmpty {
            VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                Text(overview)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundColor(theme.colors.onSurface)
                    .lineLimit(showFullText ? nil : lineLimit)
                    .lineSpacing(4)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(
                        Text(overview)
                            .lineLimit(lineLimit)
                            .textStyle(theme.typography.bodyMedium)
                            .lineSpacing(4)
                            .background(GeometryReader { displayedGeometry in
                                ZStack {
                                    Text(overview)
                                        .textStyle(theme.typography.bodyMedium)
                                        .lineSpacing(4)
                                        .background(GeometryReader { fullGeometry in
                                            Color.clear.onAppear {
                                                isTruncated = fullGeometry.size.height > displayedGeometry.size.height
                                            }
                                        })
                                }
                                .frame(height: .greatestFiniteMagnitude)
                            })
                            .hidden()
                    )

                if isTruncated {
                    HStack {
                        Spacer()

                        Button(
                            action: {
                                withAnimation { showFullText.toggle() }
                            }
                        ) {
                            Text(showFullText ? "Show Less" : "Show More")
                                .textCase(.uppercase)
                                .textStyle(theme.typography.labelMedium)
                                .foregroundStyle(theme.colors.accent)
                        }
                    }
                }
            }
        }
    }
}

struct CustomContainer<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        content
            .background(Color(.clear))
    }
}

#Preview {
    VStack(spacing: 20) {
        // Long Text
        OverviewBoxView(
            overview: "Set in the utopian region of Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League champions-and the power that will tear them apart."
        )

        // Short Text
        OverviewBoxView(
            overview: "Set in the utopian region of Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League."
        )
    }
    .padding()
}
