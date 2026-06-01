import Components
import DesignSystem
import SwiftUI
import TvManiac
import TvManiacKit

struct MyShowsSortOptionsSheet: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.dismiss) private var dismiss

    let selectedSortOption: ApiWatchlistSortOption
    let onSortOptionSelected: (ApiWatchlistSortOption) -> Void

    var body: some View {
        NavigationStack {
            ScrollView(showsIndicators: false) {
                sortBySection
                    .padding(.horizontal)
                    .padding(.top, theme.spacing.medium)
            }
            .background(theme.colors.background)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(String(\.label_library_sort_by))
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
            items: Array(ApiWatchlistSortOption.entries),
            selectedItems: [selectedSortOption],
            labelProvider: { option in
                if option == ApiWatchlistSortOption.addedDesc { return String(\.label_library_sort_added_desc) }
                if option == ApiWatchlistSortOption.addedAsc { return String(\.label_library_sort_added_asc) }
                if option == ApiWatchlistSortOption.releasedDesc { return String(\.label_library_sort_released_desc) }
                if option == ApiWatchlistSortOption.releasedAsc { return String(\.label_library_sort_released_asc) }
                if option == ApiWatchlistSortOption.titleAsc { return String(\.label_library_sort_title_asc) }
                if option == ApiWatchlistSortOption.titleDesc { return String(\.label_library_sort_title_desc) }
                return String(\.label_library_sort_added_desc)
            },
            onItemToggle: { onSortOptionSelected($0) },
            collapsedItemCount: 6
        )
    }
}
