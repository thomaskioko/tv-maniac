import SwiftUI
import SwiftUIComponents
import TvManiac
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
            items: Array(LibrarySortOption.entries),
            selectedItems: [state.sortOption],
            labelProvider: { sortOption in
                if sortOption == LibrarySortOption.rankAsc { return String(\.label_library_sort_rank_asc) }
                if sortOption == LibrarySortOption.rankDesc { return String(\.label_library_sort_rank_desc) }
                if sortOption == LibrarySortOption.addedDesc { return String(\.label_library_sort_added_desc) }
                if sortOption == LibrarySortOption.addedAsc { return String(\.label_library_sort_added_asc) }
                if sortOption == LibrarySortOption.releasedDesc { return String(\.label_library_sort_released_desc) }
                if sortOption == LibrarySortOption.releasedAsc { return String(\.label_library_sort_released_asc) }
                if sortOption == LibrarySortOption.titleAsc { return String(\.label_library_sort_title_asc) }
                if sortOption == LibrarySortOption.titleDesc { return String(\.label_library_sort_title_desc) }
                return String(\.label_library_sort_rank_desc)
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
                    if status == ShowStatus.returningSeries { return String(\.label_library_status_returning) }
                    if status == ShowStatus.planned { return String(\.label_library_status_planned) }
                    if status == ShowStatus.inProduction { return String(\.label_library_status_in_production) }
                    if status == ShowStatus.ended { return String(\.label_library_status_ended) }
                    if status == ShowStatus.canceled { return String(\.label_library_status_canceled) }
                    return status.displayName
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
