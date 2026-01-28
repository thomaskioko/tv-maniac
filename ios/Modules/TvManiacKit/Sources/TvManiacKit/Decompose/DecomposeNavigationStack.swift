import SwiftUI
import TvManiac

public struct DecomposeNavigationStack<T: AnyObject, Content: View>: View {
    @StateObject @KotlinStateFlow private var childStack: ChildStack<AnyObject, T>
    private let content: (T) -> Content
    private let onBack: (_ toIndex: Int32) -> Void

    private var stack: [Child<AnyObject, T>] {
        childStack.items
    }

    public init(
        stack: SkieSwiftStateFlow<ChildStack<AnyObject, T>>,
        onBack: @escaping (_ toIndex: Int32) -> Void,
        @ViewBuilder content: @escaping (T) -> Content
    ) {
        self.content = content
        self.onBack = onBack
        _childStack = .init(stack)
    }

    public var body: some View {
        NavigationStack(
            path: Binding(
                get: { stack.dropFirst() },
                set: { updatedPath in onBack(Int32(updatedPath.count)) }
            )
        ) {
            content(stack.first!.instance!)
                .navigationDestination(for: Child<AnyObject, T>.self) {
                    content($0.instance!)
                }
        }
    }
}
