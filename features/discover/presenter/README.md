# `:features:discover:presenter`

## Module dependency graph

<!--region graph-->
```mermaid
graph TB
  subgraph :core
    direction TB
    :core:base[base]:::multiplatform
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
  subgraph :data:featuredshows
    direction TB
    :data:featuredshows:api[api]:::multiplatform
  end
  subgraph :data:followedshows
    direction TB
    :data:followedshows:api[api]:::multiplatform
  end
  subgraph :data:genre
    direction TB
    :data:genre:api[api]:::multiplatform
  end
  subgraph :data:library
    direction TB
    :data:library:api[api]:::multiplatform
  end
  subgraph :data:popularshows
    direction TB
    :data:popularshows:api[api]:::multiplatform
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
  subgraph :data:shows
    direction TB
    :data:shows:api[api]:::multiplatform
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
  subgraph :data:topratedshows
    direction TB
    :data:topratedshows:api[api]:::multiplatform
  end
  subgraph :data:trailers
    direction TB
    :data:trailers:api[api]:::multiplatform
  end
  subgraph :data:trendingshows
    direction TB
    :data:trendingshows:api[api]:::multiplatform
  end
  subgraph :data:upcomingshows
    direction TB
    :data:upcomingshows:api[api]:::multiplatform
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
    :domain:discover[discover]:::multiplatform
    :domain:episode[episode]:::multiplatform
    :domain:followedshows[followedshows]:::multiplatform
    :domain:genre[genre]:::multiplatform
    :domain:showdetails[showdetails]:::multiplatform
    :domain:start-watching[start-watching]:::multiplatform
    :domain:sync-activity[sync-activity]:::multiplatform
  end
  subgraph :features:discover
    direction TB
    :features:discover:nav[nav]:::multiplatform
    :features:discover:presenter[presenter]:::multiplatform
  end
  subgraph :features:episode-sheet
    direction TB
    :features:episode-sheet:nav[nav]:::multiplatform
  end
  subgraph :features:home
    direction TB
    :features:home:nav[nav]:::multiplatform
  end
  subgraph :features:more-shows
    direction TB
    :features:more-shows:nav[nav]:::multiplatform
  end
  subgraph :features:progress
    direction TB
    :features:progress:nav[nav]:::multiplatform
  end
  subgraph :features:search
    direction TB
    :features:search:nav[nav]:::multiplatform
  end
  subgraph :features:season-details
    direction TB
    :features:season-details:nav[nav]:::multiplatform
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
  end

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
  :data:featuredshows:api --> :core:base
  :data:featuredshows:api --> :data:database:sqldelight
  :data:featuredshows:api --> :data:shows:api
  :data:genre:api --> :data:database:sqldelight
  :data:genre:api --> :data:shows:api
  :data:library:api --> :core:network-util:api
  :data:library:api --> :data:account-manager:api
  :data:library:api --> :data:database:sqldelight
  :data:popularshows:api --> :core:base
  :data:popularshows:api --> :data:database:sqldelight
  :data:popularshows:api --> :data:shows:api
  :data:seasondetails:api --> :data:database:sqldelight
  :data:seasons:api --> :data:database:sqldelight
  :data:showdetails:api --> :data:database:sqldelight
  :data:shows:api --> :data:account-manager:api
  :data:shows:api --> :data:database:sqldelight
  :data:similar:api --> :data:database:sqldelight
  :data:start-watching:api --> :core:network-util:api
  :data:start-watching:api --> :data:account-manager:api
  :data:sync-activity:api --> :core:network-util:api
  :data:sync-activity:api --> :data:account-manager:api
  :data:topratedshows:api --> :core:base
  :data:topratedshows:api --> :data:database:sqldelight
  :data:topratedshows:api --> :data:shows:api
  :data:trailers:api --> :data:database:sqldelight
  :data:trendingshows:api --> :core:base
  :data:trendingshows:api --> :data:database:sqldelight
  :data:trendingshows:api --> :data:shows:api
  :data:upcomingshows:api --> :core:base
  :data:upcomingshows:api --> :data:database:sqldelight
  :data:upcomingshows:api --> :data:shows:api
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
  :domain:discover --> :core:base
  :domain:discover --> :data:featuredshows:api
  :domain:discover --> :data:genre:api
  :domain:discover --> :data:popularshows:api
  :domain:discover --> :data:shows:api
  :domain:discover --> :data:topratedshows:api
  :domain:discover --> :data:trendingshows:api
  :domain:discover --> :data:upcomingshows:api
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
  :domain:genre --> :core:base
  :domain:genre --> :data:genre:api
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
  :features:discover:nav --> :navigation:api
  :features:discover:presenter --> :core:base
  :features:discover:presenter --> :core:logger:api
  :features:discover:presenter --> :core:view
  :features:discover:presenter --> :data:account-manager:api
  :features:discover:presenter -.-> :data:datastore:api
  :features:discover:presenter -.-> :data:start-watching:api
  :features:discover:presenter --> :domain:continue-watching
  :features:discover:presenter --> :domain:discover
  :features:discover:presenter --> :domain:episode
  :features:discover:presenter --> :domain:followedshows
  :features:discover:presenter --> :domain:genre
  :features:discover:presenter --> :domain:showdetails
  :features:discover:presenter --> :domain:start-watching
  :features:discover:presenter --> :features:discover:nav
  :features:discover:presenter -.-> :features:episode-sheet:nav
  :features:discover:presenter -.-> :features:home:nav
  :features:discover:presenter -.-> :features:more-shows:nav
  :features:discover:presenter -.-> :features:progress:nav
  :features:discover:presenter -.-> :features:search:nav
  :features:discover:presenter -.-> :features:season-details:nav
  :features:discover:presenter -.-> :features:show-details:nav
  :features:discover:presenter --> :i18n:api
  :features:discover:presenter --> :navigation:api
  :features:episode-sheet:nav --> :navigation:api
  :features:home:nav --> :navigation:api
  :features:more-shows:nav --> :navigation:api
  :features:progress:nav --> :navigation:api
  :features:search:nav --> :navigation:api
  :features:season-details:nav --> :navigation:api
  :features:show-details:nav --> :navigation:api
  :i18n:api --> :i18n:generator

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
