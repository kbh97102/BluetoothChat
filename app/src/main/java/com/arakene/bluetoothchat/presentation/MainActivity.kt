package com.arakene.bluetoothchat.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.arakene.bluetoothchat.presentation.ui.component.ChatScreen
import com.arakene.bluetoothchat.presentation.ui.component.DeviceScreen
import com.arakene.bluetoothchat.presentation.ui.theme.BluetoothChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val requestDiscoverableLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_CANCELED) {
                    // 사용자가 검색 허용을 거부함
                    Toast.makeText(this, "Discoverability was not enabled.", Toast.LENGTH_SHORT).show()
                } else {
                    // 사용자가 허용함. resultCode는 허용된 시간(초)입니다.
                    Toast.makeText(this, "Discoverability enabled for ${result.resultCode} seconds.", Toast.LENGTH_SHORT).show()
                }
            }

        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            // 기본값은 120초입니다. 최대 300초(5분)까지 설정 가능합니다.
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        requestDiscoverableLauncher.launch(discoverableIntent)


        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val canEnableBluetooth =
                permissions[Manifest.permission.BLUETOOTH_CONNECT] == true

            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            BluetoothChatTheme {
                val viewModel: BluetoothViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(state.errorMessage) {
                    state.errorMessage?.let {
                        // TODO: compose에서는 scaffold의 snackbar사용을 권장하던데 그 이유가 뭘까?
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                }

                LaunchedEffect(state.isConnected) {
                    if (state.isConnected) {
                        Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    when {
                        state.isConnecting -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Text("is Connecting....")
                            }
                        }

                        state.isConnected -> {
                            ChatScreen(
                                state = state,
                                onDisconnect = viewModel::disconnectFromDevice,
                                onSendMessage = viewModel::sendMessage
                            )
                        }

                        else -> {
                            DeviceScreen(
                                state = state,
                                onStartScan = viewModel::startScan,
                                onStopScan = viewModel::stopScan,
                                onDeviceClick = viewModel::connectToDevice,
                                onStartServer = viewModel::waitForIncomingConnection
                            )
                        }
                    }

                }

            }
        }
    }
}
