package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.StackNavigation

public interface RootNavigator {
    /**
     * Brings a configuration to the front of the navigation stack.
     * If the configuration is already in the stack, it will be brought to the front.
     * If not, it will be added to the front.
     *
     * @param config The configuration to bring to the front
     */
    public fun bringToFront(config: RootDestinationConfig)

    /**
     * Pushes a new configuration to the navigation stack.
     *
     * @param config The configuration to push
     */
    public fun pushNew(config: RootDestinationConfig)

    /**
     * Pushes a configuration to the front of the navigation stack.
     * If the configuration is already in the stack, it will be brought to the front.
     * If not, it will be added to the front.
     *
     * @param config The configuration to push to the front
     */
    public fun pushToFront(config: RootDestinationConfig)

    /**
     * Pops the top configuration from the navigation stack.
     */
    public fun pop()

    /**
     * Pops configurations from the navigation stack until reaching the specified index.
     *
     * @param toIndex The index to pop to
     */
    public fun popTo(toIndex: Int)

    /**
     * Returns the underlying [StackNavigation] instance.
     * This is needed for Decompose's childStack function.
     */
    public fun getStackNavigation(): StackNavigation<RootDestinationConfig>
}
