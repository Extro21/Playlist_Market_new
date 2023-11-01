package com.practicum.playlistmarket.media.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist",
//    foreignKeys = [
//        ForeignKey(
//            entity = TrackEntity::class,
//            parentColumns = ["trackId"],
//            childColumns = ["idTracks"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE
//        )
//    ]
)
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    val playListId: Int,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("description")
    val description: String?,
    @ColumnInfo("uri")
    val uri: String?,
    @ColumnInfo("idTrack")
    val idTracks: Int?,
    @ColumnInfo("quantityTracks")
    val quantityTracks: Int = 0,
)


