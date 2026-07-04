# `:features:feature-flags:ui`

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
  subgraph :core:feature-flags
    direction TB
    :core:feature-flags:api[api]:::multiplatform
  end
  subgraph :core:logger
    direction TB
    :core:logger:api[api]:::multiplatform
  end
  subgraph :core:util
    direction TB
    :core:util:api[api]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:feature-flags[feature-flags]:::multiplatform
    :domain:theme[theme]:::multiplatform
  end
  subgraph :features:feature-flags
    direction TB
    :features:feature-flags:nav[nav]:::multiplatform
    :features:feature-flags:presenter[presenter]:::multiplatform
    :features:feature-flags:ui[ui]:::android-library
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
  :domain:feature-flags --> :core:base
  :domain:feature-flags --> :core:feature-flags:api
  :domain:theme --> :i18n:generator
  :features:feature-flags:nav --> :navigation:api
  :features:feature-flags:presenter --> :core:base
  :features:feature-flags:presenter --> :core:feature-flags:api
  :features:feature-flags:presenter --> :core:util:api
  :features:feature-flags:presenter --> :domain:feature-flags
  :features:feature-flags:presenter --> :features:feature-flags:nav
  :features:feature-flags:presenter --> :i18n:api
  :features:feature-flags:presenter -.-> :i18n:generator
  :features:feature-flags:presenter --> :navigation:api
  :features:feature-flags:ui -.-> :android-designsystem
  :features:feature-flags:ui --> :core:base
  :features:feature-flags:ui -.-> :core:feature-flags:api
  :features:feature-flags:ui --> :features:feature-flags:presenter
  :features:feature-flags:ui -.-> :i18n:generator
  :features:feature-flags:ui --> :navigation:api
  :features:feature-flags:ui --> :navigation:ui
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
