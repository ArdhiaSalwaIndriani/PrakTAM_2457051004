package com.example.praktam_2457051004

import Model.Fasilink
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.praktam_2457051004.network.RetrofitClient
import com.example.praktam_2457051004.ui.theme.PrakTAM_2457051004Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class StatusLaporan {
    DIPROSES, SELESAI
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrakTAM_2457051004Theme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val daftarLaporan = remember { mutableStateListOf<Fasilink>() }
    val statusMap = remember { mutableStateMapOf<String, StatusLaporan>() }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val data = RetrofitClient.instance.getLaporan()

            daftarLaporan.clear()
            daftarLaporan.addAll(data)

            statusMap.clear()
            data.forEachIndexed { index, laporan ->
                statusMap[laporan.namaBenda] =
                    if (index % 2 == 0) StatusLaporan.DIPROSES else StatusLaporan.SELESAI
            }

            isLoading = false
            isError = false
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            isError = true
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HalamanUtama(
                    navController = navController,
                    daftarLaporan = daftarLaporan,
                    statusMap = statusMap,
                    snackbarHostState = snackbarHostState,
                    isLoading = isLoading,
                    isError = isError
                )
            }

            composable("detail/{namaBenda}") { backStackEntry ->
                val namaBenda = backStackEntry.arguments?.getString("namaBenda")
                val laporan = daftarLaporan.find { it.namaBenda == namaBenda }

                if (laporan != null) {
                    DetailScreen(
                        laporan = laporan,
                        status = statusMap[laporan.namaBenda] ?: StatusLaporan.DIPROSES,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun HalamanUtama(
    navController: NavHostController,
    daftarLaporan: MutableList<Fasilink>,
    statusMap: MutableMap<String, StatusLaporan>,
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean,
    isError: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val favoriteList = remember { mutableStateListOf<String>() }
    var showFormDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF795548))
                }
            }

            isError || daftarLaporan.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gagal Memuat Data",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Pastikan koneksi internet Anda menyala",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F3EF))
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        item {
                            HeaderFasilink()
                        }

                        item {
                            RingkasanLaporan(
                                total = daftarLaporan.size,
                                diproses = statusMap.values.count { it == StatusLaporan.DIPROSES },
                                selesai = statusMap.values.count { it == StatusLaporan.SELESAI }
                            )
                        }

                        item {
                            SectionTitle("Laporan Terbaru")

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(daftarLaporan.take(5)) { laporan ->
                                    MiniLaporanCard(
                                        laporan = laporan,
                                        onClick = {
                                            navController.navigate("detail/${laporan.namaBenda}")
                                        }
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            SectionTitle("Daftar Laporan Lengkap")
                        }

                        items(daftarLaporan) { laporan ->
                            val status = statusMap[laporan.namaBenda] ?: StatusLaporan.DIPROSES
                            val isFavorite = laporan.namaBenda in favoriteList

                            LaporanCard(
                                laporan = laporan,
                                status = status,
                                isFavorite = isFavorite,
                                onFavoriteClick = {
                                    if (isFavorite) favoriteList.remove(laporan.namaBenda)
                                    else favoriteList.add(laporan.namaBenda)
                                },
                                onDetailClick = {
                                    navController.navigate("detail/${laporan.namaBenda}")
                                }
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = { showFormDialog = true },
                        containerColor = Color(0xFF795548),
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Laporan"
                        )
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        TambahLaporanDialog(
            onDismiss = { showFormDialog = false },
            onKirim = { nama, gangguan, lokasi ->
                val laporanBaru = Fasilink(
                    jenisGangguan = gangguan,
                    namaBenda = nama,
                    lokasi = lokasi,
                    tanggalLaporan = "Hari ini",
                    imageUrl = "https://picsum.photos/seed/laporanbaru/600/400"
                )

                daftarLaporan.add(0, laporanBaru)
                statusMap[laporanBaru.namaBenda] = StatusLaporan.DIPROSES
                showFormDialog = false

                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Laporan $nama berhasil dikirim!",
                        actionLabel = "OK",
                        duration = SnackbarDuration.Long
                    )
                }
            }
        )
    }
}

@Composable
fun DetailScreen(
    laporan: Fasilink,
    status: StatusLaporan,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3EF))
    ) {
        item {
            Box {
                AsyncImage(
                    model = laporan.imageUrl,
                    contentDescription = laporan.namaBenda,
                    placeholder = painterResource(id = R.drawable.img),
                    error = painterResource(id = R.drawable.img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

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
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    StatusChip(status = status)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = laporan.namaBenda,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = laporan.jenisGangguan,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp
                    )
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
                    Text(
                        text = "Detail Laporan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )

                    DetailRow("Nama fasilitas", laporan.namaBenda)
                    DetailRow("Jenis gangguan", laporan.jenisGangguan)
                    DetailRow("Lokasi", laporan.lokasi)
                    DetailRow("Tanggal laporan", laporan.tanggalLaporan)

                    Text(
                        text = "Status: ${if (status == StatusLaporan.DIPROSES) "Diproses" else "Selesai"}",
                        fontWeight = FontWeight.Bold,
                        color = if (status == StatusLaporan.DIPROSES) Color(0xFFFFA726) else Color(0xFF43A047)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF795548)
                        )
                    ) {
                        Text("Kembali")
                    }
                }
            }
        }
    }
}

@Composable
fun TambahLaporanDialog(
    onDismiss: () -> Unit,
    onKirim: (String, String, String) -> Unit
) {
    var namaBenda by remember { mutableStateOf("") }
    var jenisGangguan by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "Tambah Laporan Baru",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF1E6DE), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Foto fasilitas rusak",
                        color = Color(0xFF795548),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = namaBenda,
                    onValueChange = { namaBenda = it },
                    label = { Text("Nama fasilitas") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = jenisGangguan,
                    onValueChange = { jenisGangguan = it },
                    label = { Text("Jenis kerusakan") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lokasi,
                    onValueChange = { lokasi = it },
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (namaBenda.isNotBlank() && jenisGangguan.isNotBlank() && lokasi.isNotBlank()) {
                        coroutineScope.launch {
                            isLoading = true
                            delay(2500)
                            isLoading = false
                            onKirim(namaBenda, jenisGangguan, lokasi)
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF795548),
                    disabledContainerColor = Color(0xFFD7CCC8)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mengirim...")
                } else {
                    Text("Kirim")
                }
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
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFA1887F),
                        Color(0xFF8D6E63)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Fasilink",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Pusat laporan fasilitas rusak di lingkungan kampus",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth(),
            color = Color.White.copy(alpha = 0.5f),
            thickness = 1.dp
        )
    }
}

@Composable
fun RingkasanLaporan(
    total: Int,
    diproses: Int,
    selesai: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatistikCard("Total", "$total", Modifier.weight(1f))
        StatistikCard("Diproses", "$diproses", Modifier.weight(1f))
        StatistikCard("Selesai", "$selesai", Modifier.weight(1f))
    }
}

@Composable
fun StatistikCard(
    judul: String,
    angka: String,
    modifier: Modifier = Modifier
) {
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
            Text(
                text = angka,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF795548)
            )

            Text(
                text = judul,
                fontSize = 12.sp,
                color = Color(0xFF6D4C41)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF3E2723),
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
    )
}

@Composable
fun MiniLaporanCard(
    laporan: Fasilink,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(135.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            AsyncImage(
                model = laporan.imageUrl,
                contentDescription = laporan.namaBenda,
                placeholder = painterResource(id = R.drawable.img),
                error = painterResource(id = R.drawable.img),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xCC3E2723)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = laporan.namaBenda,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = laporan.jenisGangguan,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                AsyncImage(
                    model = laporan.imageUrl,
                    contentDescription = laporan.namaBenda,
                    placeholder = painterResource(id = R.drawable.img),
                    error = painterResource(id = R.drawable.img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                )

                StatusChip(
                    status = status,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.30f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorit",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = laporan.namaBenda,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Gangguan: ${laporan.jenisGangguan}", fontSize = 14.sp, color = Color(0xFF5D4037))
                Text("Lokasi: ${laporan.lokasi}", fontSize = 14.sp, color = Color(0xFF5D4037))
                Text(laporan.tanggalLaporan, fontSize = 12.sp, color = Color(0xFF8D6E63))

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF795548)
                    )
                ) {
                    Text("Lihat Detail")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF8D6E63)
        )

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3E2723)
        )
    }
}

@Composable
fun StatusChip(
    status: StatusLaporan,
    modifier: Modifier = Modifier
) {
    val text = if (status == StatusLaporan.DIPROSES) "Diproses" else "Selesai"
    val color = if (status == StatusLaporan.DIPROSES) {
        Color(0xFFFFA726)
    } else {
        Color(0xFF66BB6A)
    }

    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}