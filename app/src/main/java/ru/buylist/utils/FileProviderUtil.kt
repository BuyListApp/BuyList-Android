package ru.buylist.utils

import android.net.Uri
import androidx.core.content.FileProvider
import ru.buylist.BuildConfig
import ru.buylist.BuyListApp
import ru.buylist.data.Result
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

object FileProviderUtil {

    private const val FILE_NAME = "buyList_export.json"

    fun createAndGetFileUri(json: String): Uri {
        val context = BuyListApp.get()
        val file = File(context.getExternalFilesDir(null), FILE_NAME)

        val outputStream = FileOutputStream(file)
        outputStream.write(json.toByteArray())
        outputStream.close()

        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file)
    }

    fun readFile(data: Uri?): Result<String> {
        return try {
            data
                ?.let { uri ->
                    BuyListApp.get().contentResolver.openInputStream(uri)
                }
                ?.let { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val lines = reader.readText()
                    Result.Success(lines)
                }
                ?: Result.Error(
                    Exception("File not found")
                )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}