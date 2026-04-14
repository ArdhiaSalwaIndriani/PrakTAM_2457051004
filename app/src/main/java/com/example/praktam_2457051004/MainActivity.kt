package com.example.praktam_2457051004

import Model.FasilinkSource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2457051004.ui.theme.PrakTAM_2457051004Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2457051004Theme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    HalamanUtama(innerPadding, snackbarHostState)
                }
            }
        }
    }
}

enum class StatusLaporan { PROSES, SELESAI }

@Composable
fun HalamanUtama(innerPadding: PaddingValues, snackbarHostState: SnackbarHostState) {

    val daftarLaporan = FasilinkSource.LaporKampus

    val statusMap = remember {
        daftarLaporan.associateWith {
            listOf(StatusLaporan.PROSES, StatusLaporan.SELESAI).random()
        }
    }

    val loveSet = remember { mutableStateListOf<String>() }
    val loadingSet = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F5))
            .padding(innerPadding)
            .padding(bottom = 16.dp)
    ) {

        // HEADER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC62828))
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Pusat Laporan Fasilitas Kampus",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White,
                    thickness = 1.dp
                )
            }
        }

        // LAPORAN TERBARU (SCROLL SAMPING)
        item {
            Text(
                "Laporan Terbaru",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFC62828),
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(daftarLaporan.take(5)) { laporan ->
                    val isLoved = laporan.namaBenda in loveSet

                    Card(
                        modifier = Modifier.width(130.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Image(
                                    painter = painterResource(laporan.imageRes),
                                    contentDescription = laporan.namaBenda,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color(0xAA000000))
                                            )
                                        )
                                )

                                Text(
                                    laporan.namaBenda,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                )

                                IconButton(
                                    onClick = {
                                        if (isLoved) loveSet.remove(laporan.namaBenda)
                                        else loveSet.add(laporan.namaBenda)
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = if (isLoved) Icons.Filled.Favorite
                                        else Icons.Filled.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isLoved) Color.Red else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Daftar Laporan Lengkap",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFC62828),
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
            )
        }

        // DAFTAR LENGKAP (SCROLL BAWAH)
        items(daftarLaporan) { laporan ->
            val status = statusMap[laporan] ?: StatusLaporan.PROSES
            val isLoved = laporan.namaBenda in loveSet
            val isLoading = laporan.namaBenda in loadingSet

            val warnaBadge =
                if (status == StatusLaporan.PROSES) Color(0xFF1976D2)
                else Color(0xFF388E3C)

            val teksBadge =
                if (status == StatusLaporan.PROSES) "Diproses"
                else "Selesai"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(laporan.imageRes),
                        contentDescription = laporan.namaBenda,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xBB000000))
                                )
                            )
                    )

                    Text(
                        laporan.namaBenda,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    )

                    Surface(
                        color = warnaBadge,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            teksBadge,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (isLoved) loveSet.remove(laporan.namaBenda)
                            else loveSet.add(laporan.namaBenda)
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = if (isLoved) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isLoved) Color.Red else Color.White
                        )
                    }
                }

                Column(Modifier.padding(12.dp)) {
                    Text("Gangguan: ${laporan.jenisGangguan}")
                    Text("Lokasi: ${laporan.lokasi}")
                    Text(laporan.tanggalLaporan, fontSize = 12.sp)

                    Spacer(Modifier.height(6.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                loadingSet.add(laporan.namaBenda)
                                delay(2000L)
                                loadingSet.remove(laporan.namaBenda)

                                val pesanFeedback = when (status) {
                                    StatusLaporan.PROSES ->
                                        "✅ Laporan '${laporan.namaBenda}' berhasil ditandai selesai!"
                                    StatusLaporan.SELESAI ->
                                        "ℹ️ Laporan '${laporan.namaBenda}' sudah selesai ditangani."
                                }
                                snackbarHostState.showSnackbar(
                                    message = pesanFeedback,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC62828),
                            disabledContainerColor = Color(0xFFEF9A9A)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Memproses...", color = Color.White)
                        } else {
                            Text(
                                text = if (status == StatusLaporan.SELESAI) "Lihat Detail" else "Tandai Selesai",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHalaman() {
    PrakTAM_2457051004Theme {
        val snackbarHostState = remember { SnackbarHostState() }
        HalamanUtama(PaddingValues(0.dp), snackbarHostState)
    }
}
