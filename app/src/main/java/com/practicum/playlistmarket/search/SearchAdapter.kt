package com.practicum.playlistmarket.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmarket.*


class SearchAdapter : RecyclerView.Adapter<TrackHolder>() {

    var trackList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_track_item, parent, false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences(
            HISTORY_TRACK,
            AppCompatActivity.MODE_PRIVATE
        )
        holder.bind(trackList[position])

        holder.itemView.setOnClickListener {
            val json = Gson().toJson(trackList[position])
            sharedPreferences.edit()
                .putString(KEY_HISTORY, json)
                .apply()


            val intent = Intent(it.context, MediaPlayerActivity::class.java)
            intent.putExtra(EXTRA_TRACK_NAME, trackList[position].trackName)
            intent.putExtra(EXTRA_ARTIST_NAME, trackList[position].artistName)
            intent.putExtra(EXTRA_TIME_MILLIS, trackList[position].trackTimeMillis)
            intent.putExtra(EXTRA_IMAGE, trackList[position].artworkUrl100)
            intent.putExtra(EXTRA_DATA, trackList[position].releaseDate)
            intent.putExtra(EXTRA_COLLECTION_NAME, trackList[position].collectionName)
            intent.putExtra(EXTRA_PRIMARY_NAME, trackList[position].primaryGenreName)
            intent.putExtra(EXTRA_COUNTRY, trackList[position].country)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = trackList.size


}