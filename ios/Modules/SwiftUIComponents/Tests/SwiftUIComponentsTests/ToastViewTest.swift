import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ToastViewTest: SnapshotTestCase {
    func test_ToastView_Info() {
        ToastView(
            type: .info,
            title: "Info",
            message: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            onCancelTapped: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "ToastView_Info")
    }

    func test_ToastView_Success() {
        ToastView(
            type: .success,
            title: "Success",
            message: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            onCancelTapped: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "ToastView_Success")
    }

    func test_ToastView_Warning() {
        ToastView(
            type: .warning,
            title: "Warning",
            message: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            onCancelTapped: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "ToastView_Warning")
    }

    func test_ToastView_Error() {
        ToastView(
            type: .error,
            title: "Error",
            message: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            onCancelTapped: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "ToastView_Error")
    }
}
