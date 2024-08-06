package `in`.smiley.otpless

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        responseTV.text = response

        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}
