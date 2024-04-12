package com.mycustomappapply.wotttoo.ui.main.fragments.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.HomeAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.data.local.SharedPreferencesManager
import com.mycustomappapply.wotttoo.databinding.FragmentMyProfileBinding
import com.mycustomappapply.wotttoo.models.Attributes
import com.mycustomappapply.wotttoo.models.CurrentUSerResponse
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
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.Screen
import com.mycustomappapply.wotttoo.utils.gone
import com.mycustomappapply.wotttoo.utils.showToast
import com.mycustomappapply.wotttoo.utils.toFormattedNumber
import com.mycustomappapply.wotttoo.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private lateinit var shrPref: SharedPreferencesManager

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBottomNavReselectListener()
        shrPref = SharedPreferencesManager(view.context)
        setupClickListeners()
        bindData()
        //setupRecyclerView()
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

    private fun bindData(): Unit = with(binding) {

        val username: String = shrPref.getData("username") ?: ""
        val fullName: String = shrPref.getData("fullName") ?: ""
        val bio: String = shrPref.getData("bio") ?: ""

        lifecycleScope.launch() {
            toggleProgressBar(true)
            userViewModel.createUser(username)
            toggleProgressBar(false)
        }

        noQuoteFoundContainer.gone()


        usernameTextView.text = username
        usernameTextView.text = when (shrPref.getData("username")) {
            null -> "Tvoj Profil"
            else -> shrPref.getData("username")
        }

        followerCountTextView.text = "100"
        followingCountTextView.text = "200"
        quoteCountTextView.text = "300"


        if (quotesSize == 0) {
            noQuoteFoundContainer.visible()
        } else {
            noQuoteFoundContainer.gone()
        }

     //   homeAdapter.setData(user?.attributes?.quotes ?: emptyList())
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

        /*tryAgainButton.setOnClickListener {
            userViewModel.getUser(forced = true)
        }*/
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
