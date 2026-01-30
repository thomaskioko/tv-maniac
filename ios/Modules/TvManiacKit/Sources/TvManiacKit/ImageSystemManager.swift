import Combine
import SwiftUIComponents

public final class ImageSystemManager {
    public static let shared = ImageSystemManager()

    private var cancellables = Set<AnyCancellable>()
    private var isBackgroundManagementSetup = false

    private init() {}

    public func initialize() {
        configureImageSystem()
        startImageQualityObserver()
    }

    public func handleBackgroundCleanup() {
        ImageConfiguration.handleBackgroundCleanup()
    }

    private func configureImageSystem() {
        let quality = mapToImageConfigurationQuality(SettingsAppStorage.shared.imageQuality)
        ImageConfiguration.configure(quality: quality)
        if !isBackgroundManagementSetup {
            ImageConfiguration.setupBackgroundMemoryManagement()
            isBackgroundManagementSetup = true
        }
    }

    private func mapToImageConfigurationQuality(_ quality: SwiftImageQuality) -> ImageConfiguration.Quality {
        switch quality {
        case .high:
            .high
        case .medium:
            .medium
        case .low:
            .low
        }
    }

    private func startImageQualityObserver() {
        SettingsAppStorage.shared.objectWillChange
            .sink { [weak self] _ in
                self?.configureImageSystem()
            }
            .store(in: &cancellables)
    }
}
