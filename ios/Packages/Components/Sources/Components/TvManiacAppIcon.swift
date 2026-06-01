import DesignSystem
import Models
import SwiftUI

public enum TvManiacAppIcon {
    public static func image(isDebug: Bool) -> Image {
        if isDebug {
            Image("TvManiacIconDebug", bundle: .module)
        } else {
            Image("TvManiacIcon", bundle: .module)
        }
    }
}
