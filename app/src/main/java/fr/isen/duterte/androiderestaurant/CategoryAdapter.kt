package fr.isen.duterte.androiderestaurant

import android.accounts.AccountManager.get
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatDrawableManager.get
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.duterte.androiderestaurant.databinding.ActivityCategoryAdapterBinding
import fr.isen.duterte.androiderestaurant.databinding.ActivityItemViewBinding
import java.util.*

class CategoryAdapter(val itemsAfficher : List<Item>, var listener: OnItemClickListener)
    : RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>(){

    private lateinit var binding: ActivityCategoryAdapterBinding
    inner class ItemViewHolder(binding: ActivityCategoryAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val imageView: ImageView = itemView.findViewById(R.id.itemImage)
            val textView: TextView = itemView.findViewById(R.id.itemText)

            fun onClick(item: Item) {
                listener.onItemClick(item)

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = ActivityCategoryAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val ItemsViewModel = itemsAfficher[position]

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)

        Picasso.get().load(itemsAfficher[position].image).into(holder.imageView);

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.name_fr

        holder.itemView.setOnClickListener {
            holder.onClick(ItemsViewModel)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return itemsAfficher.size
    }

    interface OnItemClickListener{
        fun onItemClick(item: Item)
    }


}

