package me.jeybi.cinerama.ui.main

import android.support.design.widget.Snackbar
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import me.jeybi.cinerama.model.Movie
import me.jeybi.cinerama.model.MovieListResponse
import me.jeybi.cinerama.network.ApiList
import me.jeybi.cinerama.utils.Constants

class MainPresenter(val view : MainMvp.view) : MainMvp.presenter {

    override fun getMoviesList(page: Int, apiList: ApiList): Disposable {
        return apiList.getMoviesList(
            page,
            Constants.API_KEY
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                view.onMoviesListReady(it.data.movies)

        }, {
            view.onErrorOccured(it.message)
            Log.d("LLLLLL","${it.message}")
        })
    }

}