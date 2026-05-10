package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * Serializes [MultiStackNavState] using the polymorphic [BaseRoute] and [NavRoot] serializers
 * contributed through the navigation multibinding sets.
 *
 * Decompose calls [serialize] when the activity is being torn down and [deserialize] when state
 * is restored. The active root and the back stack for each tab are encoded as two fields; entry
 * order within a tab is preserved by [ListSerializer] and tab order by [MapSerializer].
 *
 * @param baseRouteSerializer aggregated route and root serializer used for individual stack
 *   entries.
 * @param navRootSerializer serializer for the active tab anchor and tab keys.
 */
internal class MultiStackNavStateSerializer(
    baseRouteSerializer: KSerializer<BaseRoute>,
    private val navRootSerializer: KSerializer<NavRoot>,
) : KSerializer<MultiStackNavState> {

    private val tabStacksSerializer: KSerializer<Map<NavRoot, List<BaseRoute>>> =
        MapSerializer(navRootSerializer, ListSerializer(baseRouteSerializer))

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MultiStackNavState") {
        element("activeRoot", navRootSerializer.descriptor)
        element("tabStacks", tabStacksSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MultiStackNavState) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, navRootSerializer, value.activeRoot)
            encodeSerializableElement(descriptor, 1, tabStacksSerializer, value.tabStacks)
        }
    }

    override fun deserialize(decoder: Decoder): MultiStackNavState =
        decoder.decodeStructure(descriptor) {
            var activeRoot: NavRoot? = null
            var tabStacks: Map<NavRoot, List<BaseRoute>>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> activeRoot = decodeSerializableElement(descriptor, 0, navRootSerializer)
                    1 -> tabStacks = decodeSerializableElement(descriptor, 1, tabStacksSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected serial element index: $index")
                }
            }
            MultiStackNavState(
                activeRoot = requireNotNull(activeRoot) { "activeRoot missing from saved state" },
                tabStacks = requireNotNull(tabStacks) { "tabStacks missing from saved state" },
            )
        }
}
