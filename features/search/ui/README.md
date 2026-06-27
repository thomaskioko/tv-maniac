# `:features:search:ui`

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
  subgraph :data:genre
    direction TB
    :data:genre:api[api]:::multiplatform
  end
  subgraph :data:search
    direction TB
    :data:search:api[api]:::multiplatform
  end
  subgraph :data:shows
    direction TB
    :data:shows:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:genre[genre]:::multiplatform
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:search
    direction TB
    :features:search:nav[nav]:::multiplatform
    :features:search:presenter[presenter]:::multiplatform
    :features:search:ui[ui]:::android-library
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
  :core:view --> :core:logger:api
  :data:database:sqldelight --> :core:logger:api
  :data:genre:api --> :data:database:sqldelight
  :data:genre:api --> :data:shows:api
  :data:search:api --> :data:shows:api
  :data:shows:api --> :data:account-manager:api
  :data:shows:api --> :data:database:sqldelight
  :domain:genre --> :core:base
  :domain:genre --> :data:genre:api
  :domain:theme --> :i18n:generator
  :features:search:nav --> :navigation:api
  :features:search:presenter --> :core:base
  :features:search:presenter --> :core:logger:api
  :features:search:presenter --> :core:util:api
  :features:search:presenter --> :core:view
  :features:search:presenter --> :data:genre:api
  :features:search:presenter --> :data:search:api
  :features:search:presenter --> :domain:genre
  :features:search:presenter --> :features:search:nav
  :features:search:presenter -.-> :features:show-details:nav
  :features:search:presenter --> :i18n:api
  :features:search:presenter --> :navigation:api
  :features:search:ui -.-> :android-designsystem
  :features:search:ui --> :core:base
  :features:search:ui -.-> :core:test-tags
  :features:search:ui -.-> :core:view
  :features:search:ui -.-> :data:genre:api
  :features:search:ui --> :features:search:presenter
  :features:search:ui -.-> :i18n:generator
  :features:search:ui --> :navigation:api
  :features:search:ui --> :navigation:ui
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
