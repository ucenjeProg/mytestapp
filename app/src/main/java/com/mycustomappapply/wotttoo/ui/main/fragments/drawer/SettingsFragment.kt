package com.mycustomappapply.wotttoo.ui.main.fragments.drawer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentSettingsBinding
import com.mycustomappapply.wotttoo.utils.Constants
import com.mycustomappapply.wotttoo.utils.Constants.KEY_THEME
import com.mycustomappapply.wotttoo.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    @Inject
    @Named("themeSharedPreferences")
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupClickListeners()

    }

    private fun setupClickListeners() {
        binding.genreSpinnerContainer.setOnClickListener {
            binding.themeSpinner.performClick()
        }
        binding.sendButton.setOnClickListener {
            val contactText: String = binding.contactEditText.text.toString()
            if (contactText.isNotEmpty()) {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "message/rfc822"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.CONTACT_EMAIL))
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_with_dev))
                i.putExtra(Intent.EXTRA_TEXT, contactText)
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.select_one)))
                } catch (e: Exception) {
                    showToast(getString(R.string.smthng_went_wrong))
                }
            } else {
                showToast(getString(R.string.contact_not_empty))
            }
        }
        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sharedPrefEditor = sharedPreferences.edit()
                when (resources.getStringArray(R.array.themes)[position]) {
                    resources.getString(R.string.theme_dark) -> {
                        sharedPrefEditor.putString(
                            KEY_THEME,
                            resources.getString(R.string.theme_dark)
                        )
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }

                    resources.getString(R.string.theme_light) -> {
                        sharedPrefEditor.putString(
                            KEY_THEME,
                            resources.getString(R.string.theme_light)
                        )
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }

                    resources.getString(R.string.theme_default) -> {
                        sharedPrefEditor.putString(
                            KEY_THEME,
                            resources.getString(R.string.theme_default)
                        )
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
                sharedPrefEditor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setupUi() {
        when (sharedPreferences.getString(KEY_THEME, resources.getString(R.string.theme_default))) {
            resources.getString(R.string.theme_default) -> {
                binding.themeSpinner.setSelection(0)
            }

            resources.getString(R.string.theme_dark) -> {
                binding.themeSpinner.setSelection(1)
            }

            resources.getString(R.string.theme_light) -> {
                binding.themeSpinner.setSelection(2)
            }
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)
}