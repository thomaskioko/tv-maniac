# `:features:statistics:ui`

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
  subgraph :data:subscription
    direction TB
    :data:subscription:api[api]:::multiplatform
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
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
  end
  subgraph :features:statistics
    direction TB
    :features:statistics:nav[nav]:::multiplatform
    :features:statistics:presenter[presenter]:::multiplatform
    :features:statistics:ui[ui]:::android-library
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
  :data:database:sqldelight --> :core:logger:api
  :data:episode:api --> :data:account-manager:api
  :data:episode:api --> :data:database:sqldelight
  :data:episode:api --> :data:followedshows:api
  :data:episode:api --> :data:upnext:api
  :data:ratings:api --> :core:network-util:api
  :data:ratings:api --> :data:account-manager:api
  :data:ratings:api --> :data:database:sqldelight
  :data:ratings:api --> :data:followedshows:api
  :data:watch-status:api --> :data:database:sqldelight
  :domain:statistics --> :core:base
  :domain:statistics --> :core:util:api
  :domain:statistics --> :data:database:sqldelight
  :domain:statistics --> :data:episode:api
  :domain:statistics --> :data:ratings:api
  :domain:statistics --> :data:watch-status:api
  :domain:theme --> :i18n:generator
  :features:show-details:nav --> :navigation:api
  :features:statistics:nav --> :navigation:api
  :features:statistics:presenter --> :core:base
  :features:statistics:presenter --> :core:util:api
  :features:statistics:presenter --> :core:view
  :features:statistics:presenter --> :data:account-manager:api
  :features:statistics:presenter --> :data:database:sqldelight
  :features:statistics:presenter --> :data:episode:api
  :features:statistics:presenter --> :data:subscription:api
  :features:statistics:presenter --> :domain:statistics
  :features:statistics:presenter -.-> :features:show-details:nav
  :features:statistics:presenter --> :features:statistics:nav
  :features:statistics:presenter --> :i18n:api
  :features:statistics:presenter --> :i18n:generator
  :features:statistics:presenter --> :navigation:api
  :features:statistics:ui -.-> :android-designsystem
  :features:statistics:ui --> :core:base
  :features:statistics:ui -.-> :core:test-tags
  :features:statistics:ui -.-> :core:view
  :features:statistics:ui --> :features:statistics:presenter
  :features:statistics:ui -.-> :i18n:generator
  :features:statistics:ui --> :navigation:api
  :features:statistics:ui --> :navigation:ui
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
