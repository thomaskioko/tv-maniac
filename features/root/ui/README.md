# `:features:root:ui`

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
  subgraph :core:appconfig
    direction TB
    :core:appconfig:api[api]:::multiplatform
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
  subgraph :core:notifications
    direction TB
    :core:notifications:api[api]:::multiplatform
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
  subgraph :data:logout
    direction TB
    :data:logout:api[api]:::multiplatform
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
  subgraph :data:subscription
    direction TB
    :data:subscription:api[api]:::multiplatform
  end
  subgraph :data:sync-activity
    direction TB
    :data:sync-activity:api[api]:::multiplatform
  end
  subgraph :data:trailers
    direction TB
    :data:trailers:api[api]:::multiplatform
  end
  subgraph :data:traktlists
    direction TB
    :data:traktlists:api[api]:::multiplatform
  end
  subgraph :data:upnext
    direction TB
    :data:upnext:api[api]:::multiplatform
  end
  subgraph :data:user
    direction TB
    :data:user:api[api]:::multiplatform
  end
  subgraph :data:watchproviders
    direction TB
    :data:watchproviders:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:account-switcher[account-switcher]:::multiplatform
    :domain:continue-watching[continue-watching]:::multiplatform
    :domain:episode[episode]:::multiplatform
    :domain:library[library]:::multiplatform
    :domain:logout[logout]:::multiplatform
    :domain:notifications[notifications]:::multiplatform
    :domain:settings[settings]:::multiplatform
    :domain:showdetails[showdetails]:::multiplatform
    :domain:sync-activity[sync-activity]:::multiplatform
    :domain:theme[theme]:::multiplatform
    :domain:user[user]:::multiplatform
  end
  subgraph :features:debug
    direction TB
    :features:debug:nav[nav]:::multiplatform
  end
  subgraph :features:discover
    direction TB
    :features:discover:nav[nav]:::multiplatform
  end
  subgraph :features:home
    direction TB
    :features:home:nav[nav]:::multiplatform
    :features:home:presenter[presenter]:::multiplatform
    :features:home:ui[ui]:::android-library
  end
  subgraph :features:library
    direction TB
    :features:library:nav[nav]:::multiplatform
  end
  subgraph :features:my-shows
    direction TB
    :features:my-shows:nav[nav]:::multiplatform
  end
  subgraph :features:profile
    direction TB
    :features:profile:nav[nav]:::multiplatform
  end
  subgraph :features:progress
    direction TB
    :features:progress:nav[nav]:::multiplatform
  end
  subgraph :features:root
    direction TB
    :features:root:nav[nav]:::multiplatform
    :features:root:presenter[presenter]:::multiplatform
    :features:root:ui[ui]:::android-library
  end
  subgraph :features:season-details
    direction TB
    :features:season-details:nav[nav]:::multiplatform
  end
  subgraph :features:settings
    direction TB
    :features:settings:nav[nav]:::multiplatform
    :features:settings:presenter[presenter]:::multiplatform
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
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
  :data:sync-activity:api --> :core:network-util:api
  :data:sync-activity:api --> :data:account-manager:api
  :data:trailers:api --> :data:database:sqldelight
  :data:user:api --> :core:network-util:api
  :data:user:api --> :data:account-manager:api
  :data:user:api --> :data:database:sqldelight
  :data:watchproviders:api --> :data:database:sqldelight
  :domain:account-switcher --> :core:base
  :domain:account-switcher --> :data:account-manager:api
  :domain:account-switcher --> :data:episode:api
  :domain:account-switcher --> :data:library:api
  :domain:account-switcher --> :data:logout:api
  :domain:account-switcher --> :data:traktlists:api
  :domain:account-switcher --> :domain:continue-watching
  :domain:account-switcher --> :domain:library
  :domain:account-switcher --> :domain:user
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
  :domain:library --> :core:base
  :domain:library --> :core:logger:api
  :domain:library --> :core:network-util:api
  :domain:library --> :core:syncstate:api
  :domain:library --> :core:tasks:api
  :domain:library --> :core:util:api
  :domain:library --> :data:account-manager:api
  :domain:library --> :data:datastore:api
  :domain:library --> :data:followedshows:api
  :domain:library --> :data:library:api
  :domain:library -.-> :data:request-manager:api
  :domain:library --> :domain:showdetails
  :domain:library --> :domain:sync-activity
  :domain:logout --> :core:base
  :domain:logout --> :data:account-manager:api
  :domain:logout --> :data:datastore:api
  :domain:logout --> :data:logout:api
  :domain:logout --> :data:user:api
  :domain:notifications --> :core:base
  :domain:notifications --> :core:logger:api
  :domain:notifications --> :core:network-util:api
  :domain:notifications --> :core:notifications:api
  :domain:notifications --> :core:syncstate:api
  :domain:notifications --> :core:tasks:api
  :domain:notifications --> :core:util:api
  :domain:notifications --> :data:account-manager:api
  :domain:notifications --> :data:datastore:api
  :domain:notifications --> :data:episode:api
  :domain:notifications --> :data:seasondetails:api
  :domain:notifications --> :data:seasons:api
  :domain:notifications --> :i18n:api
  :domain:settings --> :core:base
  :domain:settings --> :core:util:api
  :domain:settings --> :data:datastore:api
  :domain:settings --> :domain:theme
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
  :domain:sync-activity --> :core:base
  :domain:sync-activity --> :data:sync-activity:api
  :domain:theme --> :i18n:generator
  :domain:user --> :core:base
  :domain:user --> :data:account-manager:api
  :domain:user --> :data:traktlists:api
  :domain:user --> :data:user:api
  :features:debug:nav --> :navigation:api
  :features:discover:nav --> :navigation:api
  :features:home:nav --> :navigation:api
  :features:home:presenter --> :core:base
  :features:home:presenter --> :domain:user
  :features:home:presenter -.-> :features:discover:nav
  :features:home:presenter --> :features:home:nav
  :features:home:presenter -.-> :features:library:nav
  :features:home:presenter -.-> :features:my-shows:nav
  :features:home:presenter -.-> :features:profile:nav
  :features:home:presenter -.-> :features:progress:nav
  :features:home:presenter --> :navigation:api
  :features:home:ui -.-> :android-designsystem
  :features:home:ui --> :core:base
  :features:home:ui -.-> :core:test-tags
  :features:home:ui -.-> :features:discover:nav
  :features:home:ui --> :features:home:presenter
  :features:home:ui -.-> :features:my-shows:nav
  :features:home:ui -.-> :features:profile:nav
  :features:home:ui -.-> :features:progress:nav
  :features:home:ui -.-> :i18n:generator
  :features:home:ui --> :navigation:api
  :features:home:ui --> :navigation:ui
  :features:library:nav --> :navigation:api
  :features:my-shows:nav --> :navigation:api
  :features:profile:nav --> :navigation:api
  :features:progress:nav --> :navigation:api
  :features:root:nav --> :domain:theme
  :features:root:presenter --> :core:base
  :features:root:presenter --> :core:logger:api
  :features:root:presenter --> :core:syncstate:api
  :features:root:presenter -.-> :core:view
  :features:root:presenter --> :data:account-manager:api
  :features:root:presenter --> :data:datastore:api
  :features:root:presenter --> :domain:logout
  :features:root:presenter -.-> :domain:theme
  :features:root:presenter --> :domain:user
  :features:root:presenter -.-> :features:debug:nav
  :features:root:presenter --> :features:home:presenter
  :features:root:presenter --> :features:root:nav
  :features:root:presenter -.-> :features:season-details:nav
  :features:root:presenter -.-> :features:settings:presenter
  :features:root:presenter -.-> :features:show-details:nav
  :features:root:presenter --> :i18n:api
  :features:root:presenter --> :navigation:api
  :features:root:ui -.-> :android-designsystem
  :features:root:ui -.-> :core:base
  :features:root:ui -.-> :features:home:presenter
  :features:root:ui -.-> :features:home:ui
  :features:root:ui -.-> :features:root:nav
  :features:root:ui --> :features:root:presenter
  :features:root:ui -.-> :i18n:generator
  :features:root:ui -.-> :navigation:api
  :features:root:ui --> :navigation:ui
  :features:season-details:nav --> :navigation:api
  :features:settings:nav --> :navigation:api
  :features:settings:presenter --> :core:appconfig:api
  :features:settings:presenter --> :core:base
  :features:settings:presenter --> :core:feature-flags:api
  :features:settings:presenter --> :core:logger:api
  :features:settings:presenter --> :core:view
  :features:settings:presenter --> :data:account-manager:api
  :features:settings:presenter --> :data:datastore:api
  :features:settings:presenter --> :data:subscription:api
  :features:settings:presenter --> :data:user:api
  :features:settings:presenter --> :domain:account-switcher
  :features:settings:presenter --> :domain:logout
  :features:settings:presenter --> :domain:notifications
  :features:settings:presenter --> :domain:settings
  :features:settings:presenter --> :domain:theme
  :features:settings:presenter -.-> :features:debug:nav
  :features:settings:presenter --> :features:settings:nav
  :features:settings:presenter --> :i18n:api
  :features:settings:presenter --> :i18n:generator
  :features:settings:presenter --> :navigation:api
  :features:show-details:nav --> :navigation:api
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
