# `:domain:statistics`

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
  subgraph :core:logger
    direction TB
    :core:logger:api[api]:::multiplatform
  end
  subgraph :core:network-util
    direction TB
    :core:network-util:api[api]:::multiplatform
  end
  subgraph :core:util
    direction TB
    :core:util:api[api]:::multiplatform
  end
  subgraph :data:account-manager
    direction TB
    :data:account-manager:api[api]:::multiplatform
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
  subgraph :data:ratings
    direction TB
    :data:ratings:api[api]:::multiplatform
  end
  subgraph :data:rewatch
    direction TB
    :data:rewatch:api[api]:::multiplatform
  end
  subgraph :data:upnext
    direction TB
    :data:upnext:api[api]:::multiplatform
  end
  subgraph :data:watch-status
    direction TB
    :data:watch-status:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:statistics[statistics]:::multiplatform
  end

  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:database:sqldelight --> :core:logger:api
  :data:episode:api --> :data:account-manager:api
  :data:episode:api --> :data:database:sqldelight
  :data:episode:api --> :data:followedshows:api
  :data:episode:api --> :data:upnext:api
  :data:ratings:api --> :core:network-util:api
  :data:ratings:api --> :data:account-manager:api
  :data:ratings:api --> :data:database:sqldelight
  :data:ratings:api --> :data:followedshows:api
  :data:rewatch:api --> :core:network-util:api
  :data:rewatch:api --> :data:account-manager:api
  :data:watch-status:api --> :data:database:sqldelight
  :domain:statistics --> :core:base
  :domain:statistics --> :core:util:api
  :domain:statistics --> :data:database:sqldelight
  :domain:statistics --> :data:episode:api
  :domain:statistics --> :data:ratings:api
  :domain:statistics --> :data:rewatch:api
  :domain:statistics --> :data:watch-status:api

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
