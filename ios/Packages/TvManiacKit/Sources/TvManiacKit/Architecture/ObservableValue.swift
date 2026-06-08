import Combine
import TvManiac

public class ObservableValue<T: AnyObject>: ObservableObject {
    @Published public var value: T

    private var cancellation: Cancellation?

    public init(_ value: Value<T>) {
        self.value = value.value
        cancellation = value.subscribe { [weak self] value in
            self?.value = value
        }
    }

    deinit {
        cancellation?.cancel()
    }
}
