package protosskai.tiktok_downloader

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import protosskai.tiktok_downloader.databinding.FragmentFirstBinding
import protosskai.tiktok_downloader.tiktok.Downloader
import protosskai.tiktok_downloader.tiktok.OnDownloadListener
import protosskai.tiktok_downloader.tiktok.UrlParser

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    companion object {
        val TAG: String = "protosskai.tiktok_downloader.FirstFragment"
    }

    private var _binding: FragmentFirstBinding? = null
    private val downloader = Downloader()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.parseButton.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            Thread {
                val url = binding.urlEditText.text
                val parser: UrlParser = UrlParser()
                val downloadUrl = parser.parseTruthDownloadURL(url.toString())
                Log.d(TAG, downloadUrl)
                activity?.runOnUiThread {
                    Toast.makeText(
                        TikTokDownloaderApplication.context,
                        "解析下载链接成功，开始下载视频",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                downloader.download(downloadUrl, "video", object : OnDownloadListener {
                    override fun onDownloadSuccess() {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                TikTokDownloaderApplication.context,
                                "下载成功！请到“下载”文件夹内查看",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        Log.d(TAG, "下载成功！")
                    }

                    override fun onDownloading(progress: Int) {
                        Log.d(TAG, "当前下载进度：$progress")
                    }

                    override fun onDownloadFailed() {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                TikTokDownloaderApplication.context,
                                "下载失败！",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.e(TAG, "下载失败！")
                    }
                })
            }.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}