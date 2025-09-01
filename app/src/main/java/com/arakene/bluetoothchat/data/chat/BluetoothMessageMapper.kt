package com.arakene.bluetoothchat.data.chat

import com.arakene.bluetoothchat.domain.chat.BluetoothMessage

fun String.toBluetoothMessage(isFromLocalUse: Boolean): BluetoothMessage {
    val senderName = substringBeforeLast("#")
    val message = substringAfter("#")
    return BluetoothMessage(
        isFromLocalUser = isFromLocalUse,
        message = message,
        senderName = senderName
    )
}

fun BluetoothMessage.toByteArray(): ByteArray {
    return "$senderName#$message".encodeToByteArray()
}