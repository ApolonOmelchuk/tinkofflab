package ru.apolonomelchuk.tinkofflab

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_main.*
import ru.apolonomelchuk.datastructures.GifWrapper
import ru.apolonomelchuk.datastructures.LinkedStack
import java.net.URL

class MainActivity : AppCompatActivity() {
    val getRandomGifUrl = "https://developerslife.ru/random?json=true"
    var gifsList = LinkedStack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPrevious.isClickable = false
    }

    override fun onDestroy() {
        Glide.get(this).clearDiskCache()
        Glide.get(this).clearMemory()
        super.onDestroy()
    }

    fun getNextGif(view: View) {
        val nextGifFromCache = gifsList.getNext()
        val nextGif = if (nextGifFromCache != null) nextGifFromCache as GifWrapper else getNextRandomGif()
        loadGif(nextGif)
        if (!gifsList.isFirstElement(nextGif)){
            btnPrevious.isClickable = true
        }
    }

    fun getPrevGif(view: View) {
        val prevGifFromCache = gifsList.getPrevious() as GifWrapper
        loadGif(prevGifFromCache)
        if (!gifsList.isFirstElement(prevGifFromCache)) {
            btnPrevious.isClickable = false
        }
    }

    fun getNextRandomGif() : GifWrapper {
        val response = URL(getRandomGifUrl).readText()
        val gif = GifWrapper(response)
        gifsList.add(gif)
        return gif
    }

    fun loadGif(gif: GifWrapper) {
        Glide.with(this)
            .load(gif.gifUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(gifView)
    }
}