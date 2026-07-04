import Components
import DesignSystem
import Models
import SwiftUI

public struct ShowInfoView: View {
    @Environment(\.appTheme) private var theme

    private let isFollowed: Bool
    private let canAddToList: Bool
    private let genres: [SwiftGenres]
    private let trackLabel: String
    private let stopTrackingLabel: String
    private let addToListLabel: String
    private let rateLabel: String
    private let onAddToLibrary: () -> Void
    private let onAddToCustomList: () -> Void
    private let onRate: () -> Void

    public init(
        isFollowed: Bool,
        canAddToList: Bool,
        genres: [SwiftGenres],
        trackLabel: String,
        stopTrackingLabel: String,
        addToListLabel: String,
        rateLabel: String,
        onAddToLibrary: @escaping () -> Void,
        onAddToCustomList: @escaping () -> Void,
        onRate: @escaping () -> Void
    ) {
        self.isFollowed = isFollowed
        self.canAddToList = canAddToList
        self.genres = genres
        self.trackLabel = trackLabel
        self.stopTrackingLabel = stopTrackingLabel
        self.addToListLabel = addToListLabel
        self.rateLabel = rateLabel
        self.onAddToLibrary = onAddToLibrary
        self.onAddToCustomList = onAddToCustomList
        self.onRate = onRate
    }

    public var body: some View {
        VStack(spacing: theme.spacing.medium) {
            genreChips
            actionButtons
        }
    }

    @ViewBuilder
    private var genreChips: some View {
        if !genres.isEmpty {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .center, spacing: theme.spacing.xSmall) {
                    ForEach(genres, id: \.name) { item in
                        ChipView(label: item.name)
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        }
    }

    private var actionButtons: some View {
        HStack(alignment: .center, spacing: theme.spacing.xSmall) {
            FilledVerticalIconButton(
                text: isFollowed ? stopTrackingLabel : trackLabel,
                systemImage: isFollowed ? "minus.circle.fill" : "plus.circle.fill",
                containerColor: isFollowed ? .red.opacity(0.65) : nil,
                symbolEffectValue: isFollowed,
                symbolEffectDirection: isFollowed ? .down : .up,
                action: onAddToLibrary
            )
            FilledVerticalIconButton(
                text: addToListLabel,
                systemImage: "rectangle.on.rectangle.angled",
                action: onAddToCustomList
            )
            .disabled(!canAddToList)

            FilledVerticalIconButton(
                text: rateLabel,
                systemImage: "star",
                action: onRate
            )
        }
    }
}

#Preview("Followed — Add to List shown") {
    ShowInfoView(
        isFollowed: true,
        canAddToList: true,
        genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        addToListLabel: "Add To List",
        rateLabel: "Rate",
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {}
    )
    .padding()
    .appPreview(LightTheme())
}

#Preview("Followed — Add to List hidden") {
    ShowInfoView(
        isFollowed: true,
        canAddToList: false,
        genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        addToListLabel: "Add To List",
        rateLabel: "Rate",
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {}
    )
    .padding()
    .appPreview(LightTheme())
}

#Preview("Not Followed") {
    ShowInfoView(
        isFollowed: false,
        canAddToList: true,
        genres: [.init(name: "Drama"), .init(name: "Fantasy")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        addToListLabel: "Add To List",
        rateLabel: "Rate",
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {}
    )
    .padding()
    .appPreview(DarkTheme())
}
