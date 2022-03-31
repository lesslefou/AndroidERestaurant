package fr.isen.duterte.androiderestaurant.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class ExpandedCharacteristic(var blecharac: BluetoothGattCharacteristic, var expand: Boolean = false) {}