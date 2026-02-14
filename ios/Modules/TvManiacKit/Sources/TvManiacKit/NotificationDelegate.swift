import TvManiac
import UserNotifications

public class NotificationDelegate: NSObject, UNUserNotificationCenterDelegate {
    private weak var rootPresenter: RootPresenter?

    override public init() {
        super.init()
    }

    public func setRootPresenter(_ presenter: RootPresenter) {
        rootPresenter = presenter
    }

    public func userNotificationCenter(
        _: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        handleNotificationTap(response.notification)
        completionHandler()
    }

    public func userNotificationCenter(
        _: UNUserNotificationCenter,
        willPresent _: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .list])
    }

    private func handleNotificationTap(_ notification: UNNotification) {
        guard let rootPresenter else { return }

        let userInfo = notification.request.content.userInfo

        guard let showId = userInfo["show_id"] as? Int64 else {
            navigateToLibrary()
            return
        }

        rootPresenter.onDeepLink(destination: DeepLinkDestination.ShowDetails(showId: showId, forceRefresh: false))
    }

    private func navigateToLibrary() {
        guard let rootPresenter else { return }

        let childStack = rootPresenter.childStack.value
        let activeChild = childStack.active.instance

        switch onEnum(of: activeChild) {
        case let .home(homeChild):
            homeChild.presenter.onLibraryClicked()
        default:
            break
        }
    }
}
