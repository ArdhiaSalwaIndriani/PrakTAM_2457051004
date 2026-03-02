package Model

import androidx.annotation.DrawableRes

data class Fasilink(
    val jenisGangguan: String,
    val namaBenda: String,
    val lokasi: String,
    val tanggalLaporan: String,
    @field:DrawableRes val imageRes: Int
)