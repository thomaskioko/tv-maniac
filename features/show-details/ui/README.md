# `:features:show-details:ui`

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
  subgraph :data:ratings
    direction TB
    :data:ratings:api[api]:::multiplatform
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
    :domain:episode[episode]:::multiplatform
    :domain:notifications[notifications]:::multiplatform
    :domain:ratings[ratings]:::multiplatform
    :domain:showdetails[showdetails]:::multiplatform
    :domain:similarshows[similarshows]:::multiplatform
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:rating-sheet
    direction TB
    :features:rating-sheet:nav[nav]:::multiplatform
  end
  subgraph :features:root
    direction TB
    :features:root:nav[nav]:::multiplatform
  end
  subgraph :features:season-details
    direction TB
    :features:season-details:nav[nav]:::multiplatform
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
    :features:show-details:presenter[presenter]:::multiplatform
    :features:show-details:ui[ui]:::android-library
  end
  subgraph :features:show-list
    direction TB
    :features:show-list:nav[nav]:::multiplatform
  end
  subgraph :features:trailers
    direction TB
    :features:trailers:nav[nav]:::multiplatform
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
  :data:ratings:api --> :core:network-util:api
  :data:ratings:api --> :data:account-manager:api
  :data:ratings:api --> :data:database:sqldelight
  :data:ratings:api --> :data:followedshows:api
  :data:seasondetails:api --> :data:database:sqldelight
  :data:seasons:api --> :data:database:sqldelight
  :data:showdetails:api --> :data:database:sqldelight
  :data:similar:api --> :data:database:sqldelight
  :data:trailers:api --> :data:database:sqldelight
  :data:watchproviders:api --> :data:database:sqldelight
  :domain:episode --> :core:base
  :domain:episode --> :core:logger:api
  :domain:episode --> :core:syncstate:api
  :domain:episode --> :core:tasks:api
  :domain:episode -.-> :core:view
  :domain:episode --> :data:account-manager:api
  :domain:episode --> :data:database:sqldelight
  :domain:episode --> :data:episode:api
  :domain:episode --> :data:library:api
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
  :domain:ratings --> :core:base
  :domain:ratings --> :data:ratings:api
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
  :domain:similarshows --> :core:base
  :domain:similarshows --> :data:similar:api
  :domain:theme --> :i18n:generator
  :features:rating-sheet:nav --> :data:ratings:api
  :features:rating-sheet:nav --> :navigation:api
  :features:root:nav --> :domain:theme
  :features:season-details:nav --> :navigation:api
  :features:show-details:nav --> :navigation:api
  :features:show-details:presenter --> :core:base
  :features:show-details:presenter --> :core:logger:api
  :features:show-details:presenter --> :core:notifications:api
  :features:show-details:presenter --> :core:view
  :features:show-details:presenter --> :data:account-manager:api
  :features:show-details:presenter --> :data:episode:api
  :features:show-details:presenter --> :data:followedshows:api
  :features:show-details:presenter --> :data:seasondetails:api
  :features:show-details:presenter --> :domain:episode
  :features:show-details:presenter --> :domain:notifications
  :features:show-details:presenter --> :domain:ratings
  :features:show-details:presenter --> :domain:showdetails
  :features:show-details:presenter --> :domain:similarshows
  :features:show-details:presenter --> :features:rating-sheet:nav
  :features:show-details:presenter --> :features:root:nav
  :features:show-details:presenter -.-> :features:season-details:nav
  :features:show-details:presenter --> :features:show-details:nav
  :features:show-details:presenter --> :features:show-list:nav
  :features:show-details:presenter -.-> :features:trailers:nav
  :features:show-details:presenter --> :i18n:api
  :features:show-details:presenter --> :navigation:api
  :features:show-details:ui -.-> :android-designsystem
  :features:show-details:ui --> :core:base
  :features:show-details:ui -.-> :core:test-tags
  :features:show-details:ui -.-> :core:view
  :features:show-details:ui -.-> :features:show-details:nav
  :features:show-details:ui --> :features:show-details:presenter
  :features:show-details:ui -.-> :i18n:generator
  :features:show-details:ui --> :navigation:api
  :features:show-details:ui --> :navigation:ui
  :features:show-list:nav --> :navigation:api
  :features:trailers:nav --> :navigation:api
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
