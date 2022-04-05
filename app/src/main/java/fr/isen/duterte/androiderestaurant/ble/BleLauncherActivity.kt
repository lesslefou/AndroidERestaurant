package fr.isen.duterte.androiderestaurant.ble

import android.Manifest
import android.bluetooth.*
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

class BleLauncherActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityBleLauncherBinding
    private lateinit var monRecycler: RecyclerView
    private lateinit var bleLauncherAdapter: BleLauncherAdapter
    private var isScanning : Boolean = false
    private lateinit var bleDevices: ArrayList<ScanResult>
    private lateinit var deviceToConnect : BluetoothDevice

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
        bleLauncherAdapter = BleLauncherAdapter(bleDevices) {
            deviceToConnect = it
            connectionToDevice()
        }
        monRecycler.adapter = bleLauncherAdapter

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

    /**
     * Arret de la recherche en cours si pas deja fait et redirection sur la page du device
     */
    private fun connectionToDevice() {
        if (isScanning){
            onStop()
        }

        val intent = Intent(this@BleLauncherActivity, BleDeviceActivity::class.java)
        intent.putExtra("Device",deviceToConnect)
        startActivity(intent)
    }

    private fun displayBLEUnavailable() {
        binding.playBtn.isVisible = false
        binding.bleText.text = getString(R.string.blenondispo)
        binding.progressBarBLE.isVisible = false
    }


    /**
     * Demande acceptation des permissions
     */
    private fun askBluetoothPermission() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }


    /**
     * Vérification des permissions avant de lancer la recherche bluetooth
     */
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

    /**
     * Activation et Désactivation de la recherche bluetooth
     */
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

    /**
     * Enregistre tout les nouveaux device dans une arrayList et met à jour les anciens deja présent sur la liste.
     * Avec ordonnance croissante des valeurs rrsi
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val index = bleDevices.indexOfFirst { it.device.address.equals(result.device.address) }
            if (index != -1) {
                bleDevices[index] = result
            }
            else {
                bleDevices.add(result)
            }
            Log.d("BLE","$result")
            bleDevices.sortBy {kotlin.math.abs(it.rssi) }
            bleLauncherAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Change l'état du bouton et du text de chargement
     */
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

