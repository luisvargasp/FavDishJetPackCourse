package com.tutorials.eu.favdish.view.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.tutorials.eu.favdish.FavDishApplication
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.ActivityAddUpdateDishBinding
import com.tutorials.eu.favdish.databinding.DialogCustomImageSelectionBinding
import com.tutorials.eu.favdish.databinding.DialogCustomListBinding
import com.tutorials.eu.favdish.model.database.FavDishRepository
import com.tutorials.eu.favdish.model.entities.FavDish
import com.tutorials.eu.favdish.utils.Constants
import com.tutorials.eu.favdish.view.adapter.CustomListItemAdapter
import com.tutorials.eu.favdish.viewmodel.FavDishViewModel
import com.tutorials.eu.favdish.viewmodel.FavDishViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.jar.Manifest

/**
 * A screen where we can add and update the dishes.
 */
class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    private var imagePath=""
    private lateinit var customDialog:Dialog
    private var mFavDishDetails:FavDish?=null
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the Intent


            val thumbnail:Bitmap=result.data?.extras!!.get("data") as Bitmap
            //mBinding.ivDishImage.setImageBitmap(thumbnail)
            Glide.with(this@AddUpdateDishActivity)
                .load(thumbnail)
                .centerCrop()
                .into(mBinding.ivDishImage)
            //do stuff here

            imagePath=saveImage(thumbnail)
        }
    }

    val startForResultGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the Intent

            val selectedPhotoUri=result.data!!.data
            //mBinding.ivDishImage.setImageURI(selectedPhotoUri)

            Glide.with(this@AddUpdateDishActivity)
                .load(selectedPhotoUri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        val bitmap:Bitmap= resource?.toBitmap()!!
                        imagePath=saveImage(bitmap)

                        return false
                    }


                })
                .into(mBinding.ivDishImage)
            //do stuff here
        }
    }

    private val favDishViewModel :FavDishViewModel by viewModels{
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_DISH_DETAIL)){
            mFavDishDetails=intent.getParcelableExtra(Constants.EXTRA_DISH_DETAIL)
        }

        mFavDishDetails?.let {

            imagePath=it.image

            Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .into(mBinding.ivDishImage)

            mBinding.etTitle.setText(it.title)
            mBinding.etType.setText(it.type)
            mBinding.etCategory.setText(it.category)
            mBinding.etIngredients.setText(it.ingredients)
            mBinding.etCookingTime.setText(it.cookingTime)
            mBinding.etDirectionToCook.setText(it.directionToCook)

            mBinding.btnAddDish.text="Update Dish"

        }

        mBinding.ivAddDishImage.setOnClickListener(this@AddUpdateDishActivity)
        mBinding.etCategory.setOnClickListener(this@AddUpdateDishActivity)
        mBinding.etCookingTime.setOnClickListener(this@AddUpdateDishActivity)
        mBinding.etType.setOnClickListener(this@AddUpdateDishActivity)
        mBinding.btnAddDish.setOnClickListener(this@AddUpdateDishActivity)





    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.iv_add_dish_image -> {

                // TODO Step 6: Replace the Toast Message with the custom dialog.
                // START
                customImageSelectionDialog()
                // END
                return
            }

            R.id.et_type -> {

                // TODO Step 6: Replace the Toast Message with the custom dialog.
                // START
                customItemsDialog("SELECT DISH TYPE",Constants.dishTypes(),Constants.DISH_TYPE)
                // END
                return
            }
            R.id.et_category -> {

                // START
                customItemsDialog("SELECT DISH CATEGORY",Constants.dishCategories(),Constants.DISH_CATEGORY)

                // END
                return
            }

            R.id.et_cooking_time -> {

                // START
                customItemsDialog("SELECT DISH COOKING TIME",Constants.dishCookingTime(),Constants.DISH_COOKING_TIME)
                // END
                return
            }

            R.id.btn_add_dish -> {

                // START
                val title=mBinding.etTitle.text.toString().trim()
                val type=mBinding.etType.text.toString().trim()

                val category=mBinding.etCategory.text.toString().trim()

                val ingredients=mBinding.etIngredients.text.toString().trim()
                val cookingTime=mBinding.etCookingTime.text.toString().trim()
                val cookingDirection=mBinding.etDirectionToCook.text.toString().trim()

                when{

                    TextUtils.isEmpty(imagePath)->{
                        Toast.makeText(this,"image is missing",Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(title)->{
                        Toast.makeText(this,"title is missing",Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(type)->{
                        Toast.makeText(this,"type is missing",Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(category)->{
                        Toast.makeText(this,"category is missing",Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(ingredients)->{
                        Toast.makeText(this,"ingredients is missing",Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(cookingTime)->{
                        Toast.makeText(this,"cookingTime is missing",Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(cookingDirection)->{
                        Toast.makeText(this,"cookingDirection is missing",Toast.LENGTH_SHORT).show()
                    }
                    else->{

                        var id=0
                        var imageResource=Constants.DISH_IMAGE_SOURCE_LOCAL
                        var favouriteDish=false
                        mFavDishDetails?.let {

                            if(it.id!=0){
                                id=it.id
                                imageResource=it.imageSource
                                favouriteDish=it.favouriteDish

                            }



                        }

                        if(id==0){
                            favDishViewModel.insert(FavDish(id=id,image= imagePath, imageSource= imageResource,

                                title=title,type=type,category=category,ingredients = ingredients,cookingTime=cookingTime,
                                directionToCook = cookingDirection,favouriteDish = favouriteDish))

                        }else{
                            favDishViewModel.update(FavDish(id=id,image= imagePath, imageSource= imageResource,

                                title=title,type=type,category=category,ingredients = ingredients,cookingTime=cookingTime,
                                directionToCook = cookingDirection,favouriteDish = favouriteDish))
                        }



                       // Toast.makeText(this,"Dish Added",Toast.LENGTH_SHORT).show()

                        finish()

                    }
                }

                // END
                return
            }
        }
    }

    /**
     * A function for ActionBar setup.
     */
    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        if(mFavDishDetails!=null&&mFavDishDetails!!.id!=0){
            supportActionBar.let {

                it?.title="Edit"
            }

        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }


    // TODO Step 5: Create a function to launch the custom dialog.
    // START
    /**
     * A function to launch the custom image selection dialog.
     */
    private fun customImageSelectionDialog() {
       val dialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(binding.root)

        // TODO Step 7: Assign the click for Camera and Gallery. Show the Toast message for now.
        // START
         binding.tvCamera.setOnClickListener {

             Dexter.withContext(this).withPermissions(
                 android.Manifest.permission.READ_EXTERNAL_STORAGE,
              //   android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                 android.Manifest.permission.CAMERA

                 ).withListener(object :MultiplePermissionsListener{
                 override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                     report?.let {
                         if(report.areAllPermissionsGranted()){


                             val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                             startForResult.launch(intent)




                         }
                     }

                 }

                 override fun onPermissionRationaleShouldBeShown(
                     permissions: MutableList<PermissionRequest>?,
                     token: PermissionToken?
                 ) {

                     showRationalDialogForPermissions()

                     //SHOW OTHER DIALOG
                 }


             }).onSameThread().check()



             dialog.dismiss()
         }

         binding.tvGallery.setOnClickListener {
             Dexter.withContext(this).withPermission(
                 android.Manifest.permission.READ_EXTERNAL_STORAGE
                // android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

             ).withListener(object : PermissionListener {



                 override fun onPermissionGranted(report: PermissionGrantedResponse?) {



                     val intentGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                     startForResultGallery.launch(intentGallery)





                 }

                 override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                     TODO("Not yet implemented")
                 }

                 override fun onPermissionRationaleShouldBeShown(
                     p0: PermissionRequest?,
                     p1: PermissionToken?
                 ) {
                     showRationalDialogForPermissions()


                 }


             }).onSameThread().check()

            // Toast.makeText(this@AddUpdateDishActivity, "You have clicked on the Gallery.", Toast.LENGTH_SHORT).show()
             dialog.dismiss()
         }
        // END

        //Start the dialog and display it on screen.
        dialog.show()
    }
    // END

    private  fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("permissions needed")
            .setPositiveButton("Go settings")
            {_,_->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)

                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
              }


            .setNegativeButton("Cancel")
            {dialog,_->
                dialog.dismiss()
            }
    }
    private fun saveImage(image:Bitmap):String{

        val wrapper=ContextWrapper(applicationContext)
        var file=wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file= File(file,"${UUID.randomUUID()}.jpg")

        try {

            val stream:OutputStream=FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()


        }catch (e:IOException){
            e.printStackTrace()

        }

        return file.absolutePath


    }
    private fun customItemsDialog(title :String,itemsList:List<String>,selection:String){
         customDialog=Dialog(this)

        val binding:DialogCustomListBinding= DialogCustomListBinding.inflate(layoutInflater)
        customDialog.setContentView(binding.root)

        binding.tvTitle.text=title
        binding.rvList.layoutManager=LinearLayoutManager(this)
        val adapter=CustomListItemAdapter(this,null,itemsList,selection)
        binding.rvList.adapter=adapter

        customDialog.show()

    }

    fun selectedListItem(item: String, selection: String) {

        when(selection){
            Constants.DISH_TYPE->mBinding.etType.setText(item)
            Constants.DISH_CATEGORY->mBinding.etCategory.setText(item)
            Constants.DISH_COOKING_TIME->mBinding.etCookingTime.setText(item)
        }

        customDialog.dismiss()

    }

    companion object{
            private const val  IMAGE_DIRECTORY="FavDishesImages"



    }


}