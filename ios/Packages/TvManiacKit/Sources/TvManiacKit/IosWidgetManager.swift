import Models
import TvManiac
import WidgetKit

final class IosWidgetManager: WidgetWidgetManager {
    private let kind = "TvManiacUpNextWidget"

    func hasInstalledWidgets(onResult: @escaping (KotlinBoolean) -> Void) {
        WidgetCenter.shared.getCurrentConfigurations { result in
            switch result {
            case let .success(widgets):
                onResult(KotlinBoolean(bool: !widgets.isEmpty))
            case .failure:
                onResult(KotlinBoolean(bool: false))
            }
        }
    }

    func containerPath() -> String? {
        WidgetContainer.directoryURL()?.path
    }

    func reloadTimelines() {
        WidgetCenter.shared.reloadTimelines(ofKind: kind)
    }
}
