package com.mycustomappapply.wotttoo.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.HomeAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentHomeBinding
import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.Quote
import com.mycustomappapply.wotttoo.ui.main.MainActivity
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.QuoteViewModel
import com.mycustomappapply.wotttoo.utils.BottomNavReselectListener
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.EVENT_REFRESH_QUOTES
import com.mycustomappapply.wotttoo.utils.EventState
import com.mycustomappapply.wotttoo.utils.Screen
import com.mycustomappapply.wotttoo.utils.safeGone
import com.mycustomappapply.wotttoo.utils.safeVisible
import com.mycustomappapply.wotttoo.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), BottomNavReselectListener {

    private val quoteViewModel: QuoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter() }

    private var quotesSize: Int = 0
    private var currentPage: Int = 1

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel.saveUser()
        initBottomNavReselectListener()
        setupClickListeners()
        setupRecyclerView()
        subscribeObservers()
        quoteViewModel.getQuotes()
    }

    private fun initBottomNavReselectListener() {
        (requireActivity() as? MainActivity)?.bottomNavItemReselectListener = this
    }

    private fun setupClickListeners() {
        binding.tryAgainButton.setOnClickListener {
            quoteViewModel.getQuotes(forced = true)
        }
    }

    private fun subscribeObservers(): Unit = with(binding) {

        quoteViewModel.quotes.observe(viewLifecycleOwner) { dataSet: DataState<ArticleResponse> ->

            when (dataSet) {
                is DataState.Success -> {

                    failContainer.safeGone()
                    homeProgressBar.safeGone()
                    homeViewPager.safeVisible()
                    quotesSize = dataSet.data?.data?.size ?: 0

                    // Filter the dataset to include only articles with state == 1
                    val visibleAdapterDataSet: List<Quote>? = dataSet.data?.data?.filter { article: Quote ->
                        article.attributes.state == 1
                    }

                    // Check if the filtered list is not null and then set it to the adapter
                    visibleAdapterDataSet?.let { data: List<Quote> ->
                        homeAdapter.setData(newQuoteList = data)
                    }

                    Log.d("mytag", "${dataSet.data?.data}")

                    if (refreshLayout.isRefreshing) {
                        refreshLayout.isRefreshing = false
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.homeViewPager.currentItem = 0
                        }, 200)
                    }
                }

                is DataState.Fail -> {

                    failContainer.visible()
                    failMessage.text = dataSet.message
                    refreshLayout.isRefreshing = false
                    homeProgressBar.safeGone()
                }

                is DataState.Loading -> {

                    failContainer.safeGone()
                    if (currentPage == 1 && !binding.refreshLayout.isRefreshing) {
                        binding.homeProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {

        binding.refreshLayout.setOnRefreshListener {
            onRefreshQuotes()
        }

        homeAdapter.onDownloadClickListener = { quote: Quote ->
            val action = HomeFragmentDirections.actionGlobalDownloadQuoteFragment(quote)
            findNavController().navigate(action)
        }

        homeAdapter.onShareClickListener = { nakana: Quote ->
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, nakana.attributes.title)
            val shareIntent: Intent = Intent.createChooser(intent, getString(R.string.share_quote))
            startActivity(shareIntent)
        }

        homeAdapter.currentUserId = authViewModel.currentUserId

        homeAdapter.onQuoteOptionsClickListener = { quote: Quote ->
            val action = HomeFragmentDirections.actionGlobalQuoteOptionsFragment(quote)
            findNavController().navigate(action)
        }

        homeAdapter.onLikeClickListener = { quote: Quote ->
            quoteViewModel.toggleLike(quote)
        }

        homeAdapter.onUserClickListener = { userId: String ->

            if (userId != authViewModel.currentUserId) {
                val action = HomeFragmentDirections.actionHomeFragmentToUserProfileFragment(userId)
                findNavController().navigate(action)
            } else {
                (requireActivity() as? MainActivity)?.setSelectedItemOnBnv(R.id.myProfileFragment)
            }
        }


        binding.homeViewPager.apply {

            adapter = homeAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == quotesSize - 4) {
                        currentPage++
                        quoteViewModel.getMoreQuotes(currentPage)
                    }
                    super.onPageSelected(position)
                }
            })
        }

    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun itemReselected(
        screen: Screen?
    ) {
        if (screen == Screen.Home) {
            binding.homeViewPager.setCurrentItem(0, true)
        }
    }

    private fun onRefreshQuotes() {

        if (!binding.refreshLayout.isRefreshing) {
            binding.refreshLayout.isRefreshing = true
        }

        Log.e("TAG", "onRefreshQuotes: ")

        currentPage = 1
        quoteViewModel.getQuotes(forced = true)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe
    public fun onEventReceive(
        eventState: EventState
    ) {
        Log.e("TAG", "onEventReceive: ")
        when (eventState.eventName) {
            EVENT_REFRESH_QUOTES -> requireActivity().runOnUiThread {
                Handler(Looper.getMainLooper()).postDelayed({
                    onRefreshQuotes()
                }, 500)

            }
        }
    }
}