//
//  KeyboardHeightManager.swift
//  SwiftUIComponents
//
//  Created by Thomas Kioko on 11/5/24.
//

import Foundation
import SwiftUI

// TODO: Move this to UICore Package
public class KeyboardHeightManager: ObservableObject {
    @Published public private(set) var keyboardHeight: CGFloat = 0
    private var notificationCenter: NotificationCenter

    public init(notificationCenter: NotificationCenter = .default) {
        self.notificationCenter = notificationCenter
        setupKeyboardNotifications()
    }

    deinit {
        removeKeyboardNotifications()
    }

    private func setupKeyboardNotifications() {
        notificationCenter.addObserver(
            self,
            selector: #selector(keyboardWillShow),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )

        notificationCenter.addObserver(
            self,
            selector: #selector(keyboardWillHide),
            name: UIResponder.keyboardWillHideNotification,
            object: nil
        )
    }

    private func removeKeyboardNotifications() {
        notificationCenter.removeObserver(
            self,
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )
        notificationCenter.removeObserver(
            self,
            name: UIResponder.keyboardWillHideNotification,
            object: nil
        )
    }

    @objc private func keyboardWillShow(_ notification: Notification) {
        let keyboardFrame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect ?? .zero
        withAnimation(.easeOut) {
            keyboardHeight = keyboardFrame.height
        }
    }

    @objc private func keyboardWillHide(_: Notification) {
        withAnimation(.easeOut) {
            keyboardHeight = 0
        }
    }
}

public struct KeyboardAwareModifier: ViewModifier {
    @StateObject private var keyboard = KeyboardHeightManager()
    let contentInset: CGFloat

    public init(contentInset: CGFloat = 0) {
        self.contentInset = contentInset
    }

    public func body(content: Content) -> some View {
        content
            .padding(.bottom, keyboard.keyboardHeight > 0 ? keyboard.keyboardHeight - contentInset : 0)
            .animation(.easeOut, value: keyboard.keyboardHeight)
    }
}

public extension View {
    func keyboardAware(contentInset: CGFloat = 0) -> some View {
        modifier(KeyboardAwareModifier(contentInset: contentInset))
    }
}

/// Custom environment key for keyboard height
private struct KeyboardHeightKey: EnvironmentKey {
    static let defaultValue: CGFloat = 0
}

public extension EnvironmentValues {
    var keyboardHeight: CGFloat {
        get {
            self[KeyboardHeightKey.self]
        }
        set {
            self[KeyboardHeightKey.self] = newValue
        }
    }
}
