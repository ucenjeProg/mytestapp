package com.mycustomappapply.wotttoo.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentQuoteBinding
import com.mycustomappapply.wotttoo.models.Quote
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.QuoteViewModel
import com.mycustomappapply.wotttoo.utils.Constants.KEY_UPDATED_QUOTE
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.invisible
import com.mycustomappapply.wotttoo.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuoteFragment : BaseFragment<FragmentQuoteBinding>(),
    View.OnClickListener {
    private val quoteViewModel: QuoteViewModel by viewModels()
    private val args: QuoteFragmentArgs by navArgs()
    private val authViewModel: AuthViewModel by viewModels()

    private var currentQuote: Quote? = null
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        setupClickListeners()
        setupFragmentResultListeners()

        if (args.quoteId != null) {
            if (currentQuote == null) {
                quoteViewModel.getSingleQuote(args.quoteId!!)
            } else {
                bindData(currentQuote!!)
            }
        }
    }

    private fun setupClickListeners() {
        binding.usernameTextView.setOnClickListener(this)
        binding.userphotoImageView.setOnClickListener(this)
        binding.postOptionsBtn.setOnClickListener(this)
        binding.likeBtn.setOnClickListener(this)
        binding.downloadButton.setOnClickListener(this)
        binding.shareBtn.setOnClickListener(this)
        binding.tryAgainButton.setOnClickListener(this)
    }

    private fun subscribeObservers(): Unit = with(binding) {
        quoteViewModel.quote.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    /*    currentQuote = it.data?.quote
                        quoteContainer.visible()
                        progressBar.invisible()
                        bindData(it.data?.quote!!)*/
                }

                is DataState.Loading -> {
                    failContainer.invisible()
                    quoteContainer.invisible()
                    progressBar.visible()
                }

                is DataState.Fail -> {
                    failContainer.visible()
                    failMessage.text = it.message
                    quoteContainer.invisible()
                    progressBar.invisible()
                }
            }
        }
    }

    private fun setupFragmentResultListeners() {
        parentFragmentManager.setFragmentResultListener(
            KEY_UPDATED_QUOTE,
            viewLifecycleOwner
        ) { s: String, bundle: Bundle ->
            val updatedQuote: Quote = (bundle[KEY_UPDATED_QUOTE] as Quote)
            currentQuote = updatedQuote
            bindData(updatedQuote)
        }
    }

    private fun bindData(quote: Quote) = with(binding) {
        /*      usernameTextView.text = quote.creator?.username
              if (quote.creator?.profileImage != "" && quote.creator?.profileImage != null) {
                  userphotoImageView.load(quote.creator?.profileImage)
              } else {
                  userphotoImageView.load(R.drawable.user)
              }
              quoteTextView.text = quote.quote

              likeCountTextView.text = quote.likes?.size?.toFormattedNumber()
              if (quote.liked) {
                  likeBtn.setImageResource(R.drawable.ic_like_blue)
              } else {
                  likeBtn.setImageResource(R.drawable.ic_like)
              }
              genreText.text = "#${quote.genre}"*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.postOptionsBtn.id -> {
                val action = QuoteFragmentDirections.actionGlobalQuoteOptionsFragment(currentQuote!!)
                findNavController().navigate(action)
            }

            binding.likeBtn.id -> {
                currentQuote?.let {
                    /*   val likes = it.likes!!.toMutableList()
                       if (!it.liked) {
                           binding.likeBtn.setImageResource(R.drawable.ic_like_blue)
                           likes.add(authViewModel.currentUserId!!)
                       } else {
                           binding.likeBtn.setImageResource(R.drawable.ic_like)
                           likes.remove(authViewModel.currentUserId)
                       }
                       it.liked = !it.liked
                       it.likes = likes.toList()
                       binding.likeCountTextView.text = it.likes!!.size.toString()
                       if (it.liked) {
                           binding.likeBtn.startAnimation(
                               AnimationUtils.loadAnimation(
                                   binding.likeBtn.context,
                                   R.anim.bouncing_anim
                               )
                           )
                       }
                       quoteViewModel.toggleLike(it)*/
                }

            }

            binding.downloadButton.id -> {
                val action = QuoteFragmentDirections.actionGlobalDownloadQuoteFragment(currentQuote)
                findNavController().navigate(action)
            }

            binding.tryAgainButton.id -> {
                quoteViewModel.getSingleQuote(args.quoteId!!)
            }

            binding.shareBtn.id -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                //  intent.putExtra(Intent.EXTRA_TEXT, currentQuote!!.quote + "\n\n#wotttoo App")
                val shareIntent: Intent = Intent.createChooser(intent, getString(R.string.share_quote))
                startActivity(shareIntent)
            }
        }

    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentQuoteBinding = FragmentQuoteBinding.inflate(inflater, container, false)


}