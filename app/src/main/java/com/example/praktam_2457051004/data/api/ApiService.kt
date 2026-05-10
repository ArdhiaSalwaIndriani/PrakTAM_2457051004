package com.example.praktam_2457051004.data.api

import com.example.praktam_2457051004.data.model.Fasilink
import retrofit2.http.GET

interface ApiService {
    @GET("https://gist.githubusercontent.com/ArdhiaSalwaIndriani/5d850650eb5880ed87e4f343e84c5cbd/raw/5cc52a5590d8bb9a80e28d5da2e4ba3ecb8832e3/fasilink.json")
    suspend fun getLaporan(): List<Fasilink>
}