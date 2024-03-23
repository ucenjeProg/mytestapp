package com.mycustomappapply.wotttoo.ui.main.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.MakeSureDialogLayoutBinding
import com.mycustomappapply.wotttoo.databinding.QuoteOptionsLayoutBinding
import com.mycustomappapply.wotttoo.databinding.ReportDialogBinding
import com.mycustomappapply.wotttoo.models.Quote
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.QuoteViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.ReportViewModel
import com.mycustomappapply.wotttoo.utils.Constants.KEY_DELETED_QUOTE
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuoteOptionsFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: QuoteOptionsLayoutBinding
    private val args by navArgs<QuoteOptionsFragmentArgs>()
    private var makeSureDialog: AlertDialog? = null
    private val authViewModel: AuthViewModel by viewModels()
    private val quoteViewModel: QuoteViewModel by viewModels()
    private val reportViewModel: ReportViewModel by viewModels()

    private var deletedQuote: Quote? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QuoteOptionsLayoutBinding.bind(
            layoutInflater.inflate(
                R.layout.quote_options_layout,
                null,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeObservers()
    }

    private fun setupUi() {
        if (authViewModel.currentUserId != null) {
            if (/*args.quote.creator?.id*/ "1" == authViewModel.currentUserId /*|| authViewModel.currentUserId == Constants.ADMIN_ID*/) {
                binding.reportQuote.visibility = View.GONE
                binding.reportUser.visibility = View.GONE
                binding.editQuote.setOnClickListener(this)
                binding.deleteQuote.setOnClickListener(this)
            } else {
                binding.deleteQuote.visibility = View.GONE
                binding.editQuote.visibility = View.GONE
                binding.reportQuote.setOnClickListener(this)
                binding.reportUser.setOnClickListener(this)
            }
        } else {
            binding.deleteQuote.visibility = View.GONE
            binding.editQuote.visibility = View.GONE
            binding.reportQuote.setOnClickListener(this)
            binding.reportUser.setOnClickListener(this)
        }
    }

    private fun subscribeObservers() {
        quoteViewModel.deleteQuote.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    parentFragmentManager.setFragmentResult(
                        KEY_DELETED_QUOTE,
                        bundleOf(KEY_DELETED_QUOTE to deletedQuote)
                    )
                    findNavController().popBackStack()
                    makeSureDialog?.dismiss()
                    showToast(getString(R.string.quote_deleted_successfully))
                }

                is DataState.Fail -> {
                    findNavController().popBackStack()
                    makeSureDialog?.dismiss()
                    showToast(it.message)
                }
            }
        }
        reportViewModel.report.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    showToast(getString(R.string.reported_successfully))
                    makeSureDialog?.dismiss()
                    findNavController().popBackStack()
                }

                is DataState.Fail -> {
                    makeSureDialog?.dismiss()
                    findNavController().popBackStack()
                    showToast(it.message)
                }
            }
        }
    }


    private fun showMakeSureDialog(quote: Quote) {
        val view: View = layoutInflater.inflate(R.layout.make_sure_dialog_layout, null, false)
        val binding: MakeSureDialogLayoutBinding = MakeSureDialogLayoutBinding.bind(view)
        makeSureDialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()
        makeSureDialog!!.show()

        with(binding) {
            notDeleteButton.setOnClickListener { makeSureDialog!!.dismiss() }
            deleteButton.setOnClickListener {
                deletedQuote = quote
                makeSureDialog?.setCancelable(false)
                binding.deleteProgressBar.visibility = View.VISIBLE
                binding.deleteButton.visibility = View.INVISIBLE
                binding.notDeleteButton.isEnabled = false
                quoteViewModel.deleteQuote(quote)
            }
        }
    }

    private fun showMakeSureDialogForReport(quote: Quote, reportQuote: Boolean) {
        val view: View = layoutInflater.inflate(R.layout.report_dialog, null, false)
        val binding: ReportDialogBinding = ReportDialogBinding.bind(view)
        makeSureDialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()
        makeSureDialog?.show()

        binding.apply {
            notNowButton.setOnClickListener { makeSureDialog!!.dismiss() }
            reportButton.setOnClickListener {
                makeSureDialog!!.setCancelable(false)
                binding.reportProgressBar.visibility = View.VISIBLE
                binding.reportButton.visibility = View.INVISIBLE
                binding.notNowButton.isEnabled = false
                if (reportQuote) {
                    reportViewModel.reportQuote(authViewModel.currentUserId ?: "", quote.id)
                } else {
                    /*  reportViewModel.reportUser(
                          authViewModel.currentUserId ?: "",
                          quote.creator!!.id
                      )*/
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        quoteViewModel.clearLiveDataValues()
    }

    override fun onClick(
        v: View?
    ) {
        when (v?.id) {
            R.id.editQuote -> {
                val action = QuoteOptionsFragmentDirections.actionGlobalAddQuoteFragment()
                action.quote = args.quote
                findNavController().popBackStack(R.id.quoteOptionsFragment, true)
                findNavController().navigate(action)
            }

            R.id.reportUser -> {
                showMakeSureDialogForReport(args.quote, false)
            }

            R.id.reportQuote -> {
                showMakeSureDialogForReport(args.quote, true)
            }

            R.id.deleteQuote -> {
                showMakeSureDialog(args.quote)
            }
        }
    }


}