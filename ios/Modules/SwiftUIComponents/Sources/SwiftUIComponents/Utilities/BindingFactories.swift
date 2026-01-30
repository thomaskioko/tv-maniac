import SwiftUI

public enum BindingFactories {
    public static func searchQuery(
        get: @escaping () -> String,
        onChanged: @escaping (String) -> Void,
        onCleared: @escaping () -> Void
    ) -> Binding<String> {
        Binding(
            get: get,
            set: { newValue in
                let trimmed = newValue.trimmingCharacters(in: .whitespaces)
                if !trimmed.isEmpty {
                    onChanged(newValue)
                } else {
                    onCleared()
                }
            }
        )
    }
}
