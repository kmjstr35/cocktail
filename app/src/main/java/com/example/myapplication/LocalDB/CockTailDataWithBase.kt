package com.example.myapplication.LocalDB

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Relation

@Entity(indices = [Index(value = ["name"], unique = true)])
data class CockTailDataWithBase (
    @Embedded
    val cocktail: CockTailData,
    @Relation(
        parentColumn = "baseId",
        entityColumn = "id",
    )
    var base: BaseData)