import SwiftUI
import TvManiac

public extension View {
    @ViewBuilder
    func flowEffect<Event>(
        for eventsFlow: SkieSwiftFlow<Event>,
        onEvent: @escaping (Event) -> Void
    ) -> some View {
        task {
            for await event in eventsFlow {
                onEvent(event)
            }
        }
    }

    @ViewBuilder
    func optionalFlowEffect<Event>(
        for eventsFlow: SkieSwiftOptionalFlow<Event>,
        onEvent: @escaping (Event?) -> Void
    ) -> some View {
        task {
            for await event in eventsFlow {
                onEvent(event)
            }
        }
    }
}
