import Combine
import Foundation
import SDWebImage
import SwiftUI
import SwiftUIComponents
import TvManiac

/// Manages image quality configuration for SDWebImage
class ImageQualityManager: ObservableObject {
    static let shared = ImageQualityManager()

    private let settingsStorage = SettingsAppStorage.shared
    private var cancellables = Set<AnyCancellable>()

    private init() {
        observeImageQualityChanges()
    }

    private func observeImageQualityChanges() {
        settingsStorage.objectWillChange
            .sink { [weak self] _ in
                self?.applyImageQuality(self?.settingsStorage.imageQuality ?? .high)
            }
            .store(in: &cancellables)
    }

    private func applyImageQuality(_ quality: SwiftImageQuality) {
        let configQuality = mapToConfigurationQuality(quality)
        ImageConfiguration.configure(quality: configQuality)

        SDImageCache.shared.clearMemory()
    }

    private func mapToConfigurationQuality(_ quality: SwiftImageQuality) -> ImageConfiguration.Quality {
        switch quality {
        case .high:
            .high
        case .low:
            .low
        case .medium:
            .medium
        }
    }

    func initializeConfiguration() {
        applyImageQuality(settingsStorage.imageQuality)
    }
}
