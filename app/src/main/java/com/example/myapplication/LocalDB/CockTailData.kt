package com.example.myapplication.LocalDB
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = BaseData::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("baseId")
    )]
)

// note : saving binary blob on database is bad idea.
// so image files are stored at an local storage, and database has just a path of image file.
data class CockTailData (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "desc") var desc: String,
    var baseId: Int,
    @ColumnInfo(name = "manual") var recipe: String,
    @ColumnInfo(name = "rating") val rating: Int = 0,
    @ColumnInfo(name = "image_path") var imagePath: String?
    )