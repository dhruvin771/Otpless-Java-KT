package `in`.smiley.otpless

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.otpless.dto.HeadlessChannelType
import com.otpless.dto.HeadlessRequest
import com.otpless.dto.HeadlessResponse
import com.otpless.main.OtplessManager
import com.otpless.main.OtplessView
import com.otpless.utils.Utility


class HeadlessActivity : AppCompatActivity() {
    private lateinit var otplessView: OtplessView
    private lateinit var whatsappButton: Button
    private lateinit var googleButton: Button
    private lateinit var appleButton: Button
    private lateinit var trueCallerButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        setContentView(R.layout.activity_headless)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headless_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeOtpless(savedInstanceState = savedInstanceState)
        initializeViews()
        setOnClickListeners()
    }

    private fun initializeOtpless(savedInstanceState: Bundle?) {
        otplessView = OtplessManager.getInstance().getOtplessView(this)
        otplessView.initHeadless("F4G12HX4JS7SQNYY19L0", savedInstanceState)
        otplessView.setHeadlessCallback(this::onHeadlessCallback)
        otplessView.verifyIntent(intent)
    }

    private fun startSSOAuth(channelType: HeadlessChannelType) {
        val request = HeadlessRequest()
        request.setChannelType(channelType)
        otplessView.startHeadless(request, ::onHeadlessCallback)
    }

    private fun onHeadlessCallback(response: HeadlessResponse) {
        if (response.statusCode == 200) {
            when (response.responseType) {
                "ONETAP" -> {
                    val responseWithToken = response.response
                    val clipboard: ClipboardManager =
                        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip =
                        ClipData.newPlainText("Otpless Response", responseWithToken.toString())
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "Copied Successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ResponseActivity::class.java)
                    intent.putExtra("RESPONSE", responseWithToken.toString())
                    startActivity(intent)
                }
            }
        } else {
            // handle error
            val error = response.response?.optString("errorMessage")
            Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()

        }
        Log.d("Otpless Callback", "Response: $response.toString()")


    }

    override fun onBackPressed() {
        if (otplessView.onBackPressed()) return;
        super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        otplessView.verifyIntent(intent)
    }

    private fun setOnClickListeners() {
        whatsappButton.setOnClickListener {
            if (Utility.isWhatsAppInstalled(applicationContext)) {
                startSSOAuth(HeadlessChannelType.WHATSAPP)
            } else {
                Toast.makeText(applicationContext, "Whatsapp not installed", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        googleButton.setOnClickListener {
            startSSOAuth(HeadlessChannelType.GMAIL)
        }

        appleButton.setOnClickListener {
            startSSOAuth(HeadlessChannelType.APPLE)
        }

        trueCallerButton.setOnClickListener {
            if (isTruecallerInstalled()) {
                Toast.makeText(
                    applicationContext,
                    "Truecaller service is not available.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(applicationContext, "TrueCaller not installed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun isTruecallerInstalled(): Boolean {
        val packageManager = applicationContext.packageManager
        return try {
            packageManager.getPackageInfo("com.truecaller", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun initializeViews() {

        whatsappButton = findViewById(R.id.whatsappButton)
        googleButton = findViewById(R.id.googleButton)
        appleButton = findViewById(R.id.appleButton)
        trueCallerButton = findViewById(R.id.trueCallerButton)
    }
}