# `:features:home:presenter`

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
  subgraph :data:account-manager
    direction TB
    :data:account-manager:api[api]:::multiplatform
  end
  subgraph :data:database
    direction TB
    :data:database:sqldelight[sqldelight]:::multiplatform
  end
  subgraph :data:traktlists
    direction TB
    :data:traktlists:api[api]:::multiplatform
  end
  subgraph :data:user
    direction TB
    :data:user:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:user[user]:::multiplatform
  end
  subgraph :features:discover
    direction TB
    :features:discover:nav[nav]:::multiplatform
  end
  subgraph :features:home
    direction TB
    :features:home:nav[nav]:::multiplatform
    :features:home:presenter[presenter]:::multiplatform
  end
  subgraph :features:library
    direction TB
    :features:library:nav[nav]:::multiplatform
  end
  subgraph :features:my-shows
    direction TB
    :features:my-shows:nav[nav]:::multiplatform
  end
  subgraph :features:profile
    direction TB
    :features:profile:nav[nav]:::multiplatform
  end
  subgraph :features:progress
    direction TB
    :features:progress:nav[nav]:::multiplatform
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
  :data:user:api --> :core:network-util:api
  :data:user:api --> :data:account-manager:api
  :data:user:api --> :data:database:sqldelight
  :domain:user --> :core:base
  :domain:user --> :data:account-manager:api
  :domain:user --> :data:traktlists:api
  :domain:user --> :data:user:api
  :features:discover:nav --> :navigation:api
  :features:home:nav --> :navigation:api
  :features:home:presenter --> :core:base
  :features:home:presenter --> :domain:user
  :features:home:presenter -.-> :features:discover:nav
  :features:home:presenter --> :features:home:nav
  :features:home:presenter -.-> :features:library:nav
  :features:home:presenter -.-> :features:my-shows:nav
  :features:home:presenter -.-> :features:profile:nav
  :features:home:presenter -.-> :features:progress:nav
  :features:home:presenter --> :navigation:api
  :features:library:nav --> :navigation:api
  :features:my-shows:nav --> :navigation:api
  :features:profile:nav --> :navigation:api
  :features:progress:nav --> :navigation:api

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
