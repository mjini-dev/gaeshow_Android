package com.devup.android_shopping_mall.view.community.board

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.post.model.PostAddRequest
import com.devup.android_shopping_mall.databinding.FragmentBoardWriteBinding
import com.devup.android_shopping_mall.view.community.adapter.BoardWriteTagAddAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardWriteViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_board_write.*
import kotlinx.android.synthetic.main.fragment_board_write.view.*
import kotlinx.android.synthetic.main.fragment_board_write.viewFinder
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class BoardWriteFragment : Fragment() {
    private val TAG = "BoardWriteFragment"

    private val viewModel: BoardWriteViewModel by inject()

    //프래그먼트 화면전환에 필요한 컨트롤러
    lateinit var navController: NavController

    private var categoryId: Int = 0
    lateinit var fmTitle: String

    private var resource_id: Int = 0

    //이미지
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val REQUEST_IMAGE_GALLERY = 200

    var photoFile: File? = null
    lateinit var mCurrentPhotoPath: String
    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt("category_id")
            fmTitle = it.getString("title", "")

            resource_id = it.getInt("resource_id")
            viewModel._isPostModify.value = resource_id != 0
            Log.d(TAG, "onCreate.isPostModify.value : ${viewModel.isPostModify.value}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //뷰를 참조하여 내비게이션을 인스턴스화 한다
        //내비게이션 그래프에 대한 참조를 가질 컨트롤러
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_board_write, container, false)
        val binding: FragmentBoardWriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_write, container, false)
        val view = binding.root
        binding.apply {
            Log.d(TAG, "onCreateView,viewModel.isPostModify.value : ${viewModel.isPostModify.value}")

            vm = viewModel
            lifecycleOwner = this@BoardWriteFragment
            view.rvTag.let {
                it.layoutManager = getFlexBoxLayoutManager()
                it.adapter = BoardWriteTagAddAdapter(categoryId, "etc",
                    object : BoardWriteTagAddAdapter.OnItemListener {
                        override fun footerClick() {
                            showDialog()
                        }
                    },
                    tagClick = { position ->
                        Log.d(TAG, "onCreateView: tagPosition $position")
                        deleteTag(position)
                    }
                )

                if (resource_id != 0) {
                    viewModel.loadPostDetails(resource_id)
                }

            }
            view.tvBoardWrite.text = fmTitle

            view.editorContent.apply {
                setPadding(10, 10, 10, 10)
                setEditorFontSize(18)
                setEditorFontColor(Color.BLACK)
                setPlaceholder("내용을 입력해 주세요")
                //setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> Log.d(TAG, "onCreateView: $text ") })
//                if (resource_id!=0) {
//                    html = viewModel.postDetails.value?.content ?: "WW"
//                    Log.d(TAG, "onCreateView,viewModel.postDetails.value?.content: ${viewModel.postDetails.value?.content}")
//                    Log.d(TAG, "onCreateView,viewModel.postDetails.value?.title: ${viewModel.postDetails.value?.title}")
//                }
            }
        }


        val main = activity as AppCompatActivity
        main.imageBtnBell.isEnabled = false

        /*//에디터 보류기능들
               view.action_undo.setOnClickListener { view.editorContent.undo() }
               view.action_redo.setOnClickListener { view.editorContent.redo() }
               view.action_subscript.setOnClickListener { view.editorContent.setSubscript() }
               view.action_superscript.setOnClickListener { view.editorContent.setSuperscript() }
               view.action_heading1.setOnClickListener { view.editorContent.setHeading(1) }
               view.action_heading2.setOnClickListener { view.editorContent.setHeading(2) }
               view.action_heading3.setOnClickListener { view.editorContent.setHeading(3) }
               view.action_heading4.setOnClickListener { view.editorContent.setHeading(4) }
               view.action_heading5.setOnClickListener { view.editorContent.setHeading(5) }
               view.action_heading6.setOnClickListener { view.editorContent.setHeading(6) }*/
        view.action_bold.setOnClickListener {
            view.editorContent.focusEditor()
            view.editorContent.setBold()
        }
        view.action_italic.setOnClickListener {
            view.editorContent.focusEditor()
            view.editorContent.setItalic()
        }
        view.action_strikethrough.setOnClickListener {
            view.editorContent.focusEditor()
            view.editorContent.setStrikeThrough()
        }
        view.action_underline.setOnClickListener {
            view.editorContent.focusEditor()
            view.editorContent.setUnderline()
        }

        view.action_insert_image.setOnClickListener {
            view.editorContent.focusEditor() //포커스이동
            if (allPermissionsGranted()) {
                showDialogImage()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
            /*view.editorContent.insertImage(
                //더미
                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
                "dachshund", 320
            )*/
        }
        // Set up the listener for take photo button
        view.btnCameraCapture.setOnClickListener { takePhoto() }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()


        //게시글 등록
        view.btnAddPost.setOnClickListener {
            if (view.etTitle.text.isNullOrEmpty()) {
                alert(R.string.title_hint_str)
                return@setOnClickListener
            }

            if (view.editorContent.html.isNullOrEmpty()) {
                alert(R.string.writing_hint_str)
                return@setOnClickListener
            }
            val content = view.editorContent.html.toString()
            val contentTagChange = content.replace("<strike>", "<s>").replace("</strike>", "</s>")
            val title = view.etTitle.text.toString()
            val tags = viewModel.requestTagList()
            val postAddRequest = PostAddRequest(categoryId, title, contentTagChange, null, null, null, tags)
            Log.d(TAG, "onCreateView, postAddRequest: $postAddRequest")

            if (resource_id != 0) {
                viewModel.modifyPost(resource_id, postAddRequest)
            } else {
                viewModel.addPost(postAddRequest)
            }

        }



        observeStartDetailActivity()

        observePickImage()

        return view
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(viewLifecycleOwner, Observer { viewFragment ->
            if (viewFragment) {
                val postId = viewModel.postId.value
                if (postId == null) {
                    Log.e(TAG, "postId is null observeStartDetailActivity")
                } else {
                    navigateAction(postId)
                }
            }
        })
    }

    fun navigateAction(postId: Int) {
        val bundle = bundleOf("resource_id" to postId, "category_id" to categoryId, "title" to fmTitle)
        navController.navigate(R.id.action_boardWriteFragment_to_boardViewFragment, bundle)
        Log.d(TAG, "bundle Of resource_id $bundle")
    }

    private fun getFlexBoxLayoutManager(): FlexboxLayoutManager {
        return FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
    }

    fun showDialog() {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_tag_input, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.tag_input_str) { dialog, which ->

                val etTagInput: EditText = view.findViewById(R.id.etTagInput)
                var inputTag = etTagInput.text.toString()
                Log.d(TAG, "showDialog: inputTag $inputTag")

                viewModel.addTag("etc", inputTag)
            }
            .setNeutralButton("취소", null)
            .create()
        //  여백 눌러도 창 안없어지게
        alertDialog.setCancelable(false)

        alertDialog.setView(view)
        alertDialog.show()
    }

    fun deleteTag(position: Int) {
        val deleteTag = viewModel.etcTags.value?.get(position).toString()
        Log.d(TAG, "deleteTag: $deleteTag ")
        viewModel.deleteTag("etc", position)
    }


    private fun showDialogImage() {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_camera, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("")
            .create()
        //  여백 누르면 창 없어짐
        alertDialog.setCancelable(true)
        alertDialog.setView(view)
        alertDialog.show()

        alertDialog.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.camera)?.setOnClickListener {
            Log.d(TAG, "showDialog: 카메라실행")
            captureCamera()
            alertDialog.dismiss()
        }

        alertDialog.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.gallery)?.setOnClickListener {
            Log.d(TAG, "showDialog: 갤러리실행")
            openGalleryForImage()
            alertDialog.dismiss()
        }

    }

    /////////////카메라X 적용
    //사진촬영 및 저장 -> 서버에도 사진저장
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        //이미지를 저장할 파일생성. 파일 이름이 고유하도록 타임 스탬프를 추가
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        //원하는 출력 방식을 지정
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {

                //이미지 캡처에 실패하거나 이미지 캡처 저장에 실패한 경우
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    //$photoFile -> /storage/emulated/0/Android/media/com.devup.android_shopping_mall/android-shopping-mall/20210105_025816417.jpg
                    Log.d(TAG, "onImageSaved, photoFile: $photoFile")
                    val msg = "Photo capture succeeded: $savedUri"
                    //$savedUri ->  file:///storage/emulated/0/Android/media/com.devup.android_shopping_mall/android-shopping-mall/20210105_025816417.jpg
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    viewModel.upLoadImage(photoFile)
                    //finish()
                    observeExistImage()
                    //cameraExecutor.shutdown()
                }
            })
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    //카메라 실행 및 미리보기
    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext()) //카메라의 라이프 사이클을 라이프 사이클 소유자에게 바인딩하는 데 사용

        cameraProviderFuture.addListener(
            Runnable {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get() //카메라의 수명주기를 LifecycleOwner애플리케이션의 프로세스 내에서 바인딩하는 데 사용

                val metrics = DisplayMetrics().also {
                    requireActivity().windowManager.defaultDisplay.getRealMetrics(it)
                    //viewFinder.display.getRealMetrics(it)
                    }
                //val screenAspectRatio = aspectRatio(metrics.heightPixels, metrics.widthPixels)
                val screenAspectRatio = aspectRatio(metrics.widthPixels,metrics.heightPixels)
                //미리보기
                val preview = Preview.Builder()
                    .setTargetAspectRatio(screenAspectRatio)
                    .build()
//                    .also {
//                        it.setSurfaceProvider(viewFinder.surfaceProvider)
//                    }
                imageCapture = ImageCapture.Builder()
                    //.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                // Select back camera as a default
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    //.requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        viewLifecycleOwner, cameraSelector, preview, imageCapture
                    )

                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
                preview.setSurfaceProvider(
                    viewFinder.surfaceProvider
                )
            }, ContextCompat.getMainExecutor(requireContext())  //메인 스레드에서 실행되는 Executor를 반환
        )
    }

    //권한 허용 알림
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    //권한 허용 여부에따라 싱행할것들
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                showDialogImage()

            } else {
                //Toast.makeText( this, "Permissions not granted by the user.", Toast.LENGTH_SHORT ).show()
                alert(R.string.notice_permission_str)
            }
        }
    }

    //새 이미지경로(파일)_생성
    private fun getOutputDirectory(): File {
        val mediaDir = context?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context?.filesDir!!
    }

    //갤러리 열기
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    //카메라실행 (cameraX)
    private fun captureCamera() {
        //카메라에서 가져오기
        view?.cameraView?.visibility = View.VISIBLE
        view?.svPost?.visibility = View.GONE
        startCamera()
    }


    //카메라 사진 퇄영 후 저장된 이미지 존재 여부
    //촬영한 이미지가 저장되었다면 실행한 카메라 종료
    fun observeExistImage() {
        viewModel.isExistImage.observe(this, androidx.lifecycle.Observer { exist ->
            if (exist) {
                cameraExecutor.shutdown()
                view?.cameraView?.visibility = View.GONE
                view?.svPost?.visibility = View.VISIBLE

                Log.d(TAG, "observeExistImage: 사진주소소ㅗㅗ ${viewModel.attachImageUrl.value}")

                view?.editorContent?.insertImage(
                    //더미
                    viewModel.attachImageUrl.value,
                    " ", 320
                )
                viewModel._isExistImage.value = false
            }
        })
    }

    //사진등록(갤러리)버튼 클릭 관찰 -> xml에사 클릭했을때 관찰하기위해 작성해놓은코트
    private fun observePickImage() {
        viewModel.pickImage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_GALLERY -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    try {
                        uri = data!!.data!!
                        //Uri스키마를 content:/// -> file:///로 변경
                        val proj = arrayOf(MediaStore.Images.Media.DATA)

                        val cursor = context?.contentResolver?.query(uri, proj, null, null, null)
                        cursor!!.moveToFirst()
                        val column_index = cursor!!.getColumnIndex(proj[0])
                        val imgDecodableString = cursor!!.getString(column_index)
                        photoFile = File(cursor!!.getString(column_index))
                        cursor!!.close()

                        Log.e(TAG, "onActivityResult: photoFile $photoFile")

                        viewModel.upLoadImage(photoFile!!)
                        observeExistImage()

                    } catch (e: Exception) {
                        Log.e(TAG, "REQUEST_IMAGE_GALLERY Exception $e")
                    }
                }
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmssSSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        private const val RATIO_4_3_VALUE = 3.0 / 4.0
        private const val RATIO_16_9_VALUE = 9.0 / 16.0
    }
}