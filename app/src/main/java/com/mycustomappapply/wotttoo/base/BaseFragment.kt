package com.mycustomappapply.wotttoo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VBinding : ViewBinding> : Fragment() {

    private var _binding: VBinding? = null
    val binding: VBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    abstract fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VBinding

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun navigateTo(
        screenId: Int
    ) {
        findNavController().navigate(screenId)
    }

    fun navigateBack() {
        findNavController().popBackStack()
    }

    fun getCurrentScreen(): Int? = findNavController().currentDestination?.id

    fun getColor(color: Int): Int = ContextCompat.getColor(requireContext(), color)

    fun showSnackBar(
        message: String
    ) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}