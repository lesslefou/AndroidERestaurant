package fr.isen.duterte.androiderestaurant.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.isen.duterte.androiderestaurant.R
import fr.isen.duterte.androiderestaurant.databinding.ActivityBleDeviceBinding

class BleDeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBleDeviceBinding
    private lateinit var monRecycler: RecyclerView
    private lateinit var bleDeviceAdapter: BleDeviceAdapterActivity
    private lateinit var bleDevice : ScanResult
    private lateinit var deviceCharacteristics: ArrayList<ExpandedCharacteristic>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBleDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bleDevice = intent.getParcelableExtra("Device")!!

        if (bleDevice.device.name == null) {
            binding.deviceName.text = getText(R.string.deviceUnknown)
        }
        else {
            binding.deviceName.text = bleDevice.device.name
        }

        var statusString = getString(R.string.status)
        if (bleDevice.isConnectable) {
            statusString += " " + getString(R.string.connected)
        } else {
            statusString += " " + getString(R.string.disconnected)
        }
        binding.status.text = statusString


        deviceCharacteristics = arrayListOf()

        monRecycler = binding.recycleViewCharacteristics
        monRecycler.layoutManager = LinearLayoutManager(this)
        bleDeviceAdapter = BleDeviceAdapterActivity(bleDevice,deviceCharacteristics) {
            it.expand = !it.expand
            bleDeviceAdapter.notifyDataSetChanged()
        }

    }
}