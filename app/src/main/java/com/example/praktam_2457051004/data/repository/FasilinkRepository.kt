package com.example.praktam_2457051004.data.repository

import android.util.Log
import com.example.praktam_2457051004.data.api.RetrofitClient
import com.example.praktam_2457051004.data.model.Fasilink

class FasilinkRepository {
    suspend fun getLaporan(): List<Fasilink> {
        return try {
            RetrofitClient.instance.getLaporan()
        } catch (e: Exception) {
            Log.e("FASILINK_API", "Gagal ambil data: ${e.message}", e)
            emptyList()
        }
    }
}