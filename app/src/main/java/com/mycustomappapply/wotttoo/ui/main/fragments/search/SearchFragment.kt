package com.mycustomappapply.wotttoo.ui.main.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.SearchPagerAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentSearchBinding
import com.mycustomappapply.wotttoo.ui.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private lateinit var pagerAdapter: SearchPagerAdapter
    private val sharedViewModel: SharedViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()
    }

    private fun setupTabLayout() {
        pagerAdapter = SearchPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sharedViewModel.currentTab =
                    if (position == 0) SearchTab.Quotes else SearchTab.Users
            }
        })
        TabLayoutMediator(
            binding.searchTabLayout,
            binding.viewPager
        ) { tab: TabLayout.Tab, i: Int ->
            tab.text = when (i) {
                0 -> getString(R.string.txt_quotes)
                1 -> getString(R.string.users)
                else -> ""
            }
        }.attach()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)

}

enum class SearchTab { Quotes, Users }