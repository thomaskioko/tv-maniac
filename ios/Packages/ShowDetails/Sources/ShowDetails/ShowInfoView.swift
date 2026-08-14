import Components
import DesignSystem
import Models
import SwiftUI

public struct ShowInfoView: View {
    @Environment(\.appTheme) private var theme

    private let isFollowed: Bool
    private let canAddToList: Bool
    private let isInList: Bool
    private let genres: [SwiftGenres]
    private let trackLabel: String
    private let stopTrackingLabel: String
    private let listActionLabel: String
    private let moreLabel: String
    private let rateLabel: String
    private let watchAgainLabel: String
    private let canWatchAgain: Bool
    private let userRating: Int?
    private let onAddToLibrary: () -> Void
    private let onAddToCustomList: () -> Void
    private let onRate: () -> Void
    private let onWatchAgain: () -> Void

    public init(
        isFollowed: Bool,
        canAddToList: Bool,
        isInList: Bool,
        genres: [SwiftGenres],
        trackLabel: String,
        stopTrackingLabel: String,
        listActionLabel: String,
        moreLabel: String,
        rateLabel: String,
        watchAgainLabel: String,
        canWatchAgain: Bool = false,
        userRating: Int? = nil,
        onAddToLibrary: @escaping () -> Void,
        onAddToCustomList: @escaping () -> Void,
        onRate: @escaping () -> Void,
        onWatchAgain: @escaping () -> Void = {}
    ) {
        self.isFollowed = isFollowed
        self.canAddToList = canAddToList
        self.isInList = isInList
        self.genres = genres
        self.trackLabel = trackLabel
        self.stopTrackingLabel = stopTrackingLabel
        self.listActionLabel = listActionLabel
        self.moreLabel = moreLabel
        self.rateLabel = rateLabel
        self.watchAgainLabel = watchAgainLabel
        self.canWatchAgain = canWatchAgain
        self.userRating = userRating
        self.onAddToLibrary = onAddToLibrary
        self.onAddToCustomList = onAddToCustomList
        self.onRate = onRate
        self.onWatchAgain = onWatchAgain
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
                text: listActionLabel,
                systemImage: isInList ? "checklist" : "rectangle.on.rectangle.angled",
                containerColor: isInList ? theme.colors.success : nil,
                action: onAddToCustomList
            )
            .disabled(!canAddToList)

            moreMenu
        }
    }

    private var moreMenu: some View {
        Menu {
            Button(action: onRate) {
                Label(rateLabel, systemImage: userRating != nil ? "star.fill" : "star")
            }

            if canWatchAgain {
                Button(action: onWatchAgain) {
                    Label(watchAgainLabel, systemImage: "arrow.counterclockwise")
                }
            }
        } label: {
            Image(systemName: "ellipsis")
                .foregroundStyle(.appOnButtonBackground)
                .frame(width: Constants.moreWidth, height: Constants.moreHeight)
        }
        .buttonStyle(.borderedProminent)
        .controlSize(.small)
        .tint(theme.colors.buttonBackground)
        .buttonBorderShape(.roundedRectangle(radius: theme.shapes.medium))
        .accessibilityLabel(moreLabel)
    }

    private enum Constants {
        static let moreWidth: CGFloat = 44
        static let moreHeight: CGFloat = 35
    }
}

#Preview("Followed — Add to List shown") {
    ShowInfoView(
        isFollowed: true,
        canAddToList: true,
        isInList: false,
        genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        listActionLabel: "Add To List",
        moreLabel: "More",
        rateLabel: "Rate",
        watchAgainLabel: "Watch again",
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {},
        onWatchAgain: {}
    )
    .padding()
    .appPreview(LightTheme())
}

#Preview("Followed — Add to List hidden") {
    ShowInfoView(
        isFollowed: true,
        canAddToList: false,
        isInList: false,
        genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        listActionLabel: "Add To List",
        moreLabel: "More",
        rateLabel: "Rate",
        watchAgainLabel: "Watch again",
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {},
        onWatchAgain: {}
    )
    .padding()
    .appPreview(LightTheme())
}

#Preview("Not Followed") {
    ShowInfoView(
        isFollowed: false,
        canAddToList: true,
        isInList: false,
        genres: [.init(name: "Drama"), .init(name: "Fantasy")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        listActionLabel: "Add To List",
        moreLabel: "More",
        rateLabel: "Rate",
        watchAgainLabel: "Watch again",
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {},
        onWatchAgain: {}
    )
    .padding()
    .appPreview(DarkTheme())
}

#Preview("Already Rated") {
    ShowInfoView(
        isFollowed: true,
        canAddToList: true,
        isInList: false,
        genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        listActionLabel: "Add To List",
        moreLabel: "More",
        rateLabel: "Rate",
        watchAgainLabel: "Watch again",
        userRating: 9,
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {},
        onWatchAgain: {}
    )
    .padding()
    .appPreview(LightTheme())
}

#Preview("In a List") {
    ShowInfoView(
        isFollowed: true,
        canAddToList: true,
        isInList: true,
        genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        listActionLabel: "In List",
        moreLabel: "More",
        rateLabel: "Rate",
        watchAgainLabel: "Watch again",
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {},
        onWatchAgain: {}
    )
    .padding()
    .appPreview(LightTheme())
}

#Preview("Watch Again Available") {
    ShowInfoView(
        isFollowed: true,
        canAddToList: true,
        isInList: false,
        genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
        trackLabel: "Track",
        stopTrackingLabel: "Stop Tracking",
        listActionLabel: "Add To List",
        moreLabel: "More",
        rateLabel: "Rate",
        watchAgainLabel: "Watch again",
        canWatchAgain: true,
        onAddToLibrary: {},
        onAddToCustomList: {},
        onRate: {},
        onWatchAgain: {}
    )
    .padding()
    .appPreview(LightTheme())
}
