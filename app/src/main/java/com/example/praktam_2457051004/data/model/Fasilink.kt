package com.example.praktam_2457051004.data.model

// Import SerializedName biar nama variabel bisa disesuaikan dengan nama field dari API atau database JSON
import com.google.gson.annotations.SerializedName


data class Fasilink(
    // Mengambil data dari JSON
    @SerializedName("jenis_gangguan")
    val jenisGangguan: String,

    @SerializedName("nama_benda")
    val namaBenda: String,

    @SerializedName("lokasi")
    val lokasi: String,

    @SerializedName("tanggal_laporan")
    val tanggalLaporan: String,

    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("deskripsi")
    val deskripsi: String? = null
)