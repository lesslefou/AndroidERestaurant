package fr.isen.duterte.androiderestaurant.erestaurant

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.duterte.androiderestaurant.R
import fr.isen.duterte.androiderestaurant.api.APIItems
import fr.isen.duterte.androiderestaurant.databinding.ActivityCategoryAdapterBinding

class CategoryAdapter(val itemsAfficher : ArrayList<APIItems>, var listener: OnItemClickListener)
    : RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>(){

    private lateinit var binding: ActivityCategoryAdapterBinding
    inner class ItemViewHolder(binding: ActivityCategoryAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val imageView: ImageView = binding.itemImage
            val textView: TextView = binding.itemText

            fun onClick(item: APIItems) {
                listener.onItemClick(item)

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = ActivityCategoryAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(binding)
    }

    /**
     * Mise en place des élements de chaque item
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemsViewModel = itemsAfficher[position]
        val url = itemsAfficher[position].images[0]

        Picasso.get().load(url.ifEmpty { null }).fit().centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imageView);

        if (itemsViewModel.name_fr.length > 25) {
            holder.textView.text = itemsViewModel.name_fr.subSequence(0,25)
        }
        else {
            holder.textView.text = itemsViewModel.name_fr
        }
        Log.d("Adapter : ", itemsViewModel.name_fr)

        holder.itemView.setOnClickListener {
            holder.onClick(itemsAfficher[position])
        }


    }

    /**
     * retourne le nombre d'éléments dans la liste
     */
    override fun getItemCount(): Int {
        return itemsAfficher.size
    }

    interface OnItemClickListener{
        fun onItemClick(item: APIItems)
    }


}

