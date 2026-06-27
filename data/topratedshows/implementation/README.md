# `:data:topratedshows:implementation`

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
    :core:paging[paging]:::multiplatform
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
  subgraph :data:topratedshows
    direction TB
    :data:topratedshows:api[api]:::multiplatform
    :data:topratedshows:implementation[implementation]:::multiplatform
  end

  :api:tmdb:api --> :core:network-util:api
  :api:trakt:api --> :core:network-util:api
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:paging -.-> :data:shows:api
  :core:view --> :core:logger:api
  :data:database:sqldelight --> :core:logger:api
  :data:shows:api --> :data:account-manager:api
  :data:shows:api --> :data:database:sqldelight
  :data:topratedshows:api --> :core:base
  :data:topratedshows:api --> :data:database:sqldelight
  :data:topratedshows:api --> :data:shows:api
  :data:topratedshows:implementation --> :api:tmdb:api
  :data:topratedshows:implementation --> :api:trakt:api
  :data:topratedshows:implementation --> :core:base
  :data:topratedshows:implementation --> :core:logger:api
  :data:topratedshows:implementation -.-> :core:network-util:api
  :data:topratedshows:implementation --> :core:paging
  :data:topratedshows:implementation --> :core:util:api
  :data:topratedshows:implementation --> :data:database:sqldelight
  :data:topratedshows:implementation --> :data:request-manager:api
  :data:topratedshows:implementation --> :data:shows:api
  :data:topratedshows:implementation --> :data:topratedshows:api

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
