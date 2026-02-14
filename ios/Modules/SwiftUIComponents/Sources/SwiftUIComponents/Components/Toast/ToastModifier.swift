import SwiftUI

public struct ToastModifier: ViewModifier {
    @Binding var toast: Toast?
    @State private var workItem: DispatchWorkItem?
    @State private var dragOffsetX: CGFloat = 0
    @State private var dragOffsetY: CGFloat = 0

    public func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .overlay(
                ZStack {
                    mainToastView()
                }
                .animation(.spring(), value: toast)
            )
            .onChange(of: toast) { _ in
                showToast()
            }
    }

    @ViewBuilder func mainToastView() -> some View {
        if let toast {
            VStack {
                ToastView(
                    type: toast.type,
                    title: toast.title,
                    message: toast.message,
                    onCancelTapped: {
                        dismissToast()
                    }
                )
                .offset(x: dragOffsetX, y: dragOffsetY)
                .opacity(1.0 - max(abs(dragOffsetX) / 300.0, abs(dragOffsetY) / 240.0))
                .gesture(
                    DragGesture()
                        .onChanged { value in
                            dragOffsetX = value.translation.width
                            if value.translation.height < 0 {
                                dragOffsetY = value.translation.height
                            }
                        }
                        .onEnded { value in
                            let predicted = value.predictedEndTranslation
                            let isFlingUp = predicted.height < -300
                            let isFlingHorizontal = abs(predicted.width) > 300

                            if value.translation.height < -50 || isFlingUp {
                                dismissToast()
                            } else if abs(value.translation.width) > 50 || isFlingHorizontal {
                                dismissToast()
                            } else {
                                withAnimation(.spring()) {
                                    dragOffsetX = 0
                                    dragOffsetY = 0
                                }
                            }
                        }
                )

                Spacer()
            }
            .transition(.move(edge: .top).combined(with: .opacity))
        }
    }

    private func showToast() {
        guard let toast else {
            return
        }

        dragOffsetX = 0
        dragOffsetY = 0
        UIImpactFeedbackGenerator(style: .light).impactOccurred()

        if toast.duration > 0 {
            workItem?.cancel()

            let task = DispatchWorkItem {
                dismissToast()
            }

            workItem = task
            DispatchQueue.main.asyncAfter(deadline: .now() + toast.duration, execute: task)
        }
    }

    private func dismissToast() {
        withAnimation {
            toast = nil
        }

        workItem?.cancel()
        workItem = nil
    }
}

public extension View {
    func toastView(toast: Binding<Toast?>) -> some View {
        modifier(ToastModifier(toast: toast))
    }
}
