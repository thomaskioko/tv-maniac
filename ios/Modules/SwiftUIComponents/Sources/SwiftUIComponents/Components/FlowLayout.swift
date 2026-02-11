import SwiftUI

// Original source: https://github.com/globulus/swiftui-flow-layout

// A view that arranges its children in a leading aligned directional flow.
//
// The following example shows a simple flow layout with three text views:
//
// ```
// var body: some View
// {
//    FlowLayout(
//        items: ["A", "B", "C"],
//        itemView:
//        {
//            Text($0)
//        }
// }
// ```
//

public struct FlowLayout<Item, ItemView: View>: View {
    // MARK: - Properties

    /// The distance between adjacent subviews.
    private let spacing: CGFloat

    /// The vertical alignment of items within each row.
    private let verticalAlignment: VerticalAlignment

    /// The items which are used to generate the child views.
    private let items: [Item]

    /// A view builder that creates a view for the corresponding item.
    private let itemView: (Item) -> ItemView

    /// The size of the content view.
    @State private var contentSize: CGSize = .zero

    // MARK: - Initialization

    /// Creates a new flow layout.
    ///
    /// - Parameter spacing : The distance between adjacent subviews (default: 8).
    /// - Parameter verticalAlignment: The vertical alignment of items within each row (default: .top).
    /// - Parameter items   : The items which are used to generate the child views.
    /// - Parameter itemView: A view builder that creates a view for the corresponding item.
    /// - Parameter item    : The item for which a view should be created
    ///
    public init(
        spacing: CGFloat = 8,
        verticalAlignment: VerticalAlignment = .top,
        items: [Item],
        @ViewBuilder itemView: @escaping (_ item: Item) -> ItemView
    ) {
        self.spacing = spacing
        self.verticalAlignment = verticalAlignment
        self.items = items
        self.itemView = itemView
    }

    // MARK: - Methods

    public var body: some View {
        GeometryReader {
            content(contentWidth: $0.size.width)
        }
        .frame(height: contentSize.height)
    }

    private func content(contentWidth: CGFloat) -> some View {
        // The current item top leading position.
        var position: CGPoint = .zero

        // The height of the current row
        var rowHeight: CGFloat = 0

        return ZStack(alignment: .topLeading) {
            ForEach(Array(items.enumerated()), id: \.offset) {
                itemIndex, item in
                itemView(item)
                    .alignmentGuide(.leading) {
                        itemDimensions in
                        // Reset values on first item
                        if itemIndex == 0 {
                            position = .zero
                            rowHeight = 0
                        }

                        // The item does not fit in this row.
                        if abs(position.x - itemDimensions.width) > contentWidth {
                            // Set the position to the start of the next row.
                            position.x = 0
                            position.y -= rowHeight + spacing

                            // Reset the row height as it still reflects the height of the current row.
                            rowHeight = 0
                        }

                        let newLeading = position.x

                        position.x -= itemDimensions.width + spacing

                        // Update the height of the current row, this will only be consumed as soon
                        // as jumping to the next row.
                        rowHeight = max(rowHeight, itemDimensions.height)

                        return newLeading
                    }
                    .alignmentGuide(.top) {
                        itemDimensions in
                        let verticalOffset: CGFloat = switch verticalAlignment {
                        case .top: 0
                        case .center: (rowHeight - itemDimensions.height) / 2
                        case .bottom: rowHeight - itemDimensions.height
                        default: 0
                        }

                        return position.y - verticalOffset
                    }
            }
        }
        .onSizeChange(update: $contentSize)
    }
}

// MARK: - Previews

struct FlowLayout_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("FlowLayout:")
                .font(.caption)
                .foregroundStyle(Color.accentColor)

            FlowLayout(
                items: items,
                itemView: {
                    Text($0)
                        .padding()
                        .background(Color.gray.opacity(0.2))
                        .border(Color.gray, width: 1)
                }
            )
            .background(Color.accentColor.opacity(0.2))
            .border(Color.accentColor, width: 1)

            Spacer()
        }
        .frame(width: 280, height: 500)
        .padding()
        .previewLayout(.sizeThatFits)
    }

    private static var items: [String] {
        [
            "Lorem ipsum dolor sit amet",
            "consetetur sadipscing",
            "elitr",
            "sed",
            "diam",
            "nonumy",
            "eirmod",
            "tempor",
            "invidunt",
            "ut labore et dolore magna aliquyam erat, sed diam",
            "voluptua",
        ]
    }
}
