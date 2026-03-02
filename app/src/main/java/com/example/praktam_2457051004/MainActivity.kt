package com.example.praktam_2457051004

import Model.FasilinkSource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2457051004.ui.theme.PrakTAM_2457051004Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2457051004Theme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(innerPadding)
                }
            }
        }
    }
}

@Composable
fun Greeting(innerPadding: PaddingValues) {
    val DataList = FasilinkSource.LaporKampus

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
            .padding(innerPadding)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4E342E))
                .padding(all = 24.dp)
        ) {
            Text(text = "Ardhia Salwa Indriani", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "NPM: 2457051004 · Kelas: B", fontSize = 13.sp, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Pusat Laporan Fasilitas Kampus", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            HorizontalDivider(modifier = Modifier.padding(top = 10.dp), color = Color.White, thickness = 1.dp)
        }

        LazyColumn(modifier = Modifier.padding(all = 16.dp)) {
            items(DataList) { Fasilink ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = Fasilink.imageRes),
                            contentDescription = Fasilink.namaBenda,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                            contentScale = Crop
                        )
                        Column(modifier = Modifier.padding(all = 12.dp)) {
                            Text(text = "Nama: ${Fasilink.namaBenda}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            Text(text = "JenisGangguan: ${Fasilink.jenisGangguan}", fontSize = 14.sp)
                            Text(text = "Lokasi: ${Fasilink.lokasi}", fontSize = 14.sp)
                            Text(text = "TanggalLaporan: ${Fasilink.tanggalLaporan}", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2457051004Theme() {
        Greeting(PaddingValues(3.dp))
    }
}