package com.mycustomappapply.wotttoo.ui.main.fragments.download

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.ColorBoxAdapter
import com.mycustomappapply.wotttoo.base.BaseFragment
import com.mycustomappapply.wotttoo.databinding.FragmentDownloadQuoteBinding
import com.mycustomappapply.wotttoo.databinding.RationaleDialogLayoutBinding
import com.mycustomappapply.wotttoo.ui.custom_views.SingleSelectBottomView
import com.mycustomappapply.wotttoo.utils.safeGone
import com.mycustomappapply.wotttoo.utils.showToast
import com.mycustomappapply.wotttoo.utils.visible
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class DownloadQuoteFragment :
    BaseFragment<FragmentDownloadQuoteBinding>() {
    private var quoteBackgroundUrl: String? = null
    private var photoBitmap: Bitmap? = null
    private var imageUri: Uri? = null
    private var imageFileName: String? = ""
    private val args: DownloadQuoteFragmentArgs by navArgs<DownloadQuoteFragmentArgs>()
    private val photoStyleColors: List<String> = listOf("#1B1B1B", "#F2F2F2", "#DCEBFE", "#C7FFCE", "#FFD1EA", "#FFF9AB")
    private val colorAdapter: ColorBoxAdapter by lazy { ColorBoxAdapter(photoStyleColors) }

    companion object {
        const val REQUEST_PICK_IMAGE = 123
    }

    @Inject
    lateinit var imagePicker: UnsplashPhotoPicker

    private val rationaleDialog: AlertDialog by lazy {
        val dBinding: RationaleDialogLayoutBinding = RationaleDialogLayoutBinding.inflate(layoutInflater, null, false)
        val view: ConstraintLayout = dBinding.root
        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        dBinding.allowButton.setOnClickListener { requestStoragePermission() }
        dBinding.denyButton.setOnClickListener { dialog.dismiss() }

        dialog
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        //this.quoteBackgroundUrl = args.quote?.backgroundUrl
        setHasOptionsMenu(true)
        setupClickListeners()
        setupUi()
        setupRecyclerView()
    }

    private fun setupRecyclerView(): RecyclerView = with(binding) {
        colorAdapter.onColorBoxClickedListener = { view, color ->
            ivQuoteBg.setImageDrawable(null)
            quoteOverlay.safeGone()
            view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.scale_anim))
            photoContainer.setBackgroundColor(Color.parseColor(color))
            if (photoStyleColors.indexOf(color) == 0) {
                quoteText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryOnDark
                    )
                )
            } else {
                quoteText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
        }
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

    }

    private fun requestStoragePermission(): Unit =
        requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

    private fun setupUi(): Unit = with(binding) {
        /*val quote = args.quote ?: return@with
        if (quote.quote == null) return@with
        textSizeSlider.value = 18f
        when (quote.quote?.length) {
            in 181..239 -> {
                textSizeSlider.valueTo = 18.toFloat()
                textSizeSlider.value = 16f
            }
            in 240..300 -> {
                textSizeSlider.valueTo = 16.toFloat()
                textSizeSlider.value = 14f
            }
            in 300..Constants.MAX_QUOTE_LENGTH -> {
                textSizeSlider.valueTo = 14.toFloat()
                textSizeSlider.value = 12f
            }
        }
        if ((quote.backgroundUrl != null && quote.backgroundUrl != "") || quoteBackgroundUrl != null) {
            loadQuoteBackground()
            ivQuoteSelectedBg.setOnClickListener { view ->
                view.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.scale_anim
                    )
                )
                loadQuoteBackground()
            }
        } else {
            ivQuoteSelectedBg.gone()
            quoteOverlay.safeGone()
        }
        quoteText.text = quote.quote*/
    }

    private fun setupClickListeners(): Unit = with(binding) {
        singleSelectBottomView.onTextClick = {
            onSelectBottomNavItemClicked(SingleSelectBottomView.Item.Text)
        }
        singleSelectBottomView.onImageClick = {
            onSelectBottomNavItemClicked(SingleSelectBottomView.Item.Image)
        }
        singleSelectBottomView.onColorClick = {
            onSelectBottomNavItemClicked(SingleSelectBottomView.Item.Color)
        }
        ibCloseStyle.setOnClickListener {
            singleSelectBottomView.setState(SingleSelectBottomView.Item.None)
            resetStyleOptionsVisibility()
        }

        singleSelectBottomView.onSameItemClick = {
            resetStyleOptionsVisibility()
        }
        textSizeSlider.addOnChangeListener { slider, value, fromUser ->
            quoteText.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }

        changeBg.setOnClickListener {
            startActivityForResult(
                UnsplashPickerActivity.getStartingIntent(
                    requireContext(),
                    false
                ),
                REQUEST_PICK_IMAGE
            )
        }
    }

    private fun onSelectBottomNavItemClicked(
        item: SingleSelectBottomView.Item
    ): Unit = with(binding) {

        resetStyleOptionsVisibility()
        when (item) {

            SingleSelectBottomView.Item.Color -> {
                listOf(styleContainer, rvColors).visible()
                tvHeader.text = getString(R.string.change_background_color)
            }

            SingleSelectBottomView.Item.Image -> {
                listOf(styleContainer, llImageChooser).visible()
                tvHeader.text = getString(R.string.change_background_image)
            }

            SingleSelectBottomView.Item.Text -> {
                listOf(styleContainer, textSizeSlider).visible()
                tvHeader.text = getString(R.string.change_txt_size)
            }

            SingleSelectBottomView.Item.None -> {}
        }
    }

    private fun resetStyleOptionsVisibility(): Unit = with(binding) {
        llImageChooser.safeGone()
        rvColors.safeGone()
        textSizeSlider.safeGone()
        styleContainer.safeGone()
    }

    private fun takeScreenshot() {
        try {
            val view: ConstraintLayout = binding.photoContainer
            photoBitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(photoBitmap!!)
            view.draw(canvas)

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission()
            } else {
                saveMediaToStorage(photoBitmap!!)
            }
        } catch (e: Exception) {
            showToast(getString(R.string.smthng_went_wrong) + e.message)
        }


    }

    private fun saveMediaToStorage(
        bitmap: Bitmap
    ) {

        imageFileName = "quote-image-${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireContext().contentResolver?.also { resolver ->
                val contentValues: ContentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir: File =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, imageFileName)
            fos = FileOutputStream(image)
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName + ".provider",
                    image
                )
            } else {
                image.toUri()
            }

        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            showToast(getString(R.string.image_downloaded_successfully))
            showImageDownloadedNotification()
        }
    }

    private fun showImageDownloadedNotification() {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(imageUri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        val pendingIntent = PendingIntent.getActivity(
            requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = if (Build.VERSION.SDK_INT > 26) {
            NotificationCompat.Builder(requireContext(), "download_image")
                .setContentTitle(getString(R.string.image_downloaded))
                .setContentText(imageFileName)
                .setSmallIcon(R.drawable.ic_download_complete)
                .setContentIntent(pendingIntent)
                .build()
        } else {
            NotificationCompat.Builder(requireContext())
                .setContentTitle(getString(R.string.image_downloaded))
                .setContentText(imageFileName)
                .setSmallIcon(R.drawable.ic_download_complete)
                .setContentIntent(pendingIntent)
                .build()
        }

        val notificationManager: NotificationManager =
            requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "download_image",
                "download_image_channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //  notificationManager.notify(5, notification)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            rationaleDialog.dismiss()
            saveMediaToStorage(photoBitmap!!)
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                rationaleDialog.show()
            } else {
                showToast(getString(R.string.image_access_denied))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        inflater.inflate(R.menu.download_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {

        if (item.itemId == R.id.download_done_menu_item) {
            takeScreenshot()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            val images: ArrayList<UnsplashPhoto>? =
                data?.getParcelableArrayListExtra<UnsplashPhoto>(UnsplashPickerActivity.EXTRA_PHOTOS)
            if (images != null && images.size > 0) {
                quoteBackgroundUrl = images[0].urls.small
                initQuoteBackground()
            }
        }
    }

    override fun onDestroyView() {
        binding.singleSelectBottomView.reset()
        super.onDestroyView()
    }

    private fun loadQuoteBackground(): Unit = with(binding) {
        ivQuoteSelectedBg.visible()
        ivQuoteBg.load(quoteBackgroundUrl) {
            allowHardware(false)
            bitmapConfig(Bitmap.Config.ARGB_8888)
        }
        ivQuoteSelectedBg.load(quoteBackgroundUrl)
        quoteText.setTextColor(getColor(R.color.colorQuoteText))
        quoteOverlay.visible()
    }

    private fun initQuoteBackground(): Unit = with(binding) {
        loadQuoteBackground()
        ivQuoteSelectedBg.setOnClickListener { view ->
            view.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.scale_anim
                )
            )
            loadQuoteBackground()
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDownloadQuoteBinding = FragmentDownloadQuoteBinding.inflate(inflater, container, false)


}