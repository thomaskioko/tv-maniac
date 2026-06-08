import Foundation
#if canImport(Darwin)
    import Darwin
#endif

public enum MemoryPressureLevel {
    case normal
    case warning
    case critical
}

public enum SystemMemory {
    public static var totalMemory: UInt64 {
        ProcessInfo.processInfo.physicalMemory
    }

    public static var usedMemory: UInt64? {
        var taskInfo = task_vm_info_data_t()
        var count = mach_msg_type_number_t(
            MemoryLayout<task_vm_info>.size / MemoryLayout<integer_t>.size
        )

        let result: kern_return_t = withUnsafeMutablePointer(to: &taskInfo) {
            $0.withMemoryRebound(to: integer_t.self, capacity: Int(count)) {
                task_info(mach_task_self_, task_flavor_t(TASK_VM_INFO), $0, &count)
            }
        }

        guard result == KERN_SUCCESS else { return nil }
        return UInt64(taskInfo.phys_footprint)
    }

    public static var memoryUsageDescription: String {
        let used = usedMemory.map { formatBytes($0) } ?? "?"
        let total = formatBytes(totalMemory)
        return "\(used) / \(total)"
    }

    public static var isLowMemoryDevice: Bool {
        let memoryGB = Double(totalMemory) / 1_073_741_824
        return memoryGB <= 4.0
    }

    public static var pressureLevel: MemoryPressureLevel {
        guard let used = usedMemory else { return .normal }
        let usedMB = Double(used) / (1024 * 1024)
        let ratio = Double(used) / Double(totalMemory)

        if usedMB > 600 || ratio > 0.35 { return .critical }
        if usedMB > 350 || ratio > 0.20 { return .warning }
        return .normal
    }

    private static func formatBytes(_ bytes: UInt64) -> String {
        let formatter = ByteCountFormatter()
        formatter.countStyle = .memory
        formatter.allowedUnits = [.useMB, .useGB]
        return formatter.string(fromByteCount: Int64(bytes))
    }
}
