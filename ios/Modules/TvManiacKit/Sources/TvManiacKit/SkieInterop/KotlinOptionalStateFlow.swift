import SwiftUI
import TvManiac

@MainActor
@propertyWrapper
public final class KotlinOptionalStateFlow<T: AnyObject>: ObservableObject {
    private let stateFlow: SkieSwiftOptionalStateFlow<T>

    @Published public var wrappedValue: T?

    private var publisher: Task<Void, Never>?

    public init(_ value: SkieSwiftOptionalStateFlow<T>) {
        stateFlow = value
        wrappedValue = value.value

        publisher = Task { [weak self] in
            guard let stateFlow = self?.stateFlow else { return }
            for await item in stateFlow {
                guard let self else { break }
                wrappedValue = item
            }
        }
    }

    deinit {
        publisher?.cancel()
    }
}

public extension ObservedObject {
    @MainActor
    init<F>(_ stateFlow: SkieSwiftOptionalStateFlow<F>) where ObjectType == KotlinOptionalStateFlow<F> {
        self.init(wrappedValue: KotlinOptionalStateFlow(stateFlow))
    }
}

public extension StateObject {
    @MainActor
    init<F>(_ stateFlow: SkieSwiftOptionalStateFlow<F>) where ObjectType == KotlinOptionalStateFlow<F> {
        self.init(wrappedValue: KotlinOptionalStateFlow(stateFlow))
    }
}
