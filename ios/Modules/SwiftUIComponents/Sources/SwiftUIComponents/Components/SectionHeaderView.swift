import SwiftUI

public struct SectionHeaderView: View {
    @Theme private var theme

    private let title: String

    public init(title: String) {
        self.title = title
    }

    public var body: some View {
        HStack {
            Spacer()
            Text(title.uppercased())
                .textStyle(theme.typography.labelMedium)
                .foregroundColor(theme.colors.onSurface)
                .padding(.horizontal, theme.spacing.medium)
                .padding(.vertical, theme.spacing.xSmall)
                .background(
                    Capsule()
                        .fill(.ultraThinMaterial)
                )
            Spacer()
        }
        .padding(.vertical, theme.spacing.small)
        .accessibilityAddTraits(.isHeader)
    }
}

#Preview {
    VStack(spacing: 20) {
        SectionHeaderView(title: "Up Next")
        SectionHeaderView(title: "Not watched for a while")
    }
}
