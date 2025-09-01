package com.arakene.bluetoothchat.presentation.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arakene.bluetoothchat.domain.chat.BluetoothMessage

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(message) {
        Log.e(">>>>", "Message ${message.message} name ${message.senderName} from local? ${message.isFromLocalUser}")
    }

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) 15.dp else 0.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = if (message.isFromLocalUser) 0.dp else 15.dp,
                )
            )
            .background(
                color = if (message.isFromLocalUser) {
                    Color.Yellow
                } else Color.LightGray
            )
            .padding(16.dp)
    ) {

        Text(text = message.senderName, fontSize = 10.sp, color = Color.Black)
        Text(text = message.message, color = Color.Black, modifier = Modifier.widthIn(250.dp))

    }
}

@Preview
@Composable
private fun FromAnotherUser() {
    ChatMessage(
        message = BluetoothMessage(
            message = "Test Message",
            senderName = "s25 ultra",
            isFromLocalUser = false
        )
    )
}

@Preview
@Composable
private fun FromLocalUser() {
    ChatMessage(
        message = BluetoothMessage(
            message = "Test Message",
            senderName = "s25 ultra",
            isFromLocalUser = true
        )
    )
}