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
    @StateValue private var episodeSheetSlot: ChildSlot<AnyObject, SheetChild>
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
                case let child as HomeDestination:
                    TabBarView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as ShowDetailsDestination:
                    ShowDetailsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as SeasonDetailsDestination:
                    SeasonDetailsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as SearchDestination:
                    SearchTab(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as SettingsDestination:
                    SettingsView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as DebugDestination:
                    DebugMenuView(presenter: child.presenter)
                        .id(ObjectIdentifier(child))
                case let child as MoreShowsDestination:
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
                    if !isPresented, let child = episodeSheetSlot.child?.instance as? EpisodeDetailDestination {
                        child.presenter.dispatch(action: EpisodeDetailSheetActionDismiss())
                    }
                }
            )
        ) {
            if let child = episodeSheetSlot.child?.instance as? EpisodeDetailDestination {
                EpisodeDetailSheetView(presenter: child.presenter)
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
