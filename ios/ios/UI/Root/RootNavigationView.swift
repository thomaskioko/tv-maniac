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
    private let navigator: Navigator
    @StateValue private var themeState: ThemeState
    @StateValue private var notificationPermissionState: NotificationPermissionState
    @StateValue private var episodeSheetSlot: ChildSlot<AnyObject, SheetChild>
    @StateObject private var store = SettingsAppStorage.shared
    @EnvironmentObject private var appDelegate: AppDelegate
    @State private var rationaleActionTaken = false

    init(rootPresenter: RootPresenter, navigator: Navigator) {
        self.rootPresenter = rootPresenter
        self.navigator = navigator
        _themeState = .init(rootPresenter.themeStateValue)
        _notificationPermissionState = .init(rootPresenter.notificationPermissionStateValue)
        _episodeSheetSlot = .init(rootPresenter.episodeSheetSlotValue)
    }

    var body: some View {
        SplashView {
            DecomposeNavigationStack(
                stack: rootPresenter.childStackValue,
                onBack: navigator.popTo
            ) { child in
                if let screen = child as? ScreenDestination<AnyObject> {
                    let presenter = screen.presenter
                    switch presenter {
                    case let presenter as HomePresenter:
                        TabBarView(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as ShowDetailsPresenter:
                        ShowDetailsView(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as SeasonDetailsPresenter:
                        SeasonDetailsView(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as SearchShowsPresenter:
                        SearchTab(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as SettingsPresenter:
                        SettingsView(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as DebugPresenter:
                        DebugMenuView(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    case let presenter as MoreShowsPresenter:
                        MoreShowsView(presenter: presenter)
                            .id(ObjectIdentifier(child))
                    default:
                        EmptyView()
                    }
                } else {
                    EmptyView()
                }
            }
        }
        .appTheme()
        .sheet(
            isPresented: Binding(
                get: { episodeSheetSlot.child != nil },
                set: { isPresented in
                    if !isPresented,
                       let sheet = episodeSheetSlot.child?.instance as? SheetDestination<AnyObject>,
                       let presenter = sheet.presenter as? EpisodeSheetPresenter
                    {
                        presenter.dispatch(action: EpisodeSheetActionDismiss())
                    }
                }
            )
        ) {
            if let sheet = episodeSheetSlot.child?.instance as? SheetDestination<AnyObject>,
               let presenter = sheet.presenter as? EpisodeSheetPresenter
            {
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
