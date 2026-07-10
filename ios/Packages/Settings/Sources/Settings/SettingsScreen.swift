import Components
import DesignSystem
import Models
import SwiftUI

public struct SettingsScreen<Theme: ThemeItem>: View {
    public struct State {
        public let isLoading: Bool
        public let rootTitle: String
        public let currentPage: SettingsPageRoute
        public let rootSections: [SettingsRootSection]
        public let themeItem: SettingsThemeItem<Theme>
        public let imageQualityItem: SettingsImageQualityItem
        public let behaviorToggles: [SettingsToggleItem]
        public let notificationToggles: [SettingsToggleItem]
        public let privacyToggles: [SettingsToggleItem]
        public let privacyLinks: [SettingsNavigationItem]
        public let infoContent: SettingsInfoContent
        public let licenseSections: [SettingsLicenseSection]
        public let accountContent: SettingsAccountContent

        public init(
            isLoading: Bool,
            rootTitle: String,
            currentPage: SettingsPageRoute,
            rootSections: [SettingsRootSection],
            themeItem: SettingsThemeItem<Theme>,
            imageQualityItem: SettingsImageQualityItem,
            behaviorToggles: [SettingsToggleItem],
            notificationToggles: [SettingsToggleItem],
            privacyToggles: [SettingsToggleItem],
            privacyLinks: [SettingsNavigationItem],
            infoContent: SettingsInfoContent,
            licenseSections: [SettingsLicenseSection],
            accountContent: SettingsAccountContent
        ) {
            self.isLoading = isLoading
            self.rootTitle = rootTitle
            self.currentPage = currentPage
            self.rootSections = rootSections
            self.themeItem = themeItem
            self.imageQualityItem = imageQualityItem
            self.behaviorToggles = behaviorToggles
            self.notificationToggles = notificationToggles
            self.privacyToggles = privacyToggles
            self.privacyLinks = privacyLinks
            self.infoContent = infoContent
            self.licenseSections = licenseSections
            self.accountContent = accountContent
        }
    }

    @Environment(\.appTheme) private var appTheme

    private let state: State
    private let onBack: () -> Void

    public init(
        state: State,
        onBack: @escaping () -> Void
    ) {
        self.state = state
        self.onBack = onBack
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                if state.isLoading {
                    SettingsLoadingUI()
                } else {
                    pageBody(for: state.currentPage)
                }
                Spacer().frame(height: appTheme.spacing.xLarge)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.top, toolbarInset)
        }
        .scrollContentBackground(.hidden)
        .background(appTheme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(handledByPresenter: true, onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: toolbarTitle(state.currentPage),
                opacity: 1.0,
                leadingIcon: {
                    GlassButton(icon: "chevron.left", action: onBack)
                }
            ),
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
    }

    @ViewBuilder
    private func pageBody(for page: SettingsPageRoute) -> some View {
        switch page {
        case .root:
            SettingsRootContentView(sections: state.rootSections)
        case .appearance:
            AppearancePageView(themeItem: state.themeItem, imageQualityItem: state.imageQualityItem)
        case .behavior:
            SettingsTogglesPageView(toggles: state.behaviorToggles)
        case .notifications:
            SettingsTogglesPageView(toggles: state.notificationToggles)
        case .privacy:
            PrivacyPageView(toggles: state.privacyToggles, links: state.privacyLinks)
        case .info:
            InfoPageView(content: state.infoContent)
        case .licenses:
            LicensesPageView(sections: state.licenseSections)
        case .account:
            AccountPageView(content: state.accountContent)
        case .layout:
            LayoutPageView()
        }
    }

    private func toolbarTitle(_ page: SettingsPageRoute) -> String {
        page == .root ? state.rootTitle : title(for: page)
    }

    private func title(for route: SettingsPageRoute) -> String {
        for section in state.rootSections {
            if let item = section.items.first(where: { $0.id == route.rawValue }) {
                return item.title
            }
        }
        return state.rootTitle
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        // GlassToolbar is `56 + safeAreaTop` tall; clear it with a small gap so the
        // first card is not tucked under the toolbar.
        return 56 + safeAreaTop + appTheme.spacing.small
    }
}
