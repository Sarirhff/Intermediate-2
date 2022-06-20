package com.example.mystoryapp.myadapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.databinding.ItemStoryBinding
import com.example.mystoryapp.ui.InfoStoryActivity

class UserAdapter: PagingDataAdapter<ListStoryItem, UserAdapter.ViewHolder>(
    DIFF_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val storyUser = getItem(position)
        holder.data(storyUser)

        holder.binding.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, InfoStoryActivity::class.java)
            intent.putExtra(InfoStoryActivity.DETAIL_STORY_EXTRA, storyUser)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.binding.imgStory, "picture"),
                    Pair(holder.binding.tvDescripStory, "description")
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    inner class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun data(storyUser: ListStoryItem?) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(storyUser?.photoUrl)
                    .into(binding.imgStory)
                tvStoryUsername.text = storyUser?.name
                tvDescripStory.text = storyUser?.description
            }
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

