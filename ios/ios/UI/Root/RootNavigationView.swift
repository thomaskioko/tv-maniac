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
    @StateValue private var themeState: ThemeState
    @StateValue private var notificationPermissionState: NotificationPermissionState
    @StateValue private var episodeSheetSlot: ChildSlot<AnyObject, AnyObject>
    @StateObject private var store = SettingsAppStorage.shared
    @EnvironmentObject private var appDelegate: AppDelegate
    @State private var rationaleActionTaken = false

    init(rootPresenter: RootPresenter, rootNavigator: RootNavigator) {
        self.rootPresenter = rootPresenter
        self.rootNavigator = rootNavigator
        _themeState = .init(rootPresenter.themeStateValue)
        _notificationPermissionState = .init(rootPresenter.notificationPermissionStateValue)
        _episodeSheetSlot = .init(rootPresenter.episodeSheetSlotValue)
    }

    var body: some View {
        SplashView {
            DecomposeNavigationStack(
                stack: rootPresenter.childStackValue,
                onBack: rootNavigator.popTo
            ) { child in
                switch child {
                case let child as RootScreenHome:
                    TabBarView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as RootScreenShowDetails:
                    ShowDetailsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as RootScreenSeasonDetails:
                    SeasonDetailsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as RootScreenSearch:
                    SearchTab(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as RootScreenSettings:
                    SettingsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as RootScreenDebug:
                    DebugMenuView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as RootScreenMoreShows:
                    MoreShowsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                default:
                    EmptyView()
                }
            }
        }
        .appTheme()
        .sheet(
            isPresented: Binding(
                get: { episodeSheetSlot.child != nil },
                set: { isPresented in
                    if !isPresented, let presenter = episodeSheetSlot.child?.instance as? EpisodeDetailSheetPresenter {
                        presenter.dispatch(action: EpisodeDetailSheetActionDismiss())
                    }
                }
            )
        ) {
            if let presenter = episodeSheetSlot.child?.instance as? EpisodeDetailSheetPresenter {
                EpisodeDetailSheetView(presenter: presenter)
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
