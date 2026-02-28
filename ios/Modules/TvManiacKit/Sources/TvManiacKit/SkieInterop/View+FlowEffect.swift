import SwiftUI
import TvManiac

public extension View {
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
