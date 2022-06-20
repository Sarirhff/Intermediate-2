package com.example.mystoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mystoryapp.R
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.databinding.ActivityInfoStoryBinding

class InfoStoryActivity : AppCompatActivity() {
    private val binding: ActivityInfoStoryBinding by lazy {
        ActivityInfoStoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        val storyUser = intent.getParcelableExtra<ListStoryItem>(DETAIL_STORY_EXTRA)
        val name = storyUser?.name
        val description = storyUser?.description
        val imgUrl = storyUser?.photoUrl
        supportActionBar?.title = name
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.tvDescripDetail.text = description
        Glide.with(this)
            .load(imgUrl)
            .into(binding.imgDetailStory)
    }

    companion object {
        const val DETAIL_STORY_EXTRA = "detail_story_extra"

    }

}