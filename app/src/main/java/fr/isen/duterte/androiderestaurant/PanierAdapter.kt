package fr.isen.duterte.androiderestaurant

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.duterte.androiderestaurant.databinding.ActivityPanierAdapterBinding

class PanierAdapter(val data: ArrayList<ItemPanier>, val clickListener: (ItemPanier) -> Unit)
    :RecyclerView.Adapter<PanierAdapter.PanierViewHolder>(){
    private lateinit var binding: ActivityPanierAdapterBinding

    inner class PanierViewHolder (binding: ActivityPanierAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.itemImage
        val nameItem: TextView = binding.nameText
        val quantityItem: TextView = binding.quantityText
        val priceItem: TextView = binding.priceText
        val deleteItem: ImageView = binding.deleteBtn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanierViewHolder {
        binding = ActivityPanierAdapterBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return PanierViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PanierViewHolder, position: Int) {
        val item = data[position]
        if (item.apiItems.name_fr.length > 13) {
            holder.nameItem.text = item.apiItems.name_fr.subSequence(0,13)
        }
        else {
            holder.nameItem.text = item.apiItems.name_fr
        }
        holder.quantityItem.text = item.quantity.toString()
        var price = item.apiItems.prices[0].price.toFloat() * item.quantity
        holder.priceItem.text = price.toString()

        val url = item.apiItems.images[0]
        Picasso.get().load(url.ifEmpty { null }).fit().centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imageView);

        holder.deleteItem.setOnClickListener {
            data.remove(data[position])
            notifyItemRemoved(position)
            clickListener(item)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}