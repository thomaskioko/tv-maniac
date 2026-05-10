import SwiftUI

public enum TvManiacAppIcon {
    public static var image: Image {
        #if DEBUG
            Image("TvManiacIconDebug", bundle: .module)
        #else
            Image("TvManiacIcon", bundle: .module)
        #endif
    }
}
