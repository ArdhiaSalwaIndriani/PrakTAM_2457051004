package Model

import com.example.praktam_2457051004.R

object FasilinkSource {
    val LaporKampus = listOf(
        Fasilink(namaBenda = "Kursi", jenisGangguan = "Rusak", lokasi = "Gedung Ilmu Komputer B", tanggalLaporan = "15 Januari 2026", imageRes =  R.drawable.img),
        Fasilink(namaBenda = "AC", jenisGangguan = "Tidak Dingin", lokasi = "Lab R1 Mipa T", tanggalLaporan = "20 Februari 2026", imageRes =  R.drawable.img_1),
        Fasilink(namaBenda = "Toilet", jenisGangguan = "Air tidak Keluar", lokasi = "Gedung Ilmu Komputer L1", tanggalLaporan = "28 Februari 2026", imageRes = R.drawable.img_2)
    )
}