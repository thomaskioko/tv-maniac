import SwiftUI

struct ToastView: View {
  var type: ToastStyle
  var title: String
  var message: String
  var onCancelTapped: () -> Void

  var body: some View {
    VStack(alignment: .leading) {
      HStack(alignment: .top) {
        Image(systemName: type.iconFileName)
          .foregroundColor(type.themeColor)

        VStack(alignment: .leading) {
          Text(title)
            .font(.system(size: 14, weight: .semibold))
            .foregroundColor(.textColorDark)

          Text(message)
            .font(.system(size: 12))
            .foregroundColor(.textColorDark)
        }

        Spacer(minLength: 10)

        Button(action: onCancelTapped) {
          Image(systemName: "xmark")
            .foregroundColor(.textColorDark)
        }
      }
      .padding()
    }
    .background(Color.white)
    .overlay(
      Rectangle()
        .fill(type.themeColor)
        .frame(width: 6)
        .clipped(),
      alignment: .leading
    )
    .frame(minWidth: 0, maxWidth: .infinity)
    .cornerRadius(8)
    .shadow(color: Color.black.opacity(0.25), radius: 4, x: 0, y: 1)
    .padding(.horizontal, 16)
  }
}

#Preview {
  VStack {
    ToastView(
      type: .error,
      title: "Error",
      message: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
      onCancelTapped: {}
    )

    ToastView(
      type: .info,
      title: "Info",
      message: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
      onCancelTapped: {}
    )
  }
}
