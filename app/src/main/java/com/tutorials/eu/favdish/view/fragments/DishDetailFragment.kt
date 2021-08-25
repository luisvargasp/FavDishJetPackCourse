package com.tutorials.eu.favdish.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tutorials.eu.favdish.FavDishApplication
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.DishDetailFragmentBinding
import com.tutorials.eu.favdish.databinding.FragmentAllDishesBinding
import com.tutorials.eu.favdish.model.entities.FavDish
import com.tutorials.eu.favdish.utils.Constants
import com.tutorials.eu.favdish.viewmodel.FavDishViewModel
import com.tutorials.eu.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOException

class DishDetailFragment : Fragment() {



    private val viewModel: FavDishViewModel by viewModels{
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }
    private lateinit var mBinding:DishDetailFragmentBinding

    private var mFavDishDetails: FavDish? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        mBinding = DishDetailFragmentBinding.inflate(inflater,container,false)


        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args:DishDetailFragmentArgs by navArgs()
        mFavDishDetails=args.dish

        args.let {

            try {
                // Load the dish image in the ImageView.
                Glide.with(requireActivity())
                    .load(it.dish.image)
                    .centerCrop()
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
                            Palette.from(resource?.toBitmap()!!).generate(){

                                palette->
                                val color=palette?.vibrantSwatch?.rgb?:0
                                mBinding.rlDishDetailMain.setBackgroundColor(color)

                            }


                            return false
                        }


                    })
                    .into(mBinding.ivDishImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mBinding!!.tvTitle.text = it.dish.title
            mBinding!!.tvType.text =
                it.dish.type.capitalize(java.util.Locale.ROOT) // Used to make first letter capital
            mBinding!!.tvCategory.text = it.dish.category
            mBinding!!.tvIngredients.text = it.dish.ingredients
            mBinding!!.tvCookingDirection.text = it.dish.directionToCook
            mBinding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dish.cookingTime)
        }
        Log.i("dishArg",args.dish.image)

        if(args.dish.favouriteDish){
            mBinding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_favorite_selected))

        }else{
            mBinding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_favorite_unselected))
        }


        mBinding.ivFavoriteDish.setOnClickListener{
            args.dish.favouriteDish = !args.dish.favouriteDish
            viewModel.update(args.dish)

            if(args.dish.favouriteDish){
                mBinding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_favorite_selected))

            }else{
                mBinding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_favorite_unselected))

            }




        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_share_dish -> {

                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                mFavDishDetails?.let {

                    var image = ""

                    if (it.imageSource == Constants.DISH_IMAGE_SOURCE_REMOTE) {
                        image = it.image
                    }

                    var cookingInstructions = ""

                    // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cookingInstructions = Html.fromHtml(
                            it.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()
                    } else {
                        @Suppress("DEPRECATION")
                        cookingInstructions = Html.fromHtml(it.directionToCook).toString()
                    }

                    extraText =
                        "$image \n" +
                                "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                                "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                                "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }


                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}