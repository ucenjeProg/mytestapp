package com.mycustomappapply.wotttoo.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycustomappapply.wotttoo.ui.main.fragments.search.SearchTab

class SharedViewModel : ViewModel() {

    var currentTab: SearchTab = SearchTab.Quotes
    var toolbarText = ""

    private val _searchTextUser = MutableLiveData<String>()
    val searchTextUser: MutableLiveData<String>
        get() = _searchTextUser

    private val _searchTextGenre = MutableLiveData<String>()
    val searchTextGenre: MutableLiveData<String>
        get() = _searchTextGenre

    fun setChangedText(
        text: String
    ) {
        when (currentTab) {
            SearchTab.Quotes -> _searchTextGenre.value = text
            SearchTab.Users -> _searchTextUser.value = text
        }

    }

}

