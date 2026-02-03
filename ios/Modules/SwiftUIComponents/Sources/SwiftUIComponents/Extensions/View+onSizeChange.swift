import SwiftUI

public extension View {
    /// Updates the value of the passed binding when the size of this view changes.
    ///
    /// - Parameter size: The size to update on change.
    ///
    func onSizeChange(update size: Binding<CGSize>) -> some View {
        onSizeChange { size.wrappedValue = $0 }
    }

    /// Adds an action to perform when the size of this view changes.
    ///
    /// - Parameter action: The action to perform when this views’s size changes.
    /// - Parameter size  : The view's new size.
    ///
    func onSizeChange(perform action: @escaping (_ size: CGSize) -> Void) -> some View {
        background(
            GeometryReader {
                proxy in
                Color.clear
                    .onAppear { action(proxy.size) }
                    .onChange(of: proxy.size, perform: action)
            })
    }

    /// Adds an action to perform when the view’s bounds rectangle, converted to a defined
    /// coordinate space changes.
    ///
    /// - Parameter coordinateSpace:
    ///   The coordinate space in which the views’s bounds rectangle should be converted.
    ///
    /// - Parameter action:
    ///   The action to perform when this views’s bounds rectangle changes.
    ///
    /// - Parameter frame:
    ///   The view’s bounds rectangle, converted to the defined coordinate space.
    ///
    func onFrameChange(
        in coordinateSpace: CoordinateSpace,
        perform action: @escaping (_ frame: CGRect) -> Void
    )
        -> some View
    {
        background(
            GeometryReader {
                proxy in
                Color.clear
                    .onAppear { action(proxy.frame(in: coordinateSpace)) }
                    .onChange(of: proxy.frame(in: coordinateSpace), perform: action)
            })
    }
}
