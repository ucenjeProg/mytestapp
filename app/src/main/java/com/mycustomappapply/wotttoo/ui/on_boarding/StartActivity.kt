package com.mycustomappapply.wotttoo.ui.on_boarding

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.ActivityStartBinding
import com.mycustomappapply.wotttoo.ui.main.MainActivity
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.utils.Constants.KEY_THEME
import com.mycustomappapply.wotttoo.utils.safeGone
import com.mycustomappapply.wotttoo.utils.safeVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    private lateinit var navController: NavController
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    @Named("themeSharedPreferences")
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        setTheme(R.style.Theme_MyCustomApp)
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (authViewModel.isAuthenticated || authViewModel.currentUser?.followingGenres!!.size > 1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        setAppTheme()
        setupNavigation()
    }

    fun hideToolbar() {
        binding.toolbarStartActivity.safeGone()
    }

    fun showToolbar() {
        binding.toolbarStartActivity.safeVisible()
    }

    private fun setupNavigation() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView_startActivity) as NavHostFragment).navController

        setSupportActionBar(binding.toolbarStartActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val appBarConfiguration: AppBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }


    private fun setAppTheme() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
        when (sharedPreferences.getString(KEY_THEME, resources.getString(R.string.theme_default))) {
            resources.getString(R.string.theme_default) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            resources.getString(R.string.theme_dark) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            resources.getString(R.string.theme_light) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}