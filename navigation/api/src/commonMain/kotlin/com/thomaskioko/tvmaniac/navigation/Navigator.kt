package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.StackNavigation

public interface Navigator {
    /**
     * Brings a route to the front of the navigation stack.
     * If the route is already in the stack, it will be brought to the front.
     * If not, it will be added to the front.
     *
     * @param route The route to bring to the front
     */
    public fun bringToFront(route: NavRoute)

    /**
     * Pushes a new route to the navigation stack.
     *
     * @param route The route to push
     */
    public fun pushNew(route: NavRoute)

    /**
     * Pushes a route to the front of the navigation stack.
     * If the route is already in the stack, it will be brought to the front.
     * If not, it will be added to the front.
     *
     * @param route The route to push to the front
     */
    public fun pushToFront(route: NavRoute)

    /**
     * Pops the top route from the navigation stack.
     */
    public fun pop()

    /**
     * Pops routes from the navigation stack until reaching the specified index.
     *
     * @param toIndex The index to pop to
     */
    public fun popTo(toIndex: Int)

    /**
     * Returns the underlying [StackNavigation] instance.
     * This is needed for Decompose's childStack function.
     */
    public fun getStackNavigation(): StackNavigation<NavRoute>
}
