# `:api:trakt:implementation`

## Module dependency graph

<!--region graph-->
```mermaid
graph TB
  subgraph :api:trakt
    direction TB
    :api:trakt:api[api]:::multiplatform
    :api:trakt:implementation[implementation]:::multiplatform
  end
  subgraph :core
    direction TB
    :core:base[base]:::multiplatform
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
  subgraph :core:logger
    direction TB
    :core:logger:api[api]:::multiplatform
  end
  subgraph :core:network-util
    direction TB
    :core:network-util:api[api]:::multiplatform
  end
  subgraph :data:account-manager
    direction TB
    :data:account-manager:api[api]:::multiplatform
  end
  subgraph :data:calendar
    direction TB
    :data:calendar:api[api]:::multiplatform
  end
  subgraph :data:database
    direction TB
    :data:database:sqldelight[sqldelight]:::multiplatform
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
  subgraph :data:oauth
    direction TB
    :data:oauth:api[api]:::multiplatform
  end
  subgraph :data:ratings
    direction TB
    :data:ratings:api[api]:::multiplatform
  end
  subgraph :data:start-watching
    direction TB
    :data:start-watching:api[api]:::multiplatform
  end
  subgraph :data:sync-activity
    direction TB
    :data:sync-activity:api[api]:::multiplatform
  end
  subgraph :data:upnext
    direction TB
    :data:upnext:api[api]:::multiplatform
  end
  subgraph :data:user
    direction TB
    :data:user:api[api]:::multiplatform
  end

  :api:trakt:api --> :core:network-util:api
  :api:trakt:implementation --> :api:trakt:api
  :api:trakt:implementation --> :core:appconfig:api
  :api:trakt:implementation -.-> :core:base
  :api:trakt:implementation --> :core:connectivity:api
  :api:trakt:implementation --> :core:logger:api
  :api:trakt:implementation --> :core:network-util:api
  :api:trakt:implementation --> :data:account-manager:api
  :api:trakt:implementation --> :data:calendar:api
  :api:trakt:implementation --> :data:episode:api
  :api:trakt:implementation --> :data:followedshows:api
  :api:trakt:implementation --> :data:library:api
  :api:trakt:implementation --> :data:oauth:api
  :api:trakt:implementation --> :data:ratings:api
  :api:trakt:implementation --> :data:start-watching:api
  :api:trakt:implementation --> :data:sync-activity:api
  :api:trakt:implementation --> :data:user:api
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:calendar:api --> :core:network-util:api
  :data:calendar:api --> :data:account-manager:api
  :data:database:sqldelight --> :core:logger:api
  :data:episode:api --> :data:account-manager:api
  :data:episode:api --> :data:database:sqldelight
  :data:episode:api --> :data:followedshows:api
  :data:episode:api --> :data:upnext:api
  :data:library:api --> :core:network-util:api
  :data:library:api --> :data:account-manager:api
  :data:library:api --> :data:database:sqldelight
  :data:oauth:api --> :data:account-manager:api
  :data:ratings:api --> :core:network-util:api
  :data:ratings:api --> :data:account-manager:api
  :data:ratings:api --> :data:database:sqldelight
  :data:ratings:api --> :data:followedshows:api
  :data:start-watching:api --> :core:network-util:api
  :data:start-watching:api --> :data:account-manager:api
  :data:sync-activity:api --> :core:network-util:api
  :data:sync-activity:api --> :data:account-manager:api
  :data:user:api --> :core:network-util:api
  :data:user:api --> :data:account-manager:api
  :data:user:api --> :data:database:sqldelight

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
