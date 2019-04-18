package me.jeybi.cinerama.model

data class Movie(
    val id : Long,
    val title : String,
    val params : MovieParams,
    val files : MovieFiles,
    val year : Int,
    val countries_str : String,
    val genres_str : String,

    ///Detailed Informations

    val title_eng : String?,
    val description : String?,
    val rates : MovieRates?,
    val directors : ArrayList<MovieDirector>?,
    val scenarists : ArrayList<MovieDirector>?,
    val producers : ArrayList<MovieDirector>?,
    val actors : ArrayList<MovieDirector>?,
    val movies: ArrayList<Movie>?
)

data class MovieRates(
    val imdb : Float?,
    val kinopoisk : Float?,
    val itv : Float?
)

data class MovieParams(
    val is_hd : Boolean,
    val is_3d : Boolean,
    val is_4k  :Boolean,
    val is_new : Boolean,
    val is_free : Boolean,
    val is_tvshow : Boolean
)

data class MovieFiles(
    val poster_url : String,
    val video_sd : Video?,
    val video_hd : Video?,
    val video_3d : Video?,
    val video_4k : Video?,
    val snapshots : ArrayList<Snapshot>?,
    val trailers : ArrayList<Trailer>?
)

data class Video(
    val video_url : String?,
    val file_id : Long?,
    val seconds : Double
)

data class Snapshot(
    val snapshot_url : String
)

data class Trailer(
    val id : Long
)

data class MovieDirector(
    val id : Long,
    val name : String,
    val photo_url : String?
)