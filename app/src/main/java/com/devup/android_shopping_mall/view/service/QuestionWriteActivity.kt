package com.devup.android_shopping_mall.view.service

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.post.model.ServicesPostAddRequest
import com.devup.android_shopping_mall.databinding.ActivityQuestionWriteBinding
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_question_write.*
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class QuestionWriteActivity : AppCompatActivity() {
    private val TAG = "QuestionWriteActivity"

    private val viewModel: ServicesViewModel by inject()

    //    val categoryId = intent.getIntExtra("categoryId",3)
    val categoryId = 3
    var servicesType: Int? = null

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

        //setContentView(R.layout.activity_question_write)
        val binding: ActivityQuestionWriteBinding = DataBindingUtil.setContentView(this, R.layout.activity_question_write)
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@QuestionWriteActivity

            editorContent.apply {
                setEditorHeight(200)
                setEditorFontSize(18)
                setEditorFontColor(Color.BLACK)
                setPadding(10, 10, 10, 10)
                setPlaceholder("내용을 입력해 주세요")
            }
        }

        action_bold.setOnClickListener {
            editorContent.focusEditor()
            editorContent.setBold()
        }
        action_italic.setOnClickListener {
            editorContent.focusEditor()
            editorContent.setItalic()
        }
        action_strikethrough.setOnClickListener {
            editorContent.focusEditor()
            editorContent.setStrikeThrough()
        }
        action_underline.setOnClickListener {
            editorContent.focusEditor()
            editorContent.setUnderline()
        }
        action_insert_image.setOnClickListener {
            editorContent.focusEditor()
            if (allPermissionsGranted()) {
                showDialogImage()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        // Set up the listener for take photo button
        btnCameraCapture.setOnClickListener { takePhoto() }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        //문의 종류 선택

        val questionType = ArrayAdapter.createFromResource(this, R.array.servicesQuestionSelector, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spQuestionType.adapter = adapter
        }

        spQuestionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            //type선택택
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when (position) {
                    0 -> viewModel.type.value = 0
                    else -> viewModel.type.value = position
                }
            }


        }


        btnQuestionAdd.setOnClickListener {
            //viewModel.content.value = editorContent.html.toString()
            //val content = editorContent.html.toString()
            //val title = etTitle.text.toString()
            //val postAddRequest = ServicesPostAddRequest(categoryId, content, title, null, 1, null, null)
            //viewModel.addServicesPost(categoryId, postAddRequest)
            checkNullOrBlank()
            Log.d(TAG, "onCreate: ${viewModel.type.value}")
            Log.d(TAG, "onCreate: ${viewModel.title.value}")
            Log.d(TAG, "onCreate: ${viewModel.content.value}")
        }

        observeStartDetailActivity()
        observePickImage()

        btnCloseToolbar.setOnClickListener {
            finish()
        }

    }
    //onCreate종료

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(this)
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }


    fun checkNullOrBlank() {
        if (!viewModel.checkTypeInput()) {
            alert(R.string.question_type_choice_str)
            return
        }
        if (!viewModel.checkTitleInput()) {
            alert(R.string.title_hint_str)
            return
        }

        if (editorContent.html.isNullOrBlank()) {
            alert(R.string.writing_hint_str)
            return
        } else {
            val content = editorContent.html.toString()
            val contentTagChange = content.replace("<strike>", "<s>").replace("</strike>", "</s>")
            viewModel.content.value = contentTagChange
        }

        val postAddRequest = ServicesPostAddRequest(categoryId, viewModel.content.value.toString(), viewModel.type.value!!, viewModel.title.value.toString(), null, null, null)
        viewModel.addServicesPost(postAddRequest)

    }

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(this@QuestionWriteActivity, Observer { viewActivity ->
            if (viewActivity) {
                val postId = viewModel.postId.value
                if (postId == null) {
                    Log.e(TAG, "postId is null observeStartDetailActivity")
                } else {
                    openDetailActivity(postId)
                }
            }
        })
    }

    fun openDetailActivity(postId: Int) {
        val intent = Intent(this, QuestionDetailActivity::class.java)
        intent.putExtra("resource_id", postId)
        startActivity(intent)
        finish()
    }


    private fun showDialogImage() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_camera, null)

        val alertDialog = AlertDialog.Builder(this)
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
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {

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
                    Toast.makeText(this@QuestionWriteActivity, msg, Toast.LENGTH_SHORT).show()
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
        cameraProviderFuture = ProcessCameraProvider.getInstance(this) //카메라의 라이프 사이클을 라이프 사이클 소유자에게 바인딩하는 데 사용

        cameraProviderFuture.addListener(
            Runnable {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get() //카메라의 수명주기를 LifecycleOwner애플리케이션의 프로세스 내에서 바인딩하는 데 사용

                val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
                val screenAspectRatio = aspectRatio(metrics.heightPixels, metrics.widthPixels)
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
                //val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this as LifecycleOwner, cameraSelector, preview, imageCapture
                    )

                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
                preview.setSurfaceProvider(
                    viewFinder.surfaceProvider
                )
            }, ContextCompat.getMainExecutor(this)  //메인 스레드에서 실행되는 Executor를 반환
        )
    }

    //권한 허용 알림
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
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
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
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
        cameraView.visibility = View.VISIBLE
        svPost.visibility = View.GONE
        startCamera()
    }


    //카메라 사진 퇄영 후 저장된 이미지 존재 여부
    //촬영한 이미지가 저장되었다면 실행한 카메라 종료
    fun observeExistImage() {
        viewModel.isExistImage.observe(this, androidx.lifecycle.Observer { exist ->
            if (exist) {
                cameraExecutor.shutdown()
                cameraView.visibility = View.GONE
                svPost.visibility = View.VISIBLE

                Log.d(TAG, "observeExistImage: 사진주소 ${viewModel.attachImageUrl.value}")

                editorContent?.insertImage(
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
        viewModel.pickImage.observe(this@QuestionWriteActivity, androidx.lifecycle.Observer {
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

                        val cursor = contentResolver?.query(uri, proj, null, null, null)
                        cursor!!.moveToFirst()
                        val column_index = cursor.getColumnIndex(proj[0])
                        val imgDecodableString = cursor.getString(column_index)
                        photoFile = File(cursor.getString(column_index))
                        cursor.close()

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