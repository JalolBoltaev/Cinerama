package me.jeybi.cinerama.model

data class MovieShowResponse(
    val code: Int,
    val message: String,
    val language : String,
    val subscription_status : String,
    val need_update : String,
    val data : MovieShowData
)

data class MovieShowData(
    val movie : Movie
)