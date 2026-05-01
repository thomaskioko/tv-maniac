# Journey Tests

## Table of Contents

- [Purpose](#purpose)
- [Existing Journeys](#existing-journeys)
- [Anatomy of a Journey](#anatomy-of-a-journey)
- [Sharp Edges](#sharp-edges)
- [Extending a Journey](#extending-a-journey)
- [Running](#running)

Journey tests are end-to-end integration tests that walk one realistic user lifecycle across
multiple surfaces in narrative order. They live alongside per-feature flow tests and share the
same harness (`BaseAppFlowTest`, robots, scenarios). See
[integration-testing.md](integration-testing.md) for the harness fundamentals.

## Purpose

Per-feature flow tests cover one surface in isolation (Discover renders, Settings logout dialog
appears, Episode Sheet opens). Journeys cover integration between surfaces: a user signs in and
the rationale fires, the synced library row reflects a follow action, the Episode Sheet
navigation lands back on Show Details, and so on. A journey is one linear `@Test` method per
class, walking phases of the lifecycle with comments. Journeys catch regressions that no
per-feature test would surface, because the regression only appears when surfaces interact.

## Existing Journeys

`UnauthenticatedUserJourneyTest`
(`app/src/sharedTest/.../compose/journey/UnauthenticatedUserJourneyTest.kt`). Two `@Test`
methods. The main `unauthenticatedUserNavigatesAllScreens...` walks the logged-out narrative
through Discover (with featured pager swipe), Search, Show Details, Progress, Calendar,
Library empty, Profile, Settings (theme change, notification rationale dismiss, notification
rationale enable with `dismissSystemDialog` follow-up), then back to
Discover for the offline-first Track action followed by Continue Tracking mark-watched, Season
Details mark-watched, Discover UpNext card opening the Episode Sheet, Library show row visible
after the local follow, and finally the Add-to-list login prompt that triggers the simulated
OAuth round-trip and lands on the Profile user card. The second test
`unauthenticatedUserSignsInFromProfileAndSeesUserCard` exercises the Profile Sign In CTA in
isolation as the symmetric entry point to the same OAuth round-trip.

`AuthenticatedUserJourneyTest`
(`app/src/sharedTest/.../compose/journey/AuthenticatedUserJourneyTest.kt`). One `@Test` method
that starts unauthenticated, configures the fake auth manager via `setOnLaunchWebView`, signs
in via the Profile Sign In button, asserts the post-LOGGED_IN rationale fires from
`DefaultRootPresenter`'s auth-state collector, walks the synced Library and Progress and
Discover surfaces, drills into Show Details via the Library row for Continue Tracking and
Season Details mark-watched, opens the Discover UpNext card to the Episode Sheet and exits via
`OPEN_SHOW`, then signs out via Settings (Trakt account row, logout confirm) and lands back on
the unauthenticated state.

## Anatomy of a Journey

1. **Initial state in `@Before`**. Call `scenarios.stubUnauthenticatedState()` so the activity
   launches in `LOGGED_OUT`. Add stubs for endpoints the journey will hit later
   (`scenarios.showDetails.stubShowDetailsEndpoints`, `scenarios.showDetails.stubSeasonDetailsEndpoints`,
   `/shows/<id>/progress/watched` for offline-first UpNext propagation).

2. **Pre-flight test setup before the action that triggers it**. Configure the fake auth
   manager via `setOnLaunchWebView { ... }` immediately before the click that calls it. Seed
   permission state via `graph.datastoreRepository.setNotificationPermissionAskedNow(false)`
   when a phase exercises the rationale path.

3. **Walk the narrative in commented phases**. One sequence of robot calls per phase, each
   ending in an assertion. Match the order a real user would take. Use only existing robots
   and scenarios. Do not call repositories or DAOs in the test body.

4. **Assert behavioural signals only**. UI state (a tag is shown, a row is hidden) and
   downstream observable state (Library row appears after follow). Do not add capture
   infrastructure for inspecting what was sent to fakes.

5. **Cross the auth boundary explicitly**. The journey must trigger the `LOGGED_OUT` to
   `LOGGED_IN` transition (or vice versa) through real UI, not by stubbing
   `traktAuthRepository.state` directly. The fake auth manager's
   `setOnLaunchWebView` simulates the OAuth round-trip when a button click invokes it.

## Sharp Edges

The harness handles several Compose and Android quirks that journeys frequently hit. Use the
provided helpers instead of working around them per-test.

- **`pressBack` and dialog windows**. `composeTestRule.pressBack()` dispatches via the
  activity's back-pressed dispatcher, which bypasses Material 3 `Dialog` and `ModalBottomSheet`
  windows. Click the dialog's confirm or dismiss button, or trigger an action that closes the
  modal (for example `clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)` for the episode
  sheet). See [integration-testing.md](integration-testing.md#pressback-behaviour).

- **System notification permission dialog**. The rationale Enable path calls
  `permissionLauncher.launch(POST_NOTIFICATIONS)`, which on real Android API 33+ shows a real
  system dialog that detaches the Compose owner. Call `dismissSystemDialog()`
  immediately after `acceptNotificationRationale()`. The helper is a no-op under Robolectric
  and on pre-Tiramisu APIs.

- **Discover featured pager auto-advance**. `LocalAutoAdvanceEnabled provides false` is wired
  in `TvManiacTestActivity`, so the pager stays where the test leaves it. Pager assertions
  remain deterministic regardless of how long the journey has been running.

- **Featured pager item order**. `FeaturedShowsStore` populates `featured_shows` from the
  trending endpoint, and the DAO's `entriesInPage` query orders by `page_order ASC`, which is
  the trending fixture order. The current trending fixture lists Breaking Bad, Better Call
  Saul, Game of Thrones in that sequence.

- **Show Details entry on the authenticated journey**. Use `libraryRobot.clickShowRow` rather
  than `discoverRobot.clickShowCard`. The Discover trending tag can match more than one node in
  the authenticated state, and `awaitNodeWithTag` requires exactly one match. The Library row
  is the unambiguous entry point once the show is in the local library.

## Extending a Journey

Prefer adding to an existing journey when the action belongs to the same lifecycle. Create a
new journey only when the lifecycle differs (a third class would only make sense if a new
auth, theme, or device-state lifecycle were introduced).

When a screen exposes a new test tag, add a robot method on the matching robot class. When an
endpoint pattern repeats across journeys, add a scenario stub group on `Scenarios` rather than
inlining stubber calls. When a system-level interaction needs handling on instrumentation
runs, add a helper next to `dismissSystemDialog` rather than inlining
UiAutomator calls in the journey.

## Running

The journey tests run under the same Gradle tasks as flow tests. See
[integration-testing.md](integration-testing.md#running-tests) for the full list and when to
use each.
