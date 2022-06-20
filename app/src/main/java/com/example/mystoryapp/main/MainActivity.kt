package com.example.mystoryapp.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.mystoryapp.R
import com.example.mystoryapp.data.showToast
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.myadapter.FragmentOptionsAdapter
import com.example.mystoryapp.ui.CameraActivity
import com.example.mystoryapp.ui.signin.LoginUserActivity

class MainActivity : AppCompatActivity() {
    private var isListMaps = true
    private lateinit var viewPager: ViewPager2

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory.getInstance(
            this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupAction()
        chooseFragment()

    }
    private fun chooseFragment() {
        val fragmentOptionsAdapter = FragmentOptionsAdapter(this)
        viewPager = findViewById(R.id.viewpager)
        viewPager.isUserInputEnabled = false
        viewPager.adapter = fragmentOptionsAdapter
    }

    override fun onResume() {
        super.onResume()
        checkSessionValid()
    }

    private fun checkSessionValid() {
        mainViewModel.checkToken().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_setting -> {
                mainViewModel.logout()
            }
            R.id.maps_setting -> {
                if (isListMaps) {
                    isListMaps = false
                    viewPager.setCurrentItem(1, true)
                } else {
                    isListMaps = true
                    viewPager.setCurrentItem(0, true)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAction() {
        binding.btnAddStory.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showToast(this, getString(R.string.permission_cam))
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}