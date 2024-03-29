package com.practicum.playlistmarket.player.ui.fragment

import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmarket.R
import com.practicum.playlistmarket.databinding.FragmentMediaPlayerBinding
import com.practicum.playlistmarket.media.domain.module.PlayList
import com.practicum.playlistmarket.media.ui.fragments.FragmentNewPlayList
import com.practicum.playlistmarket.media.ui.states.PlayListState
import com.practicum.playlistmarket.player.ui.view_model.MediaPlayerViewModel
import com.practicum.playlistmarket.player.domain.StatePlayer
import com.practicum.playlistmarket.player.domain.StatePlayer.*
import com.practicum.playlistmarket.player.domain.models.Track
import com.practicum.playlistmarket.player.ui.adapter.PlayListPlayerAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


const val EXTRA_TRACK_NAME = "track_name"
const val EXTRA_ARTIST_NAME = "artist_name"
const val EXTRA_TIME_MILLIS = "time_millis"
const val EXTRA_IMAGE = "track_Image"
const val EXTRA_COUNTRY = "track_country"
const val EXTRA_DATA = "track_data"
const val EXTRA_PRIMARY_NAME = "track_primary_name"
const val EXTRA_COLLECTION_NAME = "track_collection_name"
const val EXTRA_SONG = "track_song"
const val EXTRA_LIKE = "track_like"
const val EXTRA_ID = "track_id"
const val EXTRA_TRACK = "track_track"

class MediaPlayerFragment : Fragment() {

    private var _binding: FragmentMediaPlayerBinding? = null
    private val binding get() = _binding!!

    private var songUrl: String = ""

    private var playlistName = ""

    private val viewModel: MediaPlayerViewModel by viewModel()

    private lateinit var adapter: PlayListPlayerAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fillData()


        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable<Track>(EXTRA_TRACK, Track::class.java)
        } else {
            requireArguments().getParcelable<Track>(EXTRA_TRACK)
        }

        track?.let {
            viewModel.checkLike(track.trackId)
        }


        songUrl = requireArguments().getString(EXTRA_SONG).toString()

        viewModel.preparePlayer(songUrl)

        binding.btPlay.setOnClickListener {
            viewModel.playStart()
        }

        viewModel.timeSongSec.observe(viewLifecycleOwner) {
            binding.timeLeft.text = it
        }


        viewModel.checkState.observe(viewLifecycleOwner) {
            checkState(it)
        }

        binding.apply {
            trackName.text = requireArguments().getString(EXTRA_TRACK_NAME)
            groupName.text = requireArguments().getString(EXTRA_ARTIST_NAME)
            countryApp.text = requireArguments().getString(EXTRA_COUNTRY)

            val albumText = requireArguments().getString(EXTRA_COLLECTION_NAME)
            if (albumText != null) {
                albumApp.text = albumText
            } else {
                albumApp.visibility = View.GONE
                album.visibility = View.GONE
            }

            genreApp.text = requireArguments().getString(EXTRA_PRIMARY_NAME)
        }


        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        val data = requireArguments().getString(EXTRA_DATA).toString()
        viewModel.correctDataSong(data)
        viewModel.dataSong.observe(viewLifecycleOwner) { data ->
            binding.yearApp.text = data
        }

        val time = requireArguments().getString(EXTRA_TIME_MILLIS)

        if (time != null) {
            viewModel.correctTimeSong(time)

            viewModel.timeSong.observe(viewLifecycleOwner) { timeSong ->
                binding.durationApp.text = timeSong
            }
        }

        val urlImage = requireArguments().getString(EXTRA_IMAGE)
        viewModel.getCoverArtwork(urlImage)

        viewModel.coverArtwork.observe(viewLifecycleOwner) { urlSong ->
            var url = urlSong

            val cornerSize = resources.getDimensionPixelSize(R.dimen.corners_image_track)
            Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.icon_track_default)
                .transform(RoundedCorners(cornerSize))
                .into(binding.trackImage)
        }

        viewModel.secondCounter.observe(viewLifecycleOwner) { time ->
            Log.d("addTrackPlaylist", "fragment text")
            binding.timeLeft.text = time
        }

        binding.btLike.setOnClickListener {
            lifecycleScope.launch {
                if (track != null) {
                    viewModel.addTrackFavorite(track)
                }
            }
        }

        viewModel.likeState.observe(viewLifecycleOwner) { isLiked ->
            if (isLiked) {
                binding.btLike.setImageResource(R.drawable.button_like_true)
                if (track != null) track.isFavorite = isLiked

            } else {
                binding.btLike.setImageResource(R.drawable.button__like)
                if (track != null) track.isFavorite = isLiked
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            binding.transparencyView.visibility = View.GONE
            if (state == BottomSheetBehavior.STATE_HIDDEN) {
                binding.transparencyView.visibility = View.GONE
            }
        }
        behaviorSize()


        binding.btAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.transparencyView.visibility = View.VISIBLE
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.transparencyView.visibility =
                        View.GONE

                    BottomSheetBehavior.STATE_COLLAPSED -> binding.transparencyView.visibility =
                        View.VISIBLE
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.observeStatePlayList().observe(viewLifecycleOwner) {
            Log.d("addTrackPlaylist", "fragmentPlayList")
            checkStatePlayList(it)
        }

        binding.btAddPlayList.setOnClickListener {
            findNavController().navigate(R.id.action_mediaPlayerFragment_to_fragmentNewPlayList,
                FragmentNewPlayList.createArgs(null, null, null, null))


        }

        adapter = PlayListPlayerAdapter { playlist ->
            track?.let { track ->
                viewModel.addTrackPlaylist(track, playlist)
                playlistName = playlist.name
            }
        }

        viewModel.playlistState.observe(viewLifecycleOwner) {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED ||
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED||
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_DRAGGING ||
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_SETTLING) {
                showMassage(it)
            }
        }

        binding.rvPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPlaylist.adapter = adapter

    }


    private fun showMassage(isAdd: Boolean) {
        Log.e("addTrackPlaylist", "showMassage")
        var massage = ""
        if (!isAdd) {
            massage = getString(R.string.track_already_playlist) + " $playlistName"
            showMassage(massage)
        } else {
            viewModel.fillData()
            massage = getString(R.string.add_track_for_playlist) + " $playlistName"
            showMassage(massage)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }


    private fun checkStatePlayList(statePlayList: PlayListState) {
        when (statePlayList) {
            is PlayListState.Empty -> showEmptyPlayList()
            is PlayListState.Loading -> showLoading()
            is PlayListState.Content -> showContent(statePlayList.playList)
        }

    }

    private fun showContent(playList: List<PlayList>) = with(binding) {
        rvPlaylist.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        lifecycleScope.launch {
            playList.map {
                it.quantityTracks = viewModel.getTrackCount(it)
            }
            adapter.playList.clear()
            adapter.playList.addAll(playList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showLoading() = with(binding) {
        rvPlaylist.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }


    private fun showEmptyPlayList() = with(binding) {
        rvPlaylist.visibility = View.GONE
        progressBar.visibility = View.GONE
    }


    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun checkState(state: StatePlayer) {
        when (state) {
            STATE_PLAYING -> {
                binding.btPlay.setImageResource(R.drawable.button_pauseb)
                Log.e("mylogPlayningState", state.toString())
            }

            STATE_PAUSED, STATE_DEFAULT -> {
                binding.btPlay.setImageResource(R.drawable.bt_play)
                Log.e("mylogPauseStaytrning", state.toString())
            }

            STATE_PREPARED -> {
                binding.btPlay.setImageResource(R.drawable.bt_play)
            }
        }
    }

    private fun behaviorSize() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        binding.bottomSheet.layoutParams.height = screenHeight
        bottomSheetBehavior.peekHeight = screenHeight / 2 + screenHeight / 10
    }

    private fun showMassage(massage: String) {
        val typedValue = TypedValue()

        requireActivity().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnPrimary,
            typedValue,
            true
        )
        val colorText = typedValue.data

        requireActivity().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSecondary,
            typedValue,
            true
        )
        val colorBackground = typedValue.data


        val snackBar = Snackbar.make(binding.root, massage, Snackbar.LENGTH_LONG)
            .setBackgroundTint(colorBackground).setTextColor(colorText)
        val snackBarView = snackBar.view
        snackBarView.findViewById<TextView>(android.R.id.message)
        val textView =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackBar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun createArgs(
            trackId: String,
            artworkUrl100: String?,
            trackName: String,
            artistName: String,
            trackTimeMillis: String?,
            collectionName: String?,
            releaseDate: String?,
            primaryGenreName: String?,
            country: String?,
            isFavorite: Boolean,
            track: Track,
            previewUrl: String?
        ): Bundle =
            bundleOf(
                EXTRA_ID to trackId,
                EXTRA_IMAGE to artworkUrl100,
                EXTRA_TRACK_NAME to trackName,
                EXTRA_ARTIST_NAME to artistName,
                EXTRA_TIME_MILLIS to trackTimeMillis,
                EXTRA_COLLECTION_NAME to collectionName,
                EXTRA_DATA to releaseDate,
                EXTRA_PRIMARY_NAME to primaryGenreName,
                EXTRA_COUNTRY to country,
                EXTRA_LIKE to isFavorite,
                EXTRA_TRACK to track,
                EXTRA_SONG to previewUrl
            )
    }
}