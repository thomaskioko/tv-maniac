# `:features:calendar:ui`

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
  subgraph :data:calendar
    direction TB
    :data:calendar:api[api]:::multiplatform
  end
  subgraph :data:followedshows
    direction TB
    :data:followedshows:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:calendar[calendar]:::multiplatform
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:calendar
    direction TB
    :features:calendar:presenter[presenter]:::multiplatform
    :features:calendar:ui[ui]:::android-library
  end
  subgraph :features:episode-sheet
    direction TB
    :features:episode-sheet:nav[nav]:::multiplatform
  end
  subgraph :features:progress
    direction TB
    :features:progress:nav[nav]:::multiplatform
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

  :android-designsystem -.-> :core:test-tags
  :android-designsystem --> :domain:theme
  :android-designsystem -.-> :i18n:generator
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:network-util:api --> :core:connectivity:api
  :core:view --> :core:logger:api
  :data:calendar:api --> :core:network-util:api
  :data:calendar:api --> :data:account-manager:api
  :domain:calendar --> :core:base
  :domain:calendar --> :core:util:api
  :domain:calendar --> :data:calendar:api
  :domain:calendar --> :data:followedshows:api
  :domain:theme --> :i18n:generator
  :features:calendar:presenter -.-> :core:base
  :features:calendar:presenter --> :core:logger:api
  :features:calendar:presenter --> :core:view
  :features:calendar:presenter --> :data:account-manager:api
  :features:calendar:presenter -.-> :data:calendar:api
  :features:calendar:presenter --> :domain:calendar
  :features:calendar:presenter -.-> :features:episode-sheet:nav
  :features:calendar:presenter --> :features:progress:nav
  :features:calendar:presenter --> :i18n:api
  :features:calendar:presenter --> :i18n:generator
  :features:calendar:presenter --> :navigation:api
  :features:calendar:ui -.-> :android-designsystem
  :features:calendar:ui -.-> :core:test-tags
  :features:calendar:ui -.-> :core:view
  :features:calendar:ui --> :features:calendar:presenter
  :features:calendar:ui -.-> :i18n:generator
  :features:episode-sheet:nav --> :navigation:api
  :features:progress:nav --> :navigation:api
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
