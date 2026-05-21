import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ShowListSheetView: View {
    private let presenter: ShowListPresenter
    @StateValue private var state: ShowListState
    @State private var toast: Toast?

    init(presenter: ShowListPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        NavigationStack {
            Group {
                if state.isLoggedIn {
                    loggedInContent
                } else {
                    loginRequiredContent
                }
            }
            .background(.appBackground)
            .navigationTitle(state.sheetTitle)
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
            if state.traktLists.isEmpty {
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

    private var listsSection: some View {
        Section {
            ForEach(state.traktLists, id: \.id) { list in
                listRow(for: list)
            }
        } header: {
            Text(state.listsHeaderText)
        }
    }

    private func listRow(for list: TraktListModel) -> some View {
        HStack {
            VStack(alignment: .leading) {
                Text(list.name)
                Text(list.showCountText)
                    .foregroundStyle(.appOnSurfaceVariant)
                    .font(.footnote)
            }
            Spacer()
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
            .tint(.appAccent)
        }
        .padding(.vertical, 4)
        .listRowBackground(Color.appSurfaceVariant.opacity(0.5))
    }

    private var emptySection: some View {
        Section {
            VStack {
                Text(state.emptyListText)
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
                TextField(state.createListPlaceholder, text: Binding(
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
                        Text(state.createListDoneText)
                    }
                }
                .buttonStyle(.borderedProminent)
                .tint(.appAccent)
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
        VStack(alignment: .center, spacing: 16) {
            Spacer()
            Text(state.loginRequiredTitle)
                .font(.title3.weight(.semibold))
            Text(state.loginRequiredMessage)
                .multilineTextAlignment(.center)
                .foregroundStyle(.appOnSurfaceVariant)
            Button(action: { presenter.dispatch(action: ShowListActionLogin()) }) {
                Text(state.loginRequiredConfirmText)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 8)
            }
            .buttonStyle(.borderedProminent)
            .tint(.appAccent)
            .padding(.horizontal, 24)
            Spacer()
        }
        .padding()
    }
}
