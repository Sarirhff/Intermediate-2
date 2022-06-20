package com.example.mystoryapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.mystoryapp.R
import com.example.mystoryapp.data.*
import com.example.mystoryapp.databinding.ActivityUploadStoryBinding
import com.example.mystoryapp.main.MainActivity
import com.example.mystoryapp.ui.signin.LoginUserActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream

class UploadStoryActivity : AppCompatActivity() {
    private var file: File? = null
    private var isBack: Boolean = true
    private var reducingDone: Boolean = false
    private var loc: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val binding: ActivityUploadStoryBinding by lazy {
        ActivityUploadStoryBinding.inflate(layoutInflater)
    }

    private val addStoryViewModel: UploadViewModel by viewModels {
        UploadViewModelFactory.getInstance(
            this
        )
    }

    private val appExecutor: AppExecutor by lazy {
        AppExecutor()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.story_add)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        myLocation()
        storyResult()
        setupAction()
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkSession() {
        addStoryViewModel.getToken().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun storyResult() {
        file = intent.getSerializableExtra(PHOTO_RESULT_EXTRA) as File
        isBack = intent.getBooleanExtra(IS_CAMERA_BACK_EXTRA, true)

        val result = rotateBitmap(BitmapFactory.decodeFile((file as File).path), isBack)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

        appExecutor.diskIO.execute {
            file = reduceFileImage(file as File)
            reducingDone = true
        }


        binding.imgUpdateStory.setImageBitmap(result)
    }

    private fun setupAction() {
        binding.btnUploadstory.setOnClickListener {
            addStoryViewModel.getToken().observe(this) {
                if (reducingDone) {
                    if (it == "null") {
                        val intent = Intent(this, LoginUserActivity::class.java)
                        startActivity(intent)
                    } else {
                        postStory("Bearer $it")
                    }
                } else {
                    showToast(this, getString(R.string.loading_cam))
                }
            }
        }
    }


    private fun postStory(token: String) {
        if (binding.edtDescStory.text.isNullOrEmpty()) {
            showToast(this, getString(R.string.story_desc_empty))
        } else {
            if (file != null) {
                binding.loadingBar.visibility = View.VISIBLE
                val description = binding.edtDescStory.text.toString()

                val result = addStoryViewModel.addNewStory(
                    token,
                    file as File,
                    description,
                    loc?.latitude?.toFloat(),
                    loc?.longitude?.toFloat()
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
                                getString(R.string.story_success)
                            )
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    myLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    myLocation()
                }
            }
        }

    private fun myLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                this.loc = location
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val PHOTO_RESULT_EXTRA = "photo_result_extra"
        const val IS_CAMERA_BACK_EXTRA = "is_camera_back_extra"
    }
}