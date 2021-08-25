package com.tutorials.eu.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tutorials.eu.favdish.FavDishApplication
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.DishDetailFragmentBinding
import com.tutorials.eu.favdish.databinding.FragmentFavouriteDishesBinding
import com.tutorials.eu.favdish.model.entities.FavDish
import com.tutorials.eu.favdish.view.activities.MainActivity
import com.tutorials.eu.favdish.view.adapter.FavDishAdapter
import com.tutorials.eu.favdish.view.dashboard.DashboardViewModel
import com.tutorials.eu.favdish.viewmodel.FavDishViewModel
import com.tutorials.eu.favdish.viewmodel.FavDishViewModelFactory

class FavouriteDishesFragment : Fragment() {


    private lateinit var mBinding: FragmentFavouriteDishesBinding


    private val viewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding=FragmentFavouriteDishesBinding.inflate(inflater,container,false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvFavourite.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = FavDishAdapter(this)

        viewModel.allFavourites.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                mBinding.rvFavourite.adapter = adapter
                adapter.setList(dishes)
            }


        }


    }

    fun toDetail(dish : FavDish){
        NavHostFragment.findNavController(this)
            .navigate(FavouriteDishesFragmentDirections.toDetailFromFav(dish))

        if (requireActivity() is MainActivity) {

            (activity as MainActivity).hideBottomNav()
        }


    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity) {

            (activity as MainActivity).showBottomNav()
        }
    }
}