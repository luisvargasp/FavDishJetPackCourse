package com.tutorials.eu.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tutorials.eu.favdish.FavDishApplication
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.FragmentAllDishesBinding
import com.tutorials.eu.favdish.databinding.FragmentRandomDishBinding
import com.tutorials.eu.favdish.model.entities.FavDish
import com.tutorials.eu.favdish.model.entities.RandomDish
import com.tutorials.eu.favdish.utils.Constants
import com.tutorials.eu.favdish.viewmodel.FavDishViewModel
import com.tutorials.eu.favdish.viewmodel.FavDishViewModelFactory
import com.tutorials.eu.favdish.viewmodel.NotificationsViewModel
import com.tutorials.eu.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private lateinit var viewModel:RandomDishViewModel
    private  var mBinding: FragmentRandomDishBinding?=null

    private var mProgressDialog: Dialog? = null


    private val favDishViewModel : FavDishViewModel by viewModels{
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRandomDishBinding.inflate(inflater,container,false)


        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel=ViewModelProvider(this).get(RandomDishViewModel::class.java)

        viewModel.getRandomFromAPI()
        observer()
        mBinding!!.srlRandomDish.setOnRefreshListener {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            viewModel.getRandomFromAPI()
        }
    }

    private fun observer(){
        viewModel.loadRandomDish.observe(viewLifecycleOwner,{

            if (it && !mBinding!!.srlRandomDish.isRefreshing) {
                showCustomProgressDialog() // Used to show the progress dialog
            } else {
                hideProgressDialog()
            }




        })

        viewModel.randomDishLoadingError.observe(viewLifecycleOwner,{

            if (mBinding!!.srlRandomDish.isRefreshing) {
                mBinding!!.srlRandomDish.isRefreshing = false
            }


        })

        viewModel.randomDishResponse.observe(viewLifecycleOwner,{

            if (mBinding!!.srlRandomDish.isRefreshing) {
                mBinding!!.srlRandomDish.isRefreshing = false
            }

            setRandomDishResponseInUI(it.recipes[0])


        })
    }
    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {

        // Load the dish image in the ImageView.
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title

        // Default Dish Type
        var dishType: String = "other"

        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }

        // There is not category params present in the response so we will define it as Other.
        mBinding!!.tvCategory.text = "Other"

        var ingredients = ""
        for (value in recipe.extendedIngredients) {

            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }

        mBinding!!.tvIngredients.text = ingredients

        // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        mBinding!!.tvCookingTime.text =
            resources.getString(
                R.string.lbl_estimate_cooking_time,
                recipe.readyInMinutes.toString()
            )

        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )

        var addedToFavorite = false

        mBinding!!.ivFavoriteDish.setOnClickListener {

            if (addedToFavorite) {
                Toast.makeText(
                    requireActivity(),
                    "already added",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_REMOTE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                val mFavDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }

                mFavDishViewModel.insert(randomDishDetails)

                addedToFavorite = true

                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                Toast.makeText(
                    requireActivity(),
                    "added to favourite",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(requireActivity())

        mProgressDialog?.let {
            /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
            it.setContentView(R.layout.dialog_custom_progress)

            //Start the dialog and display it on screen.
            it.show()
        }
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    private fun hideProgressDialog() {
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding=null
    }
}