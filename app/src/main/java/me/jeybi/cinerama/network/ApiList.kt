package me.jeybi.cinerama.network

import io.reactivex.Observable
import me.jeybi.cinerama.model.MovieListResponse
import me.jeybi.cinerama.model.MovieShowResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiList {

    @GET("content/main/{page}/list")
    fun getMoviesList(@Path("page") page : Int, @Query("user") apiKey : String) : Observable<MovieListResponse>

    @GET("content/main/2/show/{id}")
    fun getMovieShow(@Path("id") id : Long,@Query("user") apiKey : String) : Observable<MovieShowResponse>

}
