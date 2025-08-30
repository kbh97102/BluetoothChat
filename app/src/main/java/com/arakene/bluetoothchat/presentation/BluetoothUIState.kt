package com.arakene.bluetoothchat.presentation

import com.arakene.bluetoothchat.domain.chat.BluetoothDeviceDomain

data class BluetoothUIState(
    val scannedDevice: List<BluetoothDeviceDomain> = emptyList(),
    val pairedDevice: List<BluetoothDeviceDomain> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)
