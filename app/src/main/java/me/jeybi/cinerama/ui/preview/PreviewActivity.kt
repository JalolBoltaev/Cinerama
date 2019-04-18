package me.jeybi.cinerama.ui.preview

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.preview_header_content.*
import kotlinx.android.synthetic.main.preview_header_image.*
import me.jeybi.cinerama.R
import me.jeybi.cinerama.adapter.MovieListAdapter
import me.jeybi.cinerama.adapter.SnapshotsAdapter
import me.jeybi.cinerama.model.Movie
import me.jeybi.cinerama.model.Snapshot
import me.jeybi.cinerama.network.ApiList
import me.jeybi.cinerama.network.RetrofitClient
import me.jeybi.cinerama.ui.BaseActivity

class PreviewActivity : AppCompatActivity(), PreviewMvp.view {

    lateinit var presenter: PreviewPresenter
    var movieID: Long? = null
    lateinit var apiList: ApiList
    private val disposable = CompositeDisposable()

    private val STATE_RESUME_WINDOW = "resumeWindow"
    private val STATE_RESUME_POSITION = "resumePosition"
    private val STATE_PLAYER_FULLSCREEN = "playerFullscreen"

    private var videoSource: MediaSource? = null
    private var exoPlayerFullscreen = false
    private var fullScreenButton: FrameLayout? = null
    private var fullScreenIcon: ImageView? = null
    private var fullScreenDialog: Dialog? = null

    private var resumeWindow: Int = 0
    private var resumePosition: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_preview)
        presenter = PreviewPresenter(this)

        if (savedInstanceState != null) {
            resumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            resumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            exoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
        }

        setUpUi()

    }

    private fun setUpUi() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar!!.title = ""



        apiList = RetrofitClient.instance.create(ApiList::class.java)
        movieID = intent.extras.getLong("id")
        disposable.add(presenter.getMovie(movieID!!, apiList))
    }

    var actorPos = 0

    override fun onMovieReady(movie: Movie) {
        Glide.with(this).load(movie.files.poster_url).into(imageViewPreview)
        textViewImbdRate.text = "${movie.rates!!.imdb}"
        textKinopoiskRate.text = "${movie.rates.kinopoisk}      |"
        textViewYear.text = "${movie.year}"
        textViewGenre.text = movie.genres_str
        textViewTitle.text = movie.title
        textViewDescription.text = Html.fromHtml(movie.description)

        textViewActors.animateText(movie.actors!![actorPos].name)

        textViewActors.setAnimationListener {
            actorPos++
            if (actorPos == movie.actors.size)
                actorPos = 0
            textViewActors.postDelayed(object : Runnable {
                override fun run() {
                    textViewActors.animateText(movie.actors[actorPos].name)
                }
            }, 1000)
        }

        recyclerViewMovies.layoutManager = GridLayoutManager(this, 2)
        val movieAdapter = MovieListAdapter(this, movie.movies!!, null)
        recyclerViewMovies.adapter = movieAdapter

        imageViewPlay.setOnClickListener {
            headerContent.animate().translationY(800f).setDuration(400).setInterpolator(AccelerateInterpolator())
                .start()
            imageViewPlay.animate().scaleX(0f).scaleY(0f).setDuration(1000)
                .setInterpolator(AnticipateOvershootInterpolator()).start()
            exoPlayerView.visibility = View.VISIBLE

            playVideo()
        }

    }

    override fun onSnapshotsReady(snapshots: ArrayList<Snapshot>) {
        recyclerViewSnapshots.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSnapshots.adapter = SnapshotsAdapter(this, snapshots)
    }

    override fun onErrorOccured(message: String?) {
        Snackbar.make(parentLayout, "${message}", Snackbar.LENGTH_SHORT).show()
        Log.d("TAGGY", "${message}")
    }


    public override fun onSaveInstanceState(outState: Bundle) {
        Log.d("TAGGGY", "SAVE INSTANCE ")
        outState.putInt(STATE_RESUME_WINDOW, resumeWindow)
        outState.putLong(STATE_RESUME_POSITION, resumePosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, exoPlayerFullscreen)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        // UNSUBSCRIBE
        disposable.dispose()
    }


    private fun initFullscreenDialog() {

        fullScreenDialog = object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            override fun onBackPressed() {
                if (exoPlayerFullscreen)
                    closeFullscreenDialog()
                super.onBackPressed()
            }
        }
    }


    private fun openFullscreenDialog() {

        (exoPlayerView.parent as ViewGroup).removeView(exoPlayerView)
        fullScreenDialog!!.addContentView(
            exoPlayerView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        fullScreenIcon!!.setImageDrawable(ContextCompat.getDrawable(this@PreviewActivity, R.drawable.ic_fullscreen_skrink))
        exoPlayerFullscreen = true
        fullScreenDialog!!.show()

    }


    private fun closeFullscreenDialog() {

        (exoPlayerView.parent as ViewGroup).removeView(exoPlayerView)
        main_media_frame.addView(exoPlayerView)
        exoPlayerFullscreen = false
        fullScreenDialog!!.dismiss()
        fullScreenIcon!!.setImageDrawable(ContextCompat.getDrawable(this@PreviewActivity, R.drawable.ic_fullscreen_expand))

    }


    private fun initFullscreenButton() {

        val controlView = exoPlayerView.findViewById<View>(R.id.exo_controller)
        fullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon)
        fullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button)
        fullScreenButton!!.setOnClickListener {
            if (!exoPlayerFullscreen)
                openFullscreenDialog()
            else
                closeFullscreenDialog()
        }
    }


    private fun initExoPlayer() {

        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()
        val player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), trackSelector, loadControl)
        exoPlayerView.player = player

        Log.d("TAGGGY", "INITIALIZATION ${resumePosition}    ${resumeWindow}")

        (exoPlayerView.player as SimpleExoPlayer).prepare(videoSource)
        exoPlayerView.player.playWhenReady = true
        exoPlayerView.player.seekTo(resumeWindow, resumePosition)
    }

    fun playVideo() {
        imageViewPlay.visibility = View.GONE
        imageViewPreview.setImageResource(R.color.colorPrimaryDark)

        if (videoSource==null) {
            Log.d("TAGGGY","NULLLLL")
            initFullscreenDialog()
            initFullscreenButton()

            videoSource = buildMediaSource(Uri.parse("https://files1.mediabox.uz/films/7400/videos/3c6b3245929076a9e8939ef7b1f675021554203376.mp4?st=in3pOXDWo6IJyA8Bz-BG5g&e=1555592853"))
        }


        initExoPlayer()

        if (exoPlayerFullscreen) {
            (exoPlayerView.parent as ViewGroup).removeView(exoPlayerView)
            fullScreenDialog!!.addContentView(
                exoPlayerView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            )
            fullScreenIcon!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this@PreviewActivity,
                    R.drawable.ic_fullscreen_skrink
                )
            )
            fullScreenDialog!!.show()
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {

        val userAgent = "exoplayer-codelab"

        return if (uri.lastPathSegment.contains("mp3") || uri.lastPathSegment.contains("mp4")) {
            ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else if (uri.lastPathSegment.contains("m3u8")) {
            HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else {
            val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
                DefaultHttpDataSourceFactory("ua", null)
            )
            val manifestDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
            DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        if (newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE){
            openFullscreenDialog()
            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)
        }else{
            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
        }

    }

    override fun onResume() {
        super.onResume()
        if (resumePosition>0){
            playVideo()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("TAGGGY", "PAUSE")


        if (exoPlayerView != null && exoPlayerView.player != null) {
            resumeWindow = exoPlayerView.player.currentWindowIndex
            resumePosition = Math.max(0, exoPlayerView.player.contentPosition)

            Log.d("TAGGGY", "${resumePosition}    ${resumeWindow}")
            exoPlayerView.player.release()
        }

        if (fullScreenDialog != null)
            fullScreenDialog!!.dismiss()
    }


}
