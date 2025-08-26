package com.rocketpay.mandate.feature.image.presentation.ui.selection.view

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentImageSelectionRpBinding
import com.rocketpay.mandate.feature.image.domain.entities.SaveImageLocal
import com.rocketpay.mandate.feature.image.presentation.injection.ImageComponent
import com.rocketpay.mandate.feature.image.presentation.injection.ImageStateMachineFactory
import com.rocketpay.mandate.feature.image.presentation.ui.selection.adapter.ImageSelectionAdapter
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionEvent
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionSimpleStateMachine
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionState
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionUSF
import com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel.DocumentSelectionMedia
import com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel.ImageSelectionListener
import com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel.ImageSelectionUM
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.presentation.utils.PermissionsUtils
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject

internal class ImageSelectionFragment  : StateMachineBottomSheetFragment<ImageSelectionEvent, ImageSelectionState, ImageSelectionUSF>() {

    @Inject
    internal lateinit var imageSelectionAdapter: ImageSelectionAdapter
    private lateinit var binding: FragmentImageSelectionRpBinding
    private lateinit var vm: ImageSelectionUM
    @Inject
    lateinit var imageStateMachineFactory: ImageStateMachineFactory
    private var cameraUri: Uri? = null
    private var imageSelectionListener: ImageSelectionListener? = null
    private var permissionType: PermissionType? = null
    private var allowedExtension: ArrayList<String> = arrayListOf()

    companion object {
        const val SELECT_PHOTO = 1
        const val SELECT_DOCUMENT = 2
        const val TAKE_PHOTO = 10
        const val REQUEST_CAMERA_PERMISSION = 100

        const val BUNDLE_TITLE = "BUNDLE_TITLE"
        const val BUNDLE_SUBTITLE = "BUNDLE_SUBTITLE"
        const val BUNDLE_ALLOWED_EXTENSIONS = "BUNDLE_ALLOWED_EXTENSIONS"
        const val BUNDLE_SIZE_LIMIT = "BUNDLE_SIZE_LIMIT"

        fun newInstance(bundle: Bundle?): ImageSelectionFragment {
            val imageSelectionFragment = ImageSelectionFragment()
            imageSelectionFragment.arguments = bundle
            return imageSelectionFragment
        }
    }

    override fun injection() {
        super.injection()
        ImageComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, imageStateMachineFactory)[ImageSelectionSimpleStateMachine::class.java]
        vm = ImageSelectionUM { stateMachine.dispatchEvent(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( parentFragment is ImageSelectionListener) {
            imageSelectionListener = parentFragment as ImageSelectionListener
        }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val title = savedInstanceState?.getString(BUNDLE_TITLE, "")
        val subTitle = savedInstanceState?.getString(BUNDLE_SUBTITLE, "")
        val maxSizeLimit = savedInstanceState?.getInt(BUNDLE_SIZE_LIMIT, 0)
        allowedExtension = savedInstanceState?.getStringArrayList(BUNDLE_ALLOWED_EXTENSIONS) ?: arrayListOf()
        stateMachine.dispatchEvent(ImageSelectionEvent.Init(title, subTitle, maxSizeLimit, allowedExtension))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageSelectionRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
        imageSelectionAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = imageSelectionAdapter

        val documentSelectionMedia = DocumentSelectionMedia.getDocumentSelectionMedia(
            isCameraOnly = false, isDocumentAllowed = allowedExtension.contains(FileUtils.PDF)
        )
        imageSelectionAdapter.swapData(documentSelectionMedia)
    }

    override fun registerListener() {
        super.registerListener()
    }

    override fun handleState(state: ImageSelectionState) {
        vm.handleState(state)
        stateMachine.dispatchEvent(ImageSelectionEvent.LoadData)
    }

    override fun handleUiSideEffect(sideEffect: ImageSelectionUSF) {
        when (sideEffect) {
            is ImageSelectionUSF.GalleryClick -> {
                handleGalleryClick()
            }
            is ImageSelectionUSF.CameraClick -> {
                handleCameraClick()
            }

            is ImageSelectionUSF.DocumentClick -> {
                handleDocumentClick()
            }
            is ImageSelectionUSF.OpenSetting -> {
                handleOpenSettings()
            }
        }
    }

    private fun handleCameraClick() {
        permissionType = PermissionType.Camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraUri = getImageUri()
        grantUriPermissionForAllApps(cameraIntent, cameraUri)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasCameraPermission =
                context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (hasCameraPermission) {
                if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(cameraIntent, TAKE_PHOTO)
                }
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission_group.STORAGE
                    ), REQUEST_CAMERA_PERMISSION
                )
            }
        } else {
            if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(cameraIntent, TAKE_PHOTO)
            }
        }
    }

    private fun handleDocumentClick() {
        try {
            permissionType = PermissionType.Storage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.apply {
                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    )
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
                }
                startActivityForResult(intent, SELECT_DOCUMENT)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                permissionsResultCallback.launch(arrayOf(READ_EXTERNAL_STORAGE))
            } else {
                permissionsResultCallback.launch(
                    arrayOf(
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }catch (ex: Exception){
            ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(
                R.string.rp_no_apps_found_to_handle_this_action))
        }
    }

    private fun handleGalleryClickAfterPermission() {
        try {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            galleryIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            galleryIntent.type = "*/*"
            startActivityForResult(galleryIntent, SELECT_DOCUMENT)
        } catch (ex: Exception) {
            ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_no_apps_found_to_handle_this_action))
        }
    }

    private val permissionsResultCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionGranted ->
            var isPermissionGranted = true
            permissionGranted.forEach { it ->
                isPermissionGranted = it.value
            }
            if (isPermissionGranted) {
                handleGalleryClickAfterPermission()
            } else {
                handleOpenSettings()
            }
        }

    private fun handleGalleryClick() {
        permissionType = PermissionType.Storage
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            var scaledUri: Uri? = null
            if (uri != null) {
                scaledUri = uri
            }
            if (scaledUri != null) {
                val fileSize = getFileSizeInMb(scaledUri.toString())
                SaveImageLocal().saveImage(scaledUri) {
                    dismiss()
                    imageSelectionListener?.onImageChange(it?.toString(), fileSize)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var scaledUri: Uri? = null
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO) {
            scaledUri = cameraUri
        } else if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            scaledUri = data.data
        } else if (requestCode == SELECT_DOCUMENT && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val fileType = FileUtils.resolveFile(requireContext(), data.data)
            if(fileType == FileUtils.PDF){
                dismiss()
                val fileSize = getFileSizeInMb(data.data.toString())
                imageSelectionListener?.onImageChange(data.data?.toString(), fileSize)
                return
            }else{
                scaledUri = data.data
            }
        }
        if (scaledUri != null) {
            val fileSize = getFileSizeInMb(scaledUri.toString())
            SaveImageLocal().saveImage(scaledUri) {
                dismiss()
                imageSelectionListener?.onImageChange(it?.toString(), fileSize)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if ( requestCode == REQUEST_CAMERA_PERMISSION ) {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                val hasCameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                if ( !hasCameraPermission ) {
                    showPermissionDeniedMessage()
                    return
                }
            }
        }
    }

    private fun showPermissionDeniedMessage(){
        lifecycleScope.launch {
            vm.permissionVisibility.set(true)
            delay(5000)
            vm.permissionVisibility.set(false)
        }
    }

    private fun handleOpenSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startForResult.launch(intent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            permissionType?.let {
                val hasPermission = PermissionsUtils.checkPermission(it)
                if ( ! hasPermission ) {
                    showPermissionDeniedMessage()
                }
            }
        }


    override fun onDetach() {
        super.onDetach()
        imageSelectionListener = null
    }

    private fun grantUriPermissionForAllApps(intent: Intent, uri: Uri?) {
        try {
            val resInfoList = requireContext().packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                requireContext().grantUriPermission(packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }catch (ex: Exception){
        }
    }

    private fun getImageUri(): Uri {
        val folder = FileUtils.getCachedPicsFolder()
        folder.mkdirs()
        val file = File(folder, Date().time.toString() + ".jpg")
        return FileUtils.getUriForFile(file)
    }

    private fun getFileSizeInMb(filePath: String?): Double {
        try {
            val inputStream = MandateManager.getInstance().getContext().contentResolver.openInputStream(Uri.parse(filePath))
            val fileLength = inputStream?.available()?.toDouble() ?: 0.0
            val fileSizeInKb = (fileLength / 1024).double().toInt()
            return fileSizeInKb / 1024.0
        }catch (e: Exception){
            return 0.0
        }
    }
}
