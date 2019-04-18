package me.jeybi.cinerama.ui.main

import android.content.Context
import io.reactivex.disposables.Disposable
import me.jeybi.cinerama.model.Movie
import me.jeybi.cinerama.model.MovieListResponse
import me.jeybi.cinerama.network.ApiList

interface MainMvp {
    interface view{
        fun onMoviesListReady(movies : ArrayList<Movie>)

        fun onErrorOccured(message : String?)
    }
    interface presenter{
        fun getMoviesList(page : Int,apiList: ApiList) : Disposable

    }
}