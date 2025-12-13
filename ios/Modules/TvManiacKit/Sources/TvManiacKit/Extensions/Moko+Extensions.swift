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

    init(_ resourceKey: KeyPath<MR.plurals, PluralsResource>, quantity: Int, _ args: Any...) {
        let argsArray = args.map { arg -> Any in
            if let intVal = arg as? Int {
                return Int32(intVal)
            } else if let int32Val = arg as? Int32 {
                return int32Val
            }
            return arg
        }
        self.init(
            ResourcesKt.getPluralFormatted(
                pluralResource: MR.plurals()[keyPath: resourceKey],
                quantity: Int32(quantity),
                args: KotlinArray(size: Int32(argsArray.count)) { index in
                    argsArray[Int(truncating: index)] as AnyObject
                }
            ).localized()
        )
    }
}

public extension Font {
    init(resource: KeyPath<MR.fonts, FontResource>, withSize: Double = 14.0) {
        self.init(MR.fonts()[keyPath: resource].uiFont(withSize: withSize))
    }
}
