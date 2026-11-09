package edu.jwu.terriblemoviesearch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/** Displays the application logo and title before opening the search screen. */
class SplashActivity : AppCompatActivity()
{
    private val handler = Handler(Looper.getMainLooper())

    private val openSearch = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler.postDelayed(openSearch, SPLASH_DURATION_MS)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        handler.removeCallbacks(openSearch)
    }

    companion object
    {
        private const val SPLASH_DURATION_MS = 850L
    }
}
