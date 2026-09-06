# `:features:lists:presenter`

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
  subgraph :data:lists
    direction TB
    :data:lists:api[api]:::multiplatform
  end
  subgraph :data:user
    direction TB
    :data:user:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:lists[lists]:::multiplatform
  end
  subgraph :features:lists
    direction TB
    :features:lists:nav[nav]:::multiplatform
    :features:lists:presenter[presenter]:::multiplatform
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
  :core:network-util:api --> :core:logger:api
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:database:sqldelight --> :core:logger:api
  :data:user:api --> :core:network-util:api
  :data:user:api --> :data:account-manager:api
  :data:user:api --> :data:database:sqldelight
  :domain:lists --> :core:base
  :domain:lists --> :data:account-manager:api
  :domain:lists --> :data:lists:api
  :domain:lists --> :data:user:api
  :features:lists:nav --> :navigation:api
  :features:lists:presenter --> :core:base
  :features:lists:presenter --> :core:logger:api
  :features:lists:presenter --> :core:view
  :features:lists:presenter -.-> :data:lists:api
  :features:lists:presenter --> :domain:lists
  :features:lists:presenter --> :features:lists:nav
  :features:lists:presenter --> :i18n:api
  :features:lists:presenter --> :navigation:api
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
