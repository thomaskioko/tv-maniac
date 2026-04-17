package com.thomaskioko.tvmaniac.navigation

/**
 * Marker interface implemented by each feature's `@Serializable` sheet config class in its
 * `nav` module.
 *
 * Each config class is registered as a Metro multibinding via [SheetConfigBinding] so that the
 * Decompose sheet slot can polymorphically serialize and deserialize configs without a central
 * sealed hierarchy. Adding a new sheet therefore only touches the feature's own `nav` module
 * plus its [SheetChildFactory] contribution; `navigation/api` and the root presenter stay
 * untouched.
 */
public interface SheetConfig
