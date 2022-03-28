package fr.isen.duterte.androiderestaurant.ble


import android.bluetooth.le.ScanResult
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.duterte.androiderestaurant.R
import fr.isen.duterte.androiderestaurant.databinding.ActivityBleAdapterBinding

class BleAdapter(val data: ArrayList<ScanResult>, val clickListener: (ScanResult) -> Unit)
    : RecyclerView.Adapter<BleAdapter.BleViewHolder>(){
    private lateinit var binding: ActivityBleAdapterBinding

    inner class BleViewHolder (binding: ActivityBleAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val rrsiView: TextView = binding.rssiText
        val nameBle: TextView = binding.name
        val uuid: TextView = binding.uuid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        binding = ActivityBleAdapterBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return BleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        val device = data[position]
        Log.d("BLEAdapter", device.toString())
        if (device.device.name == null) {
            holder.nameBle.text = holder.itemView.context.getString(R.string.deviceUnknown)
        }
        else {
            holder.nameBle.text = device.device.name
        }
        holder.uuid.text = device.device.address
        holder.rrsiView.text = device.rssi.toString()

        holder.nameBle.setOnClickListener {
            clickListener(device)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

}