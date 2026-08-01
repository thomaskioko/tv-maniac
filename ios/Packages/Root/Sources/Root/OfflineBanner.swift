//
//  OfflineBanner.swift
//  tv-maniac
//

import Components
import SwiftUI
import TvManiacKit

struct OfflineBanner: View {
    let isBackOnline: Bool
    let onDismiss: () -> Void

    var body: some View {
        TvManiacBanner(
            message: isBackOnline ? String(\.status_connected) : String(\.status_no_connection),
            style: isBackOnline ? .success : .warning,
            dismissAccessibilityLabel: String(\.cd_dismiss),
            onDismiss: onDismiss
        )
    }
}

#Preview {
    VStack {
        OfflineBanner(isBackOnline: false, onDismiss: {})
        OfflineBanner(isBackOnline: true, onDismiss: {})
    }
}
