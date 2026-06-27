# `:data:continue-watching:implementation`

## Module dependency graph

<!--region graph-->
```mermaid
graph TB
  subgraph :api:trakt
    direction TB
    :api:trakt:api[api]:::multiplatform
  end
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
  subgraph :data:continue-watching
    direction TB
    :data:continue-watching:api[api]:::multiplatform
    :data:continue-watching:implementation[implementation]:::multiplatform
  end
  subgraph :data:database
    direction TB
    :data:database:sqldelight[sqldelight]:::multiplatform
  end
  subgraph :data:datastore
    direction TB
    :data:datastore:api[api]:::multiplatform
  end
  subgraph :data:request-manager
    direction TB
    :data:request-manager:api[api]:::multiplatform
  end
  subgraph :data:shows
    direction TB
    :data:shows:api[api]:::multiplatform
  end
  subgraph :data:sync-activity
    direction TB
    :data:sync-activity:api[api]:::multiplatform
  end
  subgraph :i18n
    direction TB
    :i18n:generator[generator]:::multiplatform
  end

  :api:trakt:api --> :core:network-util:api
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:view --> :core:logger:api
  :data:continue-watching:implementation --> :api:trakt:api
  :data:continue-watching:implementation --> :core:base
  :data:continue-watching:implementation --> :core:logger:api
  :data:continue-watching:implementation -.-> :core:network-util:api
  :data:continue-watching:implementation --> :core:util:api
  :data:continue-watching:implementation --> :data:continue-watching:api
  :data:continue-watching:implementation --> :data:database:sqldelight
  :data:continue-watching:implementation --> :data:datastore:api
  :data:continue-watching:implementation --> :data:request-manager:api
  :data:continue-watching:implementation --> :data:shows:api
  :data:continue-watching:implementation --> :data:sync-activity:api
  :data:database:sqldelight --> :core:logger:api
  :data:datastore:api --> :i18n:generator
  :data:shows:api --> :data:account-manager:api
  :data:shows:api --> :data:database:sqldelight
  :data:sync-activity:api --> :core:network-util:api
  :data:sync-activity:api --> :data:account-manager:api

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
