import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

public struct ShowListSheetView: View {
    @Environment(\.appTheme) private var theme
    private let presenter: ShowListPresenter
    @StateValue private var state: ShowListState
    @State private var toast: Toast?

    public init(presenter: ShowListPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    public var body: some View {
        NavigationStack {
            Group {
                if state.isLoggedIn {
                    loggedInContent
                } else {
                    loginRequiredContent
                }
            }
            .background(.appBackground)
            .navigationTitle(state.labels.sheetTitle)
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(.appSurface, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: { presenter.dispatch(action: ShowListActionDismiss()) }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundStyle(.appAccent)
                    }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    if state.isLoggedIn, !state.showCreateListField {
                        Button(action: { presenter.dispatch(action: ShowListActionShowCreateListField()) }) {
                            Image(systemName: "plus")
                                .foregroundStyle(.appOnAccent)
                                .frame(width: 28, height: 28)
                                .background(.appAccent)
                                .clipShape(Circle())
                        }
                    }
                }
            }
        }
        .presentationDetents([.large])
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(16)
        .appTint()
        .appTheme()
        .toastView(toast: $toast)
        .onChange(of: state.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: String(\.label_error), message: message.message)
                presenter.dispatch(action: ShowListActionMessageShown(id: message.id))
            }
        }
    }

    private var loggedInContent: some View {
        Form {
            if state.isLoading {
                loadingSection
            } else if state.traktLists.isEmpty {
                emptySection
            } else {
                listsSection
            }
            if state.showCreateListField {
                createSection
            }
        }
        .scrollBounceBehavior(.basedOnSize, axes: .vertical)
        .scrollContentBackground(.hidden)
    }

    private var loadingSection: some View {
        Section {
            HStack {
                Spacer()
                ProgressView()
                    .progressViewStyle(.circular)
                    .tint(theme.colors.accent)
                    .padding(.vertical, 16)
                Spacer()
            }
        }
        .listRowBackground(Color.clear)
    }

    private var listsSection: some View {
        Section {
            ForEach(state.traktLists, id: \.id) { list in
                listRow(for: list)
            }
        } header: {
            Text(state.labels.listsHeaderText)
        }
    }

    private func listRow(for list: TraktListModel) -> some View {
        HStack {
            VStack(alignment: .leading) {
                Text(list.name)
                    .textStyle(theme.typography.bodyMedium)
                Text(list.showCountText)
                    .foregroundStyle(.appOnSurfaceVariant)
                    .textStyle(theme.typography.bodySmall)
            }
            Spacer()
            if list.isToggling {
                ProgressView()
                    .progressViewStyle(.circular)
                    .scaleEffect(0.8)
                    .tint(theme.colors.accent)
            } else {
                Toggle("", isOn: Binding(
                    get: { list.isShowInList },
                    set: { _ in
                        presenter.dispatch(action: ShowListActionToggleShowInList(
                            listId: list.id,
                            isCurrentlyInList: list.isShowInList
                        ))
                    }
                ))
                .labelsHidden()
                .tint(theme.colors.accent)
            }
        }
        .padding(.vertical, 4)
        .listRowBackground(theme.colors.surfaceVariant.opacity(0.5))
    }

    private var emptySection: some View {
        Section {
            VStack {
                Text(state.labels.emptyListText)
                    .textStyle(theme.typography.bodyMedium)
                    .multilineTextAlignment(.center)
                    .foregroundStyle(.appOnSurfaceVariant)
            }
            .frame(maxWidth: .infinity)
        }
        .listRowBackground(Color.clear)
    }

    private var createSection: some View {
        Section {
            HStack(spacing: 8) {
                TextField(state.labels.createListPlaceholder, text: Binding(
                    get: { state.createListName },
                    set: { newValue in
                        if newValue.count <= 50 {
                            presenter.dispatch(action: ShowListActionUpdateCreateListName(name: newValue))
                        }
                    }
                ))
                .disabled(state.isCreatingList)
                .padding(.horizontal, 8)
                .padding(.vertical, 6)
                .background(.appSurface)
                .clipShape(RoundedRectangle(cornerRadius: 8))
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(.appOutline.opacity(0.3), lineWidth: 1)
                )

                Button(action: { presenter.dispatch(action: ShowListActionCreateListSubmitted()) }) {
                    if state.isCreatingList {
                        ProgressView()
                            .progressViewStyle(.circular)
                            .scaleEffect(0.8)
                    } else {
                        Text(state.labels.createListDoneText)
                    }
                }
                .buttonStyle(.borderedProminent)
                .tint(theme.colors.accent)
                .disabled(
                    state.createListName.trimmingCharacters(in: .whitespaces).isEmpty ||
                        state.isCreatingList
                )
            }
            .listRowBackground(Color.clear)
            .listRowSeparator(.hidden)
        }
    }

    private var loginRequiredContent: some View {
        VStack {
            Spacer()
            ProviderSignInCard(
                title: state.labels.loginRequiredTitle,
                description: state.labels.loginRequiredMessage,
                providers: state.authProviders.compactMap { $0 as? AuthProviderOption }.map { option in
                    SwiftAuthProvider(
                        id: option.provider.name,
                        label: option.label,
                        logoName: option.provider.name == "SIMKL" ? "SimklMono" : "TraktMono"
                    )
                }
            ) { id in
                presenter.dispatch(action: ShowListActionLogin(provider: id == "SIMKL" ? .simkl : .trakt))
            }
            Spacer()
        }
        .padding()
    }
}
