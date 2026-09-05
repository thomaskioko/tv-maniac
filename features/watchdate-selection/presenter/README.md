# `:features:watchdate-selection:presenter`

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
  subgraph :data:rewatch
    direction TB
    :data:rewatch:api[api]:::multiplatform
  end
  subgraph :data:seasons
    direction TB
    :data:seasons:api[api]:::multiplatform
  end
  subgraph :data:showdetails
    direction TB
    :data:showdetails:api[api]:::multiplatform
  end
  subgraph :data:subscription
    direction TB
    :data:subscription:api[api]:::multiplatform
  end
  subgraph :data:upnext
    direction TB
    :data:upnext:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:episode[episode]:::multiplatform
    :domain:ratings[ratings]:::multiplatform
    :domain:rewatch[rewatch]:::multiplatform
  end
  subgraph :features:rating-sheet
    direction TB
    :features:rating-sheet:nav[nav]:::multiplatform
  end
  subgraph :features:watchdate-selection
    direction TB
    :features:watchdate-selection:nav[nav]:::multiplatform
    :features:watchdate-selection:presenter[presenter]:::multiplatform
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
  :data:database:sqldelight --> :core:logger:api
  :data:datastore:api --> :i18n:generator
  :data:episode:api --> :data:account-manager:api
  :data:episode:api --> :data:database:sqldelight
  :data:episode:api --> :data:followedshows:api
  :data:episode:api --> :data:upnext:api
  :data:library:api --> :core:network-util:api
  :data:library:api --> :data:account-manager:api
  :data:library:api --> :data:database:sqldelight
  :data:library:api --> :data:datastore:api
  :data:ratings:api --> :core:network-util:api
  :data:ratings:api --> :data:account-manager:api
  :data:ratings:api --> :data:database:sqldelight
  :data:ratings:api --> :data:followedshows:api
  :data:rewatch:api --> :core:network-util:api
  :data:rewatch:api --> :data:account-manager:api
  :data:seasons:api --> :data:database:sqldelight
  :data:showdetails:api --> :data:database:sqldelight
  :domain:episode --> :core:base
  :domain:episode --> :core:logger:api
  :domain:episode --> :core:syncstate:api
  :domain:episode --> :core:tasks:api
  :domain:episode --> :core:util:api
  :domain:episode -.-> :core:view
  :domain:episode --> :data:account-manager:api
  :domain:episode --> :data:database:sqldelight
  :domain:episode --> :data:datastore:api
  :domain:episode --> :data:episode:api
  :domain:episode --> :data:library:api
  :domain:episode --> :data:rewatch:api
  :domain:episode --> :domain:rewatch
  :domain:ratings --> :core:base
  :domain:ratings --> :data:datastore:api
  :domain:ratings --> :data:episode:api
  :domain:ratings --> :data:ratings:api
  :domain:ratings --> :data:seasons:api
  :domain:ratings --> :data:showdetails:api
  :domain:ratings --> :data:subscription:api
  :domain:rewatch --> :core:base
  :domain:rewatch --> :core:util:api
  :domain:rewatch --> :data:account-manager:api
  :domain:rewatch --> :data:rewatch:api
  :features:rating-sheet:nav --> :data:ratings:api
  :features:rating-sheet:nav --> :navigation:api
  :features:watchdate-selection:nav --> :data:episode:api
  :features:watchdate-selection:nav --> :navigation:api
  :features:watchdate-selection:presenter --> :core:base
  :features:watchdate-selection:presenter --> :core:logger:api
  :features:watchdate-selection:presenter --> :core:util:api
  :features:watchdate-selection:presenter --> :core:view
  :features:watchdate-selection:presenter --> :data:episode:api
  :features:watchdate-selection:presenter --> :data:ratings:api
  :features:watchdate-selection:presenter --> :domain:episode
  :features:watchdate-selection:presenter --> :domain:ratings
  :features:watchdate-selection:presenter --> :domain:rewatch
  :features:watchdate-selection:presenter --> :features:rating-sheet:nav
  :features:watchdate-selection:presenter --> :features:watchdate-selection:nav
  :features:watchdate-selection:presenter --> :i18n:api
  :features:watchdate-selection:presenter -.-> :i18n:generator
  :features:watchdate-selection:presenter --> :navigation:api
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
