import SwiftUI

public struct OverviewBoxView: View {
    private let overview: String?
    private let lineLimit: Int
    @State private var showFullText = false
    @State private var isTruncated = false

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
            VStack(alignment: .leading, spacing: 8) {
                Text(overview)
                    .font(.callout)
                    .foregroundColor(.textColor)
                    .lineLimit(showFullText ? nil : lineLimit)
                    .lineSpacing(4)
                    .multilineTextAlignment(.leading)
                    .background(
                        // Render the limited text and measure its size
                        Text(overview)
                            .lineLimit(lineLimit)
                            .font(.callout)
                            .lineSpacing(4)
                            .background(GeometryReader { displayedGeometry in
                                // Create a ZStack with unbounded height to allow the inner Text as much
                                // height as it likes, but no extra width.
                                ZStack {
                                    // Render the text without restrictions and measure its size
                                    Text(overview)
                                        .font(.callout)
                                        .lineSpacing(4)
                                        .background(GeometryReader { fullGeometry in
                                            // And compare the two
                                            Color.clear.onAppear {
                                                isTruncated = fullGeometry.size.height > displayedGeometry.size.height
                                            }
                                        })
                                }
                                .frame(height: .greatestFiniteMagnitude)
                            })
                            .hidden() // Hide the background
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
                                .font(.avenirNext(size: 12))
                                .foregroundStyle(Color.accent)
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
