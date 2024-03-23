package com.mycustomappapply.wotttoo.ui.main.fragments.search

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.HomeAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentSearchQuotesBinding
import com.mycustomappapply.wotttoo.ui.main.MainActivity
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.QuoteViewModel
import com.mycustomappapply.wotttoo.utils.BottomNavReselectListener
import com.mycustomappapply.wotttoo.utils.Constants.MIN_GENRE_COUNT
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.Screen
import com.mycustomappapply.wotttoo.utils.safeGone
import com.mycustomappapply.wotttoo.utils.safeInvisible
import com.mycustomappapply.wotttoo.utils.showToast
import com.mycustomappapply.wotttoo.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SearchQuotesFragment :
    BaseFragment<FragmentSearchQuotesBinding>(),
    BottomNavReselectListener {
    private val homeAdapter by lazy { HomeAdapter() }
    private val quoteViewModel: QuoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val args by navArgs<SearchQuotesFragmentArgs>()

    private var currentPage = 1
    private var paginatingFinished = false
    private var paginationLoading = false
    private var quotesSize = 0
    private var followingGenres: String = ""

    private var genres: MutableList<String> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initBottomNavReselectListener()
        setupRecyclerView()
        subscribeObservers()
        setupClickListeners()
        quoteViewModel.getQuotesByGenre(args.genre)
    }


    private fun initBottomNavReselectListener() {
        (requireActivity() as MainActivity).bottomNavItemReselectListener = this
    }

    private fun hideRefreshLayoutProgress() {
        if (binding.refreshLayout.isRefreshing) binding.refreshLayout.isRefreshing = false
    }

    private fun setupClickListeners() {
        binding.refreshLayout.setOnRefreshListener {
            resetPagination()
            quoteViewModel.getQuotesByGenre(args.genre, forced = true)
        }
        binding.tryAgainButton.setOnClickListener {
            quoteViewModel.getQuotesByGenre(args.genre, forced = true)
        }
    }

    private fun resetPagination() {
        paginationLoading = false
        paginatingFinished = false
        quotesSize = 0
        currentPage = 1
    }


    private fun subscribeObservers(): Unit = with(binding) {
        quoteViewModel.quotes.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    /*   hideRefreshLayoutProgress()
                       failContainer.safeGone()
                       shimmerLayout.safeInvisible()
                       paginatingFinished = quotesSize == it.data!!.quotes.size
                       quotesSize = it.data.quotes.size
                       homeAdapter.setData(it.data.quotes)
                       paginationLoading = false*/
                }

                is DataState.Fail -> {
                    hideRefreshLayoutProgress()
                    failMessage.text = it.message
                    failContainer.visible()
                    shimmerLayout.safeInvisible()
                    shimmerLayout.stopShimmer()
                    paginationLoading = false
                }

                is DataState.Loading -> {
                    if (!refreshLayout.isRefreshing) {
                        failContainer.safeGone()
                        if (currentPage == 1) {
                            shimmerLayout.visible()
                        } else {
                            paginationLoading = true
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        homeAdapter.onDownloadClickListener = { quote ->
            val action = SearchQuotesFragmentDirections.actionGlobalDownloadQuoteFragment(quote)
            findNavController().navigate(action)
        }
        homeAdapter.onShareClickListener = { quote ->
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            //intent.putExtra(Intent.EXTRA_TEXT, quote.quote + "\n\n#wotttoo App")
            val shareIntent: Intent = Intent.createChooser(intent, "Share Quote")
            startActivity(shareIntent)
        }
        homeAdapter.currentUserId = authViewModel.currentUserId
        homeAdapter.onQuoteOptionsClickListener = { quote ->
            val action = SearchQuotesFragmentDirections.actionGlobalQuoteOptionsFragment(quote)
            findNavController().navigate(action)
        }
        homeAdapter.onLikeClickListener = { quote ->
            quoteViewModel.toggleLike(quote)
        }

        homeAdapter.onUserClickListener = { userId ->
            if (userId != authViewModel.currentUserId) {
                val action: SearchQuotesFragmentDirections.ActionSearchQuotesFragmentToUserProfileFragment =
                    SearchQuotesFragmentDirections.actionSearchQuotesFragmentToUserProfileFragment(
                        userId
                    )
                findNavController().navigate(action)
            } else {
                findNavController().navigate(R.id.action_global_myProfileFragment)
            }
        }
        homeAdapter.currentUserId = authViewModel.currentUserId

        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.searchQuotesRecyclerView.apply {
            adapter = homeAdapter
            layoutManager = mLayoutManager
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        if (!paginatingFinished && mLayoutManager.findLastCompletelyVisibleItemPosition() == (quotesSize - 5) && !paginationLoading) {
                            currentPage++
                            paginationLoading = true
                            quoteViewModel.getQuotesByGenre(args.genre, currentPage)
                        }
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        inflater.inflate(R.menu.discover_quotes_menu, menu)
        followingGenres = authViewModel.getFollowingGenres()
        genres = followingGenres.split(",").toMutableList()
        if (followingGenres.contains(args.genre.toLowerCase(Locale.ROOT))) {
            menu.findItem(R.id.follow_genre_menu_item).title = getString(R.string.txt_following)
        } else {
            menu.findItem(R.id.follow_genre_menu_item).title = getString(R.string.txt_follow)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var genresChanged = true
        if (item.itemId == R.id.follow_genre_menu_item) {
            if (!genres.contains(args.genre)) {
                genres.add(args.genre)
                item.title = getString(R.string.txt_following)
            } else {
                if (genres.size > MIN_GENRE_COUNT) {
                    genres.remove(args.genre)
                    item.title = getString(R.string.txt_follow)
                } else {
                    genresChanged = false
                    showToast(getString(R.string.txt_genre_warning, MIN_GENRE_COUNT))
                }
            }
            if (genresChanged) {
                val genresText: String = genres.toCustomizedString()
                authViewModel.saveFollowingGenres(genresText, authViewModel.currentUserId)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun MutableList<String>.toCustomizedString(): String {
        var text = ""
        this.forEachIndexed { index, genre ->
            text += if (index != this.size - 1) {
                "$genre,"
            } else {
                genre
            }
        }
        return text
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchQuotesBinding = FragmentSearchQuotesBinding.inflate(inflater, container, false)

    override fun itemReselected(screen: Screen?) {
        if (screen == Screen.Search) navigateBack()
    }
}