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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2457051004.ui.theme.PrakTAM_2457051004Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2457051004Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HalamanUtama(innerPadding)
                }
            }
        }
    }
}

@Composable
fun HalamanUtama(innerPadding: PaddingValues) {

    val daftarLaporan = FasilinkSource.LaporKampus

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4E342E))
                .padding(horizontal = 16.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pusat Laporan Fasilitas Kampus",
                        fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp),
                color = Color.White,
                thickness = 1.dp
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 10.dp)
        ) {
            items(daftarLaporan) { laporan ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = laporan.imageRes),
                            contentDescription = laporan.namaBenda,
                            modifier = Modifier.size(110.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = Crop
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = laporan.namaBenda, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF3E2723))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Gangguan: ${laporan.jenisGangguan}", fontSize = 12.sp, color = Color(0xFF5D4037))
                            Text(text = "Lokasi: ${laporan.lokasi}", fontSize = 12.sp, color = Color(0xFF5D4037))
                            Text(text = "${laporan.tanggalLaporan}", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) { Text("Detail", fontSize = 12.sp, color = Color.White) }

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
        HalamanUtama(PaddingValues(3.dp))
    }
}