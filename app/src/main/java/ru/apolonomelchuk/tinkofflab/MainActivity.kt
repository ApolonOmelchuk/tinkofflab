package ru.apolonomelchuk.tinkofflab

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        GlideApp.get(this).clearDiskCache()
        GlideApp.get(this).clearMemory()
    }

    fun getNextGif(view: View) {
        val nextGifFromCache = gifsList.getNext()
        val circularProgressDrawable = getCircularProgressDrawable()
        if (nextGifFromCache == null) {
            launch {
                val result =  getNextRandomGif()
                loadGif(result, circularProgressDrawable)
                if (!gifsList.isFirstElement(result)){
                    btnPrevious.isClickable = true
                }
            }
        }
        else {
            val nextGif = nextGifFromCache as GifWrapper
            loadGif(nextGif, circularProgressDrawable)
            if (!gifsList.isFirstElement(nextGif)) {
                btnPrevious.isClickable = true
            }
        }
    }

    fun getPrevGif(view: View) {
        val circularProgressDrawable = getCircularProgressDrawable()
        val prevGifFromCache = gifsList.getPrevious() as GifWrapper
        loadGif(prevGifFromCache, circularProgressDrawable)
        if (gifsList.isFirstElement(prevGifFromCache)) {
            btnPrevious.isClickable = false
        }
    }

    private fun getCircularProgressDrawable(): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

    suspend fun getNextRandomGif() : GifWrapper {
        withContext(Dispatchers.IO) {
            val response = URL(getRandomGifUrl).readText()
            val gif = GifWrapper(response)
            gifsList.add(gif)
        }
        return gifsList.current!!.element as GifWrapper
    }

    fun loadGif(gif: GifWrapper, progressDrawable: CircularProgressDrawable) {
        GlideApp.with(this)
            .load(gif.gifUrl)
            .centerCrop()
            .placeholder(progressDrawable)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(gifView)
    }
}