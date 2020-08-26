package ru.buylist.presentation.adapters.recipe_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeGeneralInfoAdapter() : RecyclerView.Adapter<GenericViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_general_info, parent, false)
        return GeneralInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 1

    private inner class GeneralInfoViewHolder(itemView: View) : GenericViewHolder(itemView) {

        override fun bind(position: Int) {

        }

    }

}