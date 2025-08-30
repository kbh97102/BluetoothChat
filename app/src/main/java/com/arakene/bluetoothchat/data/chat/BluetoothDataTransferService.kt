package com.arakene.bluetoothchat.data.chat

import android.bluetooth.BluetoothSocket
import com.arakene.bluetoothchat.domain.chat.BluetoothMessage
import com.arakene.bluetoothchat.domain.chat.ConnectionResult
import com.arakene.bluetoothchat.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessage(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) {
                return@flow
            }

            val buffer = ByteArray(1024)
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: Exception) {
                    throw TransferFailedException()
                }
                emit(
                    buffer.decodeToString(
                        endIndex = byteCount
                    ).toBluetoothMessage(isFromLocalUse = false)
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO){
            try {
                socket.outputStream.write(bytes)
            } catch (e: Exception){
                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }

}