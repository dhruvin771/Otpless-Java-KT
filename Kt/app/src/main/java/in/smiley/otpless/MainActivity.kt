package `in`.smiley.otpless

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.otpless.dto.HeadlessChannelType
import com.otpless.dto.HeadlessRequest
import com.otpless.dto.HeadlessResponse
import com.otpless.dto.OtplessRequest
import com.otpless.dto.OtplessResponse
import com.otpless.main.OtplessManager
import com.otpless.main.OtplessView
import com.otpless.utils.Utility


class MainActivity : AppCompatActivity() {
    private lateinit var showPreBuildUIButton: Button
    private lateinit var whatsappButton: Button

    private lateinit var otplessView: OtplessView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeOtpless()
        initializeViews()
        setOnClickListeners()
    }

    private fun initializeOtpless() {
        otplessView = OtplessManager.getInstance().getOtplessView(this)
    }

    private fun initializeViews() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
        }

        showPreBuildUIButton = findViewById(R.id.btn_pre_built_ui)
        whatsappButton = findViewById(R.id.whatsapp)

    }

    private fun setOnClickListeners() {
        showPreBuildUIButton.setOnClickListener {
            showPreBuiltUI()
        }

        whatsappButton.setOnClickListener {
            startActivity(Intent(this, HeadlessActivity::class.java))
        }
    }

    private fun showPreBuiltUI() {
        val request = OtplessRequest("appid")
        otplessView.setCallback(request, this::onOtplessCallback)
        otplessView.showOtplessLoginPage(request, this::onOtplessCallback)
        otplessView.verifyIntent(intent)
    }


    private fun onOtplessCallback(response: OtplessResponse) {

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val jsonResponse = if (response.errorMessage != null) {
            gson.toJson(response.errorMessage)
        } else {
            gson.toJson(response.data)
        }

        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Otpless Response", jsonResponse)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied Successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, ResponseActivity::class.java)
        intent.putExtra("RESPONSE", jsonResponse)
        startActivity(intent)
        Log.d("OtplessCallback", "Response: $jsonResponse")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        otplessView.verifyIntent(intent)
    }

    override fun onBackPressed() {
        if (otplessView.onBackPressed()) return
        super.onBackPressed()
    }
}