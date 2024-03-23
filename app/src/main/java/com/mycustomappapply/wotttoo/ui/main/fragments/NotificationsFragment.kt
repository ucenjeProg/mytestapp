package com.mycustomappapply.wotttoo.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.NotificationAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentNotificationsBinding
import com.mycustomappapply.wotttoo.models.Notification
import com.mycustomappapply.wotttoo.models.NotificationsResponse
import com.mycustomappapply.wotttoo.ui.on_boarding.StartActivity
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.NotificationViewModel
import com.mycustomappapply.wotttoo.utils.Constants.TEXT_DIRECT_TO_LOGIN
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.gone
import com.mycustomappapply.wotttoo.utils.invisible
import com.mycustomappapply.wotttoo.utils.safeInvisible
import com.mycustomappapply.wotttoo.utils.showToast
import com.mycustomappapply.wotttoo.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {
    private val notificationAdapter by lazy { NotificationAdapter() }
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var currentNotifications: List<Notification>? = null
    private var currentPage = 1
    private var paginatingFinished = false
    private var paginationLoading = false
    private var comingBackFromQuote = false

    private var clearAllMenuItem: MenuItem? = null
    private var clearAllTextActionView: View? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupUi()
        setupRecyclerView()
        setupClickListeners()
        subscribeObservers()
    }

    private fun setupUi(): Any = with(binding) {
        if (!authViewModel.isAuthenticated) {
            notSignedinContainer.visible()
            loginButton.setOnClickListener {
                navigateLogin()
            }
        } else {
            notificationViewModel.getNotifications()
        }
    }

    private fun navigateLogin() {
        authViewModel.logout()
        val intent = Intent(requireActivity(), StartActivity::class.java)
        intent.putExtra(TEXT_DIRECT_TO_LOGIN, true)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun subscribeObservers() {
        notificationViewModel.clearNotifications.observe(viewLifecycleOwner) { dataState: DataState<NotificationsResponse> ->
            when (dataState) {
                is DataState.Success -> {
                    clearAllMenuItem?.actionView = clearAllTextActionView
                    currentNotifications = emptyList()
                    notificationAdapter.setData(currentNotifications!!)
                    if (currentNotifications!!.isEmpty()) {
                        paginatingFinished = true
                        binding.noNotificationsContainer.visibility = View.VISIBLE
                    }
                }

                is DataState.Loading -> {
                    clearAllMenuItem?.setActionView(R.layout.progress_bar_layout)
                }

                is DataState.Fail -> {
                    showToast(dataState.message)
                    clearAllMenuItem?.actionView = clearAllTextActionView
                }
            }
        }
        notificationViewModel.notifications.observe(viewLifecycleOwner) { dataState: DataState<NotificationsResponse> ->
            when (dataState) {
                is DataState.Success -> with(binding) {
                    paginationProgressBar.safeInvisible()
                    paginationLoading = false
                    if (currentNotifications?.size == dataState.data!!.notifications!!.toList().size && !comingBackFromQuote) {
                        paginatingFinished = true
                    }
                    comingBackFromQuote = false
                    currentNotifications = dataState.data.notifications!!.toList()
                    progressBar.invisible()
                    notificationAdapter.setData(currentNotifications!!)
                    if (currentNotifications!!.isEmpty()) {
                        paginatingFinished = true
                        noNotificationsContainer.visible()
                    }
                }

                is DataState.Loading -> with(binding) {
                    noNotificationsContainer.invisible()
                    failContainer.gone()
                    if (paginationLoading) paginationProgressBar.visible() else progressBar.visible()
                }

                is DataState.Fail -> with(binding) {
                    failContainer.visible()
                    failMessage.text = dataState.message
                    if (paginationProgressBar.visibility == View.VISIBLE) {
                        paginationProgressBar.invisible()
                    }
                    paginationLoading = false
                    progressBar.invisible()
                    showToast(dataState.message)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        notificationAdapter.onNotificationClickListener = { string: String? ->
            val action: NotificationsFragmentDirections.ActionNotificationsFragmentToQuoteFragment =
                NotificationsFragmentDirections.actionNotificationsFragmentToQuoteFragment(string)
            findNavController().navigate(action)
        }
        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.notificationsRecyclerView.apply {
            adapter = notificationAdapter
            layoutManager = mLayoutManager
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        if (!paginatingFinished && mLayoutManager.findLastCompletelyVisibleItemPosition() == (currentNotifications!!.size - 1) && !paginationLoading) {
                            currentPage++
                            paginationLoading = true
                            notificationViewModel.getNotifications(currentPage, forced = true)
                        }
                    }
                }
            })
        }


    }

    private fun setupClickListeners() {
        binding.tryAgainButton.setOnClickListener {
            notificationViewModel.getNotifications(1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (authViewModel.currentUserId != null) {
            inflater.inflate(R.menu.notifications_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clear_notifications_menu_item) {
            if (currentNotifications!!.isNotEmpty()) {
                clearAllMenuItem = item
                clearAllTextActionView = clearAllMenuItem?.actionView
                notificationViewModel.clearNotifications()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationsBinding = FragmentNotificationsBinding.inflate(inflater, container, false)
}