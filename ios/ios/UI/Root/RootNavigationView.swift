//
//  RootNavigationView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 12/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit
import UserNotifications

struct RootNavigationView: View {
    private let rootPresenter: RootPresenter
    private let rootNavigator: RootNavigator
    @StateObject @KotlinStateFlow private var themeState: ThemeState
    @StateObject @KotlinStateFlow private var notificationPermissionState: NotificationPermissionState
    @StateObject private var store = SettingsAppStorage.shared
    @EnvironmentObject private var appDelegate: AppDelegate
    @State private var rationaleActionTaken = false

    init(rootPresenter: RootPresenter, rootNavigator: RootNavigator) {
        self.rootPresenter = rootPresenter
        self.rootNavigator = rootNavigator
        _themeState = .init(rootPresenter.themeState)
        _notificationPermissionState = .init(rootPresenter.notificationPermissionState)
    }

    var body: some View {
        SplashView {
            DecomposeNavigationStack(
                stack: rootPresenter.childStack,
                onBack: rootNavigator.popTo
            ) { child in
                switch onEnum(of: child) {
                case let .home(child):
                    TabBarView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let .showDetails(child):
                    ShowDetailsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let .seasonDetails(child):
                    SeasonDetailsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let .profile(child):
                    ProfileTab(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let .settings(child):
                    SettingsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let .debug(child):
                    DebugMenuView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case .moreShows:
                    EmptyView()
                case .trailers:
                    EmptyView()
                case .genreShows:
                    EmptyView()
                }
            }
        }
        .appTheme()
        .onChange(of: themeState.appTheme) { newTheme in
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
        #if DEBUG
        .debugTapGesture {
                rootPresenter.onDeepLink(
                    destination: DeepLinkDestination.DebugMenu.shared
                )
            }
        #endif
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
