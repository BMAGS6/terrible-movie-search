package edu.jwu.terriblemoviesearch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

/** Accepts a movie title, queries OMDb, and opens the first result. */
class MainActivity : AppCompatActivity()
{
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.search_edit_text)
        searchButton   = findViewById(R.id.search_button)
        progressBar    = findViewById(R.id.search_progress)
        requestQueue   = Volley.newRequestQueue(applicationContext)

        savedInstanceState?.getString(KEY_QUERY)?.let { savedQuery ->
            searchEditText.setText(savedQuery)
            searchEditText.setSelection(savedQuery.length)
        }

        searchButton.setOnClickListener {
            searchForMovie()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                searchForMovie()
                true
            }

            else
                false
        }
    }

    /** Performs a title search and handles success, empty results, and network errors. */
    private fun searchForMovie()
    {
        val query = searchEditText.text.toString().trim()

        if (query.isEmpty())
        {
            showToast(R.string.error_empty_query)
            return
        }

        if (BuildConfig.OMDB_API_KEY.isBlank())
        {
            showToast(R.string.error_missing_key)
            return
        }

        val url = Uri.Builder()
            .scheme("https")
            .authority("www.omdbapi.com")
            .appendQueryParameter("apikey", BuildConfig.OMDB_API_KEY)
            .appendQueryParameter("s", query)
            .build()
            .toString()

        setLoading(true)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                setLoading(false)

                if (!response.optString("Response").equals("True", ignoreCase = true))
                    showToast(R.string.error_no_movies)

                else
                {
                    val firstResult = response.optJSONArray("Search")?.optJSONObject(0)
                    val title       = firstResult?.optString("Title").orEmpty()
                    val posterUrl   = firstResult?.optString("Poster").orEmpty()

                    if (title.isBlank())
                        showToast(R.string.error_no_movies)

                    else
                    {
                        val intent = Intent(this, ResultActivity::class.java)
                        intent.putExtra(ResultActivity.EXTRA_MOVIE_TITLE, title)
                        intent.putExtra(ResultActivity.EXTRA_POSTER_URL, posterUrl)
                        startActivity(intent)
                    }
                }
            },
            {
                setLoading(false)
                showToast(R.string.error_network)
            })

        request.tag = SEARCH_REQUEST_TAG
        requestQueue.add(request)
    }

    /** Keeps the EditText contents when Android recreates the Activity on rotation. */
    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, searchEditText.text.toString())
    }

    /** Stops an older Activity instance from receiving an in-flight Volley callback. */
    override fun onStop()
    {
        super.onStop()
        requestQueue.cancelAll(SEARCH_REQUEST_TAG)
        setLoading(false)
    }

    private fun setLoading(loading: Boolean)
    {
        searchButton.isEnabled = !loading
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showToast(messageId: Int)
    {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
    }

    companion object
    {
        private const val SEARCH_REQUEST_TAG = "movie_search"
        private const val KEY_QUERY = "saved_movie_query"
    }
}
