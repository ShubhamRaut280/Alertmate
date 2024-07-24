package com.shubham.emergencyapplication.Ui.Activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.databinding.ActivityLoginBinding
import android.text.Editable
import android.text.TextWatcher
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class LoginActivity : AppCompatActivity() {

    private var isLogin = true
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        init()
    }

    private fun init() {
        binding.login.setOnClickListener {
            val email = binding.email.editText?.text.toString()
            val password = binding.pass.editText?.text.toString()

            // Clear previous errors
            binding.email.error = null
            binding.pass.error = null

            // Validate inputs
            if (email.isEmpty()) binding.email.error = "Email empty"
            if (password.isEmpty()) binding.pass.error = "Password empty"
            if (email.isNotEmpty() && !email.contains('@')) binding.email.error = "Invalid email format"
            if (password.length < 6) binding.pass.error = "Password must be at least 6 characters long"

            // Proceed if no errors
            if (email.isNotEmpty() && password.isNotEmpty() && email.contains('@') && password.length >= 6) {
                if (isLogin) {
                    loginUser(email, password)
                } else {
                    signUpUser(email, password)
                }
            }
        }

        binding.newAccount.setOnClickListener {
            if (isLogin) {
                binding.login.text = "Create Account"
                binding.forgorPass.visibility = View.INVISIBLE
                binding.newAccount.text = "Login with existing account"
                isLogin = false
                applyTransitionAnimations()
            } else {
                binding.login.text = "Login"
                binding.forgorPass.visibility = View.VISIBLE
                binding.newAccount.text = "Create a new account"
                isLogin = true
                applyTransitionAnimations()
            }
        }

        // Add TextWatchers to clear errors when typing
        binding.email.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.email.error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.pass.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.pass.error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    // Optionally start a new activity
                    showLoginSuccessDialog()
                } else {
                    val exception = task.exception
                    Log.w("Login", "signInWithEmail:failure", exception)
                    handleAuthException(exception)
                }
            }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SignUp", "createUserWithEmail:success")
                    showSigninsuccess()
//                    Toast.makeText(this@LoginActivity, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                } else {
                    val exception = task.exception
                    Log.w("SignUp", "createUserWithEmail:failure", exception)
                    handleAuthException(exception)
                }
            }
    }

    private fun handleAuthException(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                binding.email.error = "Email is not registered"
            }
            is FirebaseAuthInvalidCredentialsException -> {
                if (exception.message?.contains("The email address is badly formatted") == true) {
                    binding.email.error = "Invalid email format"
                } else {
                    binding.email.error = "Invalid credentials"
                    binding.pass.error = "Invalid credentials"
                }
            }
            is FirebaseAuthWeakPasswordException -> {
                binding.pass.error = "Password must be at least 6 characters long"
            }
            else -> {
                Toast.makeText(this@LoginActivity, "Operation Failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyTransitionAnimations() {
        val enterAnim = if (isLogin) R.anim.slide_in_right else R.anim.slide_in_left
        val exitAnim = if (isLogin) R.anim.slide_out_left else R.anim.slide_out_right

        overridePendingTransition(enterAnim, exitAnim)
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
//             startActivity(Intent(this, DashboardActivity::class.java))
        }
    }


    fun showLoginSuccessDialog(){
        try {
            val dialogToCreateAlert = Dialog(this)
            dialogToCreateAlert.setContentView(R.layout.success_dialog)

            dialogToCreateAlert.window!!.setBackgroundDrawableResource(R.drawable.round_corner_card_bg)
            dialogToCreateAlert.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val confirm = dialogToCreateAlert.findViewById<MaterialButton>(R.id.action)
            val title = dialogToCreateAlert.findViewById<TextView>(R.id.title)
            val subtitle = dialogToCreateAlert.findViewById<TextView>(R.id.subTitle)
            title.text = "Welcome back!"
            subtitle.text = resources.getString(R.string.logininSuccessSubtitle)

            confirm.text = "Go to Dashboard"

            title.textAlignment = TEXT_ALIGNMENT_CENTER

            confirm.setOnClickListener { v: View? ->
                startActivity(Intent(this, DashboardActivity::class.java))
                dialogToCreateAlert.dismiss()
            }
            dialogToCreateAlert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun showSigninsuccess()
    {
        try {
            val dialogToCreateAlert = Dialog(this)
            dialogToCreateAlert.setContentView(R.layout.success_dialog)

            dialogToCreateAlert.window!!.setBackgroundDrawableResource(R.drawable.round_corner_card_bg)
            dialogToCreateAlert.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)


            val confirm = dialogToCreateAlert.findViewById<MaterialButton>(R.id.action)
            val title = dialogToCreateAlert.findViewById<TextView>(R.id.title)
            val subtitle = dialogToCreateAlert.findViewById<TextView>(R.id.subTitle)
            title.text = "Success"
            subtitle.text = resources.getString(R.string.singUpsuccessSubtitle)

            confirm.text = "Login"

            title.textAlignment = TEXT_ALIGNMENT_CENTER

            confirm.setOnClickListener { v: View? ->
                dialogToCreateAlert.dismiss()
                isLogin = true
                binding.email.editText!!.clearComposingText()
                binding.pass.editText!!.clearComposingText()
                binding.login.text = "Login"
                binding.forgorPass.visibility = View.VISIBLE
                binding.newAccount.text = "Create new account"
            }
            dialogToCreateAlert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
