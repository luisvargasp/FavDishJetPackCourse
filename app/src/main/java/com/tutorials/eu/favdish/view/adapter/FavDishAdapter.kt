package com.tutorials.eu.favdish.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.ItemListLayoutBinding
import com.tutorials.eu.favdish.model.entities.FavDish
import com.tutorials.eu.favdish.utils.Constants
import com.tutorials.eu.favdish.view.activities.AddUpdateDishActivity
import com.tutorials.eu.favdish.view.fragments.AllDishesFragment
import com.tutorials.eu.favdish.view.fragments.FavouriteDishesFragment

class FavDishAdapter(private val fragment: Fragment) :RecyclerView.Adapter<FavDishAdapter.ViewHolder>(){
    private var dishes:List<FavDish> = listOf<FavDish>()



    class ViewHolder(view :ItemListLayoutBinding):RecyclerView.ViewHolder(view.root){
        val image=view.ivDishImage
        val title=view.tvDishTitle
        val ibMore=view.ibMore

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding:ItemListLayoutBinding= ItemListLayoutBinding.inflate(
            LayoutInflater.from(fragment.context),parent,false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dish = dishes.get(position)

        holder.title.text = dish.title

        Glide.with(fragment)
            .load(dish.image)
            .into(holder.image)

        holder.itemView.setOnClickListener {

            if (fragment is AllDishesFragment) {
                fragment.toDetail(dish)
            }else if(fragment is FavouriteDishesFragment){
                fragment.toDetail(dish)


            }
        }
        holder.ibMore.setOnClickListener{
            val popup=PopupMenu(fragment.context,holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter,popup.menu)
            popup.setOnMenuItemClickListener {
                if(it.itemId==R.id.action_edit_dish){
                    val intent= Intent(fragment.activity,AddUpdateDishActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAIL,dish)
                    fragment.requireActivity().startActivity(intent)

                }else if(it.itemId==R.id.action_delete_dish){

                    if(fragment is AllDishesFragment){
                        fragment.deleteDish(dish)
                    }

                }
                true

            }
            popup.show()
        }
        if(fragment is AllDishesFragment){
            holder.ibMore.visibility=View.VISIBLE
        }else{
            holder.ibMore.visibility=View.GONE
        }

    }

    override fun getItemCount(): Int {
            return   dishes.size
    }
    fun setList(list:List<FavDish>){
        dishes=list
        notifyDataSetChanged()
    }
}