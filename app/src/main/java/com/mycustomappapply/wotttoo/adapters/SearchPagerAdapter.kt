package com.mycustomappapply.wotttoo.adapters

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.ui.main.fragments.search.SearchUsersFragment
import com.mycustomappapply.wotttoo.ui.main.fragments.search.SelectGenresFragment

class SearchPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val fragments: List<BaseFragment<out ViewBinding>> = listOf(SelectGenresFragment(), SearchUsersFragment())
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): BaseFragment<out ViewBinding> = fragments[position]
}