package com.example.mystoryapp.myadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystoryapp.databinding.ItemLoadingBinding

class LoadingStateAdapter (private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        return LoadingStateViewHolder(
            ItemLoadingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Loading) {
            holder.binding.loadingBar.visibility = View.VISIBLE
            holder.binding.buttonRetry.visibility = View.INVISIBLE
            holder.binding.msgError.visibility = View.INVISIBLE
        } else {
            holder.binding.loadingBar.visibility = View.INVISIBLE
        }

        if (loadState is LoadState.Error) {
            holder.binding.msgError.text = loadState.error.localizedMessage
            holder.binding.msgError.visibility = View.VISIBLE
            holder.binding.buttonRetry.visibility = View.VISIBLE
        }

        holder.binding.buttonRetry.setOnClickListener {
            retry.invoke()
        }
    }

    class LoadingStateViewHolder(val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)
}
