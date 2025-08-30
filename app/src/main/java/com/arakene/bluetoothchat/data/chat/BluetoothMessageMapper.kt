package com.arakene.bluetoothchat.data.chat

import com.arakene.bluetoothchat.domain.chat.BluetoothMessage

fun String.toBluetoothMessage(isFromLocalUse: Boolean): BluetoothMessage {
    val message = substringBeforeLast("#")
    val senderName = substringAfter("#")
    return BluetoothMessage(
        isFromLocalUser = isFromLocalUse,
        message = message,
        senderName = senderName
    )
}

fun BluetoothMessage.toByteArray(): ByteArray {
    return "$senderName#$message".encodeToByteArray()
}