package `in`.smiley.otpless

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.otpless.dto.HeadlessRequest
import com.otpless.dto.HeadlessResponse
import com.otpless.main.OtplessManager
import com.otpless.main.OtplessView


class BottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var otplessView: OtplessView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet, container, false)

        val countryCodeField = view.findViewById<EditText>(R.id.countryCodeField)
        val mobileNumberField = view.findViewById<EditText>(R.id.mobileNumberField)
        val otpField = view.findViewById<EditText>(R.id.otpField)
        val sendOtpButton = view.findViewById<Button>(R.id.sendOtpButton)
        val verifyOtp = view.findViewById<Button>(R.id.verifyOtp)
        initializeOtpless(savedInstanceState = savedInstanceState)
        sendOtpButton.setOnClickListener {
            val countryCode = countryCodeField.text.toString()
            val mobileNumber = mobileNumberField.text.toString()

            if (countryCode.isEmpty()) {
                Toast.makeText(context, "Country code is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mobileNumber.isEmpty()) {
                Toast.makeText(context, "Mobile number is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(context, "OTP Sent to $countryCode$mobileNumber", Toast.LENGTH_SHORT).show()
            val request = HeadlessRequest()
            request.setPhoneNumber(countryCode, mobileNumber)
            otplessView.startHeadless(request, this::onHeadlessCallback)
        }

        verifyOtp.setOnClickListener {
            val countryCode = countryCodeField.text.toString()
            val mobileNumber = mobileNumberField.text.toString()
            val otp = otpField.text.toString()

            if (countryCode.isEmpty()) {
                Toast.makeText(context, "Country code is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mobileNumber.isEmpty()) {
                Toast.makeText(context, "Mobile number is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (otp.isEmpty()) {
                Toast.makeText(context, "OTP is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = HeadlessRequest()
            request.setPhoneNumber(countryCode, mobileNumber)
            request.setOtp(otp)
            otplessView.startHeadless(request, ::onHeadlessCallback)
        }
        return view
    }

    private fun initializeOtpless(savedInstanceState: Bundle?) {
        otplessView = OtplessManager.getInstance().getOtplessView(activity)
        otplessView.initHeadless("appid", savedInstanceState)
        otplessView.setHeadlessCallback(this::onHeadlessCallback)
    }

    private fun onHeadlessCallback(response: HeadlessResponse) {
        if (response.statusCode == 200) {
            when (response.responseType) {
                "ONETAP" -> {
                    val responseWithToken = response.response
                    val clipboard: ClipboardManager =
                        requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Otpless Response", responseWithToken.toString())
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(requireContext(), "Copied Successfully", Toast.LENGTH_SHORT).show()

                    dismiss()

                    val intent = Intent(requireContext(), ResponseActivity::class.java)
                    intent.putExtra("RESPONSE", responseWithToken.toString())
                    startActivity(intent)
                }
            }
        } else {
            val error = response.response?.optString("errorMessage")
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
        Log.d("Otpless Callback", "Response: ${response.toString()}")
    }

}
