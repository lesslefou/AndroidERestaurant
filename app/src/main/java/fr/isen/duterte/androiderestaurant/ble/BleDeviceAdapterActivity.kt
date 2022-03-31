package fr.isen.duterte.androiderestaurant.ble

import android.bluetooth.BluetoothGattCharacteristic
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
import fr.isen.duterte.androiderestaurant.databinding.ActivityBleDeviceAdapterBinding

class BleDeviceAdapterActivity(val device: ScanResult, val data: ArrayList<ExpandedCharacteristic>, val clickListener: (ExpandedCharacteristic) -> Unit)
    : RecyclerView.Adapter<BleDeviceAdapterActivity.BleDeviceViewHolder>(){
    private lateinit var binding: ActivityBleDeviceAdapterBinding

    inner class BleDeviceViewHolder (binding: ActivityBleDeviceAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        binding = ActivityBleDeviceAdapterBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return BleDeviceViewHolder(binding)
    }


    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        val characteristic = data[position]
        Log.d("BleAdapter", device.device.address)
        if (device.device.name == null) {
            holder.nameBle.text = holder.itemView.context.getString(R.string.deviceUnknown)
        }
        else {
            holder.nameBle.text = device.device.name
        }
        holder.uuid.text = device.device.address

        /*
        holder.uuidExpanded.text = holder.itemView.context.getString(R.string.uuid) + characteristic.scR!!.device.address
        holder.proprieteExpanded.text = holder.itemView.context.getString(R.string.proprietes) + characteristic.scR!!.device.address //CHANGER
        holder.valeurExpanded.text = holder.itemView.context.getString(R.string.valeur) + characteristic.scR!!.device.address //CHANGER


        holder.expandedLayout.visibility = if (characteristic.expand) View.VISIBLE else View.GONE
*/
        holder.itemLayout.setOnClickListener {
            clickListener(characteristic)
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