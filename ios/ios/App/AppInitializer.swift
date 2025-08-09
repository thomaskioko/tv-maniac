import Foundation
import SDWebImage
import SwiftUIComponents
import TvManiacKit

/// Initializes app-wide configurations and services
class AppInitializer {
    static let shared = AppInitializer()

    private init() {}

    func initialize() {
        ImageQualityManager.shared.initializeConfiguration()

        configureAppearance()
    }

    private func configureAppearance() {
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
}
