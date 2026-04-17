package com.thomaskioko.tvmaniac.navigation

/**
 * Creates a [NavigationResultRequest] scoped to the source route [Source] and result type [R].
 *
 * Example:
 * ```
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
 *   navigator.pushNew(SignInRoute(resultKey = signInRequest.key))
 * }
 * ```
 *
 * The generated key is stable for a given [Source] + [R] pair, so it is safe to embed inside the
 * target route and round-trip through save/restore.
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
 */
public fun <R : Any> NavigationResultRegistry.deliverNavigationResult(
    key: NavigationResultRequest.Key<R>,
    result: R,
): Unit = deliver(key, result)
