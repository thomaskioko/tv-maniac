# `:features:lists:ui`

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
  subgraph :data:showdetails
    direction TB
    :data:showdetails:api[api]:::multiplatform
  end
  subgraph :data:user
    direction TB
    :data:user:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:lists[lists]:::multiplatform
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:lists
    direction TB
    :features:lists:nav[nav]:::multiplatform
    :features:lists:presenter[presenter]:::multiplatform
    :features:lists:ui[ui]:::android-library
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
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
  :core:network-util:api --> :core:logger:api
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:database:sqldelight --> :core:logger:api
  :data:showdetails:api --> :data:database:sqldelight
  :data:user:api --> :core:network-util:api
  :data:user:api --> :data:account-manager:api
  :data:user:api --> :data:database:sqldelight
  :domain:lists --> :core:base
  :domain:lists --> :data:account-manager:api
  :domain:lists --> :data:lists:api
  :domain:lists --> :data:showdetails:api
  :domain:lists --> :data:user:api
  :domain:theme --> :i18n:generator
  :features:lists:nav --> :navigation:api
  :features:lists:presenter --> :core:base
  :features:lists:presenter --> :core:logger:api
  :features:lists:presenter --> :core:view
  :features:lists:presenter -.-> :data:lists:api
  :features:lists:presenter --> :domain:lists
  :features:lists:presenter --> :features:lists:nav
  :features:lists:presenter -.-> :features:show-details:nav
  :features:lists:presenter --> :i18n:api
  :features:lists:presenter --> :navigation:api
  :features:lists:ui -.-> :android-designsystem
  :features:lists:ui --> :core:base
  :features:lists:ui -.-> :core:test-tags
  :features:lists:ui --> :features:lists:presenter
  :features:lists:ui -.-> :i18n:generator
  :features:lists:ui --> :navigation:api
  :features:lists:ui --> :navigation:ui
  :features:show-details:nav --> :navigation:api
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
