//
//  AppleTVStyleIndicator.swift
//  SwiftUIComponents
//
//  Created by Thomas Kioko on 8/15/25.
//
import SwiftUI

// MARK: - Apple TV Style Indicator

public struct CircularIndicator: View {
    @Theme private var theme

    let totalItems: Int
    let currentIndex: Int
    let isDragging: Bool

    @State private var indicatorProgress: CGFloat = 0
    @State private var progressTimer: Timer?
    @State private var lastIndex: Int = 0

    private let maxVisibleDots = 8

    public init(
        totalItems: Int,
        currentIndex: Int,
        isDragging: Bool
    ) {
        self.totalItems = totalItems
        self.currentIndex = currentIndex
        self.isDragging = isDragging
    }

    public var body: some View {
        HStack(spacing: 5) {
            if totalItems <= maxVisibleDots {
                ForEach(0 ..< totalItems, id: \.self) { index in
                    indicatorDot(for: index, isActive: currentIndex == index)
                        .id("\(index)-\(currentIndex == index)")
                }
            } else {
                ForEach(0 ..< maxVisibleDots, id: \.self) { dotIndex in
                    dynamicIndicatorDot(dotIndex: dotIndex)
                        .id("\(dotIndex)-\(currentIndex)")
                }
            }
        }
        .drawingGroup()
        .animation(nil, value: indicatorProgress)
        .onChange(of: currentIndex) { newIndex in
            if lastIndex != newIndex {
                let isWrapAround = (lastIndex == totalItems - 1 && newIndex == 0) ||
                    (lastIndex == 0 && newIndex == totalItems - 1)

                lastIndex = newIndex

                if !isDragging {
                    if isWrapAround {
                        progressTimer?.invalidate()
                        progressTimer = nil
                        indicatorProgress = 0

                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                            resetAndStartProgress()
                        }
                    } else {
                        resetAndStartProgress()
                    }
                } else {
                    progressTimer?.invalidate()
                    progressTimer = nil
                    indicatorProgress = 0
                }
            }
        }
        .onChange(of: isDragging) { dragging in
            if !dragging, progressTimer == nil {
                resetAndStartProgress()
            } else if dragging {
                progressTimer?.invalidate()
                progressTimer = nil
                indicatorProgress = 0
            }
        }
        .onAppear {
            lastIndex = currentIndex
            if !isDragging {
                resetAndStartProgress()
            }
        }
    }

    @ViewBuilder
    private func dynamicIndicatorDot(dotIndex: Int) -> some View {
        let actualIndex = calculateActualIndex(
            dotIndex: dotIndex,
            currentIndex: currentIndex,
            totalCount: totalItems,
            maxVisible: maxVisibleDots
        )

        let isActive = actualIndex == currentIndex
        let isEdgeIndicator = (dotIndex == 0 && currentIndex > maxVisibleDots - 2) ||
            (dotIndex == maxVisibleDots - 1 && currentIndex < totalItems - 2)

        if isEdgeIndicator {
            Circle()
                .fill(Color.gray.opacity(0.3))
                .frame(width: 4, height: 4)
        } else {
            indicatorDot(for: actualIndex, isActive: isActive)
        }
    }

    @ViewBuilder
    private func indicatorDot(for _: Int, isActive: Bool) -> some View {
        if isActive {
            ProgressIndicatorBar(progress: indicatorProgress, activeColor: theme.colors.onPrimary)
        } else {
            Circle()
                .fill(theme.colors.surfaceVariant.opacity(0.5))
                .frame(width: 6, height: 6)
        }
    }

    private func resetAndStartProgress() {
        progressTimer?.invalidate()
        progressTimer = nil

        withAnimation(nil) {
            indicatorProgress = 0
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            let startTime = Date()
            progressTimer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { timer in
                let elapsed = Date().timeIntervalSince(startTime)
                let progress = min(elapsed / 5.0, 1.0)

                withAnimation(nil) {
                    indicatorProgress = CGFloat(progress)
                }

                if progress >= 1.0 {
                    timer.invalidate()
                    progressTimer = nil
                }
            }
        }
    }

    private func calculateActualIndex(dotIndex: Int, currentIndex: Int, totalCount: Int, maxVisible: Int) -> Int {
        if currentIndex <= maxVisible - 2 {
            dotIndex
        } else if currentIndex >= totalCount - 2 {
            totalCount - maxVisible + dotIndex
        } else {
            currentIndex - (maxVisible - 2) + dotIndex
        }
    }
}

// MARK: - Progress Indicator Bar

struct ProgressIndicatorBar: View {
    let progress: CGFloat
    var activeColor: Color = .white

    var body: some View {
        Canvas { context, _ in
            let backgroundRect = CGRect(x: 0, y: 0, width: 25, height: 8)
            context.fill(
                RoundedRectangle(cornerRadius: 4).path(in: backgroundRect),
                with: .color(Color.gray.opacity(0.5))
            )

            let progressWidth = 25 * progress
            if progressWidth > 0 {
                let progressRect = CGRect(x: 0, y: 0, width: progressWidth, height: 8)
                context.fill(
                    RoundedRectangle(cornerRadius: 4).path(in: progressRect),
                    with: .color(activeColor)
                )
            }
        }
        .frame(width: 25, height: 8)
    }
}
