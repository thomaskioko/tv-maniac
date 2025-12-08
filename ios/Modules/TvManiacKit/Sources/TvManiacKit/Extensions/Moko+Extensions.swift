import i18n
import SwiftUI
import TvManiac

public extension String {
    init(_ resourceKey: KeyPath<MR.strings, StringResource>) {
        self.init(
            ResourcesKt.getString(
                stringResource: MR.strings()[keyPath: resourceKey]
            ).localized()
        )
    }

    init(_ resourceKey: KeyPath<MR.strings, StringResource>, parameter: Any) {
        self.init(
            ResourcesKt.getString(
                stringResource: MR.strings()[keyPath: resourceKey],
                parameter: parameter
            ).localized()
        )
    }

    init(_ resourceKey: KeyPath<MR.plurals, PluralsResource>, quantity: Int) {
        self.init(
            ResourcesKt.getPluralFormatted(
                pluralResource: MR.plurals()[keyPath: resourceKey],
                quantity: Int32(quantity)
            ).localized()
        )
    }
}

public extension Font {
    init(resource: KeyPath<MR.fonts, FontResource>, withSize: Double = 14.0) {
        self.init(MR.fonts()[keyPath: resource].uiFont(withSize: withSize))
    }
}
