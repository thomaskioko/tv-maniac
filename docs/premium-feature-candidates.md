# Premium Feature Candidates

Candidate features to lock behind TvManiac Premium in future releases, informed by a survey of what SeriesGuide and Showly paywall (2026-07-06). This document is a product backlog, not an implementation plan. Each candidate ships through the existing lock pipeline when picked up.

## Prior art

Both apps monetize convenience, cosmetic, and power features. Core tracking (following shows, progress, history, search, Trakt sync, backup) is free in both.

### SeriesGuide (supporter "X" subscription)

Upsell mechanism: redirect to the purchase screen.

| Feature | Enforcement |
|---|---|
| Episode notifications | The notification service refuses to run without access |
| Theme switcher | Changing theme opens the purchase screen |
| Cloud backup and sync (Hexagon) | Sign-in requires access |
| List home-screen widgets | Widget configuration opens the purchase screen |
| "Watch again" rewatch tracking (episodes and movies) | Menu action opens the purchase screen |
| Pin show shortcut to home screen | Action opens the purchase screen |

### Showly ("Showly Premium")

Upsell mechanism: locked settings rows dim with a lock icon; tapping opens an informational paywall.

| Feature | Enforcement |
|---|---|
| Extra app themes | Settings row locked |
| Widget theme and transparency | Settings rows locked |
| Quick rate (auto-rate prompt when marking watched) | Settings row locked |
| Collection view types (compact list and grid) | View-mode chip routes to the paywall |
| News section | Designed and marketed, module removed from the build |
| Custom posters and fanart | Marketed on the paywall, not actually enforced |

## Current TvManiac locks

`SubscriptionFeature` ships with three entries: `EpisodeNotifications` (SeriesGuide precedent), `CustomThemes` (precedent in both apps), and `Calendar` (no precedent in either app; both keep upcoming views free; this is a deliberate TvManiac differentiation and the closest of the three to core tracking).

## Candidates

Ordered by precedent strength and existing TvManiac infrastructure.

### 1. List view types

Lock switching the library and list layouts (`ListStyle` grid versus list) for free users. Showly locks exactly this surface: the view-mode affordance routes to the paywall instead of applying the layout. The toggle already exists in TvManiac, so the lock is a presenter access check plus the shared lock treatment on the affordance.

### 2. Quick rate

Offer an auto-rate prompt when marking an episode or show watched, as a Premium convenience on top of the ratings feature that shipped in #1059. Showly precedent (`QUICK_RATING`). Requires building the prompt itself first; the lock rides along.

### 3. Rewatch tracking (multiple plays)

Track repeat watches of an episode or show. SeriesGuide locks this for both episodes and movies. TvManiac does not track multiple plays today, so this is a data-layer feature with the lock designed in from the start.

### 4. Widgets and widget theming

Home-screen widgets do not exist in TvManiac yet. Both apps monetize this surface: SeriesGuide locks the whole list widget, Showly locks widget theming and transparency. Decide the split (whole widget versus theming only) when the widget ships.

### 5. Custom artwork

Let users override a show's poster or backdrop. Showly markets this on its paywall (without enforcing it). TvManiac has no custom image machinery, so this is the furthest out.

## Principles

- Never lock core tracking: following, progress, history, search, discovery, account sync, and backup stay free. Both surveyed apps hold this line.
- Every new lock reuses the shipped mechanism: add the `SubscriptionFeature` entry, expose access from the owning presenter through `SubscriptionManager.observeAccess`, render the shared lock treatment (`LockedContent` on Android, `lockedContent` on iOS), and dispatch a feature-specific upgrade action that `PaywallCoordinator.requestPaywall(source)` handles once `paywall-integration` ships.
- The `enable_paywall` flag stays off in production until the purchase flow exists; every lock is inert while the flag is off.
