import SwiftUI

public struct RoundedButton: View {
    private let imageName: String
    private let tintColor: Color
    private let foregroundColor: Color
    private let action: () -> Void

    public init(
        imageName: String,
        tintColor: Color,
        foregroundColor: Color = .white,
        action: @escaping () -> Void
    ) {
        self.imageName = imageName
        self.tintColor = tintColor
        self.foregroundColor = foregroundColor
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            Image(systemName: imageName)
                .imageScale(.medium)
                .fontWeight(.semibold)
                .foregroundStyle(foregroundColor)
                .padding(.horizontal, 4)
                .padding(.vertical, 2)
        }
        .buttonStyle(.borderedProminent)
        .contentShape(Circle())
        .clipShape(Circle())
        .tint(tintColor)
        .shadow(radius: 2.5)
    }
}
