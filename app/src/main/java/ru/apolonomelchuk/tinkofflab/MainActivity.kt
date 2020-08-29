package ru.apolonomelchuk.tinkofflab

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import ru.apolonomelchuk.datastructures.GifWrapper
import ru.apolonomelchuk.datastructures.LinkedStack
import java.net.URL
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    val getRandomGifUrl = "https://developerslife.ru/random?json=true"
    var gifsList = LinkedStack()
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPrevious.isClickable = false
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        GlideApp.get(this).clearDiskCache()
        GlideApp.get(this).clearMemory()
    }

    fun getNextGif(view: View) {
        val nextGifFromCache = gifsList.getNext()
        if (nextGifFromCache == null) {
            launch {
                val result =  getNextRandomGif()
                loadGif(result)
                if (!gifsList.isFirstElement(result)){
                    btnPrevious.isClickable = true
                }
            }
        }
        else {
            val nextGif = nextGifFromCache as GifWrapper
            loadGif(nextGif)
            if (!gifsList.isFirstElement(nextGif)) {
                btnPrevious.isClickable = true
            }
        }
    }

    fun getPrevGif(view: View) {
        val prevGifFromCache = gifsList.getPrevious() as GifWrapper
        loadGif(prevGifFromCache)
        if (gifsList.isFirstElement(prevGifFromCache)) {
            btnPrevious.isClickable = false
        }
    }

    suspend fun getNextRandomGif() : GifWrapper {
        withContext(Dispatchers.IO) {
            val response = URL(getRandomGifUrl).readText()
            val gif = GifWrapper(response)
            gifsList.add(gif)
        }
        return gifsList.current!!.element as GifWrapper
    }

    fun loadGif(gif: GifWrapper) {
        GlideApp.with(this)
            .load(gif.gifUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(gifView)
    }
}