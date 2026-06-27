# `:features:upnext:ui`

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
    :domain:sync-activity[sync-activity]:::multiplatform
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:episode-sheet
    direction TB
    :features:episode-sheet:nav[nav]:::multiplatform
  end
  subgraph :features:progress
    direction TB
    :features:progress:nav[nav]:::multiplatform
  end
  subgraph :features:season-details
    direction TB
    :features:season-details:nav[nav]:::multiplatform
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
  end
  subgraph :features:upnext
    direction TB
    :features:upnext:presenter[presenter]:::multiplatform
    :features:upnext:ui[ui]:::android-library
  end
  subgraph :i18n
    direction TB
    :i18n:generator[generator]:::multiplatform
  end
  subgraph :navigation
    direction TB
    :navigation:api[api]:::multiplatform
  end

  :android-designsystem -.-> :core:test-tags
  :android-designsystem --> :domain:theme
  :android-designsystem -.-> :i18n:generator
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:view --> :core:logger:api
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
  :domain:sync-activity --> :core:base
  :domain:sync-activity --> :data:sync-activity:api
  :domain:theme --> :i18n:generator
  :features:episode-sheet:nav --> :navigation:api
  :features:progress:nav --> :navigation:api
  :features:season-details:nav --> :navigation:api
  :features:show-details:nav --> :navigation:api
  :features:upnext:presenter --> :core:base
  :features:upnext:presenter --> :core:logger:api
  :features:upnext:presenter --> :core:syncstate:api
  :features:upnext:presenter --> :core:view
  :features:upnext:presenter --> :data:account-manager:api
  :features:upnext:presenter --> :data:upnext:api
  :features:upnext:presenter --> :domain:continue-watching
  :features:upnext:presenter --> :domain:episode
  :features:upnext:presenter --> :domain:followedshows
  :features:upnext:presenter -.-> :features:episode-sheet:nav
  :features:upnext:presenter --> :features:progress:nav
  :features:upnext:presenter -.-> :features:season-details:nav
  :features:upnext:presenter -.-> :features:show-details:nav
  :features:upnext:presenter --> :navigation:api
  :features:upnext:ui -.-> :android-designsystem
  :features:upnext:ui -.-> :core:test-tags
  :features:upnext:ui -.-> :core:view
  :features:upnext:ui -.-> :domain:continue-watching
  :features:upnext:ui --> :features:upnext:presenter
  :features:upnext:ui -.-> :i18n:generator

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
