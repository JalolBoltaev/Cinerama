package me.jeybi.cinerama.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.jeybi.cinerama.R
import me.jeybi.cinerama.model.Movie
import org.w3c.dom.Text
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import me.jeybi.cinerama.ui.preview.PreviewActivity


class MovieListAdapter(val context: Context, val movies: ArrayList<Movie>, val onScrolledToBottom: OnScrolledToBottom?) :
    RecyclerView.Adapter<MovieListAdapter.MovieListHolder>() {
    private val FADE_DURATION = 1000

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MovieListHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, p0, false)
        return MovieListHolder(view)
    }

    override fun getItemCount(): Int {
        return if (movies.size == 0) 10 else movies.size
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = FADE_DURATION.toLong()
        view.startAnimation(anim)
    }

    override fun onBindViewHolder(holder: MovieListHolder, position: Int) {
        if (movies.size != 0) {
            val movie = movies[position]
            holder.textViewTitle.setBackgroundColor(Color.TRANSPARENT)
            holder.textViewTitle.text = movie.title
            holder.textViewGenre.text = "${movie.genres_str} (${movie.year})"
            holder.textViewGenre.setBackgroundColor(Color.TRANSPARENT)
            Glide.with(context).load(movie.files.poster_url).into(holder.imageViewPoster)

            if (movie.params.is_hd) {
                holder.rvHd.visibility = View.VISIBLE
            } else {
                holder.rvHd.visibility = View.GONE
            }

            holder.textViewYear.text = "${movie.year}"


            holder.itemView.setOnClickListener {
                val intent = Intent(context, PreviewActivity::class.java)
                intent.putExtra("id", movie.id)
                context.startActivity(intent)
                if (onScrolledToBottom==null){
                    (context as PreviewActivity).finish()
                }

            }
        }

        setFadeAnimation(holder.itemView)

        if (position == movies.size - 1) {
            if (onScrolledToBottom!=null)
            onScrolledToBottom.onScrolledToBottom()
        }

    }

    class MovieListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        val textViewGenre = view.findViewById<TextView>(R.id.textViewGenre)
        val imageViewPoster = view.findViewById<ImageView>(R.id.imageViewPoster)
        val rvHd = view.findViewById<RelativeLayout>(R.id.rvHd)
        val textViewYear = view.findViewById<TextView>(R.id.textViewYear)
    }

    interface OnScrolledToBottom {
        fun onScrolledToBottom()
    }
}