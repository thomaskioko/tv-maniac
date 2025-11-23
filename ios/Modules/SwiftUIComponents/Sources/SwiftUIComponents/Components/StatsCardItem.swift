import SwiftUI

public struct StatsCardItem<Content: View>: View {
    private let systemImage: String
    private let title: String
    private let content: () -> Content

    public init(
        systemImage: String,
        title: String,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.systemImage = systemImage
        self.title = title
        self.content = content
    }

    public var body: some View {
        VStack(spacing: 0) {
            Spacer().frame(height: 18)

            HStack(spacing: 8) {
                Spacer()

                Image(systemName: systemImage)
                    .font(.system(size: 18))
                    .foregroundColor(.accent)

                Text(title)
                    .font(.avenirNext(size: 14))
                    .fontWeight(.medium)
                    .foregroundColor(.textColor)

                Spacer()
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 16)

            Spacer().frame(height: 12)

            Rectangle()
                .fill(Color.textColor)
                .frame(height: 1)
                .foregroundColor(.background)

            Spacer().frame(height: 8)

            // --- User-provided content (slot) ---
            VStack {
                Spacer()

                content()
                    .frame(maxWidth: .infinity)

                Spacer()
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 16)
        }
        .frame(height: 120)
        .background(Color.backgroundColor)
        .overlay(
            RoundedRectangle(cornerRadius: 4)
                .stroke(Color.textColor, lineWidth: 2)
        )
        .cornerRadius(4)
    }
}

#Preview {
    ScrollView(.horizontal, showsIndicators: false) {
        HStack(alignment: .top, spacing: 12) {
            StatsCardItem(
                systemImage: "calendar",
                title: "Watch Time"
            ) {
                HStack(spacing: 24) {
                    VStack(spacing: 4) {
                        Text("14")
                            .font(.avenirNext(size: 18))
                            .fontWeight(.semibold)
                            .foregroundColor(.textColor)

                        Text("MONTHS")
                            .font(.avenirNext(size: 12))
                            .fontWeight(.medium)
                            .foregroundColor(.textColor)
                    }
                    VStack(spacing: 4) {
                        Text("45")
                            .font(.avenirNext(size: 18))
                            .fontWeight(.semibold)
                            .foregroundColor(.textColor)

                        Text("DAYS")
                            .font(.avenirNext(size: 12))
                            .fontWeight(.medium)
                            .foregroundColor(.textColor)
                    }
                    VStack(spacing: 4) {
                        Text("12")
                            .font(.avenirNext(size: 18))
                            .fontWeight(.semibold)
                            .foregroundColor(.textColor)

                        Text("HOURS")
                            .font(.avenirNext(size: 12))
                            .fontWeight(.medium)
                            .foregroundColor(.textColor)
                    }
                }
            }

            // Episodes Watched Card
            StatsCardItem(
                systemImage: "tv",
                title: "Episodes Watched"
            ) {
                VStack(spacing: 0) {
                    Text("5,123")
                        .font(.avenirNext(size: 18))
                        .fontWeight(.semibold)
                        .foregroundColor(.textColor)
                        .frame(maxWidth: .infinity)
                }.padding(10)
            }
        }
        .padding(.horizontal, 16)
    }
}
