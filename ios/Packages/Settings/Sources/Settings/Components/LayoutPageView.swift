import DesignSystem
import SwiftUI

struct LayoutPageView: View {
    @Environment(\.appTheme) private var appTheme

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {}
    }
}

#if DEBUG
    #Preview {
        LayoutPageView()
            .padding()
            .appPreview()
    }
#endif
