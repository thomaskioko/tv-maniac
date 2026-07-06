# `:core:integration:infra`

## Module dependency graph

<!--region graph-->
```mermaid
graph TB
  subgraph :api:simkl
    direction TB
    :api:simkl:api[api]:::multiplatform
    :api:simkl:implementation[implementation]:::multiplatform
  end
  subgraph :api:tmdb
    direction TB
    :api:tmdb:api[api]:::multiplatform
    :api:tmdb:implementation[implementation]:::multiplatform
  end
  subgraph :api:trakt
    direction TB
    :api:trakt:api[api]:::multiplatform
    :api:trakt:implementation[implementation]:::multiplatform
  end
  subgraph :core
    direction TB
    :core:base[base]:::multiplatform
    :core:view[view]:::multiplatform
  end
  subgraph :core:appconfig
    direction TB
    :core:appconfig:api[api]:::multiplatform
    :core:appconfig:implementation[implementation]:::multiplatform
  end
  subgraph :core:connectivity
    direction TB
    :core:connectivity:api[api]:::multiplatform
    :core:connectivity:implementation[implementation]:::multiplatform
  end
  subgraph :core:feature-flags
    direction TB
    :core:feature-flags:api[api]:::multiplatform
    :core:feature-flags:implementation[implementation]:::multiplatform
  end
  subgraph :core:imageloading
    direction TB
    :core:imageloading:api[api]:::multiplatform
  end
  subgraph :core:integration
    direction TB
    :core:integration:infra[infra]:::multiplatform
  end
  subgraph :core:locale
    direction TB
    :core:locale:api[api]:::multiplatform
  end
  subgraph :core:logger
    direction TB
    :core:logger:api[api]:::multiplatform
    :core:logger:implementation[implementation]:::multiplatform
  end
  subgraph :core:network-util
    direction TB
    :core:network-util:api[api]:::multiplatform
  end
  subgraph :core:notifications
    direction TB
    :core:notifications:api[api]:::multiplatform
    :core:notifications:implementation[implementation]:::multiplatform
  end
  subgraph :core:syncstate
    direction TB
    :core:syncstate:api[api]:::multiplatform
    :core:syncstate:implementation[implementation]:::multiplatform
  end
  subgraph :core:tasks
    direction TB
    :core:tasks:api[api]:::multiplatform
    :core:tasks:implementation[implementation]:::multiplatform
  end
  subgraph :core:util
    direction TB
    :core:util:api[api]:::multiplatform
    :core:util:implementation[implementation]:::multiplatform
  end
  subgraph :data:account-manager
    direction TB
    :data:account-manager:api[api]:::multiplatform
    :data:account-manager:implementation[implementation]:::multiplatform
  end
  subgraph :data:calendar
    direction TB
    :data:calendar:api[api]:::multiplatform
    :data:calendar:implementation[implementation]:::multiplatform
  end
  subgraph :data:cast
    direction TB
    :data:cast:api[api]:::multiplatform
    :data:cast:implementation[implementation]:::multiplatform
  end
  subgraph :data:continue-watching
    direction TB
    :data:continue-watching:api[api]:::multiplatform
    :data:continue-watching:implementation[implementation]:::multiplatform
  end
  subgraph :data:database
    direction TB
    :data:database:sqldelight[sqldelight]:::multiplatform
  end
  subgraph :data:datastore
    direction TB
    :data:datastore:api[api]:::multiplatform
    :data:datastore:implementation[implementation]:::multiplatform
  end
  subgraph :data:episode
    direction TB
    :data:episode:api[api]:::multiplatform
    :data:episode:implementation[implementation]:::multiplatform
  end
  subgraph :data:followedshows
    direction TB
    :data:followedshows:api[api]:::multiplatform
    :data:followedshows:implementation[implementation]:::multiplatform
  end
  subgraph :data:library
    direction TB
    :data:library:api[api]:::multiplatform
    :data:library:implementation[implementation]:::multiplatform
  end
  subgraph :data:logout
    direction TB
    :data:logout:api[api]:::multiplatform
    :data:logout:implementation[implementation]:::multiplatform
  end
  subgraph :data:oauth
    direction TB
    :data:oauth:api[api]:::multiplatform
    :data:oauth:implementation[implementation]:::multiplatform
  end
  subgraph :data:ratings
    direction TB
    :data:ratings:api[api]:::multiplatform
    :data:ratings:implementation[implementation]:::multiplatform
  end
  subgraph :data:request-manager
    direction TB
    :data:request-manager:api[api]:::multiplatform
    :data:request-manager:implementation[implementation]:::multiplatform
  end
  subgraph :data:seasondetails
    direction TB
    :data:seasondetails:api[api]:::multiplatform
    :data:seasondetails:implementation[implementation]:::multiplatform
  end
  subgraph :data:seasons
    direction TB
    :data:seasons:api[api]:::multiplatform
    :data:seasons:implementation[implementation]:::multiplatform
  end
  subgraph :data:showdetails
    direction TB
    :data:showdetails:api[api]:::multiplatform
    :data:showdetails:implementation[implementation]:::multiplatform
  end
  subgraph :data:shows
    direction TB
    :data:shows:api[api]:::multiplatform
    :data:shows:implementation[implementation]:::multiplatform
  end
  subgraph :data:similar
    direction TB
    :data:similar:api[api]:::multiplatform
  end
  subgraph :data:simklauth
    direction TB
    :data:simklauth:implementation[implementation]:::multiplatform
  end
  subgraph :data:start-watching
    direction TB
    :data:start-watching:api[api]:::multiplatform
    :data:start-watching:implementation[implementation]:::multiplatform
  end
  subgraph :data:subscription
    direction TB
    :data:subscription:api[api]:::multiplatform
    :data:subscription:implementation[implementation]:::multiplatform
  end
  subgraph :data:sync-activity
    direction TB
    :data:sync-activity:api[api]:::multiplatform
    :data:sync-activity:implementation[implementation]:::multiplatform
  end
  subgraph :data:trailers
    direction TB
    :data:trailers:api[api]:::multiplatform
  end
  subgraph :data:traktauth
    direction TB
    :data:traktauth:implementation[implementation]:::multiplatform
  end
  subgraph :data:traktlists
    direction TB
    :data:traktlists:api[api]:::multiplatform
  end
  subgraph :data:upnext
    direction TB
    :data:upnext:api[api]:::multiplatform
  end
  subgraph :data:user
    direction TB
    :data:user:api[api]:::multiplatform
    :data:user:implementation[implementation]:::multiplatform
  end
  subgraph :data:watch-status
    direction TB
    :data:watch-status:api[api]:::multiplatform
    :data:watch-status:implementation[implementation]:::multiplatform
  end
  subgraph :data:watchproviders
    direction TB
    :data:watchproviders:api[api]:::multiplatform
    :data:watchproviders:implementation[implementation]:::multiplatform
  end
  subgraph :domain
    direction TB
    :domain:account-switcher[account-switcher]:::multiplatform
    :domain:continue-watching[continue-watching]:::multiplatform
    :domain:episode[episode]:::multiplatform
    :domain:library[library]:::multiplatform
    :domain:logout[logout]:::multiplatform
    :domain:notifications[notifications]:::multiplatform
    :domain:settings[settings]:::multiplatform
    :domain:showdetails[showdetails]:::multiplatform
    :domain:sync-activity[sync-activity]:::multiplatform
    :domain:theme[theme]:::multiplatform
    :domain:user[user]:::multiplatform
  end
  subgraph :features:debug
    direction TB
    :features:debug:nav[nav]:::multiplatform
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
  subgraph :features:root
    direction TB
    :features:root:nav[nav]:::multiplatform
    :features:root:presenter[presenter]:::multiplatform
  end
  subgraph :features:season-details
    direction TB
    :features:season-details:nav[nav]:::multiplatform
  end
  subgraph :features:settings
    direction TB
    :features:settings:nav[nav]:::multiplatform
    :features:settings:presenter[presenter]:::multiplatform
  end
  subgraph :features:show-details
    direction TB
    :features:show-details:nav[nav]:::multiplatform
  end
  subgraph :i18n
    direction TB
    :i18n:api[api]:::multiplatform
    :i18n:generator[generator]:::multiplatform
    :i18n:implementation[implementation]:::multiplatform
  end
  subgraph :navigation
    direction TB
    :navigation:api[api]:::multiplatform
    :navigation:implementation[implementation]:::multiplatform
  end

  :api:simkl:api --> :core:network-util:api
  :api:simkl:implementation --> :api:simkl:api
  :api:simkl:implementation --> :core:appconfig:api
  :api:simkl:implementation -.-> :core:base
  :api:simkl:implementation --> :core:logger:api
  :api:simkl:implementation --> :core:network-util:api
  :api:simkl:implementation --> :data:account-manager:api
  :api:simkl:implementation --> :data:calendar:api
  :api:simkl:implementation --> :data:episode:api
  :api:simkl:implementation --> :data:followedshows:api
  :api:simkl:implementation --> :data:library:api
  :api:simkl:implementation --> :data:oauth:api
  :api:simkl:implementation --> :data:ratings:api
  :api:simkl:implementation --> :data:start-watching:api
  :api:simkl:implementation --> :data:sync-activity:api
  :api:simkl:implementation --> :data:user:api
  :api:tmdb:api --> :core:network-util:api
  :api:tmdb:implementation --> :api:tmdb:api
  :api:tmdb:implementation --> :core:appconfig:api
  :api:tmdb:implementation -.-> :core:base
  :api:tmdb:implementation --> :core:connectivity:api
  :api:tmdb:implementation --> :core:logger:api
  :api:tmdb:implementation --> :core:network-util:api
  :api:trakt:api --> :core:network-util:api
  :api:trakt:implementation --> :api:trakt:api
  :api:trakt:implementation --> :core:appconfig:api
  :api:trakt:implementation -.-> :core:base
  :api:trakt:implementation --> :core:connectivity:api
  :api:trakt:implementation --> :core:logger:api
  :api:trakt:implementation --> :core:network-util:api
  :api:trakt:implementation --> :data:account-manager:api
  :api:trakt:implementation --> :data:calendar:api
  :api:trakt:implementation --> :data:episode:api
  :api:trakt:implementation --> :data:followedshows:api
  :api:trakt:implementation --> :data:library:api
  :api:trakt:implementation --> :data:oauth:api
  :api:trakt:implementation --> :data:ratings:api
  :api:trakt:implementation --> :data:start-watching:api
  :api:trakt:implementation --> :data:sync-activity:api
  :api:trakt:implementation --> :data:user:api
  :core:appconfig:implementation --> :api:tmdb:api
  :core:appconfig:implementation --> :api:trakt:api
  :core:appconfig:implementation --> :core:appconfig:api
  :core:appconfig:implementation -.-> :core:base
  :core:base --> :core:logger:api
  :core:base --> :core:view
  :core:connectivity:implementation -.-> :core:base
  :core:connectivity:implementation --> :core:connectivity:api
  :core:feature-flags:implementation --> :core:appconfig:api
  :core:feature-flags:implementation --> :core:base
  :core:feature-flags:implementation --> :core:feature-flags:api
  :core:feature-flags:implementation --> :core:logger:api
  :core:imageloading:api --> :domain:theme
  :core:integration:infra --> :api:simkl:implementation
  :core:integration:infra --> :api:tmdb:api
  :core:integration:infra --> :api:tmdb:implementation
  :core:integration:infra --> :api:trakt:api
  :core:integration:infra --> :api:trakt:implementation
  :core:integration:infra --> :core:appconfig:api
  :core:integration:infra --> :core:appconfig:implementation
  :core:integration:infra --> :core:base
  :core:integration:infra --> :core:connectivity:api
  :core:integration:infra --> :core:connectivity:implementation
  :core:integration:infra --> :core:feature-flags:api
  :core:integration:infra --> :core:feature-flags:implementation
  :core:integration:infra --> :core:locale:api
  :core:integration:infra --> :core:logger:api
  :core:integration:infra --> :core:logger:implementation
  :core:integration:infra --> :core:notifications:implementation
  :core:integration:infra --> :core:syncstate:api
  :core:integration:infra --> :core:syncstate:implementation
  :core:integration:infra --> :core:tasks:implementation
  :core:integration:infra --> :core:util:api
  :core:integration:infra --> :core:util:implementation
  :core:integration:infra --> :data:account-manager:implementation
  :core:integration:infra --> :data:calendar:implementation
  :core:integration:infra --> :data:cast:implementation
  :core:integration:infra --> :data:continue-watching:implementation
  :core:integration:infra --> :data:database:sqldelight
  :core:integration:infra --> :data:datastore:api
  :core:integration:infra --> :data:datastore:implementation
  :core:integration:infra --> :data:episode:implementation
  :core:integration:infra --> :data:followedshows:implementation
  :core:integration:infra --> :data:library:implementation
  :core:integration:infra --> :data:logout:implementation
  :core:integration:infra --> :data:oauth:implementation
  :core:integration:infra --> :data:ratings:implementation
  :core:integration:infra --> :data:request-manager:implementation
  :core:integration:infra --> :data:seasondetails:implementation
  :core:integration:infra --> :data:seasons:implementation
  :core:integration:infra --> :data:showdetails:implementation
  :core:integration:infra --> :data:shows:implementation
  :core:integration:infra --> :data:simklauth:implementation
  :core:integration:infra --> :data:start-watching:implementation
  :core:integration:infra --> :data:subscription:api
  :core:integration:infra --> :data:subscription:implementation
  :core:integration:infra --> :data:sync-activity:implementation
  :core:integration:infra --> :data:traktauth:implementation
  :core:integration:infra --> :data:user:api
  :core:integration:infra --> :data:user:implementation
  :core:integration:infra --> :data:watch-status:implementation
  :core:integration:infra --> :data:watchproviders:implementation
  :core:integration:infra --> :domain:continue-watching
  :core:integration:infra --> :domain:episode
  :core:integration:infra --> :domain:library
  :core:integration:infra --> :domain:logout
  :core:integration:infra --> :domain:notifications
  :core:integration:infra --> :domain:user
  :core:integration:infra --> :features:discover:nav
  :core:integration:infra --> :features:home:nav
  :core:integration:infra --> :features:home:presenter
  :core:integration:infra --> :features:library:nav
  :core:integration:infra --> :features:my-shows:nav
  :core:integration:infra --> :features:profile:nav
  :core:integration:infra --> :features:progress:nav
  :core:integration:infra --> :features:root:presenter
  :core:integration:infra --> :i18n:implementation
  :core:integration:infra --> :navigation:api
  :core:integration:infra --> :navigation:implementation
  :core:logger:implementation --> :core:appconfig:api
  :core:logger:implementation --> :core:base
  :core:logger:implementation --> :core:logger:api
  :core:network-util:api --> :core:connectivity:api
  :core:notifications:implementation -.-> :core:base
  :core:notifications:implementation --> :core:logger:api
  :core:notifications:implementation --> :core:notifications:api
  :core:notifications:implementation --> :core:util:api
  :core:syncstate:implementation --> :core:syncstate:api
  :core:tasks:implementation --> :core:logger:api
  :core:tasks:implementation --> :core:tasks:api
  :core:util:implementation --> :core:util:api
  :core:view --> :core:logger:api
  :data:account-manager:api --> :data:database:sqldelight
  :data:account-manager:implementation --> :core:base
  :data:account-manager:implementation --> :data:account-manager:api
  :data:calendar:api --> :core:network-util:api
  :data:calendar:api --> :data:account-manager:api
  :data:calendar:implementation --> :core:base
  :data:calendar:implementation -.-> :core:network-util:api
  :data:calendar:implementation --> :core:syncstate:api
  :data:calendar:implementation --> :data:account-manager:api
  :data:calendar:implementation --> :data:calendar:api
  :data:calendar:implementation --> :data:database:sqldelight
  :data:calendar:implementation --> :data:followedshows:api
  :data:calendar:implementation --> :data:request-manager:api
  :data:calendar:implementation --> :data:shows:api
  :data:cast:api --> :data:database:sqldelight
  :data:cast:implementation --> :api:tmdb:api
  :data:cast:implementation --> :api:trakt:api
  :data:cast:implementation --> :core:base
  :data:cast:implementation -.-> :core:network-util:api
  :data:cast:implementation --> :core:util:api
  :data:cast:implementation --> :data:cast:api
  :data:cast:implementation --> :data:database:sqldelight
  :data:cast:implementation --> :data:request-manager:api
  :data:cast:implementation --> :data:shows:api
  :data:continue-watching:implementation --> :api:trakt:api
  :data:continue-watching:implementation --> :core:base
  :data:continue-watching:implementation --> :core:logger:api
  :data:continue-watching:implementation -.-> :core:network-util:api
  :data:continue-watching:implementation --> :core:util:api
  :data:continue-watching:implementation --> :data:continue-watching:api
  :data:continue-watching:implementation --> :data:database:sqldelight
  :data:continue-watching:implementation --> :data:datastore:api
  :data:continue-watching:implementation --> :data:request-manager:api
  :data:continue-watching:implementation --> :data:shows:api
  :data:continue-watching:implementation --> :data:sync-activity:api
  :data:database:sqldelight --> :core:logger:api
  :data:datastore:api --> :i18n:generator
  :data:datastore:implementation -.-> :core:base
  :data:datastore:implementation --> :core:imageloading:api
  :data:datastore:implementation --> :core:locale:api
  :data:datastore:implementation --> :core:logger:api
  :data:datastore:implementation --> :data:datastore:api
  :data:episode:api --> :data:account-manager:api
  :data:episode:api --> :data:database:sqldelight
  :data:episode:api --> :data:followedshows:api
  :data:episode:api --> :data:upnext:api
  :data:episode:implementation --> :core:base
  :data:episode:implementation --> :core:logger:api
  :data:episode:implementation -.-> :core:network-util:api
  :data:episode:implementation --> :core:syncstate:api
  :data:episode:implementation --> :core:util:api
  :data:episode:implementation --> :data:account-manager:api
  :data:episode:implementation --> :data:calendar:api
  :data:episode:implementation --> :data:database:sqldelight
  :data:episode:implementation --> :data:datastore:api
  :data:episode:implementation --> :data:episode:api
  :data:episode:implementation --> :data:followedshows:api
  :data:episode:implementation --> :data:request-manager:api
  :data:episode:implementation --> :data:shows:api
  :data:episode:implementation --> :data:sync-activity:api
  :data:episode:implementation --> :data:upnext:api
  :data:episode:implementation --> :data:watch-status:api
  :data:followedshows:implementation --> :core:base
  :data:followedshows:implementation --> :core:logger:api
  :data:followedshows:implementation --> :core:util:api
  :data:followedshows:implementation --> :data:database:sqldelight
  :data:followedshows:implementation --> :data:followedshows:api
  :data:library:api --> :core:network-util:api
  :data:library:api --> :data:account-manager:api
  :data:library:api --> :data:database:sqldelight
  :data:library:implementation --> :api:tmdb:api
  :data:library:implementation --> :core:base
  :data:library:implementation --> :core:logger:api
  :data:library:implementation --> :core:network-util:api
  :data:library:implementation --> :core:syncstate:api
  :data:library:implementation --> :core:util:api
  :data:library:implementation --> :data:account-manager:api
  :data:library:implementation --> :data:database:sqldelight
  :data:library:implementation --> :data:datastore:api
  :data:library:implementation --> :data:followedshows:api
  :data:library:implementation --> :data:library:api
  :data:library:implementation --> :data:request-manager:api
  :data:library:implementation --> :data:shows:api
  :data:library:implementation --> :data:sync-activity:api
  :data:library:implementation --> :data:watchproviders:api
  :data:logout:implementation --> :data:database:sqldelight
  :data:logout:implementation --> :data:logout:api
  :data:logout:implementation --> :data:ratings:api
  :data:logout:implementation --> :data:request-manager:api
  :data:logout:implementation --> :data:sync-activity:api
  :data:logout:implementation --> :data:user:api
  :data:oauth:api --> :data:account-manager:api
  :data:oauth:implementation --> :core:base
  :data:oauth:implementation --> :core:logger:api
  :data:oauth:implementation --> :core:util:api
  :data:oauth:implementation --> :data:account-manager:api
  :data:oauth:implementation --> :data:datastore:api
  :data:oauth:implementation --> :data:oauth:api
  :data:oauth:implementation --> :data:request-manager:api
  :data:ratings:api --> :core:network-util:api
  :data:ratings:api --> :data:account-manager:api
  :data:ratings:api --> :data:database:sqldelight
  :data:ratings:api --> :data:followedshows:api
  :data:ratings:implementation --> :core:base
  :data:ratings:implementation --> :core:logger:api
  :data:ratings:implementation --> :core:network-util:api
  :data:ratings:implementation --> :core:syncstate:api
  :data:ratings:implementation --> :core:util:api
  :data:ratings:implementation --> :data:account-manager:api
  :data:ratings:implementation --> :data:database:sqldelight
  :data:ratings:implementation --> :data:followedshows:api
  :data:ratings:implementation --> :data:ratings:api
  :data:ratings:implementation --> :data:request-manager:api
  :data:ratings:implementation --> :data:shows:api
  :data:request-manager:implementation --> :core:util:api
  :data:request-manager:implementation --> :data:database:sqldelight
  :data:request-manager:implementation --> :data:request-manager:api
  :data:seasondetails:api --> :data:database:sqldelight
  :data:seasondetails:implementation --> :api:tmdb:api
  :data:seasondetails:implementation --> :core:base
  :data:seasondetails:implementation -.-> :core:network-util:api
  :data:seasondetails:implementation --> :core:util:api
  :data:seasondetails:implementation --> :data:cast:api
  :data:seasondetails:implementation --> :data:database:sqldelight
  :data:seasondetails:implementation --> :data:datastore:api
  :data:seasondetails:implementation --> :data:episode:api
  :data:seasondetails:implementation --> :data:request-manager:api
  :data:seasondetails:implementation --> :data:seasondetails:api
  :data:seasondetails:implementation --> :data:seasons:api
  :data:seasons:api --> :data:database:sqldelight
  :data:seasons:implementation --> :core:base
  :data:seasons:implementation --> :data:database:sqldelight
  :data:seasons:implementation --> :data:datastore:api
  :data:seasons:implementation --> :data:seasons:api
  :data:showdetails:api --> :data:database:sqldelight
  :data:showdetails:implementation --> :api:tmdb:api
  :data:showdetails:implementation --> :api:trakt:api
  :data:showdetails:implementation --> :core:base
  :data:showdetails:implementation -.-> :core:network-util:api
  :data:showdetails:implementation --> :core:util:api
  :data:showdetails:implementation --> :data:database:sqldelight
  :data:showdetails:implementation --> :data:request-manager:api
  :data:showdetails:implementation --> :data:seasons:api
  :data:showdetails:implementation --> :data:showdetails:api
  :data:showdetails:implementation --> :data:shows:api
  :data:shows:api --> :data:account-manager:api
  :data:shows:api --> :data:database:sqldelight
  :data:shows:implementation --> :api:tmdb:api
  :data:shows:implementation --> :core:base
  :data:shows:implementation --> :core:logger:api
  :data:shows:implementation --> :data:account-manager:api
  :data:shows:implementation --> :data:database:sqldelight
  :data:shows:implementation --> :data:shows:api
  :data:similar:api --> :data:database:sqldelight
  :data:simklauth:implementation --> :core:appconfig:api
  :data:simklauth:implementation --> :core:base
  :data:simklauth:implementation --> :data:account-manager:api
  :data:simklauth:implementation --> :data:oauth:api
  :data:start-watching:api --> :core:network-util:api
  :data:start-watching:api --> :data:account-manager:api
  :data:start-watching:implementation --> :api:tmdb:api
  :data:start-watching:implementation --> :core:base
  :data:start-watching:implementation --> :core:logger:api
  :data:start-watching:implementation --> :core:network-util:api
  :data:start-watching:implementation --> :core:util:api
  :data:start-watching:implementation --> :data:account-manager:api
  :data:start-watching:implementation --> :data:database:sqldelight
  :data:start-watching:implementation --> :data:followedshows:api
  :data:start-watching:implementation --> :data:request-manager:api
  :data:start-watching:implementation --> :data:shows:api
  :data:start-watching:implementation --> :data:start-watching:api
  :data:subscription:implementation --> :core:appconfig:api
  :data:subscription:implementation --> :core:feature-flags:api
  :data:subscription:implementation --> :data:datastore:api
  :data:subscription:implementation --> :data:subscription:api
  :data:sync-activity:api --> :core:network-util:api
  :data:sync-activity:api --> :data:account-manager:api
  :data:sync-activity:implementation --> :core:base
  :data:sync-activity:implementation --> :core:logger:api
  :data:sync-activity:implementation --> :core:network-util:api
  :data:sync-activity:implementation --> :core:util:api
  :data:sync-activity:implementation --> :data:account-manager:api
  :data:sync-activity:implementation --> :data:database:sqldelight
  :data:sync-activity:implementation --> :data:request-manager:api
  :data:sync-activity:implementation --> :data:sync-activity:api
  :data:trailers:api --> :data:database:sqldelight
  :data:traktauth:implementation --> :api:trakt:api
  :data:traktauth:implementation --> :core:base
  :data:traktauth:implementation --> :core:logger:api
  :data:traktauth:implementation --> :core:tasks:api
  :data:traktauth:implementation --> :data:account-manager:api
  :data:traktauth:implementation --> :data:oauth:api
  :data:user:api --> :core:network-util:api
  :data:user:api --> :data:account-manager:api
  :data:user:api --> :data:database:sqldelight
  :data:user:implementation --> :core:base
  :data:user:implementation -.-> :core:network-util:api
  :data:user:implementation --> :core:util:api
  :data:user:implementation --> :data:account-manager:api
  :data:user:implementation --> :data:database:sqldelight
  :data:user:implementation --> :data:request-manager:api
  :data:user:implementation --> :data:user:api
  :data:watch-status:api --> :data:database:sqldelight
  :data:watch-status:implementation --> :core:base
  :data:watch-status:implementation --> :core:util:api
  :data:watch-status:implementation --> :data:database:sqldelight
  :data:watch-status:implementation --> :data:watch-status:api
  :data:watchproviders:api --> :data:database:sqldelight
  :data:watchproviders:implementation --> :api:tmdb:api
  :data:watchproviders:implementation --> :core:base
  :data:watchproviders:implementation --> :core:util:api
  :data:watchproviders:implementation --> :data:database:sqldelight
  :data:watchproviders:implementation --> :data:request-manager:api
  :data:watchproviders:implementation --> :data:watchproviders:api
  :domain:account-switcher --> :core:base
  :domain:account-switcher --> :data:account-manager:api
  :domain:account-switcher --> :data:episode:api
  :domain:account-switcher --> :data:library:api
  :domain:account-switcher --> :data:logout:api
  :domain:account-switcher --> :data:traktlists:api
  :domain:account-switcher --> :domain:continue-watching
  :domain:account-switcher --> :domain:library
  :domain:account-switcher --> :domain:user
  :domain:continue-watching --> :core:base
  :domain:continue-watching --> :core:feature-flags:api
  :domain:continue-watching --> :core:logger:api
  :domain:continue-watching --> :core:network-util:api
  :domain:continue-watching --> :core:tasks:api
  :domain:continue-watching --> :core:util:api
  :domain:continue-watching --> :data:account-manager:api
  :domain:continue-watching --> :data:continue-watching:api
  :domain:continue-watching --> :data:datastore:api
  :domain:continue-watching --> :data:episode:api
  :domain:continue-watching --> :data:request-manager:api
  :domain:continue-watching --> :data:upnext:api
  :domain:continue-watching -.-> :domain:episode
  :domain:continue-watching --> :domain:showdetails
  :domain:continue-watching --> :domain:sync-activity
  :domain:episode --> :core:base
  :domain:episode --> :core:logger:api
  :domain:episode --> :core:syncstate:api
  :domain:episode --> :core:tasks:api
  :domain:episode -.-> :core:view
  :domain:episode --> :data:account-manager:api
  :domain:episode --> :data:database:sqldelight
  :domain:episode --> :data:episode:api
  :domain:episode --> :data:library:api
  :domain:library --> :core:base
  :domain:library --> :core:logger:api
  :domain:library --> :core:network-util:api
  :domain:library --> :core:syncstate:api
  :domain:library --> :core:tasks:api
  :domain:library --> :core:util:api
  :domain:library --> :data:account-manager:api
  :domain:library --> :data:datastore:api
  :domain:library --> :data:followedshows:api
  :domain:library --> :data:library:api
  :domain:library -.-> :data:request-manager:api
  :domain:library --> :domain:showdetails
  :domain:library --> :domain:sync-activity
  :domain:logout --> :core:base
  :domain:logout --> :data:account-manager:api
  :domain:logout --> :data:datastore:api
  :domain:logout --> :data:logout:api
  :domain:logout --> :data:user:api
  :domain:notifications --> :core:base
  :domain:notifications --> :core:logger:api
  :domain:notifications --> :core:network-util:api
  :domain:notifications --> :core:notifications:api
  :domain:notifications --> :core:syncstate:api
  :domain:notifications --> :core:tasks:api
  :domain:notifications --> :core:util:api
  :domain:notifications --> :data:account-manager:api
  :domain:notifications --> :data:datastore:api
  :domain:notifications --> :data:episode:api
  :domain:notifications --> :data:seasondetails:api
  :domain:notifications --> :data:seasons:api
  :domain:notifications --> :i18n:api
  :domain:settings --> :core:base
  :domain:settings --> :core:util:api
  :domain:settings --> :data:datastore:api
  :domain:settings --> :domain:theme
  :domain:showdetails --> :core:base
  :domain:showdetails --> :core:util:api
  :domain:showdetails --> :data:cast:api
  :domain:showdetails --> :data:episode:api
  :domain:showdetails --> :data:followedshows:api
  :domain:showdetails --> :data:library:api
  :domain:showdetails --> :data:seasondetails:api
  :domain:showdetails --> :data:seasons:api
  :domain:showdetails --> :data:showdetails:api
  :domain:showdetails --> :data:similar:api
  :domain:showdetails --> :data:trailers:api
  :domain:showdetails --> :data:watchproviders:api
  :domain:sync-activity --> :core:base
  :domain:sync-activity --> :data:sync-activity:api
  :domain:theme --> :i18n:generator
  :domain:user --> :core:base
  :domain:user --> :data:account-manager:api
  :domain:user --> :data:traktlists:api
  :domain:user --> :data:user:api
  :features:debug:nav --> :navigation:api
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
  :features:root:nav --> :domain:theme
  :features:root:presenter --> :core:base
  :features:root:presenter --> :core:logger:api
  :features:root:presenter --> :core:syncstate:api
  :features:root:presenter -.-> :core:view
  :features:root:presenter --> :data:account-manager:api
  :features:root:presenter --> :data:datastore:api
  :features:root:presenter --> :domain:logout
  :features:root:presenter -.-> :domain:theme
  :features:root:presenter --> :domain:user
  :features:root:presenter -.-> :features:debug:nav
  :features:root:presenter --> :features:home:presenter
  :features:root:presenter --> :features:root:nav
  :features:root:presenter -.-> :features:season-details:nav
  :features:root:presenter -.-> :features:settings:presenter
  :features:root:presenter -.-> :features:show-details:nav
  :features:root:presenter --> :i18n:api
  :features:root:presenter --> :navigation:api
  :features:season-details:nav --> :navigation:api
  :features:settings:nav --> :navigation:api
  :features:settings:presenter --> :core:appconfig:api
  :features:settings:presenter --> :core:base
  :features:settings:presenter --> :core:feature-flags:api
  :features:settings:presenter --> :core:logger:api
  :features:settings:presenter --> :core:view
  :features:settings:presenter --> :data:account-manager:api
  :features:settings:presenter --> :data:datastore:api
  :features:settings:presenter --> :data:subscription:api
  :features:settings:presenter --> :data:user:api
  :features:settings:presenter --> :domain:account-switcher
  :features:settings:presenter --> :domain:logout
  :features:settings:presenter --> :domain:notifications
  :features:settings:presenter --> :domain:settings
  :features:settings:presenter --> :domain:theme
  :features:settings:presenter -.-> :features:debug:nav
  :features:settings:presenter --> :features:settings:nav
  :features:settings:presenter --> :i18n:api
  :features:settings:presenter --> :i18n:generator
  :features:settings:presenter --> :navigation:api
  :features:show-details:nav --> :navigation:api
  :i18n:api --> :i18n:generator
  :i18n:implementation --> :core:base
  :i18n:implementation --> :core:locale:api
  :i18n:implementation -.-> :core:network-util:api
  :i18n:implementation --> :i18n:api
  :navigation:implementation --> :core:base
  :navigation:implementation -.-> :features:home:nav
  :navigation:implementation --> :navigation:api

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
