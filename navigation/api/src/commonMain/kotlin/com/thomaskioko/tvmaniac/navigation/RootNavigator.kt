package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.StackNavigation

interface RootNavigator {
    /**
     * Brings a configuration to the front of the navigation stack.
     * If the configuration is already in the stack, it will be brought to the front.
     * If not, it will be added to the front.
     *
     * @param config The configuration to bring to the front
     */
    fun bringToFront(config: RootDestinationConfig)

    /**
     * Pushes a new configuration to the navigation stack.
     *
     * @param config The configuration to push
     */
    fun pushNew(config: RootDestinationConfig)

    /**
     * Pushes a configuration to the front of the navigation stack.
     * If the configuration is already in the stack, it will be brought to the front.
     * If not, it will be added to the front.
     *
     * @param config The configuration to push to the front
     */
    fun pushToFront(config: RootDestinationConfig)

    /**
     * Pops the top configuration from the navigation stack.
     */
    fun pop()

    /**
     * Pops configurations from the navigation stack until reaching the specified index.
     *
     * @param toIndex The index to pop to
     */
    fun popTo(toIndex: Int)

    /**
     * Returns the underlying [StackNavigation] instance.
     * This is needed for Decompose's childStack function.
     */
    fun getStackNavigation(): StackNavigation<RootDestinationConfig>
}
