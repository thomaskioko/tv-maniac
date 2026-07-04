# `:domain:discover`

## Module dependency graph

<!--region graph-->
```mermaid
graph TB
  subgraph :core
    direction TB
    :core:base[base]:::multiplatform
    :core:view[view]:::multiplatform
  end
  subgraph :core:logger
    direction TB
    :core:logger:api[api]:::multiplatform
  end
  subgraph :data:account-manager
    direction TB
    :data:account-manager:api[api]:::multiplatform
  end
  subgraph :data:database
    direction TB
    :data:database:sqldelight[sqldelight]:::multiplatform
  end
  subgraph :data:featuredshows
    direction TB
    :data:featuredshows:api[api]:::multiplatform
  end
  subgraph :data:genre
    direction TB
    :data:genre:api[api]:::multiplatform
  end
  subgraph :data:popularshows
    direction TB
    :data:popularshows:api[api]:::multiplatform
  end
  subgraph :data:shows
    direction TB
    :data:shows:api[api]:::multiplatform
  end
  subgraph :data:topratedshows
    direction TB
    :data:topratedshows:api[api]:::multiplatform
  end
  subgraph :data:trendingshows
    direction TB
    :data:trendingshows:api[api]:::multiplatform
  end
  subgraph :data:upcomingshows
    direction TB
    :data:upcomingshows:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:discover[discover]:::multiplatform
  end

  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:database:sqldelight --> :core:logger:api
  :data:featuredshows:api --> :core:base
  :data:featuredshows:api --> :data:database:sqldelight
  :data:featuredshows:api --> :data:shows:api
  :data:genre:api --> :data:database:sqldelight
  :data:genre:api --> :data:shows:api
  :data:popularshows:api --> :core:base
  :data:popularshows:api --> :data:database:sqldelight
  :data:popularshows:api --> :data:shows:api
  :data:shows:api --> :data:account-manager:api
  :data:shows:api --> :data:database:sqldelight
  :data:topratedshows:api --> :core:base
  :data:topratedshows:api --> :data:database:sqldelight
  :data:topratedshows:api --> :data:shows:api
  :data:trendingshows:api --> :core:base
  :data:trendingshows:api --> :data:database:sqldelight
  :data:trendingshows:api --> :data:shows:api
  :data:upcomingshows:api --> :core:base
  :data:upcomingshows:api --> :data:database:sqldelight
  :data:upcomingshows:api --> :data:shows:api
  :domain:discover --> :core:base
  :domain:discover --> :data:featuredshows:api
  :domain:discover --> :data:genre:api
  :domain:discover --> :data:popularshows:api
  :domain:discover --> :data:shows:api
  :domain:discover --> :data:topratedshows:api
  :domain:discover --> :data:trendingshows:api
  :domain:discover --> :data:upcomingshows:api

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
