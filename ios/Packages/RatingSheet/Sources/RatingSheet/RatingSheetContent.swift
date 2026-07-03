import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct RatingSheetContent: View {
    @Environment(\.appTheme) private var theme

    private let userRating: Int?
    private let onRatingSelected: (Int) -> Void
    private let onRemove: () -> Void

    public init(
        userRating: Int?,
        onRatingSelected: @escaping (Int) -> Void,
        onRemove: @escaping () -> Void
    ) {
        self.userRating = userRating
        self.onRatingSelected = onRatingSelected
        self.onRemove = onRemove
    }

    public var body: some View {
        VStack(spacing: theme.spacing.large) {
            Text(String(\.label_rating_sheet_title))
                .textStyle(theme.typography.titleLarge)
                .foregroundStyle(.appOnSurface)
                .padding(.top, theme.spacing.large)

            starRow

            if userRating != nil {
                Button(action: onRemove) {
                    Text(String(\.label_action_remove_rating))
                        .textStyle(theme.typography.bodyLarge)
                        .foregroundStyle(.appError)
                }
                .buttonStyle(PlainButtonStyle())
                .padding(.bottom, theme.spacing.small)
            }
        }
        .padding(.horizontal, theme.spacing.medium)
        .padding(.bottom, theme.spacing.large)
        .frame(maxWidth: .infinity)
        .background(.appSurface)
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

    private func symbol(for value: Int) -> String {
        guard let userRating else { return "star" }
        if userRating >= value { return "star.fill" }
        if userRating == value - 1 { return "star.leadinghalf.filled" }
        return "star"
    }
}

private let starCount = 5
private let pointsPerStar = 2

#Preview("Unrated") {
    RatingSheetContent(
        userRating: nil,
        onRatingSelected: { _ in },
        onRemove: {}
    )
    .appPreview()
}

#Preview("Rated") {
    RatingSheetContent(
        userRating: 8,
        onRatingSelected: { _ in },
        onRemove: {}
    )
    .appPreview()
}
