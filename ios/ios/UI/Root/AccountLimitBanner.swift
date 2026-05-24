//
//  AccountLimitBanner.swift
//  tv-maniac
//

import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct AccountLimitBanner: View {
    let onDismiss: () -> Void

    @Environment(\.openURL) private var openURL

    private static let traktVipUrl = URL(string: "https://trakt.tv/vip")!

    var body: some View {
        AccountLimitBannerView(
            message: String(\.account_limit_banner_message),
            upgradeTitle: String(\.account_limit_upgrade_cta),
            dismissAccessibilityLabel: String(\.account_limit_dismiss_cta),
            onUpgrade: { openURL(Self.traktVipUrl) },
            onDismiss: onDismiss
        )
    }
}

#Preview {
    AccountLimitBanner(onDismiss: {})
}
