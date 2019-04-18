package me.jeybi.cinerama.model

data class MovieListResponse(
    val code : Int,
    val message : String,
    val language : String,
    val subscription_status : String,
    val need_update : Boolean,
    val data : MovieListData
)

data class MovieListData(
    val total_items : Double,
    val items_per_page : Int,
    val movies : ArrayList<Movie>
)


