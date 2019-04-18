package me.jeybi.cinerama.ui.preview

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.jeybi.cinerama.network.ApiList
import me.jeybi.cinerama.utils.Constants

class PreviewPresenter(val view : PreviewMvp.view) : PreviewMvp.presenter {
    override fun getMovie(id: Long, apiList: ApiList): Disposable {
        return apiList.getMovieShow(
            id,
            Constants.API_KEY
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            view.onMovieReady(it.data.movie)
            if (it.data.movie.files.snapshots!=null)
            view.onSnapshotsReady(it.data.movie.files.snapshots)
        }, {
            view.onErrorOccured(it.message)
        })
    }


}