package com.example.mystoryapp.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.showToast
import com.example.mystoryapp.databinding.FragmentHomeBinding
import com.example.mystoryapp.myadapter.LoadingStateAdapter
import com.example.mystoryapp.myadapter.UserAdapter
import kotlinx.coroutines.launch

class FragmentHome : Fragment() {

    private lateinit var adapter: UserAdapter
    private var dbinding: FragmentHomeBinding? = null
    private val binding get() = dbinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dbinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel: MainViewModel by viewModels {
            MainViewModel.MainViewModelFactory.getInstance(requireContext())
        }
        initAdapter()
        initStory(viewModel)
    }

    private fun initAdapter() {
        adapter = UserAdapter()

        binding.rvStoryFeed.apply {
            adapter = this@FragmentHome.adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    retry()
                }
            )

            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }
        binding.swipeLayoutStory.setOnRefreshListener {
            binding.tvErrorStory.visibility = View.INVISIBLE
            adapter.retry()
            adapter.refresh()
        }
        adapter.addLoadStateListener { loadState ->
            if (loadState.mediator?.refresh is LoadState.Loading) {
                if (adapter.snapshot().isEmpty()) {
                    binding.loadingBar.visibility = View.VISIBLE
                }

                binding.tvNoStoryFound.visibility = View.INVISIBLE
            } else {
                binding.loadingBar.visibility = View.INVISIBLE
                binding.swipeLayoutStory.isRefreshing = false

                val error = when {
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    else -> null
                }

                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        showToast(
                            requireContext(),
                            getString(R.string.failed_story)
                        )
                    }
                    binding.tvErrorStory.visibility = View.VISIBLE
                }
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun initStory(viewModel: MainViewModel) {
        viewModel.getToken().observe(requireActivity()) {
            if (it != "null") {
                getStory(viewModel, "Bearer $it")
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getStory(viewModel: MainViewModel, token: String) {
        lifecycleScope.launch {
            viewModel.getStories(token).observe(requireActivity()) {
                adapter.submitData(requireActivity().lifecycle, it)
            }
        }
    }

    private fun retry() {
        adapter.retry()
    }
}