package protosskai.tiktok_downloader.tiktok

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

import org.json.JSONObject

import org.json.JSONArray


@SuppressLint("LongLogTag")
class UrlParser {

    lateinit var client: OkHttpClient

    companion object {
        val TAG = "protosskai.tiktok_downloader.tiktok.UrlParser"
    }

    init {
        client = OkHttpClient().newBuilder()
            .followRedirects(false)
            .build()

    }

    fun parseItemId(url: String): String {
        val userAgent: String =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"
        val request: Request = Request.Builder()
            .addHeader("user-agent", userAgent)
            .url(url)
            .build()
        val locationUrl: String = client.newCall(request).execute().header("Location")
        Log.d(TAG, "Location is :$locationUrl")
        val itemIdsPattern = Regex("video/(.*?)/\\?")
        var itemIds: String? = itemIdsPattern.find(locationUrl)?.value
        return if (itemIds != null) {
            itemIds = itemIds.substring(6, itemIds.length - 2)
            itemIds
        } else {
            ""
        }
    }

    fun parseVideovid(itemId: String): String {
        val url = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + itemId
        val userAgent: String =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"
        val request: Request = Request.Builder()
            .addHeader("user-agent", userAgent)
            .url(url)
            .build()
        val response = client.newCall(request).execute().body().string()
        val jObject: JSONObject = JSONObject(response)
        val itemList: JSONArray = jObject.getJSONArray("item_list")
        val vid: String =
            ((itemList.get(0) as JSONObject).get("video") as JSONObject).get("vid") as String
        return vid
    }

    fun parseDownloadUrl(videovid: String): String {
        val url = "https://aweme.snssdk.com/aweme/v1/play/?video_id=$videovid&ratio=720p&line=0"
        val userAgent: String =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"
        val request: Request = Request.Builder()
            .addHeader("user-agent", userAgent)
            .url(url)
            .build()
        return client.newCall(request).execute().header("Location")
    }

    fun parseTruthDownloadURL(url: String): String {
        val itemId = parseItemId(url)
        if (itemId == "") {
            return ""
        }
        Log.d(TAG, "itemId is $itemId")
        val vid = parseVideovid(itemId)
        Log.d(TAG, "vid is $vid")
        val truthDownloadUrl = parseDownloadUrl(vid)
        Log.d(TAG, "truthDownloadUrl is $truthDownloadUrl")
        return truthDownloadUrl
    }
}