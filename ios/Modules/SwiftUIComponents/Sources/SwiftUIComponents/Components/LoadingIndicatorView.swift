import SwiftUI

public struct LoadingIndicatorView: View {
    @Theme private var theme

    public init() {}

    public var body: some View {
        ZStack {
            Spacer()

            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.5)
                .tint(.accentColor)
                .padding(.horizontal)
                .padding(.bottom, 8)

            Spacer()
        }
        .padding(theme.spacing.medium)
        .edgesIgnoringSafeArea(.all)
    }
}

#Preview {
    LoadingIndicatorView()
}
