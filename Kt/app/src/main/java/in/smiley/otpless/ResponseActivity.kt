package `in`.smiley.otpless

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException

class ResponseActivity : AppCompatActivity() {
    private lateinit var responseTV: TextView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_response)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.response)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        responseTV = findViewById(R.id.tv_response)
        backButton = findViewById(R.id.btn_back)

        val response = intent.getStringExtra("RESPONSE")

        responseTV.text = formatJson(response)

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun formatJson(response: String?): String {
        return try {
            val jsonElement = JsonParser.parseString(response)
            Gson().newBuilder().setPrettyPrinting().create().toJson(jsonElement)
        } catch (e: JsonSyntaxException) {
            "Invalid JSON format"
        }
    }
}
