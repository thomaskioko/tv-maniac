import TvManiac

// This class is responsible for managing a root Decompose component at the application root.
public final class ComponentHolder<T> {
    public let lifecycle: LifecycleRegistry
    public let component: T

    public init(factory: (ComponentContext) -> T) {
        let lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        let componentContext = DefaultComponentContext(lifecycle: lifecycle)
        let component = factory(componentContext)
        self.lifecycle = lifecycle
        self.component = component

        LifecycleRegistryExtKt.create(lifecycle)
    }

    deinit {
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}
