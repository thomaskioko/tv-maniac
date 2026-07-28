public enum CalendarScreenState: Equatable {
    case loading
    case loginRequired(title: String, message: String)
    case empty(title: String, message: String)
    indirect case locked(underlying: CalendarScreenState, title: String, message: String)
    case content(dateGroups: [SwiftCalendarDateGroup])
}
