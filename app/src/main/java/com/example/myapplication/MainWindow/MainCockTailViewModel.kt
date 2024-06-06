package com.example.myapplication.MainWindow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.myapplication.LocalDB.CockTailLocalDB
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainCockTailViewModel
    @Inject constructor (val handle : SavedStateHandle,
                         private val cockTailDb: CockTailLocalDB) : ViewModel() {
    val cockTailList = cockTailDb.getCocktailDao().sortByRating()
}