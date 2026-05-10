package com.thomaskioko.tvmaniac.navigation

/**
 * Creates a [NavigationResultRequest] scoped to the source route [Source] and result type [R].
 *
 * Example:
 * ```kotlin
 * // In a presenter hosted by SignInCallerRoute
 * private val signInRequest = resultRegistry.registerForNavigationResult<SignInCallerRoute, SignInResult>()
 *
 * init {
 *   coroutineScope.launch {
 *     signInRequest.results.collect { result -> handleSignIn(result) }
 *   }
 * }
 *
 * fun onSignInClicked() {
 *   navigator.navigateTo(SignInRoute(resultKey = signInRequest.key))
 * }
 * ```
 *
 * The generated key is stable for a given [Source] and [R] pair, so it is safe to embed inside
 * the target route and round-trip through save and restore.
 *
 * @param Source source route hosting the presenter that registered for the result.
 * @param R result type the source expects to receive.
 * @return request whose [NavigationResultRequest.results] flow emits each delivered result.
 */
public inline fun <reified Source : NavRoute, reified R : Any> NavigationResultRegistry.registerForNavigationResult(): NavigationResultRequest<R> {
    val key = NavigationResultRequest.Key<R>(
        ownerRouteQualifiedName = requireNotNull(Source::class.qualifiedName) {
            "Source route class must have a qualified name to register for navigation results."
        },
        resultQualifiedName = requireNotNull(R::class.qualifiedName) {
            "Result class must have a qualified name to register for navigation results."
        },
    )
    return NavigationResultRequest(key = key, results = register(key))
}

/**
 * Delivers [result] for the request identified by [key]. The target destination is responsible for
 * popping itself from the stack after delivery if that is the desired flow.
 *
 * @param R result type expected by the source destination.
 * @param key identifier embedded in the target route by the source.
 * @param result value to deliver to the source.
 */
public fun <R : Any> NavigationResultRegistry.deliverNavigationResult(
    key: NavigationResultRequest.Key<R>,
    result: R,
): Unit = deliver(key, result)
