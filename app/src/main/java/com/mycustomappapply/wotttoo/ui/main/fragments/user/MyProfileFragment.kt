package com.mycustomappapply.wotttoo.ui.main.fragments.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.HomeAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.data.local.SharedPreferencesManager
import com.mycustomappapply.wotttoo.databinding.FragmentMyProfileBinding
import com.mycustomappapply.wotttoo.models.Attributes
import com.mycustomappapply.wotttoo.models.Quote
import com.mycustomappapply.wotttoo.models.User
import com.mycustomappapply.wotttoo.ui.main.MainActivity
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.QuoteViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.UserViewModel
import com.mycustomappapply.wotttoo.utils.BottomNavReselectListener
import com.mycustomappapply.wotttoo.utils.Constants.KEY_DELETED_QUOTE
import com.mycustomappapply.wotttoo.utils.Constants.KEY_UPDATED_QUOTE
import com.mycustomappapply.wotttoo.utils.Constants.TEXT_UPDATED_USER
import com.mycustomappapply.wotttoo.utils.Screen
import com.mycustomappapply.wotttoo.utils.gone
import com.mycustomappapply.wotttoo.utils.toFormattedNumber
import com.mycustomappapply.wotttoo.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(), BottomNavReselectListener {

    private val homeAdapter: HomeAdapter by lazy { HomeAdapter() }
    private val userViewModel: UserViewModel by viewModels()
    private val quoteViewModel: QuoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var paginationLoading = false
    private var currentPage = 1
    private var paginationFinished = false
    private var quotesSize = 0
    private var currentUser: User? = null
    lateinit var shrPref: SharedPreferencesManager

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBottomNavReselectListener()
        shrPref = SharedPreferencesManager(view.context)
        setupClickListeners()
        setFragmentResultListener()
        setupRecyclerView()
    }

    private fun initBottomNavReselectListener() {
        (requireActivity() as MainActivity).bottomNavItemReselectListener = this
    }


    private fun toggleProgressBar(
        loading: Boolean,
        failed: Boolean = false
    ) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.profileContent.visibility = if (loading || failed) View.INVISIBLE else View.VISIBLE
    }

    private fun bindData(
        user: User?
    ): Unit = with(binding) {

        val userBind: Attributes? = user?.attributes

        val username: String? =shrPref.getData("username")

        usernameTextView.text=username
        usernameTextView.text = when (shrPref.getData("username")) {
            null -> "Tvoj Profil"
            else -> userBind?.username
        }

        followerCountTextView.text = when {
            userBind?.followers?.size?.toFormattedNumber() == null -> "0"
            else -> userBind.followers?.size?.toFormattedNumber()
        }

        followingCountTextView.text = when {
            userBind?.followingUsers?.size == null -> "0"
            else -> userBind.followingUsers.size.toString()
        }

        quoteCountTextView.text = (userBind?.totalQuoteCount ?: 0).toFormattedNumber()

        if (userBind?.profileImage != null && userBind.profileImage != "") {
            userPhotoImageView.load(userBind.profileImage) {
                error(R.drawable.user)
            }

        } else {
            userPhotoImageView.load(R.drawable.user)
        }

        if (quotesSize == 0) {
            noQuoteFoundContainer.visible()
        } else {
            noQuoteFoundContainer.gone()
        }

        homeAdapter.setData(user?.attributes?.quotes ?: emptyList())
    }



    private fun hideRefreshLayoutProgress() {
        if (binding.refreshLayout.isRefreshing) binding.refreshLayout.isRefreshing = false
    }



    private fun setupClickListeners(): Unit = with(binding) {

        refreshLayout.setOnRefreshListener {
            userViewModel.getUser(forced = true)
        }

        editUserButton.setOnClickListener {
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToEditUserFragment()
            action.user = currentUser
            findNavController().navigate(action)
        }

        tryAgainButton.setOnClickListener {
            userViewModel.getUser(forced = true)
        }
    }



    private fun setFragmentResultListener() {

        parentFragmentManager.setFragmentResultListener(
            KEY_DELETED_QUOTE,
            viewLifecycleOwner
        ) { requestKey: String, deletedQuote: Bundle ->
            userViewModel.notifyQuoteRemoved(deletedQuote[KEY_DELETED_QUOTE] as Quote)
        }

        parentFragmentManager.setFragmentResultListener(
            KEY_UPDATED_QUOTE,
            viewLifecycleOwner
        ) { s: String, updatedQuote: Bundle ->
            userViewModel.notifyQuoteUpdated((updatedQuote[KEY_UPDATED_QUOTE] as Quote))
        }

        parentFragmentManager.setFragmentResultListener(
            TEXT_UPDATED_USER,
            viewLifecycleOwner
        ) { s: String, updatedUser: Bundle ->
            binding.apply {
                currentUser = updatedUser[TEXT_UPDATED_USER] as User
                bindData(currentUser)
            }
        }
    }



    private fun setupRecyclerView() {

        homeAdapter.currentUserId = authViewModel.currentUserId

        homeAdapter.onLikeClickListener = { quote ->
            quoteViewModel.toggleLike(quote)
        }

        homeAdapter.onQuoteOptionsClickListener = { quote ->
            val action = MyProfileFragmentDirections.actionGlobalQuoteOptionsFragment(quote)
            findNavController().navigate(action)
        }

        homeAdapter.onDownloadClickListener = { quote ->
            val action = MyProfileFragmentDirections.actionGlobalDownloadQuoteFragment(quote)
            findNavController().navigate(action)
        }

        homeAdapter.onShareClickListener = { quote ->
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, quote.attributes.title + "\n\n#wotttoo App")
            val shareIntent: Intent = Intent.createChooser(intent, getString(R.string.share_quote))
            startActivity(shareIntent)

        }

        binding.userQuotesRecyclerView.adapter = homeAdapter
        binding.userQuotesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.profileContent.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                val notReachedBottom: Boolean = v.canScrollVertically(1)
                if (!notReachedBottom && !paginationLoading && !paginationFinished) {
                    currentPage++
                    paginationLoading = true
                    userViewModel.getMoreUserQuotes(page = currentPage)
                }
            }
        })
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileBinding = FragmentMyProfileBinding.inflate(inflater, container, false)

    override fun itemReselected(screen: Screen?) {
        if (screen == Screen.MyProfile) binding.profileContent.smoothScrollTo(0, 0, 1000)
    }


}
