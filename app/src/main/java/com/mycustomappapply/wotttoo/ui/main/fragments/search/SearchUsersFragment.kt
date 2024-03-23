package com.mycustomappapply.wotttoo.ui.main.fragments.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.adapters.SearchUserAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentSearchUsersBinding
import com.mycustomappapply.wotttoo.models.UsersResponse
import com.mycustomappapply.wotttoo.ui.main.MainActivity
import com.mycustomappapply.wotttoo.ui.viewmodels.SharedViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.UserViewModel
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchUsersFragment :
    BaseFragment<FragmentSearchUsersBinding>() {
    private val searchAdapter by lazy { SearchUserAdapter() }
    private val userViewModel: UserViewModel by viewModels({ requireParentFragment() })
    private val sharedViewModel: SharedViewModel by viewModels()

    private var searchViewTextChanged = false
    private var currentSearchText = ""
    private var currentPage = 1
    private var paginatingFinished = false
    private var paginationLoading = false
    private var usersSize = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper())
            .postDelayed({
                userViewModel.getUsers("text")
            }, 300)

        setupRecyclerView()
        subscribeObservers()
    }

    private fun setupRecyclerView() {
        searchAdapter.onUserClickListener = { user ->
            if (sharedViewModel.searchTextUser.value?.isNotEmpty() == true) {
                (requireActivity() as MainActivity).dismissSearchKeyboard()
            }
            val action: SearchFragmentDirections.ActionSearchFragmentToUserProfileFragment =
                SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(user.id)
            findNavController().navigate(action)

        }
        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.searchUsersRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = mLayoutManager

            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        if (!paginatingFinished && mLayoutManager.findLastCompletelyVisibleItemPosition() == (usersSize - 1) && !paginationLoading) {
                            currentPage++
                            paginationLoading = true
                            userViewModel.getUsers(currentSearchText, true, currentPage)
                        }
                    }
                }
            })
        }

    }

    private fun subscribeObservers() {
        userViewModel.users.observe(viewLifecycleOwner) {dataState: DataState<UsersResponse> ->
            when (dataState) {
                is DataState.Success -> {
                    paginationLoading = false
                    if (dataState.data!!.data.isEmpty()) {
                        paginatingFinished = true
                        binding.noUserFound.visibility = View.VISIBLE
                    }
                    usersSize = dataState.data.data.size
                    searchAdapter.setData(dataState.data.data)
                }

                is DataState.Fail -> {
                    paginationLoading = false
                    showToast(dataState.message)
                }

                is DataState.Loading -> {

                }
            }
        }
        sharedViewModel.searchTextUser.observe(viewLifecycleOwner) { text ->
            if (text.isNotEmpty()) {
                currentSearchText = text
                searchViewTextChanged = true
                Handler(Looper.getMainLooper())
                    .postDelayed({
                        userViewModel.getUsers(text)
                    }, 300)
            }
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchUsersBinding = FragmentSearchUsersBinding.inflate(inflater, container, false)

}