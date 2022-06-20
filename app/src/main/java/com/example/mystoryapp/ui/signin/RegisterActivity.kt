package com.example.mystoryapp.ui.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.R
import com.example.mystoryapp.data.showToast
import com.example.mystoryapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val signupViewModel: RegisViewModel by viewModels {
        RegisViewModel.RegisViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
    private fun setupAction() {
        binding.buttonRegis.setOnClickListener {
            val name = binding.edtUsrnameRegis.text.toString()
            val email = binding.edtEmailRegis.text.toString()
            val password = binding.edtPasswordRegis.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val result = signupViewModel.regisUser(
                    name,
                    email,
                    password
                )
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
                            showToast(
                                this,
                                getString(R.string.sign_up_success))
                            val intent = Intent(this, LoginUserActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                if (name.isEmpty()) binding.edtUsrnameRegis.error =
                    getString(R.string.empty_name)
                if (email.isEmpty()) binding.edtEmailRegis.error =
                    getString(R.string.empty_email)
                if (password.isEmpty()) binding.edtPasswordRegis.error =
                    getString(R.string.empty_pass)
            }
        }
        binding.toLogin.setOnClickListener {
            val intent = Intent(this, LoginUserActivity::class.java)
            startActivity(intent)
        }
    }

}