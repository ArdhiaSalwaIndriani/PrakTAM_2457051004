package com.example.praktam_2457051004

import Model.food
import Model.FoodSource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.praktam_2457051004.ui.theme.PrakTAM_2457051004Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image


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
fun Greeting(innerPadding: PaddingValues){
    val food = FoodSource.dummyFood[0]

    Column(modifier = Modifier.fillMaxSize().padding(all = 30.dp)){
        Image(
            painter = painterResource(id = food.ImageRes),
            contentDescription = food.nama,
            modifier = Modifier.size(200.dp),
            contentScale = Crop
        )
        Text(text = "Nama: ${food.nama}")
        Text(text = "Deskripsi: ${food.deskripsi}")
        Text(text = "Harga: ${food.harga}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2457051004Theme() {
        Greeting(PaddingValues(0.dp))
    }
}