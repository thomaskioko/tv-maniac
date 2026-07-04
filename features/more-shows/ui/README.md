# `:features:more-shows:ui`

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
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:more-shows
    direction TB
    :features:more-shows:nav[nav]:::multiplatform
    :features:more-shows:presenter[presenter]:::multiplatform
    :features:more-shows:ui[ui]:::android-library
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
  end
  subgraph :i18n
    direction TB
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
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:database:sqldelight --> :core:logger:api
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
  :domain:theme --> :i18n:generator
  :features:more-shows:nav --> :navigation:api
  :features:more-shows:presenter --> :core:base
  :features:more-shows:presenter --> :data:popularshows:api
  :features:more-shows:presenter --> :data:topratedshows:api
  :features:more-shows:presenter --> :data:trendingshows:api
  :features:more-shows:presenter --> :data:upcomingshows:api
  :features:more-shows:presenter --> :features:more-shows:nav
  :features:more-shows:presenter -.-> :features:show-details:nav
  :features:more-shows:presenter --> :navigation:api
  :features:more-shows:ui -.-> :android-designsystem
  :features:more-shows:ui --> :core:base
  :features:more-shows:ui -.-> :core:test-tags
  :features:more-shows:ui --> :features:more-shows:presenter
  :features:more-shows:ui --> :navigation:api
  :features:more-shows:ui --> :navigation:ui
  :features:show-details:nav --> :navigation:api
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
