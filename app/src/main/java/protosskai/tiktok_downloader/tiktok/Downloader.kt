package protosskai.tiktok_downloader.tiktok

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import protosskai.tiktok_downloader.TikTokDownloaderApplication
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SuppressLint("LongLogTag")
class Downloader {
    lateinit var client: OkHttpClient
    var fileName = ""

    companion object {
        val TAG: String = "protosskai.tiktok_downloader.tiktok.Downloader"
    }

    init {
        client = OkHttpClient()
    }

    fun download(url: String, saveDir: String, listener: OnDownloadListener) {
        val userAgent: String =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"
        val request: Request = Request.Builder()
            .addHeader("user-agent", userAgent)
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                listener.onDownloadFailed()
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response != null) {
                    var inputStream: InputStream? = null
                    val buf: ByteArray = ByteArray(2048)
                    var len = 0
                    var fileOutStream: FileOutputStream? = null
                    val savePath = isExistDir(saveDir)
                    try {
                        inputStream = response.body().byteStream()
                        val total: Long = response.body().contentLength()
                        val file: File = File(savePath, getNameFromUrl(url))
                        fileName = file.absolutePath
                        fileOutStream = FileOutputStream(file)
                        var sum: Long = 0
                        var progress: Int = 0
                        len = inputStream.read(buf)
                        while (len != -1) {
                            fileOutStream.write(buf, 0, len)
                            sum += len
                            progress = (sum * 1.0f / total * 100).toInt()
                            listener.onDownloading(progress)
                            len = inputStream.read(buf)
                        }
                        // 下载成功
                        fileOutStream.flush()
                        listener.onDownloadSuccess()
                    } catch (e: Exception) {
                        listener.onDownloadFailed()
                    }
                } else {
                    Log.e(TAG, "下载文件出错: response为空")
                    listener.onDownloadFailed()
                }
            }
        })
    }


    private fun isExistDir(saveDir: String): String {
        val downloadFile: File =
            File(TikTokDownloaderApplication.context.getExternalFilesDir(null), saveDir)
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    fun getNameFromUrl(url: String): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter) + ".mp4"
    }
}

interface OnDownloadListener {
    /**
     * 下载成功
     */
    fun onDownloadSuccess()

    /**
     * @param progress
     * 下载进度
     */
    fun onDownloading(progress: Int)

    /**
     * 下载失败
     */
    fun onDownloadFailed()
}