package com.thomaskioko.tvmaniac.core.networkutil.api.model

public class ApiHttpException(
    public val code: Int,
    override val message: String,
) : Exception(message)

public class ApiSerializationException(
    override val message: String,
) : Exception(message)
