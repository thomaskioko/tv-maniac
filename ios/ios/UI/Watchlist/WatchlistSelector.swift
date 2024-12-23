import SwiftUI
import SwiftUIComponents

public struct WatchlistSelector: View {
  @Binding var showView: Bool
  private let title: String
  private let posterUrl: String?
  private let customLists: [String] = []

  public init(showView: Binding<Bool>, title: String, posterUrl: String?) {
    self.title = title
    self.posterUrl = posterUrl
    _showView = showView
  }

  public var body: some View {
    NavigationStack {
      Form {
        Section {
          VStack(alignment: .center) {
            HStack(alignment: .center) {
              PosterItemView(
                title: title,
                posterUrl: posterUrl,
                posterWidth: 150,
                posterHeight: 220
              )
              .frame(width: 150, height: 220)
              .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
              .shadow(color: .black.opacity(0.2), radius: 10, x: 0, y: 10)
            }
            .frame(maxWidth: .infinity)

            Text(title)
              .fontWeight(.semibold)
              .font(.title3)
              .multilineTextAlignment(.center)
          }
        }
        .listRowInsets(EdgeInsets())
        .listRowBackground(Color.clear)

        if !customLists.isEmpty {
          Section {
            List {
              // Add Custom list
            }
          } header: { Text("Lists") }
        } else {
          emptyList
        }
      }
      .scrollBounceBehavior(.basedOnSize, axes: .vertical)
      .scrollContentBackground(.visible)
      .navigationTitle("Add to...")
      .navigationBarTitleDisplayMode(.inline)
      .toolbar {
        ToolbarItem(placement: .topBarLeading) {
          RoundedButton(
            imageName: "xmark",
            tintColor: .accentBlue,
            action: { showView.toggle() }
          )
        }

        ToolbarItem(placement: .topBarTrailing) {
          // TODO: Custom list
        }
      }
      .background(TransparentBlurView(style: .systemThinMaterial))
    }
    .appTint()
    .appTheme()
    .presentationDetents([.large])
    .presentationDragIndicator(.visible)
    .presentationCornerRadius(12)
  }

  private var emptyList: some View {
    Section {
      VStack {
        Text("Create a List")
          .font(.avenirNext(size: 22))
          .fontWeight(.bold)
          .foregroundColor(.textColor)
          .multilineTextAlignment(.center)
          .padding([.horizontal], 8)

        Text("You don't have any lists. Create a new one?")
          .font(.caption)

        Button(action: {}) {
          VStack {
            Image(systemName: "plus.rectangle.on.rectangle.fill")
              .resizable()
              .aspectRatio(contentMode: .fit)
              .frame(height: 24)

            Text("Create")
              .font(.caption)
          }
          .padding(.vertical, 4)
          .frame(width: 120, height: 45)
        }
        .buttonStyle(.borderedProminent)
        .controlSize(.small)
        .tint(Color.accent)
        .buttonBorderShape(.roundedRectangle(radius: 12))
        .padding(.top, 8)
      }
      .frame(maxWidth: .infinity)
    }
    .listRowInsets(EdgeInsets())
    .listRowBackground(Color.clear)
  }
}
