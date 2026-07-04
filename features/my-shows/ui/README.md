# `:features:my-shows:ui`

## Module dependency graph

<!--region graph-->
```mermaid
graph TB
  :android-designsystem[android-designsystem]:::android-library
  subgraph :core
    direction TB
    :core:base[base]:::multiplatform
    :core:test-tags[test-tags]:::multiplatform
    :core:view[view]:::multiplatform
  end
  subgraph :core:connectivity
    direction TB
    :core:connectivity:api[api]:::multiplatform
  end
  subgraph :core:feature-flags
    direction TB
    :core:feature-flags:api[api]:::multiplatform
  end
  subgraph :core:logger
    direction TB
    :core:logger:api[api]:::multiplatform
  end
  subgraph :core:network-util
    direction TB
    :core:network-util:api[api]:::multiplatform
  end
  subgraph :core:syncstate
    direction TB
    :core:syncstate:api[api]:::multiplatform
  end
  subgraph :core:tasks
    direction TB
    :core:tasks:api[api]:::multiplatform
  end
  subgraph :core:util
    direction TB
    :core:util:api[api]:::multiplatform
  end
  subgraph :data:account-manager
    direction TB
    :data:account-manager:api[api]:::multiplatform
  end
  subgraph :data:cast
    direction TB
    :data:cast:api[api]:::multiplatform
  end
  subgraph :data:continue-watching
    direction TB
    :data:continue-watching:api[api]:::multiplatform
  end
  subgraph :data:database
    direction TB
    :data:database:sqldelight[sqldelight]:::multiplatform
  end
  subgraph :data:datastore
    direction TB
    :data:datastore:api[api]:::multiplatform
  end
  subgraph :data:episode
    direction TB
    :data:episode:api[api]:::multiplatform
  end
  subgraph :data:followedshows
    direction TB
    :data:followedshows:api[api]:::multiplatform
  end
  subgraph :data:library
    direction TB
    :data:library:api[api]:::multiplatform
  end
  subgraph :data:request-manager
    direction TB
    :data:request-manager:api[api]:::multiplatform
  end
  subgraph :data:seasondetails
    direction TB
    :data:seasondetails:api[api]:::multiplatform
  end
  subgraph :data:seasons
    direction TB
    :data:seasons:api[api]:::multiplatform
  end
  subgraph :data:showdetails
    direction TB
    :data:showdetails:api[api]:::multiplatform
  end
  subgraph :data:similar
    direction TB
    :data:similar:api[api]:::multiplatform
  end
  subgraph :data:start-watching
    direction TB
    :data:start-watching:api[api]:::multiplatform
  end
  subgraph :data:sync-activity
    direction TB
    :data:sync-activity:api[api]:::multiplatform
  end
  subgraph :data:trailers
    direction TB
    :data:trailers:api[api]:::multiplatform
  end
  subgraph :data:upnext
    direction TB
    :data:upnext:api[api]:::multiplatform
  end
  subgraph :data:watchlist-prefs
    direction TB
    :data:watchlist-prefs:api[api]:::multiplatform
  end
  subgraph :data:watchproviders
    direction TB
    :data:watchproviders:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:continue-watching[continue-watching]:::multiplatform
    :domain:episode[episode]:::multiplatform
    :domain:followedshows[followedshows]:::multiplatform
    :domain:showdetails[showdetails]:::multiplatform
    :domain:start-watching[start-watching]:::multiplatform
    :domain:sync-activity[sync-activity]:::multiplatform
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:continue-watching
    direction TB
    :features:continue-watching:presenter[presenter]:::multiplatform
    :features:continue-watching:ui[ui]:::android-library
  end
  subgraph :features:home
    direction TB
    :features:home:nav[nav]:::multiplatform
  end
  subgraph :features:my-shows
    direction TB
    :features:my-shows:nav[nav]:::multiplatform
    :features:my-shows:presenter[presenter]:::multiplatform
    :features:my-shows:ui[ui]:::android-library
  end
  subgraph :features:season-details
    direction TB
    :features:season-details:nav[nav]:::multiplatform
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
  end
  subgraph :features:start-watching
    direction TB
    :features:start-watching:presenter[presenter]:::multiplatform
    :features:start-watching:ui[ui]:::android-library
  end
  subgraph :i18n
    direction TB
    :i18n:api[api]:::multiplatform
    :i18n:generator[generator]:::multiplatform
  end
  subgraph :navigation
    direction TB
    :navigation:api[api]:::multiplatform
    :navigation:ui[ui]:::android-library
  end

  :android-designsystem -.-> :core:test-tags
  :android-designsystem --> :domain:theme
  :android-designsystem -.-> :i18n:generator
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:cast:api --> :data:database:sqldelight
  :data:database:sqldelight --> :core:logger:api
  :data:datastore:api --> :i18n:generator
  :data:episode:api --> :data:account-manager:api
  :data:episode:api --> :data:database:sqldelight
  :data:episode:api --> :data:followedshows:api
  :data:episode:api --> :data:upnext:api
  :data:library:api --> :core:network-util:api
  :data:library:api --> :data:account-manager:api
  :data:library:api --> :data:database:sqldelight
  :data:seasondetails:api --> :data:database:sqldelight
  :data:seasons:api --> :data:database:sqldelight
  :data:showdetails:api --> :data:database:sqldelight
  :data:similar:api --> :data:database:sqldelight
  :data:start-watching:api --> :core:network-util:api
  :data:start-watching:api --> :data:account-manager:api
  :data:sync-activity:api --> :core:network-util:api
  :data:sync-activity:api --> :data:account-manager:api
  :data:trailers:api --> :data:database:sqldelight
  :data:watchproviders:api --> :data:database:sqldelight
  :domain:continue-watching --> :core:base
  :domain:continue-watching --> :core:feature-flags:api
  :domain:continue-watching --> :core:logger:api
  :domain:continue-watching --> :core:network-util:api
  :domain:continue-watching --> :core:tasks:api
  :domain:continue-watching --> :core:util:api
  :domain:continue-watching --> :data:account-manager:api
  :domain:continue-watching --> :data:continue-watching:api
  :domain:continue-watching --> :data:datastore:api
  :domain:continue-watching --> :data:episode:api
  :domain:continue-watching --> :data:request-manager:api
  :domain:continue-watching --> :data:upnext:api
  :domain:continue-watching -.-> :domain:episode
  :domain:continue-watching --> :domain:showdetails
  :domain:continue-watching --> :domain:sync-activity
  :domain:episode --> :core:base
  :domain:episode --> :core:logger:api
  :domain:episode --> :core:syncstate:api
  :domain:episode --> :core:tasks:api
  :domain:episode -.-> :core:view
  :domain:episode --> :data:account-manager:api
  :domain:episode --> :data:database:sqldelight
  :domain:episode --> :data:episode:api
  :domain:episode --> :data:library:api
  :domain:followedshows --> :core:base
  :domain:followedshows --> :data:followedshows:api
  :domain:followedshows --> :data:library:api
  :domain:showdetails --> :core:base
  :domain:showdetails --> :core:util:api
  :domain:showdetails --> :data:cast:api
  :domain:showdetails --> :data:episode:api
  :domain:showdetails --> :data:followedshows:api
  :domain:showdetails --> :data:library:api
  :domain:showdetails --> :data:seasondetails:api
  :domain:showdetails --> :data:seasons:api
  :domain:showdetails --> :data:showdetails:api
  :domain:showdetails --> :data:similar:api
  :domain:showdetails --> :data:trailers:api
  :domain:showdetails --> :data:watchproviders:api
  :domain:start-watching --> :core:base
  :domain:start-watching --> :data:episode:api
  :domain:start-watching --> :data:start-watching:api
  :domain:sync-activity --> :core:base
  :domain:sync-activity --> :data:sync-activity:api
  :domain:theme --> :i18n:generator
  :features:continue-watching:presenter --> :core:base
  :features:continue-watching:presenter --> :core:feature-flags:api
  :features:continue-watching:presenter --> :core:logger:api
  :features:continue-watching:presenter --> :core:view
  :features:continue-watching:presenter --> :data:account-manager:api
  :features:continue-watching:presenter --> :data:watchlist-prefs:api
  :features:continue-watching:presenter --> :domain:continue-watching
  :features:continue-watching:presenter --> :domain:episode
  :features:continue-watching:presenter --> :domain:followedshows
  :features:continue-watching:presenter --> :features:my-shows:nav
  :features:continue-watching:presenter -.-> :features:season-details:nav
  :features:continue-watching:presenter -.-> :features:show-details:nav
  :features:continue-watching:presenter --> :i18n:api
  :features:continue-watching:presenter --> :navigation:api
  :features:continue-watching:ui -.-> :android-designsystem
  :features:continue-watching:ui -.-> :core:test-tags
  :features:continue-watching:ui -.-> :core:view
  :features:continue-watching:ui --> :features:continue-watching:presenter
  :features:continue-watching:ui -.-> :i18n:generator
  :features:home:nav --> :navigation:api
  :features:my-shows:nav --> :navigation:api
  :features:my-shows:presenter --> :core:base
  :features:my-shows:presenter --> :data:watchlist-prefs:api
  :features:my-shows:presenter --> :features:continue-watching:presenter
  :features:my-shows:presenter -.-> :features:home:nav
  :features:my-shows:presenter --> :features:my-shows:nav
  :features:my-shows:presenter --> :features:start-watching:presenter
  :features:my-shows:presenter --> :i18n:api
  :features:my-shows:presenter --> :navigation:api
  :features:my-shows:ui -.-> :android-designsystem
  :features:my-shows:ui --> :core:base
  :features:my-shows:ui -.-> :core:test-tags
  :features:my-shows:ui -.-> :data:watchlist-prefs:api
  :features:my-shows:ui -.-> :features:continue-watching:presenter
  :features:my-shows:ui -.-> :features:continue-watching:ui
  :features:my-shows:ui -.-> :features:home:nav
  :features:my-shows:ui --> :features:my-shows:presenter
  :features:my-shows:ui -.-> :features:start-watching:presenter
  :features:my-shows:ui -.-> :features:start-watching:ui
  :features:my-shows:ui -.-> :i18n:generator
  :features:my-shows:ui --> :navigation:api
  :features:my-shows:ui --> :navigation:ui
  :features:season-details:nav --> :navigation:api
  :features:show-details:nav --> :navigation:api
  :features:start-watching:presenter --> :core:base
  :features:start-watching:presenter --> :core:logger:api
  :features:start-watching:presenter --> :core:syncstate:api
  :features:start-watching:presenter --> :core:view
  :features:start-watching:presenter --> :data:account-manager:api
  :features:start-watching:presenter -.-> :data:start-watching:api
  :features:start-watching:presenter --> :data:watchlist-prefs:api
  :features:start-watching:presenter --> :domain:start-watching
  :features:start-watching:presenter --> :features:my-shows:nav
  :features:start-watching:presenter -.-> :features:show-details:nav
  :features:start-watching:presenter --> :navigation:api
  :features:start-watching:ui -.-> :android-designsystem
  :features:start-watching:ui -.-> :core:test-tags
  :features:start-watching:ui -.-> :core:view
  :features:start-watching:ui --> :features:start-watching:presenter
  :features:start-watching:ui -.-> :i18n:generator
  :i18n:api --> :i18n:generator
  :navigation:ui --> :core:base
  :navigation:ui --> :navigation:api

classDef application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef multiplatform fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

<details><summary>Graph legend</summary>

```mermaid
graph TB
  application[application]:::application
  multiplatform[multiplatform]:::multiplatform
  android-library[android-library]:::android-library
  jvm-library[jvm-library]:::jvm-library

  api["api dependency"] --> implementation["implementation dependency"]

classDef application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef multiplatform fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

</details>
<!--endregion-->
