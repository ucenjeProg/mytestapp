package com.mycustomappapply.wotttoo.ui.main.fragments.add

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.SpinnerAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentAddQuoteBinding
import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.Quote
import com.mycustomappapply.wotttoo.ui.viewmodels.AuthViewModel
import com.mycustomappapply.wotttoo.ui.viewmodels.QuoteViewModel
import com.mycustomappapply.wotttoo.utils.Constants.KEY_GENRE
import com.mycustomappapply.wotttoo.utils.Constants.KEY_NEW_QUOTE
import com.mycustomappapply.wotttoo.utils.Constants.KEY_QUOTE
import com.mycustomappapply.wotttoo.utils.Constants.KEY_QUOTE_BG
import com.mycustomappapply.wotttoo.utils.Constants.KEY_UPDATED_QUOTE
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.EVENT_REFRESH_QUOTES
import com.mycustomappapply.wotttoo.utils.EventState
import com.mycustomappapply.wotttoo.utils.focusWithKeyboard
import com.mycustomappapply.wotttoo.utils.showToast
import com.mycustomappapply.wotttoo.utils.unFocus
import com.mycustomappapply.wotttoo.utils.unFocusWithKeyboard
import com.mycustomappapply.wotttoo.utils.visible
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity.Companion.EXTRA_PHOTOS
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.security.MessageDigest
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddQuoteFragment : BaseFragment<FragmentAddQuoteBinding>() {

    private val args: AddQuoteFragmentArgs by navArgs<AddQuoteFragmentArgs>()
    private val quoteViewModel: QuoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var updatedQuote: Quote? = null
    private var quoteBackgroundUrl: String? = null

    @Inject
    lateinit var unsplashPhotoPicker: UnsplashPhotoPicker

    companion object {
        const val REQUEST_PICK_UNSPLASH_PHOTO = 1
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initUi()
        initClickListeners()
        initUi()
    }


    private fun initClickListeners(): Unit = with(binding) {
        btnSelectBg.setOnClickListener { navigateToUnsplashPhotoPicker() }
        spinnerIcon.setOnClickListener { genreSpinner.performClick() }
        addQuoteBtn.setOnClickListener {
            submitQuote()
        }
    }

    private fun navigateToUnsplashPhotoPicker() {
        startActivityForResult(
            UnsplashPickerActivity.getStartingIntent(
                binding.root.context,
                false
            ),
            REQUEST_PICK_UNSPLASH_PHOTO
        )
    }

    private fun submitQuote(
        update: Boolean = false
    ): Any = with(binding) {

        val quote: String = etQuote.text.toString()
        val genre: String =
            binding.genreSpinner.selectedItem.toString().toLowerCase(Locale.ROOT)

        val newQuote: Map<String, String> =
            mapOf(
                KEY_QUOTE to quote,
                KEY_GENRE to genre,
                KEY_QUOTE_BG to (quoteBackgroundUrl ?: "")
            )

        if (update) {
            updatedQuote = args.quote?.copy()
            /*updatedQuote?.quote = quote
            updatedQuote?.genre = genre
            updatedQuote?.backgroundUrl = quoteBackgroundUrl*/
            quoteViewModel.updateQuote(args.quote!!, newQuote)

        } else {

            val requestBodyMap: Map<String, String> = mapOf(
                "alias" to generateUniqueString(quote),
                "articletext" to quote,
                "catid" to "13",
                "language" to "*",
                "metadesc" to "",
                "metakey" to "",
                "title" to quote,
                "state" to "1"
            )


            quoteViewModel.postQuote(requestBodyMap)
        }
    }

    fun generateUniqueString(
        input: String
    ): String {
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes: ByteArray = digest.digest(input.toByteArray())
        val truncatedHash: ByteArray = hashBytes.copyOf(newSize = 20)
        return bytesToHex(truncatedHash)
    }

    fun bytesToHex(
        bytes: ByteArray
    ): String {
        val hexChars: CharArray = "0123456789ABCDEF".toCharArray()
        val hex = StringBuilder(bytes.size * 2)
        for (byte: Byte in bytes) {
            val index: Int = byte.toInt() and 0xFF
            hex.append(hexChars[index shr 4])
            hex.append(hexChars[index and 0x0F])
        }
        return hex.toString()
    }


    private fun initUi(): Unit = with(binding) {

        etQuote.focusWithKeyboard()

        val genres: List<String> = resources.getStringArray(R.array.quote_genres).toList()
        genreSpinner.adapter = SpinnerAdapter(requireContext(), genres)

        if (args.quote != null) {

            val quote: Quote? = args.quote
            addQuoteBtn.text = getString(R.string.txt_update)
            etQuote.setText(quote?.attributes?.text)

            val genresArray: Array<String> = resources.getStringArray(R.array.quote_genres)

            /* val genreIndex = genresArray.indexOf(
                 quote?.genre!!.capitalize(
                     Locale.ROOT
                 )
             )
             if (quote.backgroundUrl != "" && quote.backgroundUrl != null) {
                 ivQuoteBg.load(quote.backgroundUrl)
                 quoteOverlay.visible()
             } else {
                 quoteOverlay.safeGone()
             }*/

            genreSpinner.adapter = SpinnerAdapter(requireContext(), genresArray.toList())
            //genreSpinner.setSelection(genreIndex)

            addQuoteBtn.setOnClickListener {
                submitQuote(update = true)
            }
        }
    }


    private fun subscribeObservers() {

        quoteViewModel.quote.observe(viewLifecycleOwner) { dataState: DataState<ArticleResponse> ->
            when (dataState) {
                is DataState.Success -> {
                    showToast(getString(R.string.action_quote_addad))
                    parentFragmentManager.setFragmentResult(
                        KEY_NEW_QUOTE,
                        bundleOf(KEY_NEW_QUOTE to dataState.data?.data)
                    )
                    binding.etQuote.unFocusWithKeyboard()
                    findNavController().popBackStack()
                    binding.addQuoteBtn.hideLoading()
                    findNavController().navigateUp()
                    EventBus.getDefault().post(EventState(EVENT_REFRESH_QUOTES))
                }

                is DataState.Fail -> {
                    EventBus.getDefault().post(EventState(EVENT_REFRESH_QUOTES))
                    showToast(dataState.message)
                    binding.addQuoteBtn.hideLoading()
                    findNavController().navigateUp()
                }

                is DataState.Loading -> {
                    binding.addQuoteBtn.showLoading()
                }
            }
        }

        quoteViewModel.updateQuote.observe(viewLifecycleOwner) { dataState: DataState<ArticleResponse> ->
            when (dataState) {
                is DataState.Success -> {
                    parentFragmentManager.setFragmentResult(
                        KEY_UPDATED_QUOTE,
                        bundleOf(KEY_UPDATED_QUOTE to updatedQuote)
                    )
                    binding.etQuote.unFocusWithKeyboard()
                    findNavController().popBackStack()
                    binding.addQuoteBtn.hideLoading()
                }

                is DataState.Fail -> {
                    showToast(dataState.message)
                    binding.addQuoteBtn.hideLoading()
                }

                is DataState.Loading -> binding.addQuoteBtn.showLoading()
            }
        }
    }


    override fun onDestroyView() {
        binding.etQuote.unFocus()
        quoteViewModel.clearLiveDataValues()
        super.onDestroyView()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_UNSPLASH_PHOTO && resultCode == RESULT_OK) {

            val photos: ArrayList<UnsplashPhoto>? =
                data?.getParcelableArrayListExtra<UnsplashPhoto>(EXTRA_PHOTOS)

            if (photos != null && photos.size > 0) {
                val selectedImage = photos[0].urls.small
                this.quoteBackgroundUrl = selectedImage
                binding.ivQuoteBg.load(selectedImage)
                binding.quoteOverlay.visible()
            }
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddQuoteBinding = FragmentAddQuoteBinding.inflate(inflater, container, false)

}