package Model
import androidx.annotation.DrawableRes

data class food(
    val nama: String,
    val deskripsi: String,
    val harga: Int,
    @DrawableRes val ImageRes: Int
)

