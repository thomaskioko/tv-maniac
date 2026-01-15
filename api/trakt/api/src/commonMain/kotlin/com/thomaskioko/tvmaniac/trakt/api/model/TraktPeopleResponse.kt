package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from Trakt's `/shows/:id/people` endpoint.
 *
 * @see [Trakt Show People](https://trakt.docs.apiary.io/#reference/shows/people)
 */
@Serializable
public data class TraktShowPeopleResponse(
    @SerialName("cast") val cast: List<TraktCastMember> = emptyList(),
)

/**
 * Represents a cast member in a show.
 *
 * @property characters List of character names played by this person
 * @property episodeCount Number of episodes this person appeared in
 * @property person The person details
 */
@Serializable
public data class TraktCastMember(
    @SerialName("characters") val characters: List<String> = emptyList(),
    @SerialName("episode_count") val episodeCount: Int? = null,
    @SerialName("person") val person: TraktPerson,
)

/**
 * Represents a person (actor/crew member) in Trakt.
 */
@Serializable
public data class TraktPerson(
    @SerialName("name") val name: String,
    @SerialName("ids") val ids: TraktPersonIds,
    @SerialName("biography") val biography: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("death") val death: String? = null,
    @SerialName("birthplace") val birthplace: String? = null,
    @SerialName("homepage") val homepage: String? = null,
)

/**
 * IDs for a person across different services.
 */
@Serializable
public data class TraktPersonIds(
    @SerialName("trakt") val trakt: Long,
    @SerialName("slug") val slug: String = "",
    @SerialName("imdb") val imdb: String? = null,
    @SerialName("tmdb") val tmdb: Long? = null,
    @SerialName("tvrage") val tvrage: Long? = null,
)
