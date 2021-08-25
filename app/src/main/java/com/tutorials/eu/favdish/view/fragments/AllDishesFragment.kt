package com.tutorials.eu.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tutorials.eu.favdish.FavDishApplication
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.ActivityAddUpdateDishBinding
import com.tutorials.eu.favdish.databinding.DialogCustomListBinding
import com.tutorials.eu.favdish.databinding.FragmentAllDishesBinding
import com.tutorials.eu.favdish.model.entities.FavDish
import com.tutorials.eu.favdish.utils.Constants
import com.tutorials.eu.favdish.view.activities.AddUpdateDishActivity
import com.tutorials.eu.favdish.view.activities.MainActivity
import com.tutorials.eu.favdish.view.adapter.CustomListItemAdapter
import com.tutorials.eu.favdish.view.adapter.FavDishAdapter
import com.tutorials.eu.favdish.viewmodel.FavDishViewModel
import com.tutorials.eu.favdish.viewmodel.FavDishViewModelFactory
import com.tutorials.eu.favdish.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var mBinding: FragmentAllDishesBinding

    private lateinit var mFavDishAdapter: FavDishAdapter
     private lateinit var mCustomListDialog:Dialog



    private val favDishViewModel : FavDishViewModel by viewModels{
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAllDishesBinding.inflate(inflater,container,false)


        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }

            R.id.action_filter_dish->{

                filterListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.rvDishes.layoutManager=GridLayoutManager(requireContext(),2)
         mFavDishAdapter=FavDishAdapter(this)

        favDishViewModel.allDishes.observe(viewLifecycleOwner){
            dishes->
            dishes.let {

                mBinding.rvDishes.adapter=mFavDishAdapter
                mFavDishAdapter.setList(dishes)

            }



        }
    }
    fun toDetail(dish : FavDish){
        NavHostFragment.findNavController(this).navigate(AllDishesFragmentDirections.toDetail(dish))

        if(requireActivity() is MainActivity){

            (activity as MainActivity).hideBottomNav()
        }


    }

    fun deleteDish(dish:FavDish){

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Dish")
        builder.setMessage("are you sure to delete this ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("yes") { dialog, _ ->
            favDishViewModel.delete(dish)
            dialog.dismiss()
        }
        builder.setNegativeButton("no") { dialog, _ ->
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    override fun onResume() {
        super.onResume()

        if(requireActivity() is MainActivity){

            (activity as MainActivity).showBottomNav()
        }
    }

    private fun filterListDialog(){

         mCustomListDialog= Dialog(requireActivity())

        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text="Select item to filter"
        binding.rvList.layoutManager= LinearLayoutManager(requireContext())

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0,Constants.All_ITEMS)
        val adapter= CustomListItemAdapter(requireActivity(),this,dishTypes,Constants.FILTER_SELECTION)
        binding.rvList.adapter=adapter

        mCustomListDialog.show()
    }
    fun filterSelection(filter:String){

        mCustomListDialog.dismiss()

        if(filter==Constants.All_ITEMS){
            favDishViewModel.allDishes.observe(viewLifecycleOwner){
                    dishes->
                dishes.let {
                    mBinding.rvDishes.adapter=mFavDishAdapter
                    mFavDishAdapter.setList(dishes)
                }
            }


        }else{
            favDishViewModel.getFilteredList(filter).observe(viewLifecycleOwner){
                    dishes->
                dishes.let {
                    mBinding.rvDishes.adapter=mFavDishAdapter
                    mFavDishAdapter.setList(dishes)
                }
            }

        }


    }
}
