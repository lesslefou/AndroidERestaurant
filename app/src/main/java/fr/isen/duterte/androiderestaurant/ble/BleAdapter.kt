package fr.isen.duterte.androiderestaurant.ble


import android.bluetooth.le.ScanResult
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import fr.isen.duterte.androiderestaurant.R
import fr.isen.duterte.androiderestaurant.databinding.ActivityBleAdapterBinding

class BleAdapter(val data: ArrayList<ExpandedDevice>, val clickListener: (ExpandedDevice) -> Unit)
    : RecyclerView.Adapter<BleAdapter.BleViewHolder>(){
    private lateinit var binding: ActivityBleAdapterBinding

    inner class BleViewHolder (binding: ActivityBleAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val rrsiView: TextView = binding.rssiText
        val nameBle: TextView = binding.name
        val uuid: TextView = binding.uuid
        val uuidExpanded : TextView = binding.uuidExpanded
        val proprieteExpanded : TextView = binding.proprieteExpanded
        val valeurExpanded : TextView = binding.valeurExpanded
        val lectureBtn : Button = binding.btnLecture
        val ecrireBtn : Button = binding.btnEcrire
        val notifierBtn : Button = binding.btnNotifier

        var itemLayout : ConstraintLayout = binding.itemView
        var expandedLayout : ConstraintLayout = binding.expandedView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        binding = ActivityBleAdapterBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return BleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        val device = data[position]
        Log.d("BLEAdapter", device.toString())
        if (device.scR.device.name == null) {
            holder.nameBle.text = holder.itemView.context.getString(R.string.deviceUnknown)
        }
        else {
            holder.nameBle.text = device.scR.device.name
        }
        holder.uuid.text = device.scR.device.address
        holder.rrsiView.text = device.scR.rssi.toString()

        holder.uuidExpanded.text = holder.itemView.context.getString(R.string.uuid) + device.scR.device.address
        holder.proprieteExpanded.text = holder.itemView.context.getString(R.string.proprietes) + device.scR.device.address //CHANGER
        holder.valeurExpanded.text = holder.itemView.context.getString(R.string.valeur) + device.scR.device.address //CHANGER


        holder.expandedLayout.visibility = if (device.expand) View.VISIBLE else View.GONE

        holder.itemLayout.setOnClickListener {
            clickListener(device)
        }


        holder.lectureBtn.setOnClickListener {

        }
        holder.ecrireBtn.setOnClickListener {

        }
        holder.notifierBtn.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

}