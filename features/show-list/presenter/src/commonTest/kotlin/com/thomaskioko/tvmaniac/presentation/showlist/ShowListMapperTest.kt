package com.thomaskioko.tvmaniac.presentation.showlist

import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import kotlin.test.Test

internal class ShowListMapperTest {

    private val mapper = ShowListMapper(localizer = FakeLocalizer())

    @Test
    fun `should map TraktList to TraktListModel preserving identifiers and toggle state`() {
        val source = TraktList(
            id = 7L,
            slug = "favorites",
            name = "Favorites",
            description = "My favorite shows",
            itemCount = 12L,
            isShowInList = true,
        )

        val models = mapper.toModels(listOf(source), togglingListIds = emptySet())

        models shouldHaveSize 1
        val model = models[0]
        model.id shouldBe 7L
        model.slug shouldBe "favorites"
        model.name shouldBe "Favorites"
        model.description shouldBe "My favorite shows"
        model.isShowInList shouldBe true
        model.isToggling shouldBe false
        model.showCountText.shouldNotBeEmpty()
    }

    @Test
    fun `should mark TraktListModel as toggling given list id is in toggling set`() {
        val source = TraktList(
            id = 7L,
            slug = "favorites",
            name = "Favorites",
            description = null,
            itemCount = 3L,
            isShowInList = false,
        )

        val models = mapper.toModels(listOf(source), togglingListIds = setOf(7L))

        models[0].isToggling shouldBe true
    }

    @Test
    fun `should return empty list given empty input`() {
        mapper.toModels(emptyList(), togglingListIds = emptySet()).shouldBeEmpty()
    }

    @Test
    fun `should resolve every copy field via the Localizer`() {
        val copy = mapper.resolveCopy()

        copy.sheetTitle.shouldNotBeEmpty()
        copy.createListButtonText.shouldNotBeEmpty()
        copy.createListDoneText.shouldNotBeEmpty()
        copy.createListPlaceholder.shouldNotBeEmpty()
        copy.emptyListText.shouldNotBeEmpty()
        copy.listsHeaderText.shouldNotBeEmpty()
        copy.loginRequiredTitle.shouldNotBeEmpty()
        copy.loginRequiredMessage.shouldNotBeEmpty()
        copy.loginRequiredConfirmText.shouldNotBeEmpty()
    }
}
