package com.example.myapplication.EditCockTail

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
import com.example.myapplication.LocalDB.CockTailLocalDB
import com.example.myapplication.LocalDB.DbModule
import com.example.myapplication.databinding.AddCocktailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class EditCockTail : ComponentActivity() {
    lateinit var binding: AddCocktailBinding
    lateinit var database: CockTailLocalDB
    var imageUri: Uri? = null
    private lateinit var mediaPicker : ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = AddCocktailBinding.inflate(layoutInflater)
        database = DbModule().provideDbInstance(this)

        mediaPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if(uri != null) {

                Glide.with(this)
                    .load(uri)
                    .into(binding.addImage)

                imageUri = uri
                Log.d("test", uri.toString())
            }
        }


        binding.addRecipe.setText("수정")
        binding.materialToolbar2.title = "수정"

        binding.addImage.isClickable = true

        val itemId = intent.extras!!.getInt("item_id")

        Log.d("id", "$itemId")
        val data = database.getCocktailDao().getFullDataById(itemId)

        data.observe(this) { data ->
            val cocktail = data.cocktail
            val base = data.base.name

            binding.addBaseTxt.setText(base)
            binding.addName.setText(cocktail.name)
            binding.addShortDesc.setText(cocktail.desc)
            binding.addRecipeText.setText(cocktail.recipe)

            if(cocktail.imagePath != null) {
                Glide.with(this)
                    .load(cocktail.imagePath)
                    .into(binding.addImage)
            }


            binding.addRecipe.setOnClickListener {
                val name = binding.addName.text.toString()
                val desc = binding.addShortDesc.text.toString()
                val recipe = binding.addRecipeText.text.toString()
                val base = binding.addBaseTxt.text.toString()

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
                            val img = Glide.with(this@EditCockTail)
                                .asBitmap()
                                .load(imageUri)
                                .submit()
                                .get()

                            val file =
                                File(this@EditCockTail.filesDir, UUID.randomUUID().toString() + ".png")
                            val fOut = FileOutputStream(file)

                            img.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                            fOut.close()
                            path = file.absolutePath
                        }

                        data.cocktail.name = name
                        data.cocktail.desc = desc
                        data.cocktail.recipe = recipe

                        var baseId = database.getBaseDao().searchByName(base)
                        if(baseId == null) {
                            database.getBaseDao().insert(BaseData(name = base))
                            baseId = database.getBaseDao().searchByName(base)
                        }

                        data.cocktail.baseId = baseId!!
                        if(path != null)
                            cocktail.imagePath = path
                        database.getCocktailDao().Update(data.cocktail)

                        Log.d("edit cocktail", "input finished. go back to the main activity")
                        finish()
                    }
                }
            }
        }

        binding.addImage.setOnClickListener {
            mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }



        binding.addCancel.setOnClickListener {
            finish()
        }


        setContentView(binding.root)
        super.onCreate(savedInstanceState)
    }
}