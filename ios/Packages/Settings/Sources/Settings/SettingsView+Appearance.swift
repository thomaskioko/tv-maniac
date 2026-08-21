import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac
import TvManiacKit
import UserNotifications

extension SettingsView {
    // MARK: - Root Sections

    var rootSections: [SettingsRootSection] {
        uiState.rootGroups.map { group in
            SettingsRootSection(
                id: group.label,
                label: group.label,
                items: group.items.map { item in
                    let route = item.page.toRoute()
                    return SettingsNavigationItem(
                        id: route.rawValue,
                        icon: route.iconName,
                        title: item.title,
                        subtitle: item.summary,
                        onTap: { presenter.dispatch(action: OpenSettingsPage(page: item.page)) }
                    )
                }
            )
        }
    }

    // MARK: - Theme

    var themeItem: SettingsThemeItem<DeviceAppTheme> {
        SettingsThemeItem(
            icon: "paintpalette",
            title: uiState.labels.themeTitle,
            subtitle: uiState.labels.themeSubtitle,
            themes: DeviceAppTheme.sortedThemes,
            selectedTheme: store.appTheme,
            isCustomThemesLocked: uiState.premium.customThemesLocked,
            lockedBadgeText: uiState.premium.badgeText,
            lockedTitle: uiState.premium.themesLockedTitle,
            lockedMessage: uiState.premium.themesLockedMessage,
            lockedActionText: uiState.premium.upgradeText,
            lockedAccessibilityLabel: uiState.premium.lockedContentDescription,
            onUpgradeClick: { presenter.dispatch(action: UpgradeToPremiumClicked()) },
            onThemeSelected: { selectedTheme in
                store.appTheme = selectedTheme
                let theme = selectedTheme.toTheme()
                presenter.dispatch(action: ThemeSelected(theme: theme.toThemeModel()))
            }
        )
    }

    // MARK: - Image Quality

    var imageQualityItem: SettingsImageQualityItem {
        let currentQuality = uiState.imageQuality.toSwift()
        return SettingsImageQualityItem(
            icon: "photo",
            title: uiState.labels.imageQualityTitle,
            subtitle: uiState.labels.imageQualityDescription,
            options: SwiftImageQuality.allCases.map { quality in
                SettingsImageQualityOption(
                    id: quality.rawValue,
                    label: imageQualityTitle(for: quality),
                    onSelect: {
                        let kmpQuality = TvManiac.ImageQuality.fromSwift(quality)
                        presenter.dispatch(action: ImageQualitySelected(quality: kmpQuality))
                        store.imageQuality = quality
                    }
                )
            },
            selectedOptionId: currentQuality.rawValue
        )
    }

    // MARK: - Layout Toggles

    var layoutToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "haptic",
                icon: "iphone.radiowaves.left.and.right",
                title: uiState.labels.hapticFeedbackTitle,
                subtitle: uiState.labels.hapticFeedbackDescription,
                isOn: uiState.hapticFeedbackEnabled,
                onToggle: { presenter.dispatch(action: HapticFeedbackToggled(enabled: $0)) }
            ),
            SettingsToggleItem(
                id: "season-order",
                icon: "arrow.up.arrow.down",
                title: uiState.labels.seasonOrderTitle,
                subtitle: uiState.labels.seasonOrderDescription,
                isOn: uiState.newestSeasonFirst,
                onToggle: { presenter.dispatch(action: SeasonOrderToggled(enabled: $0)) }
            ),
            SettingsToggleItem(
                id: "blur-unwatched",
                icon: "eye.slash",
                title: uiState.labels.blurUnwatchedTitle,
                subtitle: uiState.labels.blurUnwatchedDescription,
                isOn: uiState.blurImage,
                onToggle: { presenter.dispatch(action: BlurUnwatchedToggled(enabled: $0)) }
            ),
        ]
    }

    // MARK: - Font Size

    var fontSizeItem: SettingsFontSizeItem {
        SettingsFontSizeItem(
            title: uiState.labels.fontSizeTitle,
            description: uiState.labels.fontSizeDescription,
            previewText: uiState.labels.fontSizePreview,
            resetLabel: uiState.labels.fontSizeReset,
            percent: store.fontSizePercent,
            onPercentChange: { newPercent in
                presenter.dispatch(action: FontSizeChanged(percent: Int32(newPercent)))
                store.fontSizePercent = newPercent
            }
        )
    }

    // MARK: - Discover Sections

    var discoverSectionsNavItem: SettingsNavigationItem {
        SettingsNavigationItem(
            id: SettingsPageRoute.discoverSections.rawValue,
            icon: SettingsPageRoute.discoverSections.iconName,
            title: uiState.labels.discoverSectionsTitle,
            subtitle: uiState.labels.discoverSectionsDescription,
            onTap: { presenter.dispatch(action: OpenSettingsPage(page: SettingsPage.discoverSections)) }
        )
    }

    var discoverSectionToggles: [SettingsToggleItem] {
        uiState.discoverSectionToggles.map { toggle in
            SettingsToggleItem(
                id: toggle.section.name,
                icon: discoverSectionIcon(for: toggle.section),
                title: toggle.label,
                subtitle: "",
                isOn: toggle.visible,
                onToggle: { presenter.dispatch(action: DiscoverSectionToggled(section: toggle.section, visible: $0)) }
            )
        }
    }

    func discoverSectionIcon(for section: ApiDiscoverSection) -> String {
        switch section.name {
        case "START_WATCHING": "play.circle"
        case "TRENDING_TODAY": "flame"
        case "UPCOMING": "calendar"
        case "POPULAR": "star"
        case "TOP_RATED": "trophy"
        default: "square.grid.2x2"
        }
    }

    // MARK: - Poster Style

    var posterStyleNavItem: SettingsNavigationItem {
        SettingsNavigationItem(
            id: SettingsPageRoute.posterStyle.rawValue,
            icon: SettingsPageRoute.posterStyle.iconName,
            title: uiState.labels.posterStyle.title,
            subtitle: uiState.labels.posterStyle.subtitle,
            onTap: { presenter.dispatch(action: OpenSettingsPage(page: SettingsPage.posterStyle)) }
        )
    }

    var posterStyleItem: SettingsPosterStyleItem {
        let labels = uiState.labels.posterStyle
        return SettingsPosterStyleItem(
            title: labels.title,
            description: labels.subtitle,
            livePreviewLabel: labels.livePreview,
            resetLabel: labels.reset,
            postersLabel: labels.postersLabel,
            landscapeLabel: labels.landscapeLabel,
            cornerLabel: labels.cornerLabel,

            postersOptions: posterWidthOptions(keyPrefix: "posters") { width in
                presenter.dispatch(action: PosterWidthSelected(width: width))
                store.posterWidthScale = Double(width.scale)
            },
            landscapeOptions: posterWidthOptions(keyPrefix: "landscape") { width in
                presenter.dispatch(action: LandscapeWidthSelected(width: width))
                store.landscapeWidthScale = Double(width.scale)
            },
            cornerOptions: ApiPosterCornerStyle.entries.map { style in
                SettingsPosterStyleOption(
                    id: style.name,
                    label: cornerStyleLabel(style),
                    onSelect: {
                        presenter.dispatch(action: PosterCornerStyleSelected(style: style))
                        store.posterCornerRadius = Double(style.cornerRadius)
                    }
                )
            },
            selectedPostersId: "posters-\(uiState.posterWidth.name)",
            selectedLandscapeId: "landscape-\(uiState.landscapeWidth.name)",
            selectedCornerId: uiState.posterCornerStyle.name,
            posterScale: CGFloat(uiState.posterWidth.scale),
            landscapeScale: CGFloat(uiState.landscapeWidth.scale),
            cornerRadius: CGFloat(uiState.posterCornerStyle.cornerRadius),
            isLocked: uiState.premium.posterStyleLocked,
            lockedBadgeText: uiState.premium.badgeText,
            lockedActionText: uiState.premium.upgradeText,
            lockedAccessibilityLabel: uiState.premium.lockedContentDescription,
            onUpgradeClick: { presenter.dispatch(action: UpgradeToPremiumClicked()) },
            onReset: {
                presenter.dispatch(action: PosterStyleReset())
                store.posterWidthScale = 1
                store.landscapeWidthScale = 1
                store.posterCornerRadius = 0
            }
        )
    }

    func posterWidthOptions(keyPrefix: String, onSelect: @escaping (ApiPosterWidth) -> Void) -> [SettingsPosterStyleOption] {
        ApiPosterWidth.entries.map { width in
            SettingsPosterStyleOption(
                id: "\(keyPrefix)-\(width.name)",
                label: widthLabel(width),
                onSelect: { onSelect(width) }
            )
        }
    }

    func widthLabel(_ width: ApiPosterWidth) -> String {
        let labels = uiState.labels.posterStyle
        return switch width.name {
        case "COMPACT": labels.widthCompact
        case "STANDARD": labels.widthStandard
        case "COMFORTABLE": labels.widthComfortable
        case "LARGE": labels.widthLarge
        default: width.name
        }
    }

    func cornerStyleLabel(_ style: ApiPosterCornerStyle) -> String {
        let labels = uiState.labels.posterStyle
        return switch style.name {
        case "SHARP": labels.cornerSharp
        case "CLASSIC": labels.cornerClassic
        case "ROUNDED": labels.cornerRounded
        case "PILL": labels.cornerPill
        default: style.name
        }
    }

    var autoBackupContent: SettingsAutoBackupContent {
        let autoBackup = uiState.backup.autoBackup
        return SettingsAutoBackupContent(
            title: autoBackup.title,
            description: autoBackup.description,
            isOn: autoBackup.enabled,
            locationTitle: autoBackup.locationTitle,
            locationLabel: autoBackup.locationLabel,
            hasLocation: autoBackup.hasLocation,
            scheduleTitle: autoBackup.scheduleTitle,
            scheduleOptions: autoBackup.scheduleOptions.map { option in
                SettingsAutoBackupScheduleOption(
                    id: option.interval.name,
                    label: option.label,
                    isSelected: option.selected,
                    onSelect: { presenter.dispatch(action: AutoBackupScheduleSelected(interval: option.interval)) }
                )
            },
            lastRunLabel: autoBackup.lastRunLabel,
            failureWarning: autoBackup.failureWarning,
            backupNowTitle: autoBackup.backupNowTitle,
            backupNowDescription: autoBackup.backupNowDescription,
            isBackingUp: autoBackup.isBackingUp,
            onToggle: { presenter.dispatch(action: AutoBackupToggled(enabled: $0)) },
            onBackupNow: { presenter.dispatch(action: BackupNowClicked()) },
            onChooseLocation: { presenter.dispatch(action: AutoBackupLocationClicked()) }
        )
    }

    var backupContent: SettingsBackupContent {
        SettingsBackupContent(
            exportTitle: uiState.backup.exportTitle,
            exportDescription: uiState.backup.exportDescription,
            isExporting: uiState.backup.isExporting || uiState.backup.awaitingDestination,
            importTitle: uiState.backup.importTitle,
            importDescription: uiState.backup.importDescription,
            isImporting: uiState.backup.isImporting || uiState.backup.awaitingSource,
            summary: uiState.backup.summary?.toContent(),
            summaryDismissAccessibilityLabel: String(\.cd_dismiss),
            autoBackup: autoBackupContent,
            isLocked: uiState.premium.backupLocked,
            lockedBadgeText: uiState.premium.badgeText,
            lockedTitle: uiState.premium.backupLockedTitle,
            lockedMessage: uiState.premium.backupLockedMessage,
            lockedActionText: uiState.premium.upgradeText,
            lockedAccessibilityLabel: uiState.premium.lockedContentDescription,
            onExport: { presenter.dispatch(action: BackupExportClicked()) },
            onImport: { presenter.dispatch(action: BackupImportClicked()) },
            onUpgradeClick: { presenter.dispatch(action: UpgradeToPremiumClicked()) },
            onDismissSummary: { presenter.dispatch(action: BackupSummaryDismissed()) }
        )
    }
}
