package com.example.coroutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coroutine.ui.theme.CoroutineTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoroutineTheme {
                var coroutineCount by remember { mutableIntStateOf(1) }
                var statusText by remember {
                    mutableStateOf("Move the slider and launch coroutines")
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CoroutineDemoScreen(
                        coroutineCount = coroutineCount,
                        statusText = statusText,
                        onCountChange = { selectedCount ->
                            coroutineCount = selectedCount
                            statusText = "$selectedCount coroutines selected"
                        },
                        onLaunchClick = {
                            launchCoroutines(coroutineCount) { status ->
                                statusText = status
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

    private suspend fun performTask(taskNumber: Int): Deferred<String> =
        coroutineScope.async(Dispatchers.Main) {
            delay(5_000)
            "Finished Coroutine $taskNumber"
        }

    private fun launchCoroutines(count: Int, onStatusChange: (String) -> Unit) {
        (1..count).forEach { taskNumber ->
            onStatusChange("Started Coroutine $taskNumber")
            coroutineScope.launch(Dispatchers.Main) {
                onStatusChange(performTask(taskNumber).await())
            }
        }
    }
}

@Composable
fun CoroutineDemoScreen(
    coroutineCount: Int,
    statusText: String,
    onCountChange: (Int) -> Unit,
    onLaunchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "$coroutineCount coroutines",
            style = MaterialTheme.typography.headlineSmall,
        )
        Slider(
            value = coroutineCount.toFloat(),
            onValueChange = { onCountChange(it.toInt().coerceAtLeast(1)) },
            valueRange = 1f..2000f,
            steps = 1998,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        )
        Button(
            onClick = onLaunchClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Launch coroutines")
        }
        Text(
            text = statusText,
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CoroutineDemoScreenPreview() {
    CoroutineTheme {
        CoroutineDemoScreen(
            coroutineCount = 10,
            statusText = "Finished Coroutine 10",
            onCountChange = {},
            onLaunchClick = {},
        )
    }
}
