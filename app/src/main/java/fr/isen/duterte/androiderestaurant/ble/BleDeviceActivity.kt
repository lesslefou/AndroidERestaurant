package fr.isen.duterte.androiderestaurant.ble

import android.app.AlertDialog
import android.bluetooth.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.duterte.androiderestaurant.R
import fr.isen.duterte.androiderestaurant.databinding.ActivityBleDeviceBinding
import java.util.*

class BleDeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBleDeviceBinding
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var bleDeviceAdapter: BleDeviceAdapterActivity
    private var timer: Timer? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBleDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bleDevice = intent.getParcelableExtra<BluetoothDevice>("Device")
        binding.deviceName.text = bleDevice?.name ?: getString(R.string.deviceUnknown)
        binding.status.text = getString(
            R.string.ble_device_status,
            getString(BleConnexionState.STATE_CONNECTING.text)
        )


        connectToDevice(bleDevice)
    }

    /**
     * Mise en place de la connexion au device avec appelle de fonctions en fonction de l'usage
     */
    private fun connectToDevice(bleDevice: BluetoothDevice?) {
        bluetoothGatt = bleDevice?.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                ConnectionStateChange(newState, gatt)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                ServicesDiscovered(gatt)
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                runOnUiThread {
                    bleDeviceAdapter.updateFromChangedCharacteristic(characteristic)
                    bleDeviceAdapter.notifyDataSetChanged()
                }
            }
        })
        bluetoothGatt?.connect()
    }

    /**
     * Change dynamiquement le TextView du status en fonction de la connexion du device
     */
    private fun ConnectionStateChange(newState: Int, gatt: BluetoothGatt?) {
        BleConnexionState.getBLEConnexionStateFromState(newState)?.let {
            runOnUiThread {
                binding.status.text = getString(R.string.ble_device_status, getString(it.text))
            }

            if (it.state == BleConnexionState.STATE_CONNECTED.state) {
                gatt?.discoverServices()
            }
        }
    }

    /**
     * Abonnement à la notification + mise à jour des données
     */
    private fun toggleNotificationOnCharacteristic( gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, enable: Boolean ) {
        characteristic.descriptors.forEach {
            it.value =
                if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
        gatt.setCharacteristicNotification(characteristic, enable)
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                gatt.readCharacteristic(characteristic)
            }
        }, 0, 1000)
    }

    /**
     * Mise en place du recycler view en fonction des services et caractéristiques propre au device auquel on s'appaire
     */
    private fun ServicesDiscovered(gatt: BluetoothGatt?) {
        gatt?.services?.let {
            runOnUiThread {
                bleDeviceAdapter = BleDeviceAdapterActivity(
                    this,
                    it.map { service ->
                        Log.i("service",service.toString())
                        BleService(service.uuid.toString(), service.characteristics)
                    }.toMutableList(),
                    { characteristic -> gatt.readCharacteristic(characteristic) },
                    { characteristic -> writeIntoCharacteristic(gatt, characteristic) },
                    { characteristic, enable ->
                        toggleNotificationOnCharacteristic(
                            gatt,
                            characteristic,
                            enable
                        )
                    }
                )
                binding.recycleViewService.layoutManager = LinearLayoutManager(this@BleDeviceActivity)
                binding.recycleViewService.adapter = bleDeviceAdapter
                binding.recycleViewService.addItemDecoration(
                    DividerItemDecoration(
                        this@BleDeviceActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
            }
        }
    }

    /**
     * A l'appuie du bouton Ecrire, on affiche en premier plan un EditText permettant l'écriture de la caractéristique
     */
    private fun writeIntoCharacteristic( gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic ) {
        runOnUiThread {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(16, 0, 16, 0)
            input.layoutParams = params

            AlertDialog.Builder(this@BleDeviceActivity)
                .setTitle(R.string.ble_device_write_characteristic_title)
                .setView(input)
                .setPositiveButton(R.string.ble_device_write_characteristic_confirm) { _, _ ->
                    characteristic.value = input.text.toString().toByteArray()
                    gatt.writeCharacteristic(characteristic)
                    gatt.readCharacteristic(characteristic)
                }
                .setNegativeButton(R.string.ble_device_write_characteristic_cancel) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        }
    }

    override fun onStop() {
        super.onStop()
        closeBluetoothGatt()
    }

    /**
     * Permet l'arret du bluetooth et ainsi une gestion optimale de la conso
     */
    private fun closeBluetoothGatt() {
        binding.status.text =
            getString(
                R.string.ble_device_status,
                getString(BleConnexionState.STATE_DISCONNECTED.text)
            )
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}