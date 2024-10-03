import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_bluetoothlowenergyesp32.data.ConfigData
import com.example.ble_bluetoothlowenergyesp32.presentation.ConfigViewModel

@Composable
fun DualSlider(
    value1: Float,
    value2: Float,
    onValueChange: (Float, Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    var sliderState by remember { mutableStateOf(value1 to value2) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(sliderState.first.toInt().toString(), modifier = Modifier.weight(1f))
        Slider(
            value = sliderState.first,
            onValueChange = { newValue1 ->
                if (newValue1 <= sliderState.second) {
                    sliderState = newValue1 to sliderState.second
                    onValueChange(sliderState.first, sliderState.second)
                }
            },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = Color.Blue,
                activeTrackColor = Color.Cyan,
                inactiveTrackColor = Color.LightGray
            )
        )
        Slider(
            value = sliderState.second,
            onValueChange = { newValue2 ->
                if (newValue2 >= sliderState.first) {
                    sliderState = sliderState.first to newValue2
                    onValueChange(sliderState.first, sliderState.second)
                }
            },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = Color.Red,
                activeTrackColor = Color.Yellow,
                inactiveTrackColor = Color.LightGray
            )
        )
        Text(sliderState.second.toInt().toString(), modifier = Modifier.weight(1f))
    }
}

@Composable
fun ConfigScreen(
    configViewModel: ConfigViewModel = viewModel()
) {
    val minJarak by configViewModel.minJarak
    val maxJarak by configViewModel.maxJarak
    val minAmplitudo by configViewModel.minAmplitudo
    val maxAmplitudo by configViewModel.maxAmplitudo
    val durasiGetar by configViewModel.durasiGetar
    val opsiUX by configViewModel.opsiUX

    var newMinJarak by remember { mutableStateOf(minJarak) }
    var newMaxJarak by remember { mutableStateOf(maxJarak) }
    var newMinAmplitudo by remember { mutableStateOf(minAmplitudo) }
    var newMaxAmplitudo by remember { mutableStateOf(maxAmplitudo) }
    var newDurasiGetar by remember { mutableStateOf(durasiGetar) }
    var newOpsiUX by remember { mutableStateOf(opsiUX) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Gradient Slider for Jarak
        Text("Jarak: ${newMinJarak.toInt()} - ${newMaxJarak.toInt()}")
        DualSlider(
            value1 = newMinJarak,
            value2 = newMaxJarak,
            onValueChange = { min, max ->
                newMinJarak = min
                newMaxJarak = max
            },
            valueRange = 0f..100f // Adjust the range as needed
        )

        // Gradient Slider for Amplitudo
        Text("Amplitudo: ${newMinAmplitudo} - ${newMaxAmplitudo}")
        DualSlider(
            value1 = newMinAmplitudo.toFloat(),
            value2 = newMaxAmplitudo.toFloat(),
            onValueChange = { min, max ->
                newMinAmplitudo = min.toInt()
                newMaxAmplitudo = max.toInt()
            },
            valueRange = 1f..100f // Adjust the range as needed
        )

        // Slider for Durasi Getar
        Text("Durasi Getar (ms): ${newDurasiGetar.toInt()}")
        Slider(
            value = newDurasiGetar.toFloat(),
            onValueChange = { newDurasiGetar = it.toLong() },
            valueRange = 0f..5000f, // Set the appropriate range for duration
            steps = 50
        )

        // Radio Buttons for Opsi UX
        Text("Opsi UX")
        val options = listOf("Getar", "Suara")
        options.forEach { option ->
            Row {
                RadioButton(
                    selected = (newOpsiUX == option),
                    onClick = { newOpsiUX = option }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                configViewModel.updateConfigData(
                    ConfigData(
                        minJarak = newMinJarak,
                        maxJarak = newMaxJarak,
                        minAmplitudo = newMinAmplitudo,
                        maxAmplitudo = newMaxAmplitudo,
                        durasiGetar = newDurasiGetar,
                        opsiUX = newOpsiUX
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Configuration")
        }
    }
}
