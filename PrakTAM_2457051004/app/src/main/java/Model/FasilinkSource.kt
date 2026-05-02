package Model

import android.content.Context

object FasilinkSource {
    fun getResourceId(context: Context, imageName: String): Int {
        return context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
    }
}