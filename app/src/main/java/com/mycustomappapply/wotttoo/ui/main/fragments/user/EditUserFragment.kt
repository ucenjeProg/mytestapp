package com.mycustomappapply.wotttoo.ui.main.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.data.local.SharedPreferencesManager
import com.mycustomappapply.wotttoo.databinding.FragmentEditProfileBinding
import com.mycustomappapply.wotttoo.models.User
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.UserViewModel
import com.mycustomappapply.wotttoo.utils.Constants.MIN_USERNAME_LENGTH
import com.mycustomappapply.wotttoo.utils.isValidUsername
import com.mycustomappapply.wotttoo.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUserFragment : BaseFragment<FragmentEditProfileBinding>() {

    private val args: EditUserFragmentArgs by navArgs<EditUserFragmentArgs>()
    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var menuItem: MenuItem? = null

    private var updatedUser: User? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        sharedPreferencesManager = SharedPreferencesManager(view.context)
        setupUi()
    }

    private fun setupUi() = with(binding) {
        maxCharactersTextView.text = "${args.user?.bio?.length}/150"

        bioEditText.addTextChangedListener {
            maxCharactersTextView.text = "${it.toString().length}/150"
        }
    }


    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        inflater.inflate(R.menu.edit_profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {
        if (item.itemId == R.id.edit_profile_save_menu_item) {
            menuItem = item
            // item.setActionView(R.layout.progress_bar_layout)
            updateUser()
            navigateBack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUser(): Unit = with(binding) {

        val username: String = usernameEditText.text.toString().trim()
        val fullName: String = fullnameEditText.text.toString().trim()
        val bio: String = bioEditText.text.toString().trim()

        sharedPreferencesManager.saveData("username", username)
        sharedPreferencesManager.saveData("fullName", fullName)
        sharedPreferencesManager.saveData("bio", bio)

    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditProfileBinding = FragmentEditProfileBinding.inflate(inflater, container, false)
}