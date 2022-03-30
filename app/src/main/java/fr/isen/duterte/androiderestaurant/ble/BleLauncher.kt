package fr.isen.duterte.androiderestaurant.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.isen.duterte.androiderestaurant.R
import fr.isen.duterte.androiderestaurant.databinding.ActivityBleLauncherBinding

class BleLauncher : AppCompatActivity()  {
    private lateinit var binding: ActivityBleLauncherBinding
    private lateinit var monRecycler: RecyclerView
    private lateinit var bleAdapter: BleAdapter
    private var isScanning : Boolean = false
    private lateinit var bleDevices: ArrayList<ExpandedDevice>

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    companion object {
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
        private const val ALL_PERMISSION_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBleLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bleDevices = arrayListOf()

        monRecycler = binding.recycleViewBle
        monRecycler.layoutManager = LinearLayoutManager(this)
        bleAdapter = BleAdapter(bleDevices) {
            it.expand = !it.expand
            bleAdapter.notifyDataSetChanged()
        }
        monRecycler.adapter = bleAdapter

        when {
            bluetoothAdapter?.isEnabled == true -> {
                    binding.playBtn.setOnClickListener {
                        startLeScanBLEWithPermission(!isScanning)
                    }

                binding.bleText.setOnClickListener {
                    startLeScanBLEWithPermission(!isScanning)
                }
            }
            bluetoothAdapter != null ->
                askBluetoothPermission()
            else -> {
                displayBLEUnavailable()
            }
        }
    }

    private fun displayBLEUnavailable() {
        binding.playBtn.isVisible = false
        binding.bleText.text = getString(R.string.blenondispo)
        binding.progressBarBLE.isVisible = false
    }


    private fun askBluetoothPermission() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }


    private fun startLeScanBLEWithPermission(enable : Boolean) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            startLeScanBLE(enable)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),ALL_PERMISSION_REQUEST_CODE)
        }
    }

    private fun startLeScanBLE(enable: Boolean) {
        bluetoothAdapter?.bluetoothLeScanner?.apply {
            if (enable){
                isScanning  = true
                startScan(scanCallback)

            } else {
                isScanning  = false
                stopScan(scanCallback)
            }
            handlePlayPauseAction()
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val index = bleDevices.indexOfFirst { it.scR.device.address.equals(result.device.address) }
            if (index != -1) {
                bleDevices[index].scR = result
            }
            else {
                bleDevices.add( ExpandedDevice(result))
            }
            Log.d("BLE","$result")
            bleDevices.sortBy {kotlin.math.abs(it.scR.rssi) }
            bleAdapter.notifyDataSetChanged()
        }
    }

    private fun handlePlayPauseAction() {
        if (isScanning) {
            binding.playBtn.setImageResource(R.drawable.pause)
            binding.bleText.text = getString(R.string.bleMessageEnCours)
            binding.progressBarBLE.isIndeterminate = true
        } else {
            binding.playBtn.setImageResource(R.drawable.play)
            binding.bleText.text = getString(R.string.bleMessage)
            binding.progressBarBLE.isIndeterminate = false
        }
    }


    override fun onStop() {
        super.onStop()
        startLeScanBLEWithPermission(false)
    }

}

