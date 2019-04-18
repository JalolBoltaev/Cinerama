package me.jeybi.cinerama.adapter

import android.content.Context
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import me.jeybi.cinerama.R
import me.jeybi.cinerama.model.Snapshot
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions



class SnapshotsAdapter(val context : Context,val snapshots : ArrayList<Snapshot>) : RecyclerView.Adapter<SnapshotsAdapter.SnapshotsHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SnapshotsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_snapshot,p0,false)
        return SnapshotsHolder(view)
    }

    override fun getItemCount(): Int {
        return snapshots.size
    }

    override fun onBindViewHolder(p0: SnapshotsHolder, p1: Int) {
        val snapshot = snapshots[p1]
        Glide.with(context).load(snapshot.snapshot_url).apply(RequestOptions.bitmapTransform(RoundedCorners(8))).into(p0.imageView)
    }


    class SnapshotsHolder(view : View) : RecyclerView.ViewHolder(view){
        val imageView = view.findViewById<ImageView>(R.id.imageViewSnapshot)
    }
}