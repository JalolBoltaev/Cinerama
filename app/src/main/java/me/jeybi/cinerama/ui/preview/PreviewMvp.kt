package me.jeybi.cinerama.ui.preview

import io.reactivex.disposables.Disposable
import me.jeybi.cinerama.model.Movie
import me.jeybi.cinerama.model.Snapshot
import me.jeybi.cinerama.network.ApiList

interface PreviewMvp {
    interface view{
        fun onMovieReady(movie: Movie)
        fun onSnapshotsReady(snapshots: ArrayList<Snapshot>)
        fun onErrorOccured(message : String?)
    }
    interface presenter{
        fun getMovie(id : Long,apiList: ApiList) : Disposable
    }
}