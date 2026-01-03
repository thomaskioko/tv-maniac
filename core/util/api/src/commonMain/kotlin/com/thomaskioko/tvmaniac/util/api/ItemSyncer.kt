package com.thomaskioko.tvmaniac.util.api

public data class ItemSyncerResult<T>(
    val added: List<T> = emptyList(),
    val deleted: List<T> = emptyList(),
    val updated: List<T> = emptyList(),
) {
    public fun dataSetChanged(): Boolean = added.isNotEmpty() || deleted.isNotEmpty()
}

public class ItemSyncer<LocalType, NetworkType, Key>(
    private val upsertEntity: (LocalType) -> Unit,
    private val deleteEntity: (LocalType) -> Unit,
    private val localEntityToKey: (LocalType) -> Key?,
    private val networkEntityToKey: (NetworkType) -> Key,
    private val networkEntityToLocalEntity: (NetworkType, LocalType?) -> LocalType,
) {
    public fun sync(
        currentValues: Collection<LocalType>,
        networkValues: Collection<NetworkType>,
        removeNotMatched: Boolean = true,
    ): ItemSyncerResult<LocalType> {
        val currentDbEntities = currentValues.associateBy { localEntityToKey(it) }

        val added = mutableListOf<LocalType>()
        val deleted = mutableListOf<LocalType>()
        val updated = mutableListOf<LocalType>()

        val networkKeys = networkValues.map { networkEntityToKey(it) }.toSet()

        for (networkEntity in networkValues) {
            val networkKey = networkEntityToKey(networkEntity)
            val currentDbEntity = currentDbEntities[networkKey]

            val entity = networkEntityToLocalEntity(networkEntity, currentDbEntity)

            if (currentDbEntity == null) {
                added.add(entity)
            } else if (currentDbEntity != entity) {
                updated.add(entity)
            }

            upsertEntity(entity)
        }

        if (removeNotMatched) {
            for ((key, entity) in currentDbEntities) {
                if (key != null && key !in networkKeys) {
                    deleteEntity(entity)
                    deleted.add(entity)
                }
            }
        }

        return ItemSyncerResult(added = added, deleted = deleted, updated = updated)
    }
}

public fun <Type, Key> syncerForEntity(
    upsertEntity: (Type) -> Unit,
    deleteEntity: (Type) -> Unit,
    entityToKey: (Type) -> Key?,
    mapper: (Type, Type?) -> Type,
): ItemSyncer<Type, Type, Key> = ItemSyncer(
    upsertEntity = upsertEntity,
    deleteEntity = deleteEntity,
    localEntityToKey = entityToKey,
    networkEntityToKey = { entityToKey(it)!! },
    networkEntityToLocalEntity = mapper,
)
