package com.example.praktam_2457051004

import Model.Fasilink
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.example.praktam_2457051004.network.RetrofitClient
import com.example.praktam_2457051004.ui.theme.PrakTAM_2457051004Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class StatusLaporan { DIPROSES, SELESAI }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2457051004Theme {
                AppNavigation(rememberNavController())
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val snackbar = remember { SnackbarHostState() }
    val laporan = remember { mutableStateListOf<Fasilink>() }
    val status = remember { mutableStateMapOf<String, StatusLaporan>() }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val data = RetrofitClient.instance.getLaporan()
            laporan.clear()
            laporan.addAll(data)
            status.clear()
            data.forEachIndexed { i, item ->
                status[item.namaBenda] = if (i % 2 == 0) StatusLaporan.DIPROSES else StatusLaporan.SELESAI
            }
            loading = false
            error = false
        } catch (e: Exception) {
            loading = false
            error = true
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        NavHost(navController, "home", Modifier.padding(padding)) {
            composable("home") {
                HomeScreen(navController, laporan, status, snackbar, loading, error)
            }
            composable("detail/{namaBenda}") { backStack ->
                val nama = backStack.arguments?.getString("namaBenda")
                val item = laporan.find { it.namaBenda == nama }
                if (item != null) {
                    DetailScreen(item, status[item.namaBenda] ?: StatusLaporan.DIPROSES, navController)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    laporan: MutableList<Fasilink>,
    status: MutableMap<String, StatusLaporan>,
    snackbar: SnackbarHostState,
    loading: Boolean,
    error: Boolean
) {
    val scope = rememberCoroutineScope()
    val fav = remember { mutableStateListOf<String>() }
    var showDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        when {
            loading -> CenterMessage(loading = true)
            error || laporan.isEmpty() -> CenterMessage(loading = false)
            else -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F3EF))
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        item { HeaderFasilink() }

                        item {
                            Summary(
                                total = laporan.size,
                                diproses = status.values.count { it == StatusLaporan.DIPROSES },
                                selesai = status.values.count { it == StatusLaporan.SELESAI }
                            )
                        }

                        item {
                            SectionTitle("Laporan Terbaru")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(laporan.take(5)) { item ->
                                    MiniCard(item) {
                                        navController.navigate("detail/${item.namaBenda}")
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(Modifier.height(14.dp))
                            SectionTitle("Daftar Laporan Lengkap")
                        }

                        items(laporan) { item ->
                            val isFav = item.namaBenda in fav
                            LaporanCard(
                                laporan = item,
                                status = status[item.namaBenda] ?: StatusLaporan.DIPROSES,
                                isFavorite = isFav,
                                onFavoriteClick = {
                                    if (isFav) fav.remove(item.namaBenda) else fav.add(item.namaBenda)
                                },
                                onDetailClick = {
                                    navController.navigate("detail/${item.namaBenda}")
                                }
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = { showDialog = true },
                        containerColor = Color(0xFF795548),
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp)
                    ) {
                        Icon(Icons.Default.Add, "Tambah Laporan")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddDialog(
            onDismiss = { showDialog = false },
            onKirim = { nama, gangguan, lokasi ->
                val baru = Fasilink(
                    jenisGangguan = gangguan,
                    namaBenda = nama,
                    lokasi = lokasi,
                    tanggalLaporan = "Hari ini",
                    imageUrl = "https://picsum.photos/seed/laporanbaru/600/400"
                )
                laporan.add(0, baru)
                status[baru.namaBenda] = StatusLaporan.DIPROSES
                showDialog = false
                scope.launch { snackbar.showSnackbar("Laporan $nama berhasil dikirim!") }
            }
        )
    }
}

@Composable
fun CenterMessage(loading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(color = Color(0xFF795548))
        } else {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Gagal Memuat Data",
                    color = Color.Red,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Pastikan koneksi internet Anda menyala",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DetailScreen(laporan: Fasilink, status: StatusLaporan, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3EF))
    ) {
        item {
            Box {
                ReportImage(laporan, Modifier.fillMaxWidth().height(260.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.45f)
                                )
                            )
                        )
                )

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    StatusChip(status)
                    Spacer(Modifier.height(10.dp))
                    Text(laporan.namaBenda, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(laporan.jenisGangguan, color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Detail Laporan", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                    DetailRow("Nama fasilitas", laporan.namaBenda)
                    DetailRow("Jenis gangguan", laporan.jenisGangguan)
                    DetailRow("Lokasi", laporan.lokasi)
                    DetailRow("Tanggal laporan", laporan.tanggalLaporan)
                    Text(
                        "Status: ${if (status == StatusLaporan.DIPROSES) "Diproses" else "Selesai"}",
                        fontWeight = FontWeight.Bold,
                        color = if (status == StatusLaporan.DIPROSES) Color(0xFFFFA726) else Color(0xFF43A047)
                    )
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
                    ) {
                        Text("Kembali")
                    }
                }
            }
        }
    }
}

@Composable
fun AddDialog(onDismiss: () -> Unit, onKirim: (String, String, String) -> Unit) {
    var nama by remember { mutableStateOf("") }
    var gangguan by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Tambah Laporan Baru", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF1E6DE), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Foto fasilitas rusak", color = Color(0xFF795548), fontWeight = FontWeight.Bold)
                }
                OutlinedTextField(nama, { nama = it }, label = { Text("Nama fasilitas") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(gangguan, { gangguan = it }, label = { Text("Jenis kerusakan") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(lokasi, { lokasi = it }, label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF795548),
                    disabledContainerColor = Color(0xFFD7CCC8)
                ),
                onClick = {
                    if (nama.isNotBlank() && gangguan.isNotBlank() && lokasi.isNotBlank()) {
                        scope.launch {
                            loading = true
                            delay(2500)
                            loading = false
                            onKirim(nama, gangguan, lokasi)
                        }
                    }
                }
            ) {
                if (loading) {
                    CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Mengirim...")
                } else Text("Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color(0xFF795548))
            }
        }
    )
}

@Composable
fun HeaderFasilink() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFFA1887F), Color(0xFF8D6E63))))
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Fasilink", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            "Pusat laporan fasilitas rusak di lingkungan kampus",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth(),
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun Summary(total: Int, diproses: Int, selesai: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Total", "$total", Modifier.weight(1f))
        StatCard("Diproses", "$diproses", Modifier.weight(1f))
        StatCard("Selesai", "$selesai", Modifier.weight(1f))
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF795548))
            Text(title, fontSize = 12.sp, color = Color(0xFF6D4C41))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF3E2723),
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
    )
}

@Composable
fun MiniCard(laporan: Fasilink, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(135.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            ReportImage(laporan, Modifier.fillMaxSize())
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC3E2723))))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(laporan.namaBenda, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(laporan.jenisGangguan, color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun LaporanCard(
    laporan: Fasilink,
    status: StatusLaporan,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box {
                ReportImage(laporan, Modifier.fillMaxWidth().height(185.dp))
                StatusChip(status, Modifier.align(Alignment.TopStart).padding(12.dp))
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.30f), CircleShape)
                ) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        "Favorit",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text(laporan.namaBenda, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                Spacer(Modifier.height(8.dp))
                Text("Gangguan: ${laporan.jenisGangguan}", fontSize = 14.sp, color = Color(0xFF5D4037))
                Text("Lokasi: ${laporan.lokasi}", fontSize = 14.sp, color = Color(0xFF5D4037))
                Text(laporan.tanggalLaporan, fontSize = 12.sp, color = Color(0xFF8D6E63))
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
                ) {
                    Text("Lihat Detail")
                }
            }
        }
    }
}

@Composable
fun ReportImage(laporan: Fasilink, modifier: Modifier) {
    AsyncImage(
        model = laporan.imageUrl,
        contentDescription = laporan.namaBenda,
        placeholder = painterResource(R.drawable.img),
        error = painterResource(R.drawable.img),
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color(0xFF8D6E63))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3E2723))
    }
}

@Composable
fun StatusChip(status: StatusLaporan, modifier: Modifier = Modifier) {
    val proses = status == StatusLaporan.DIPROSES
    Box(
        modifier = modifier
            .background(if (proses) Color(0xFFFFA726) else Color(0xFF66BB6A), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            if (proses) "Diproses" else "Selesai",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}