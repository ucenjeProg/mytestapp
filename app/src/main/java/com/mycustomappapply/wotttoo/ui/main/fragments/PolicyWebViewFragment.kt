package com.mycustomappapply.wotttoo.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentPolicyWebviewBinding
import com.mycustomappapply.wotttoo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyWebViewFragment :
    BaseFragment<FragmentPolicyWebviewBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.loadUrl(Constants.PRIVACY_POLICY_URL)
    }


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPolicyWebviewBinding =
        FragmentPolicyWebviewBinding.inflate(inflater, container, false)
}