import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SettingsTab: View {
  private let presenter: SettingsPresenter
  @StateObject @KotlinStateFlow private var uiState: SettingsState
  @StateObject private var store = SettingsAppStorage.shared
  @State private var showingAlert: Bool = false
  @State private var openInYouTube: Bool = false
  @State private var showPolicy = false
  @State private var aboutPage = false
  @Environment(\.openURL) var openURL
  @Environment(\.presentationMode) var presentationMode
  @ObservedObject private var model = TraktAuthViewModel()

  init(presenter: SettingsPresenter) {
    self.presenter = presenter
    _uiState = .init(presenter.state)
  }

  var body: some View {
    Form {
      Section("App Theme") {
        Picker(
          selection: $store.appTheme,
          label: Text("Change Theme")
            .bodyMediumFont(size: 16),
          content: {
            ForEach(DeveiceAppTheme.allCases, id: \.self) { theme in

              Text(theme.localizableName)
                .tag(theme)
            }
          }
        )
        .pickerStyle(.segmented)
        .padding(.vertical, 6)
        .onChange(of: uiState.appTheme) { theme in
          presenter.dispatch(action: ThemeSelected(appTheme: theme))
        }
      }

      Section("Behavior") {
        Toggle(isOn: $openInYouTube) {
          settingsLabel(title: "Open Trailers in Youtube App", icon: "tv", color: .red)
        }
      }

      Section("Trakt Account") {
        Button {
          showingAlert = !(uiState.showTraktDialog)

        } label: {
          settingsLabel(title: "Connect to Trakt", icon: "person.fill", color: Color.accent)
        }
        .buttonStyle(.plain)
        .alert(isPresented: $showingAlert) {
          Alert(
            title: Text("Trakt Coming Soon"),
            message: Text("Trakt is a platform that does many things, but primarily keeps track of TV shows and movies you watch."),
            primaryButton: .default(Text("Login")) {
              model.initiateAuthorization()
            },
            secondaryButton: .destructive(Text("Cancel"))
          )
        }
      }

      Section("Info") {
        Button {
          aboutPage.toggle()
        } label: {
          settingsLabel(title: "About TvManiac", icon: "info.circle", color: .black)
        }
        .buttonStyle(.plain)
        .sheet(isPresented: $aboutPage) {
          if let url = URL(string: "https://github.com/c0de-wizard/tv-maniac") {
            SFSafariViewWrapper(url: url)
              .appTint()
              .appTheme()
          }
        }

        Button {
          showPolicy.toggle()
        } label: {
          settingsLabel(title: "Privacy Policy", icon: "hand.raised", color: .indigo)
        }
        .buttonStyle(.plain)
        .sheet(isPresented: $showPolicy) {
          // TODO: Add Privacy Policy
          if let url = URL(string: "https://github.com/c0de-wizard/tv-maniac") {
            SFSafariViewWrapper(url: url)
              .appTint()
              .appTheme()
          }
        }
      }
    }
    .scrollContentBackground(.hidden)
    .background(Color.backgroundColor)
    .navigationTitle("Settings")
    .navigationBarTitleDisplayMode(.inline)
    .onChange(of: uiState.appTheme) { newTheme in
      store.appTheme = newTheme.toDeveiceAppTheme()
    }
  }

  private func settingsLabel(title: String, icon: String, color: Color) -> some View {
    HStack {
      ZStack {
        Rectangle()
          .fill(color)
          .clipShape(RoundedRectangle(cornerRadius: 6, style: .continuous))
        Image(systemName: icon)
          .foregroundColor(.white)
      }
      .frame(width: 30, height: 30, alignment: .center)
      .padding(.trailing, 8)
      .accessibilityHidden(true)

      Text(title)
    }
    .padding(.vertical, 2)
  }
}
