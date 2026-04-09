import SwiftUI
import TvManiac

@propertyWrapper
public struct StateValue<T: AnyObject>: DynamicProperty {
    @StateObject private var obj: ObservableValue<T>

    public var wrappedValue: T {
        obj.value
    }

    public init(_ value: Value<T>) {
        _obj = StateObject(wrappedValue: ObservableValue(value))
    }
}
