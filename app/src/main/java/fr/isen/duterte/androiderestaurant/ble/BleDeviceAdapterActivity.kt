package fr.isen.duterte.androiderestaurant.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import fr.isen.duterte.androiderestaurant.R
import java.util.*
import kotlin.collections.ArrayList

class BleDeviceAdapterActivity(
    val context: Context,
    val serviceList: MutableList<BleService>,
    private val readCharacteristicCallback: (BluetoothGattCharacteristic) -> Unit,
    private val writeCharacteristicCallback: (BluetoothGattCharacteristic) -> Unit,
    private val notifyCharacteristicCallback: (BluetoothGattCharacteristic, Boolean) -> Unit
    ):ExpandableRecyclerViewAdapter<BleDeviceAdapterActivity.ServiceViewHolder, BleDeviceAdapterActivity.CharacteristicViewHolder>(
        serviceList
    ){

    class ServiceViewHolder(itemView: View) : GroupViewHolder(itemView) {
        val nameService: TextView = itemView.findViewById(R.id.serviceName)
        val uuidService: TextView = itemView.findViewById(R.id.serviceUuid)
        private val serviceArrow: View = itemView.findViewById(R.id.serviceArrow)
        override fun expand() {
            serviceArrow.animate().rotation(-180f).setDuration(400L).start()
        }

        override fun collapse() {
            serviceArrow.animate().rotation(0f).setDuration(400L).start()
        }
    }

    class CharacteristicViewHolder(itemView: View) : ChildViewHolder(itemView) {
        val characteristicName : TextView = itemView.findViewById(R.id.characteristicName)
        val characteristicUuid : TextView = itemView.findViewById(R.id.characteristicUuid)
        val characteristicProperties : TextView = itemView.findViewById(R.id.characteristicProperties)
        val characteristicValue : TextView = itemView.findViewById(R.id.characteristicValue)
        val lectureBtn : Button = itemView.findViewById(R.id.readAction)
        val ecrireBtn : Button = itemView.findViewById(R.id.writeAction)
        val notifierBtn : Button = itemView.findViewById(R.id.notifyAction)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder =
        ServiceViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_ble_device_service, parent, false)
        )

    override fun onCreateChildViewHolder( parent: ViewGroup, viewType: Int): CharacteristicViewHolder =
        CharacteristicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_ble_device_characteristic, parent, false)
        )

    override fun onBindGroupViewHolder( holder: ServiceViewHolder, flatPosition: Int, group: ExpandableGroup<*>
    ) {
        val title = BleUUIDAttributes.getBLEAttributeFromUUID(group.title).title
        holder.nameService.text = title
        holder.uuidService.text = group.title
    }

    override fun onBindChildViewHolder(holder: CharacteristicViewHolder, flatPosition: Int, group: ExpandableGroup<*>, childIndex: Int
    ) {
        val characteristic = (group.items[childIndex] as BluetoothGattCharacteristic)
        val title = BleUUIDAttributes.getBLEAttributeFromUUID(characteristic.uuid.toString()).title

        val uuidMessage = "UUID : ${characteristic.uuid}"
        holder.characteristicUuid.text = uuidMessage

        holder.characteristicName.text = title
        val properties = arrayListOf<String>()

        addPropertyFromCharacteristic(
            characteristic,
            properties,
            "Lecture",
            BluetoothGattCharacteristic.PROPERTY_READ,
            holder.lectureBtn,
            readCharacteristicCallback
        )

        addPropertyFromCharacteristic(
            characteristic,
            properties,
            "Ecrire",
            (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE),
            holder.ecrireBtn,
            writeCharacteristicCallback
        )

        addPropertyNotificationFromCharacteristic(
            characteristic,
            properties,
            holder.notifierBtn,
            notifyCharacteristicCallback
        )

        val proprietiesMessage = "Proprietés : ${properties.joinToString()}"
        holder.characteristicProperties.text = proprietiesMessage
        characteristic.value?.let {
            val hex = it.joinToString("") { byte -> "%02x".format(byte)}.uppercase(Locale.FRANCE)
            val value = "Valeur : ${String(it)} Hex : 0x$hex"
            holder.characteristicValue.visibility = View.VISIBLE
            holder.characteristicValue.text = value
        }
    }

    /**
     * On vient check quelles propriétés est à activer en mettant sur écoute le bouton associé et en colorant le text en orange
     */
    private fun addPropertyFromCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        properties: ArrayList<String>,
        propertyName: String,
        propertyType: Int,
        propertyAction: Button,
        propertyCallBack: (BluetoothGattCharacteristic) -> Unit
    ) {
        if ((characteristic.properties and propertyType) != 0) {
            properties.add(propertyName)
            propertyAction.isEnabled = true
            propertyAction.setTextColor(ContextCompat.getColor(context, R.color.orange))

            propertyAction.setOnClickListener {
                propertyCallBack.invoke(characteristic)
            }
        }
    }

    /**
     * On vient check si la caractéristique à notifiable
     */
    private fun addPropertyNotificationFromCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        properties: ArrayList<String>,
        propertyAction: Button,
        propertyCallBack: (BluetoothGattCharacteristic, Boolean) -> Unit
    ) {
        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            properties.add("Notifier")
            propertyAction.isEnabled = true
            val isNotificationEnable = characteristic.descriptors.any {
                it.value?.contentEquals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) ?: false
            }
            if (isNotificationEnable) {
                propertyAction.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
                propertyAction.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            } else {
                propertyAction.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                propertyAction.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }
            propertyAction.setOnClickListener {
                propertyCallBack.invoke(characteristic, !isNotificationEnable)
            }
        }
    }

    fun updateFromChangedCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        serviceList.forEach {
            val index = it.items.indexOf(characteristic)
            if(index != -1) {
                it.items.removeAt(index)
                it.items.add(index, characteristic)
            }
        }
    }

}