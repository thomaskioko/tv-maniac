import Components
import DesignSystem
import SwiftUI

public struct RatingSheetContent: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let removeLabel: String
    private let userRating: Int?
    private let onRatingSelected: (Int) -> Void
    private let onRemove: () -> Void

    public init(
        title: String,
        removeLabel: String,
        userRating: Int?,
        onRatingSelected: @escaping (Int) -> Void,
        onRemove: @escaping () -> Void
    ) {
        self.title = title
        self.removeLabel = removeLabel
        self.userRating = userRating
        self.onRatingSelected = onRatingSelected
        self.onRemove = onRemove
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            grabber

            VStack(alignment: .leading, spacing: theme.spacing.medium) {
                Text(title)
                    .textStyle(theme.typography.titleLarge)
                    .foregroundStyle(.appOnSurface)

                starRow
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.top, theme.spacing.small)

            if userRating != nil {
                Divider()
                    .padding(.top, theme.spacing.medium)

                removeRow
            }
        }
        .padding(.bottom, theme.spacing.large)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(.appSurface)
        .clipShape(.rect(topLeadingRadius: sheetCornerRadius, topTrailingRadius: sheetCornerRadius))
    }

    private var grabber: some View {
        RoundedRectangle(cornerRadius: 2.5)
            .fill(theme.colors.onSurface.opacity(0.4))
            .frame(width: 36, height: 5)
            .frame(maxWidth: .infinity, alignment: .center)
            .padding(.vertical, theme.spacing.small)
    }

    private var starRow: some View {
        HStack(spacing: theme.spacing.small) {
            ForEach(1 ... starCount, id: \.self) { star in
                starButton(value: star * pointsPerStar)
            }
        }
    }

    private func starButton(value: Int) -> some View {
        Button(action: { onRatingSelected(value) }, label: {
            Image(systemName: symbol(for: value))
                .textStyle(theme.typography.displaySmall)
                .foregroundStyle(.appAccent)
        })
        .buttonStyle(PlainButtonStyle())
        .accessibilityLabel("Rate \(value) out of 10")
    }

    private var removeRow: some View {
        Button(action: onRemove) {
            HStack(spacing: theme.spacing.medium) {
                Image(systemName: "trash")
                    .textStyle(theme.typography.titleMedium)
                    .foregroundStyle(.appError)
                    .frame(width: 24)

                Text(removeLabel)
                    .textStyle(theme.typography.bodyLarge)
                    .foregroundStyle(.appError)

                Spacer()
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.vertical, theme.spacing.small)
        }
        .buttonStyle(PlainButtonStyle())
    }

    private func symbol(for value: Int) -> String {
        guard let userRating else { return "star" }
        if userRating >= value { return "star.fill" }
        if userRating == value - 1 { return "star.leadinghalf.filled" }
        return "star"
    }
}

private let starCount = 5
private let pointsPerStar = 2
private let sheetCornerRadius: CGFloat = 16

#Preview("Unrated") {
    RatingSheetContent(
        title: "Your rating",
        removeLabel: "Remove rating",
        userRating: nil,
        onRatingSelected: { _ in },
        onRemove: {}
    )
    .appPreview()
}

#Preview("Rated") {
    RatingSheetContent(
        title: "Your rating",
        removeLabel: "Remove rating",
        userRating: 8,
        onRatingSelected: { _ in },
        onRemove: {}
    )
    .appPreview()
}
