import Models

public extension ListDetailScreen {
    struct State: Equatable {
        public let title: String
        public let isLoading: Bool
        public let canRefresh: Bool
        public let emptyMessage: String
        public let errorMessage: String?
        public let dismissErrorLabel: String
        public let items: [ShowPosterImage]
        public let isLoadingMore: Bool
        public let loadError: String?
        public let retryLabel: String
        public let removeButtonLabel: String
        public let removeConfirmation: RemoveConfirmation?
        public let cancelLabel: String
        public let renameLabel: String
        public let deleteLabel: String
        public let renameDialog: RenameDialogState?
        public let deleteConfirmation: DeleteConfirmationState?

        public init(
            title: String,
            isLoading: Bool = true,
            canRefresh: Bool = false,
            emptyMessage: String = "",
            errorMessage: String? = nil,
            dismissErrorLabel: String = "",
            items: [ShowPosterImage] = [],
            isLoadingMore: Bool = false,
            loadError: String? = nil,
            retryLabel: String = "",
            removeButtonLabel: String = "",
            removeConfirmation: RemoveConfirmation? = nil,
            cancelLabel: String = "",
            renameLabel: String = "",
            deleteLabel: String = "",
            renameDialog: RenameDialogState? = nil,
            deleteConfirmation: DeleteConfirmationState? = nil
        ) {
            self.title = title
            self.isLoading = isLoading
            self.canRefresh = canRefresh
            self.emptyMessage = emptyMessage
            self.errorMessage = errorMessage
            self.dismissErrorLabel = dismissErrorLabel
            self.items = items
            self.isLoadingMore = isLoadingMore
            self.loadError = loadError
            self.retryLabel = retryLabel
            self.removeButtonLabel = removeButtonLabel
            self.removeConfirmation = removeConfirmation
            self.cancelLabel = cancelLabel
            self.renameLabel = renameLabel
            self.deleteLabel = deleteLabel
            self.renameDialog = renameDialog
            self.deleteConfirmation = deleteConfirmation
        }
    }

    struct RemoveConfirmation: Equatable {
        public let tmdbId: Int64
        public let title: String
        public let message: String
        public let confirmLabel: String

        public init(
            tmdbId: Int64,
            title: String,
            message: String,
            confirmLabel: String
        ) {
            self.tmdbId = tmdbId
            self.title = title
            self.message = message
            self.confirmLabel = confirmLabel
        }
    }

    struct RenameDialogState: Equatable {
        public let title: String
        public let name: String
        public let canSave: Bool
        public let saveLabel: String
        public let isSaving: Bool

        public init(
            title: String,
            name: String,
            canSave: Bool,
            saveLabel: String,
            isSaving: Bool
        ) {
            self.title = title
            self.name = name
            self.canSave = canSave
            self.saveLabel = saveLabel
            self.isSaving = isSaving
        }
    }

    struct DeleteConfirmationState: Equatable {
        public let title: String
        public let message: String
        public let confirmLabel: String

        public init(
            title: String,
            message: String,
            confirmLabel: String
        ) {
            self.title = title
            self.message = message
            self.confirmLabel = confirmLabel
        }
    }
}
