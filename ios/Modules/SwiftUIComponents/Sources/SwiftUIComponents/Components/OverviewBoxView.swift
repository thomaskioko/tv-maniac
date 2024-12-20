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
                    .font(.avenirNext(size: 17))
                    .foregroundColor(.textColor)
                    .lineLimit(showFullText ? nil : lineLimit)
                    .multilineTextAlignment(.leading)
                    .padding(.bottom, -14)

                GeometryReader { geometry in
                    Text(overview)
                        .font(.avenirNext(size: 17))
                        .foregroundColor(.clear)
                        .lineLimit(lineLimit)
                        .background(
                            GeometryReader { fullTextGeometry in
                                Color.clear.onAppear {
                                    self.isTruncated = fullTextGeometry.size.height > geometry.size.height
                                }
                            }
                        )
                }
                .frame(height: 0)

                if isTruncated {
                    HStack {
                        Spacer()

                        Button(
                            action: {
                                withAnimation { showFullText.toggle() }
                            }) {
                                Text(showFullText ? "Show Less" : "Show More")
                                    .textCase(.uppercase)
                                    .font(.avenirNext(size: 12))
                                    .foregroundStyle(Color.accent)
                                    .padding(.trailing, 16)
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
