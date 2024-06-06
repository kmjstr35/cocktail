package com.example.myapplication.LocalDB

import androidx.room.Embedded
import androidx.room.Relation

data class CockTailByBase (
    @Embedded val base: BaseData,
    @Relation(
        parentColumn = "id",
        entityColumn = "baseId",
    )
    val cocktails: List<CockTailData>
)