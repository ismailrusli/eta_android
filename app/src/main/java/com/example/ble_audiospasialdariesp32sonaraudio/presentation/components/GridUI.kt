package com.example.ble_audiospasialdariesp32sonaraudio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.OccupiedGridViewModel

@Composable
fun GridUI(
    viewModel: OccupiedGridViewModel
) {
    // Ambil grid dari ViewModel
    val grid = viewModel.getOccupiedGrid() // List of 2D grids
    val layersCount = grid.size // Jumlah layer Z

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Occupied Grid (3D Representation)")

        // Gunakan LazyColumn untuk membuat grid yang dapat digulir
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Untuk memberikan space agar grid bisa scrollable
        ) {
            items(layersCount) { z ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Layer Z = $z",
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .fillMaxWidth()
                    )

                    // Buat grid 2D untuk layer Z
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(grid[z].size) { y ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                grid[z][y].forEach { cell ->
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(
                                                if (cell) Color.Red else Color.Gray,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
