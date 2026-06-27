# `:data:similar:implementation`

## Module dependency graph

<!--region graph-->
```mermaid
graph TB
  subgraph :api:tmdb
    direction TB
    :api:tmdb:api[api]:::multiplatform
  end
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
  subgraph :data:database
    direction TB
    :data:database:sqldelight[sqldelight]:::multiplatform
  end
  subgraph :data:request-manager
    direction TB
    :data:request-manager:api[api]:::multiplatform
  end
  subgraph :data:shows
    direction TB
    :data:shows:api[api]:::multiplatform
  end
  subgraph :data:similar
    direction TB
    :data:similar:api[api]:::multiplatform
    :data:similar:implementation[implementation]:::multiplatform
  end

  :api:tmdb:api --> :core:network-util:api
  :api:trakt:api --> :core:network-util:api
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:view --> :core:logger:api
  :data:database:sqldelight --> :core:logger:api
  :data:shows:api --> :data:account-manager:api
  :data:shows:api --> :data:database:sqldelight
  :data:similar:api --> :data:database:sqldelight
  :data:similar:implementation --> :api:tmdb:api
  :data:similar:implementation --> :api:trakt:api
  :data:similar:implementation --> :core:base
  :data:similar:implementation -.-> :core:network-util:api
  :data:similar:implementation --> :core:util:api
  :data:similar:implementation --> :data:request-manager:api
  :data:similar:implementation --> :data:shows:api
  :data:similar:implementation --> :data:similar:api

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
