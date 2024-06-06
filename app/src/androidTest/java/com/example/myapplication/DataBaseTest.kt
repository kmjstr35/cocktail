package com.example.myapplication
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.LocalDB.BaseData
import com.example.myapplication.LocalDB.CockTailDao
import com.example.myapplication.LocalDB.CockTailData
import com.example.myapplication.LocalDB.CockTailLocalDB
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DataBaseTest {
    private lateinit var cockDao: CockTailDao
    private lateinit var db: CockTailLocalDB

    @Before
    fun createDb() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx,
            CockTailLocalDB::class.java).build()

        cockDao = db.getCocktailDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun baseInsertTest() {
        val base = BaseData(name = "whiskey")
        val base2 = BaseData(name = "rum")
        db.getBaseDao().insert(base, base2, base2)
        val ret = db.getBaseDao().searchByName(base.name)

        cockDao.insert(
            CockTailData(
            name = "highball",
            desc = "",
            baseId =  ret!!,
            recipe = "",
            rating = 0,
            imagePath = null))

        val whiskey = db.getBaseDao().searchCockTailByBaseName("whiskey")
        assertEquals(whiskey.cocktails[0].name, "highball")
        val id2 = db.getBaseDao().searchByName(base2.name)
        Log.d("test", "$base2 $id2")
    }
}