package edu.jwu.terriblemoviesearch

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

/** Displays the first OMDb search result sent by MainActivity through Intent extras. */
class ResultActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val movieTitle: TextView = findViewById(R.id.movie_title)
        val posterImage: ImageView = findViewById(R.id.poster_image)
        val backButton: Button = findViewById(R.id.back_button)

        movieTitle.text = intent.getStringExtra(EXTRA_MOVIE_TITLE)
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.unknown_movie)

        val posterUrl = intent.getStringExtra(EXTRA_POSTER_URL)

        if (posterUrl.isNullOrBlank() || posterUrl.equals("N/A", ignoreCase = true))
        {
            posterImage.setImageResource(R.drawable.ic_no_poster)
        }
        else
        {
            Picasso.get()
                .load(posterUrl)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_no_poster)
                .fit()
                .centerInside()
                .into(posterImage)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    companion object
    {
        const val EXTRA_MOVIE_TITLE = "edu.jwu.terriblemoviesearch.EXTRA_MOVIE_TITLE"
        const val EXTRA_POSTER_URL = "edu.jwu.terriblemoviesearch.EXTRA_POSTER_URL"
    }
}
