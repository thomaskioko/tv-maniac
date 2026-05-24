//
//  RootNavigationView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 12/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit
import UserNotifications

struct RootNavigationView: View {
    private let rootPresenter: RootPresenter
    private let navigator: Navigator
    private let registry: ScreenRegistry
    @StateValue private var themeState: ThemeState
    @StateValue private var notificationPermissionState: NotificationPermissionState
    @StateValue private var episodeSheetSlot: ChildSlot<AnyObject, SheetChild>
    @StateValue private var accountLimitBannerVisible: KotlinBoolean
    @StateObject private var store = SettingsAppStorage.shared
    @EnvironmentObject private var appDelegate: AppDelegate
    @State private var rationaleActionTaken = false

    init(rootPresenter: RootPresenter, navigator: Navigator, registry: ScreenRegistry) {
        self.rootPresenter = rootPresenter
        self.navigator = navigator
        self.registry = registry
        _themeState = .init(rootPresenter.themeStateValue)
        _notificationPermissionState = .init(rootPresenter.notificationPermissionStateValue)
        _episodeSheetSlot = .init(rootPresenter.episodeSheetSlotValue)
        _accountLimitBannerVisible = .init(rootPresenter.accountLimitBannerVisibleValue)
    }

    var body: some View {
        SplashView(isDebug: appDelegate.isDebug) {
            VStack(spacing: 0) {
                if accountLimitBannerVisible.boolValue {
                    AccountLimitBanner(onDismiss: { rootPresenter.onDismissAccountLimitBanner() })
                }
                TabBarView(
                    presenter: rootPresenter.homePresenter,
                    navigator: navigator,
                    registry: registry
                )
            }
        }
        .appTheme()
        .sheet(
            isPresented: Binding(
                get: { episodeSheetSlot.child != nil },
                set: { isPresented in
                    if !isPresented, let child = episodeSheetSlot.child?.instance {
                        registry.dismissSheet(child: child)
                    }
                }
            )
        ) {
            if let child = episodeSheetSlot.child?.instance {
                registry.sheet(for: child)
            }
        }
        .onChange(of: themeState.appTheme) { _, newTheme in
            store.appTheme = newTheme.toDeviceAppTheme()
        }
        .sheet(
            isPresented: Binding(
                get: { notificationPermissionState.showRationale },
                set: { newValue in
                    if !newValue, !rationaleActionTaken {
                        rootPresenter.onRationaleDismissed()
                    }
                    rationaleActionTaken = false
                }
            )
        ) {
            NotificationRationaleSheet(
                title: String(\.notification_rationale_title),
                message: String(\.notification_rationale_message),
                enableButtonText: String(\.notification_rationale_enable),
                dismissButtonText: String(\.notification_rationale_not_now),
                onEnable: {
                    rationaleActionTaken = true
                    rootPresenter.onRationaleAccepted()
                },
                onDismiss: {
                    rationaleActionTaken = true
                    rootPresenter.onRationaleDismissed()
                }
            )
            .presentationDetents([.medium])
            .presentationDragIndicator(.visible)
            .appTheme()
        }
        .debugTapGesture(isEnabled: appDelegate.isDebug) {
            rootPresenter.onDeepLink(
                destination: DeepLinkDestination.DebugMenu.shared
            )
        }
        .onChange(of: notificationPermissionState.requestPermission) { _, _ in
            requestNotificationPermissionIfNeeded()
        }
        .onChange(of: notificationPermissionState.showRationale) { _, _ in
            requestNotificationPermissionIfNeeded()
        }
    }

    private func requestNotificationPermissionIfNeeded() {
        guard notificationPermissionState.requestPermission,
              !notificationPermissionState.showRationale else { return }
        Task {
            do {
                let granted = try await UNUserNotificationCenter.current()
                    .requestAuthorization(options: [.alert, .badge, .sound])
                rootPresenter.onNotificationPermissionResult(granted: granted)
            } catch {
                rootPresenter.onNotificationPermissionResult(granted: false)
            }
        }
    }
}
