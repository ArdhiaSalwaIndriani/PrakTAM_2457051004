package Model

import com.google.gson.annotations.SerializedName

data class Fasilink(
    @SerializedName("jenis_gangguan")
    val jenisGangguan: String,

    @SerializedName("nama_benda")
    val namaBenda: String,

    @SerializedName("lokasi")
    val lokasi: String,

    @SerializedName("tanggal_laporan")
    val tanggalLaporan: String,

    @SerializedName("image_url")
    val imageUrl: String
)