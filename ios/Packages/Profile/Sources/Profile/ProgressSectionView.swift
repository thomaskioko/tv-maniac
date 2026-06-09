import Components
import DesignSystem
import Models
import SwiftUI

public struct ProgressSectionView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass
    @SwiftUI.State private var filter: ProgressFilter = .completed

    private var posterWidth: CGFloat {
        ImageType.poster.width(widthSizeClass)
    }

    private let inProgress: SwiftSectionState<SwiftProfileShow>
    private let completed: SwiftSectionState<SwiftProfileShow>
    private let title: String
    private let inProgressLabel: String
    private let completedLabel: String
    private let emptyLabel: String
    private let retryLabel: String
    private let onShowClick: (Int64) -> Void
    private let onRetry: () -> Void

    public init(
        inProgress: SwiftSectionState<SwiftProfileShow>,
        completed: SwiftSectionState<SwiftProfileShow>,
        title: String,
        inProgressLabel: String,
        completedLabel: String,
        emptyLabel: String,
        retryLabel: String,
        onShowClick: @escaping (Int64) -> Void,
        onRetry: @escaping () -> Void
    ) {
        self.inProgress = inProgress
        self.completed = completed
        self.title = title
        self.inProgressLabel = inProgressLabel
        self.completedLabel = completedLabel
        self.emptyLabel = emptyLabel
        self.retryLabel = retryLabel
        self.onShowClick = onShowClick
        self.onRetry = onRetry
    }

    public var body: some View {
        if isHidden {
            EmptyView()
        } else {
            CollapsibleSection(title: title, contentSpacing: theme.spacing.small) {
                VStack(alignment: .leading, spacing: theme.spacing.small) {
                    filterRow
                    body(for: selectedState)
                }
            }
        }
    }

    private var isHidden: Bool {
        if case .empty = inProgress, case .empty = completed {
            return true
        }
        return false
    }

    private var selectedState: SwiftSectionState<SwiftProfileShow> {
        switch filter {
        case .inProgress: inProgress
        case .completed: completed
        }
    }

    private var filterRow: some View {
        HStack(spacing: theme.spacing.small) {
            ProgressFilterChip(
                label: completedLabel,
                systemImage: "checkmark",
                isSelected: filter == .completed
            ) { filter = .completed }

            ProgressFilterChip(
                label: inProgressLabel,
                systemImage: "hourglass",
                isSelected: filter == .inProgress
            ) { filter = .inProgress }
        }
        .padding(.horizontal, theme.spacing.medium)
    }

    @ViewBuilder
    private func body(for sectionState: SwiftSectionState<SwiftProfileShow>) -> some View {
        switch sectionState {
        case .loading:
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: theme.spacing.small) {
                    ForEach(0 ..< 3, id: \.self) { _ in
                        ShimmerView(cornerRadius: theme.shapes.medium)
                            .frame(width: posterWidth, height: posterWidth / ImageType.poster.aspect)
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        case let .error(message):
            InlineSectionError(
                message: message,
                retryLabel: retryLabel,
                onRetry: onRetry
            )
        case let .content(shows):
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: theme.spacing.small) {
                    ForEach(shows) { show in
                        Button(action: { onShowClick(show.id) }) {
                            PosterItemView(
                                title: show.title,
                                posterUrl: show.posterUrl,
                                posterWidth: posterWidth,
                                aspectRatio: ImageType.poster.aspect,
                                posterRadius: theme.shapes.medium
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        case .empty:
            Text(emptyLabel)
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.horizontal, theme.spacing.medium)
                .padding(.vertical, theme.spacing.large)
        }
    }
}

private enum ProgressFilter {
    case completed
    case inProgress
}

private struct ProgressFilterChip: View {
    @Environment(\.appTheme) private var theme

    let label: String
    let systemImage: String
    let isSelected: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: theme.spacing.xSmall) {
                Image(systemName: systemImage)
                    .textStyle(theme.typography.labelLarge)

                Text(label)
                    .textStyle(theme.typography.labelLarge)
            }
            .foregroundStyle(theme.colors.secondary)
            .padding(.horizontal, theme.spacing.small)
            .padding(.vertical, theme.spacing.xSmall)
            .background(
                RoundedRectangle(cornerRadius: theme.shapes.small, style: .continuous)
                    .fill(theme.colors.secondary.opacity(isSelected ? 0.24 : 0.08))
            )
            .overlay(
                RoundedRectangle(cornerRadius: theme.shapes.small, style: .continuous)
                    .stroke(theme.colors.secondary.opacity(isSelected ? 0.5 : 0), lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    ProgressSectionView(
        inProgress: .content([
            SwiftProfileShow(id: 1, title: "Breaking Bad", posterUrl: nil),
            SwiftProfileShow(id: 2, title: "Game of Thrones", posterUrl: nil),
        ]),
        completed: .content([
            SwiftProfileShow(id: 3, title: "Stranger Things", posterUrl: nil),
        ]),
        title: "Progress",
        inProgressLabel: "In Progress",
        completedLabel: "Completed",
        emptyLabel: "Nothing here yet",
        retryLabel: "Retry",
        onShowClick: { _ in },
        onRetry: {}
    )
}
