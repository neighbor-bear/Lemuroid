package com.swordfish.lemuroid.app.feature.home

import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.carousel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.swordfish.lemuroid.BuildConfig
import com.swordfish.lemuroid.R
import com.swordfish.lemuroid.app.shared.GameInteractor
import com.swordfish.lemuroid.app.shared.withModelsFrom
import com.swordfish.lemuroid.lib.library.db.entity.Game

class EpoxyHomeController(private val gameInteractor: GameInteractor) : EpoxyController() {
    private var recentGames = listOf<Game>()
    private var favoriteGames = listOf<Game>()
    private var discoverGames = listOf<Game>()

    fun updateRecents(games: List<Game>) {
        recentGames = games
        requestDelayedModelBuild(UPDATE_DELAY_TIME)
    }

    fun updateFavorites(games: List<Game>) {
        favoriteGames = games
        requestDelayedModelBuild(UPDATE_DELAY_TIME)
    }

    fun updateDiscover(games: List<Game>) {
        discoverGames = games
        requestDelayedModelBuild(UPDATE_DELAY_TIME)
    }

    override fun buildModels() {
        if (recentGames.isNotEmpty()) {
            addCarousel("recent", R.string.recent, recentGames)
        }

        if (favoriteGames.isNotEmpty()) {
            addCarousel("favorites", R.string.favorites, favoriteGames)
        }

        if (discoverGames.isNotEmpty()) {
            addCarousel("discover", R.string.discover, discoverGames)
        }
    }

    private fun addCarousel(id: String, titleId: Int, games: List<Game>) {
        epoxyHomeSection {
            id("section_$id")
            title(titleId)
        }
        carousel {
            id("carousel_$id")
            withModelsFrom(games) { item ->
                EpoxyGameView_()
                        .id(item.id)
                        .title(item.title)
                        .coverUrl(item.coverFrontUrl)
                        .favorite(item.isFavorite)
                        .onFavoriteChanged { gameInteractor.onFavoriteToggle(item, it) }
                        .onClick { gameInteractor.onGameClick(item) }
            }
        }
    }

    init {
        if (BuildConfig.DEBUG) {
            isDebugLoggingEnabled = true
        }
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {
        throw exception
    }

    companion object {
        const val UPDATE_DELAY_TIME = 160
    }
}
