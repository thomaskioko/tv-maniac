import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct SortOptionsSheet: View {
    @Theme private var theme
    @Environment(\.dismiss) private var dismiss

    let state: LibraryState
    let onSortOptionSelected: (LibrarySortOption) -> Void
    let onGenreToggle: (String) -> Void
    let onStatusToggle: (ShowStatus) -> Void
    let onClearFilters: () -> Void
    let onApplyFilters: () -> Void

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: theme.spacing.large) {
                        sortBySection
                        genresSection
                        statusSection
                    }
                    .padding(.horizontal)
                    .padding(.top, theme.spacing.medium)
                }

                actionBar
            }
            .background(theme.colors.background)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(String(\.label_library_filter_title))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                }
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark")
                            .foregroundColor(theme.colors.onSurface)
                    }
                }
            }
        }
    }

    private var sortBySection: some View {
        FilterChipSection(
            title: String(\.label_library_sort_by),
            items: LibrarySortOption.allCases,
            selectedItems: [state.sortOption],
            labelProvider: { sortOption in
                switch sortOption {
                case .lastWatchedDesc:
                    String(\.label_library_sort_last_watched_desc)
                case .lastWatchedAsc:
                    String(\.label_library_sort_last_watched_asc)
                case .newEpisodes:
                    String(\.label_library_sort_new_episodes)
                case .episodesLeftDesc:
                    String(\.label_library_sort_episodes_left_desc)
                case .episodesLeftAsc:
                    String(\.label_library_sort_episodes_left_asc)
                case .alphabetical:
                    String(\.label_library_sort_alphabetical)
                default:
                    String(\.label_library_sort_last_watched_desc)
                }
            },
            onItemToggle: { onSortOptionSelected($0) },
            collapsedItemCount: 5
        )
    }

    @ViewBuilder
    private var genresSection: some View {
        let genres = Array(state.availableGenres)
        if !genres.isEmpty {
            FilterChipSection(
                title: String(\.label_library_filter_genres),
                items: genres,
                selectedItems: Set(state.selectedGenres),
                labelProvider: { $0 },
                onItemToggle: { onGenreToggle($0) },
                collapsedItemCount: 5
            )
        }
    }

    @ViewBuilder
    private var statusSection: some View {
        let statuses = Array(state.availableStatuses)
        if !statuses.isEmpty {
            FilterChipSection(
                title: String(\.label_library_filter_status),
                items: statuses,
                selectedItems: Set(state.selectedStatuses),
                labelProvider: { status in
                    switch status {
                    case .returningSeries:
                        String(\.label_library_status_returning)
                    case .planned:
                        String(\.label_library_status_planned)
                    case .inProduction:
                        String(\.label_library_status_in_production)
                    case .ended:
                        String(\.label_library_status_ended)
                    case .canceled:
                        String(\.label_library_status_canceled)
                    default:
                        status.displayName
                    }
                },
                onItemToggle: { onStatusToggle($0) },
                collapsedItemCount: 5
            )
        }
    }

    private var actionBar: some View {
        HStack(spacing: theme.spacing.medium) {
            Button {
                onClearFilters()
            } label: {
                HStack(spacing: 4) {
                    Image(systemName: "trash")
                        .font(.subheadline)
                    Text(String(\.label_library_filter_clear))
                        .textStyle(theme.typography.bodyMedium)
                }
                .foregroundColor(theme.colors.onSurface)
                .padding(.horizontal, theme.spacing.medium)
                .padding(.vertical, theme.spacing.small)
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.extraLarge)
                        .stroke(theme.colors.outline, lineWidth: 1)
                )
            }
            .buttonStyle(.plain)

            Button {
                onApplyFilters()
                dismiss()
            } label: {
                Text(String(\.label_library_filter_apply))
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundColor(theme.colors.onPrimary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, theme.spacing.small)
                    .background(theme.colors.primary)
                    .clipShape(RoundedRectangle(cornerRadius: theme.shapes.extraLarge))
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal)
        .padding(.vertical, theme.spacing.medium)
        .background(theme.colors.surface)
    }
}
