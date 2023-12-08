//
//  DetailScreenHelperView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 14.07.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

import SwiftUI

/// Custom View Modifier Extension
extension View {
    @ViewBuilder
    func detailScreenCover<Content: View>(show: Binding<Bool>, @ViewBuilder content: @escaping () -> Content) -> some View {
        self
            .modifier(DetailScreenHelperView(showView: show, overlay: content()))
    }
}

/// Helper Modifier
fileprivate struct DetailScreenHelperView<Overlay: View>: ViewModifier {
    
    @Binding var showView: Bool
    var overlay: Overlay
    
    /// View Properties
    @State private var hostView: CustomHostingView<Overlay>?
    @State private var parentController: UIViewController?
    
    func body(content: Content) -> some View {
        content
            /// Attatch it as background to fetch the parent Controller
            .background(content: {
                ExtractSwiftUIParentController(content: overlay, hostView: $hostView) { viewController in
                    parentController = viewController
                }
            })
            /// Presenting/Dismissing Host View based on Show State
            .onChange(of: showView) { newValue in
                if newValue {
                    hostView = CustomHostingView(show: $showView, rootView: overlay)
                    /// Present View
                    if let hostView {
                        /// Changing Presentation Style and Transition Style
                        hostView.modalPresentationStyle = .overCurrentContext
                        hostView.modalTransitionStyle = .crossDissolve
                        hostView.view.backgroundColor = .clear
                        /// We Need a parent View controller to present it
                        parentController?.present(hostView, animated: false)
                    }
                } else {
                    /// Dismiss View
                    hostView?.dismiss(animated: false)
                }
            }
    }
}

fileprivate struct ExtractSwiftUIParentController<Content: View>: UIViewRepresentable {
    var content: Content
    @Binding var hostView: CustomHostingView<Content>?
    var parentController: (UIViewController?) -> ()
    
    func makeUIView(context: Context) -> UIView {
        /// Simply Return Empty View
        return UIView()
    }
    
    func updateUIView(_ uiView: UIView, context: Context) {
        /// Update HostView's Root View (So that SwiftUI Will be updated when ever any state changes occurs in it's view)
        hostView?.rootView = content
        DispatchQueue.main.async {
            /// Retrieve it's parent view controller
            parentController(uiView.superview?.superview?.parentController)
        }
    }
}

class CustomHostingView<Content: View>: UIHostingController<Content> {
    @Binding var show: Bool
    
    init(show: Binding<Bool>, rootView: Content) {
        self._show = show
        super.init(rootView: rootView)
    }
    
    required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        /// Since we don't need any default animation while dismissing
        super.viewWillDisappear(false)
        /// Setting show Status to false
        show = false
    }
}

/// Return parent view controller for the given UIView
public extension UIView {
    var parentController: UIViewController? {
        var responder = self.next
        while responder != nil {
            if let viewController = responder as? UIViewController {
                return viewController
            }
            responder = responder?.next
        }
        
        return nil
    }
}
