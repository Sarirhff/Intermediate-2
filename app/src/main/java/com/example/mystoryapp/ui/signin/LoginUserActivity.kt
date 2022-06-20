package com.example.mystoryapp.ui.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.showToast
import com.example.mystoryapp.databinding.ActivityLoginUserBinding
import com.example.mystoryapp.main.MainActivity

class LoginUserActivity : AppCompatActivity() {
    private val binding: ActivityLoginUserBinding by lazy {
        ActivityLoginUserBinding.inflate(layoutInflater)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAction()
        setupView()
    }

    override fun onResume() {
        super.onResume()
        setupCheck()
    }

    private fun setupCheck() {
        loginViewModel.checkIfNewUser().observe(this) {
            if (it) {
                val intent = Intent(this, BoardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.buttonLogin.setOnClickListener {
            if (!binding.edtEmailLogin.text.isNullOrEmpty() && !binding.edtPasswordLogin.text.isNullOrEmpty()) {
                val email = binding.edtEmailLogin.text.toString()
                val password = binding.edtPasswordLogin.text.toString()
                val result = loginViewModel.loginUser(email, password)

                result.observe(this) {
                    when (it) {
                        is Result.Loading -> {
                            binding.loadingBar.visibility = View.VISIBLE
                        }

                        is Result.Error -> {
                            binding.loadingBar.visibility = View.INVISIBLE
                            val error = it.error
                            showToast(this, error)
                        }

                        is Result.Success -> {
                            binding.loadingBar.visibility = View.INVISIBLE
                            val data = it.data
                            loginViewModel.saveToken(data.loginResult.token)
                            Log.d("LoginActivity", "Token: ${data.loginResult.token}")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                binding.edtEmailLogin.error = resources.getString(R.string.empty_email)

                if (binding.edtPasswordLogin.text.isNullOrEmpty()) {
                    binding.edtPasswordLogin.error =
                        resources.getString(R.string.empty_pass)
                }
            }
        }
        binding.btnRegist.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupView() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}