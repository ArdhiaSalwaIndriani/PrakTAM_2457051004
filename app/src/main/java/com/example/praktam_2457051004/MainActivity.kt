package com.example.praktam_2457051004

// Import Bundle untuk menyimpan state activity
import android.os.Bundle

// Import ComponentActivity sebagai activity utama aplikasi
import androidx.activity.ComponentActivity

// Import setContent untuk menampilkan UI Jetpack Compose
import androidx.activity.compose.setContent

// Import agar tampilan bisa full screen sampai area edge layar
import androidx.activity.enableEdgeToEdge

// Import untuk membuat NavController
import androidx.navigation.compose.rememberNavController

// Import AppNavigation sebagai pusat navigasi aplikasi
import com.example.praktam_2457051004.navigation.AppNavigation

// Import tema aplikasi
import com.example.praktam_2457051004.ui.theme.PrakTAM_2457051004Theme

// MainActivity adalah activity utama yang pertama kali dijalankan saat aplikasi dibuka
class MainActivity : ComponentActivity() {

    // onCreate dijalankan ketika aplikasi pertama kali dibuat/dibuka
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengaktifkan tampilan edge-to-edge agar UI bisa memenuhi layar
        enableEdgeToEdge()

        // setContent digunakan untuk menampilkan UI Jetpack Compose
        setContent {

            // Menerapkan tema aplikasi
            PrakTAM_2457051004Theme {

                // Memanggil AppNavigation dan membuat NavController untuk navigasi halaman
                AppNavigation(rememberNavController())
            }
        }
    }
}