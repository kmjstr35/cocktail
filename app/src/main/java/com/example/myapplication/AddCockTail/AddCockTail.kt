package com.example.myapplication.AddCockTail

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.myapplication.LocalDB.BaseData
import com.example.myapplication.LocalDB.CockTailData
import com.example.myapplication.LocalDB.CockTailLocalDB
import com.example.myapplication.LocalDB.DbModule
import com.example.myapplication.databinding.AddCocktailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@AndroidEntryPoint
class AddCockTail : ComponentActivity() {
    private lateinit var binding : AddCocktailBinding
    private lateinit var localDb : CockTailLocalDB

    private var imageUri : Uri? = null
    private lateinit var mediaPicker : ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = AddCocktailBinding.inflate(layoutInflater)
        localDb = DbModule().provideDbInstance(this)
        binding.materialToolbar2.title = "나만의 레시피"

        // note : startActivityForResult api is deprecated
        // and android documentation highly recommend to use registerForActivityResult api
        // https://developer.android.com/training/basics/intents/result
        mediaPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if(uri != null) {

                Glide.with(this)
                    .load(uri)
                    .into(binding.addImage)

                imageUri = uri
                Log.d("test", uri.toString())
            }
        }

        setContentView(binding.root)

        binding.addRecipe.setOnClickListener {
            val name = binding.addName.text.toString()
            val desc = binding.addShortDesc.text.toString()
            val recipe = binding.addRecipeText.text.toString()
            val base = binding.addBaseTxt.text.toString()


            // TODO : do not validate inputs manually. use validator api instead.

            if (name == "" || desc == "" || recipe == "" || base == "") {
                val toast = Toast(this)
                toast.duration = Toast.LENGTH_SHORT
                toast.setText("입력하지 않은 필드가 있습니다")
                toast.show()
            } else {
                // use coroutine for prevent blocking on main thread
                CoroutineScope(Dispatchers.IO).launch {
                    var path: String? = null

                    if (imageUri != null) {
                        val img = Glide.with(this@AddCockTail)
                            .asBitmap()
                            .load(imageUri)
                            .submit()
                            .get()

                        val file =
                            File(this@AddCockTail.filesDir, UUID.randomUUID().toString() + ".png")
                        val fOut = FileOutputStream(file)

                        img.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                        fOut.close()
                        path = file.absolutePath
                    }

                    val base = BaseData(name = base)
                    localDb.getBaseDao().insert(base)
                    val baseId = localDb.getBaseDao().searchByName(base.name)!!

                    val cocktail = CockTailData(name = name,
                        desc = desc,
                        recipe = recipe,
                        imagePath = path,
                        baseId = baseId)

                    localDb.getCocktailDao().insert(cocktail)
                    Log.d("add cocktail", "input finished. go back to the main activity")
                    finish()
                }
            }
        }

        binding.addCancel.setOnClickListener {
            finish()
        }

        binding.addImage.setOnClickListener {
            mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        super.onCreate(savedInstanceState)
        Log.d("add cocktail", "initialize add cocktail view")
    }
}