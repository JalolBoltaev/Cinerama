package me.jeybi.cinerama.ui.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import me.jeybi.cinerama.R
import me.jeybi.cinerama.adapter.MovieListAdapter
import me.jeybi.cinerama.model.Movie
import me.jeybi.cinerama.network.ApiList
import me.jeybi.cinerama.network.RetrofitClient
import me.jeybi.cinerama.ui.BaseActivity
import me.jeybi.cinerama.utils.Constants

class MainActivity : BaseActivity(), MainMvp.view,MovieListAdapter.OnScrolledToBottom{

    override fun setLayoutId(): Int {
        return R.layout.activity_main
    }

    lateinit var presenter: MainPresenter
    lateinit var apiList: ApiList
    private val disposable = CompositeDisposable()
    var page = 2
    var movies = ArrayList<Movie>()
    lateinit var movieAdapter : MovieListAdapter

    override fun onViewDidCreate(savedInstanceState: Bundle?) {
        presenter = MainPresenter(this)
        apiList = RetrofitClient.instance.create(ApiList::class.java)
        setUpUi()

    }

    private fun setUpUi() {
        recyclerViewMovies.layoutManager = GridLayoutManager(this, 2)
        swipeRefresh.isRefreshing = true
        movieAdapter = MovieListAdapter(this, movies,this)
        recyclerViewMovies.adapter = movieAdapter
        getMoviesList()

        swipeRefresh.setOnRefreshListener {
            page=2
            getMoviesList()
        }
    }

    private fun getMoviesList() {
        disposable.add(presenter.getMoviesList(page,apiList))
    }

    override fun onMoviesListReady(movies: ArrayList<Movie>) {
        swipeRefresh.isRefreshing = false
        this.movies.addAll(movies)
        recyclerViewMovies.adapter!!.notifyDataSetChanged()
    }


    //// PAGINATION , but honestly I didn't read any documentation.
    // That's why I really don't know how to get next page movies.

    override fun onScrolledToBottom() {
//        page++
//        disposable.add(presenter.getMoviesList(page,apiList))
    }

    override fun onDestroy(){
        super.onDestroy()
        // UNSUBSCRIBE
        disposable.dispose()
    }

    override fun onErrorOccured(message: String?) {
        Snackbar.make(parentLayout,"${message}", Snackbar.LENGTH_SHORT).show()
        swipeRefresh.isRefreshing = false
    }



}
