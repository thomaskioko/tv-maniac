import TvManiac

// MARK: - SettingsPage Mapping

public extension SettingsPage {
    func toRoute() -> SettingsPageRoute {
        if self == SettingsPage.appearance { return .appearance }
        if self == SettingsPage.behavior { return .behavior }
        if self == SettingsPage.notifications { return .notifications }
        if self == SettingsPage.privacy { return .privacy }
        if self == SettingsPage.info { return .info }
        if self == SettingsPage.licenses { return .licenses }
        if self == SettingsPage.trakt { return .trakt }
        return .root
    }
}
