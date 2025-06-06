import SwiftUI
import TvManiac

@propertyWrapper
public class KotlinStateFlow<T: AnyObject>: ObservableObject {
    @Published public private(set) var wrappedValue: T
    private var publisher: Task<Void, Never>?
    private let stateFlow: SkieSwiftStateFlow<T>

    public init(_ value: SkieSwiftStateFlow<T>) {
        stateFlow = value
        wrappedValue = value.value

        startObserving()
    }

    private func startObserving() {
        publisher = Task { @MainActor [weak self] in
            if let stateFlow = self?.stateFlow {
                for await item in stateFlow {
                    self?.wrappedValue = item
                }
            }
        }
    }

    deinit {
        publisher?.cancel()
    }
}

public extension ObservedObject {
    init<F>(_ stateFlow: SkieSwiftStateFlow<F>) where ObjectType == KotlinStateFlow<F> {
        self.init(wrappedValue: KotlinStateFlow(stateFlow))
    }
}

public extension StateObject {
    init<F>(_ stateFlow: SkieSwiftStateFlow<F>) where ObjectType == KotlinStateFlow<F> {
        self.init(wrappedValue: KotlinStateFlow(stateFlow))
    }
}
