package com.devup.android_shopping_mall.view.join

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.util.EMAIL_REG_EXP
import com.devup.android_shopping_mall.util.NICKNAME_REG_EXP
import com.devup.android_shopping_mall.util.PASSWORD_REG_EXP
import kotlinx.android.synthetic.main.activity_signup_step01.*
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.devup.android_shopping_mall.databinding.ActivitySignupStep01Binding
import com.google.common.util.concurrent.ListenableFuture
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SignupStep01Activity : AppCompatActivity() {
    private val TAG: String = "SignupStep01Activity"
    private val viewModel: SignupViewModel by inject()

    //email _가입여부 / 중복체크
    var isEmailValidate = false
    var isPasswordRegexp = false
    var isPasswordCheck = false
    var isNicknameRegexp = false

    val emailRegexp = Regex(EMAIL_REG_EXP)
    val passwordRegexp = PASSWORD_REG_EXP.toRegex()
    var nicknameRegexp = NICKNAME_REG_EXP.toRegex()

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val REQUEST_IMAGE_GALLERY = 200
    private val REQUEST_IMAGE_CAPTURE = 201
    private val REQUEST_IMAGE_CROP = 202
    private val RC_PICK_IMAGE = 203

    var photoFile: File? = null
    lateinit var mCurrentPhotoPath: String
    lateinit var uri: Uri

    lateinit var binding: ActivitySignupStep01Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_signup_step01)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_step01)
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@SignupStep01Activity
            Log.d(TAG, "onCreate: ${viewModel.profileImageUrl.value.toString()}")
        }


        //textWatcher를 통해 실시간 이메일 중복체크
        //사용자의 입력이 끝나는 시점에서 통신 후 체크한다.
        //해당 이메일 사용여부는 tv_email_validate로 사용자에게 알려준다
        //사용가능한이메일이면 isEmailValidate를 true로 변경한다
        //이메일형식 체크하는 이유
        //_ 사용자가 입력할때마다 서버와 통신하는것을 어느정도 방지하기위해

        etProfileEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (etProfileEmail.text.toString().matches(emailRegexp)) {
                    viewModel.checkUser(etProfileEmail.text.toString())
                } else if (!etProfileEmail.text.toString().matches(emailRegexp)) {
                    tvEmailValidate.visibility = View.GONE
                    isEmailValidate = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

//----- 비밀번호 정규식 확인
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etPassword.text.toString().matches(passwordRegexp)) {
                    tvPasswordInfo.setTextColor(
                        ContextCompat.getColor(
                            this@SignupStep01Activity,
                            R.color.colorBlack
                        )
                    )
                    isPasswordRegexp = true

                } else if (!etPassword.text.toString().matches(passwordRegexp)) {
                    tvPasswordInfo.setTextColor(
                        ContextCompat.getColor(
                            this@SignupStep01Activity,
                            R.color.colorRed
                        )
                    )
                    isPasswordRegexp = false
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


//----- 비밀번호 확인
        etPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etPasswordCheck.text.toString().equals(etPassword.text.toString())) {
                    tvPasswordCheckInfo.visibility = View.GONE
                    isPasswordCheck = true

                } else if (!etPasswordCheck.text.toString().equals(etPassword.text.toString())) {
                    tvPasswordCheckInfo.setTextColor(
                        ContextCompat.getColor(
                            this@SignupStep01Activity,
                            R.color.colorRed
                        )
                    )
                    tvPasswordCheckInfo.visibility = View.VISIBLE
                    isPasswordCheck = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

//----- 닉네임 확인
        etNickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etNickname.text.toString().matches(nicknameRegexp)) {
                    viewModel.checkNickname(etNickname.text.toString())
                } else if (!etNickname.text.toString().matches(nicknameRegexp)) {
                    tvNicknameInfo.setTextColor(ContextCompat.getColor(this@SignupStep01Activity, R.color.colorRed))
                    tvNicknameValidate.visibility = View.GONE
                    isNicknameRegexp = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


//----- 프로필 사진 등록

        ivProfile.setOnClickListener {
            if (allPermissionsGranted()) {
                //다이얼로그 띄우기기
               /* var alertDialog = AlertDialog.Builder(this@SignupStep01Activity).apply {
                    setTitle("")
                    setMessage("")
                    setNeutralButton(R.string.gallery_str) { dialog, which ->
                        openGalleryForImage()
                        //갤러리 이미지_간편
                        //viewModel.pickImage()
                    }
                    setPositiveButton(R.string.camera_str) { dialog, which -> captureCamera()}
                }
                alertDialog.show()*/

                showDialog()

            } else {
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }

        // Set up the listener for take photo button
        btnCameraCapture.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()


// ----- 회원가입
        btnSignup.setOnClickListener {
            val email: String = etProfileEmail.text.toString()
            val password: String = etPassword.text.toString()
            val nickname: String = etNickname.text.toString()

            if (email.equals("")) {
                alert(R.string.notice_email_str)
                return@setOnClickListener
            }

            if (nickname.equals("")) {
                alert(R.string.notice_nickname_str)
                return@setOnClickListener
            }

            if (password.equals("")) {
                alert(R.string.notice_password_str)
                return@setOnClickListener
            }
            if (!isEmailValidate) {
                alert(R.string.notice_emailRegexp_str)
                joinView.scrollTo(0, 0)  //스크롤 이동
                etProfileEmail.requestFocus() //포커스이동
                return@setOnClickListener
            }

            if (!isPasswordRegexp) {
                alert(R.string.notice_passwordRegexp_str)
                joinView.scrollTo(0, 0)  //스크롤 이동
                etPassword.requestFocus() //포커스이동
                return@setOnClickListener
            }

            if (!isPasswordCheck) {
                alert(R.string.notice_passwordCheck_str)
                joinView.scrollTo(0, tvProfileEmail.bottom)  //스크롤 이동
                etPasswordCheck.requestFocus() //포커스이동
                return@setOnClickListener

            }

            if (!isNicknameRegexp) {
                alert(R.string.notice_nicknameRegexp_str)
                joinView.scrollTo(0, tvPassword.bottom)  //스크롤 이동
                etNickname.requestFocus() //포커스이동
                return@setOnClickListener
            }

/*
//            Log.d(TAG,"photoFile $photoFile")
            val intent = Intent(this, SignupStep02Activity::class.java)
            intent.putExtra("profileEmail", email)
            intent.putExtra("password", password)
            intent.putExtra("profileNickname", nickname)
            startActivity(intent)*/

            viewModel.join(nickname,email,password,viewModel.profileImageUrl.value)

        }

        observeViewModel()

    }
    //-----onCreate종료

    override fun onResume() {
        super.onResume()
        if (!viewModel.profileImageUrl.value.isNullOrBlank()) {
            Log.d(TAG, "onResume: ${viewModel.profileImageUrl.value}")
            val ivProfile = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.ivProfile)

            Glide.with(this).load(viewModel.profileImageUrl.value).error(R.drawable.default_img_3x).into(ivProfile)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // cameraExecutor.shutdown()
        Log.d(TAG, "onDestroy: ")
    }

    private fun showDialog() {
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

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(this@SignupStep01Activity)
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    fun observeViewModel() {
        observeJoinPossible()
        observeNicknamePossible()
        observePickImage()
        observeOpenDetailActivity()
    }

    //가입여부체크
    fun observeJoinPossible() {
        viewModel.isJoinPossible.observe(this@SignupStep01Activity, Observer { isValidate ->
            if (isValidate) {
                tvEmailValidate.text = "사용 가능한 이메일 입니다"
                tvEmailValidate.setTextColor(ContextCompat.getColor(this@SignupStep01Activity, R.color.colorBlack))
                tvEmailValidate.visibility = View.VISIBLE
                isEmailValidate = true
            } else {
                tvEmailValidate.text = "사용할 수 없는 이메일 입니다"
                tvEmailValidate.setTextColor(ContextCompat.getColor(this@SignupStep01Activity, R.color.colorRed))
                tvEmailValidate.visibility = View.VISIBLE
                isEmailValidate = false
            }
        })
    }

    //닉네임중복채크
    fun observeNicknamePossible() {
        viewModel.isNicknamePossible.observe(this@SignupStep01Activity, Observer { isValidate ->
            if (isValidate) {
                tvNicknameValidate.text = "사용 가능한 닉네임 입니다"
                tvNicknameValidate.setTextColor(ContextCompat.getColor(this@SignupStep01Activity, R.color.color_delete))
                tvNicknameInfo.setTextColor(ContextCompat.getColor(this@SignupStep01Activity, R.color.colorBlack))
                tvNicknameValidate.visibility = View.VISIBLE
                //tvNicknameInfo.visibility = View.GONE
                isNicknameRegexp = true

            } else {
                tvNicknameValidate.text = "이미 사용중인 닉네임 입니다."
                tvNicknameValidate.visibility = View.VISIBLE
                tvNicknameValidate.setTextColor(ContextCompat.getColor(this@SignupStep01Activity, R.color.colorRed))

                isNicknameRegexp = false
            }
        })
    }

    //카메라 사진 퇄영 후 저장된 이미지 존재 여부
    //촬영한 이미지가 저장되었다면 실행한 카메라 종료
    fun observeExistImage() {
        viewModel.isExistImage.observe(this, androidx.lifecycle.Observer { exist ->
            if (exist) {
                cameraExecutor.shutdown()
                cameraView.visibility = View.GONE
                joinView.visibility = View.VISIBLE
                //Glide.with(view.context).load(viewModel.profileImageUrl.value.toString()).error(R.drawable.default_img_3x).into(ivProfile)
            }
        })
    }

    //사진등록(갤러리)버튼 클릭 관찰 -> xml에사 클릭했을때 관찰하기위해 작성해놓은코트
    fun observePickImage() {
        viewModel.pickImage.observe(this@SignupStep01Activity, Observer {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        })
    }

    fun observeOpenDetailActivity() {
        viewModel.openDetailActivity.observe(this, Observer { viewFragment ->
            if (viewFragment) {
                val intent = Intent(this, SignupStep02Activity::class.java)

                startActivity(intent)
                finish()
            }
        })
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
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
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

                val metrics = DisplayMetrics().also {
                    windowManager.defaultDisplay.getRealMetrics(it)
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
                //카메라로찍을것인지, 갤러리 선택인지
                //startCamera()

                /*var alertDialog = AlertDialog.Builder(this@SignupStep01Activity)
                alertDialog.setTitle("").setMessage("")
                alertDialog.setNeutralButton(R.string.gallery_str) { dialog, which -> openGalleryForImage() }
                alertDialog.setPositiveButton(R.string.camera_str) { dialog, which -> captureCamera() }
                alertDialog.show()*/

                showDialog()

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
        //startActivityForResult(intent, RC_PICK_IMAGE)

        //구
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    //카메라실행 (cameraX)
    fun captureCamera() {
        //카메라에서 가져오기
        cameraView.visibility = View.VISIBLE
        joinView.visibility = View.GONE
        startCamera()
    }
    //카메라 실행 구버전
/*    private fun captureCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {

//            var photoFile : File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Log.e(TAG, "captureCamera Error  Exception $ex")
                return
            }

            if (Build.VERSION.SDK_INT < 24) {
                if (photoFile != null) {
                    uri = Uri.fromFile(photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            } else {
                if (photoFile != null) { // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                    uri = FileProvider.getUriForFile(this, packageName, photoFile!!)
                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }*/

    //임시파일 경로 생성(구버전)
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File { // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp"

        //이미지가 저장될 폴더이름
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        //빈파일 생성
        val imageFile = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        return imageFile.apply {
            mCurrentPhotoPath = absolutePath
            Log.d(TAG, "mCurrentPhotoPath:$mCurrentPhotoPath")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            /*REQUEST_IMAGE_CAPTURE -> {
                Log.i(TAG, "REQUEST_IMAGE_CAPTURE ${Activity.RESULT_OK} $resultCode")
                if (resultCode == RESULT_OK) {
                    try {
                        rotateImageIfRequired(mCurrentPhotoPath)
                        cropCameraImage()
                        //galleryAddPic()
                        btnProfileImageAdd.text = ""

                    } catch (e: Exception) {
                        Log.e(TAG, "REQUEST_TAKE_PHOTO Exception $e")
                    }

                } else if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this@SignupStep01Activity, R.string.camera_cancel_str, Toast.LENGTH_SHORT)
                        .show()
                    //촬영중 뒤로가기 눌러서 생긴 빈 파일 삭제
                    if (photoFile != null) {
                        if (photoFile!!.exists()) {
                            photoFile!!.delete()
                            if (photoFile!!.delete()) {
                                Log.d(
                                    TAG,
                                    "deleted photoFile of IMAGE_CAPTURE cancel ${photoFile!!.absolutePath}"
                                )
                                photoFile = null
                            }
                        }
                    }
                    return
                }
            }*/

            REQUEST_IMAGE_CROP -> {
                lateinit var bitmap: Bitmap
                if (resultCode == RESULT_OK) {
                    try {
                        //bitmap 형태의 이미지로 가져온다
                        // Thumbnail 추출
                        try {
                            uri.let {
                                if (Build.VERSION.SDK_INT < 28) {
                                    bitmap =
                                        MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                                } else {
                                    val source =
                                        ImageDecoder.createSource(this.contentResolver, uri)
                                    bitmap = ImageDecoder.decodeBitmap(source)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 80, 80)
                        val bs = ByteArrayOutputStream()

                        thumbImage.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            bs
                        ) //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축

                        //이미지뷰에 띄우기기
                        ivProfile.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        Log.e(TAG, "REQUEST_IMAGE_CROP Exception $e")
                    }

                } else if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this@SignupStep01Activity, R.string.notice_cancel_str, Toast.LENGTH_SHORT).show()
                    if (photoFile != null) {
                        if (photoFile!!.exists()) {
                            photoFile!!.delete()
                            if (photoFile!!.delete()) {
                                Log.d(TAG, "deleted photoFile of REQUEST_IMAGE_CROP cancel ${photoFile!!.absolutePath}")
                                photoFile = null
                            }
                        }
                    }
                    return
                }
            }

            REQUEST_IMAGE_GALLERY -> {
                if (resultCode == RESULT_OK) {
                    try {
                        uri = data!!.data!!
                        //Uri스키마를 content:/// -> file:///로 변경
                        val proj = arrayOf(MediaStore.Images.Media.DATA)

                        val cursor = contentResolver.query(uri, proj, null, null, null)
                        cursor!!.moveToFirst()
                        val column_index = cursor!!.getColumnIndex(proj[0])
                        val imgDecodableString = cursor!!.getString(column_index)
                        photoFile = File(cursor!!.getString(column_index))
                        cursor!!.close()

                        //cropCameraImage()
                        Log.e(TAG, "onActivityResult: photoFile $photoFile")
                        //$photoFile-> /storage/emulated/0/DCIM/Camera/IMG_20210104_000624.jpg

                        viewModel.upLoadImage(photoFile!!)
                        observeExistImage()

                        //ivProfile.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString))

                    } catch (e: Exception) {
                        Log.e(TAG, "REQUEST_IMAGE_GALLERY Exception $e")
                    }

                }

            }

            RC_PICK_IMAGE -> {
                data?.data?.let {
                    binding.ivProfile.setImageURI(it)
                    Log.e(TAG, "onActivityResult: $it")
                    viewModel.imageUri = it
                }
            }

        }
    }

    //카메라로 찍은 사진 앨범에 저장 ->  안됨
/*    private fun galleryAddPic() {
        Log.i(TAG, "galleryAddPic Called")

//        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        // 해당 경로에 있는 파일을 객체화
//        val f = File(mCurrentPhotoPath)
//        val contentUri: Uri = Uri.fromFile(f)
//        mediaScanIntent.data = contentUri
//        sendBroadcast(mediaScanIntent)
//        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()

//        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
//            val f = File(mCurrentPhotoPath)
//            mediaScanIntent.data = Uri.fromFile(f)
//            sendBroadcast(mediaScanIntent)
//        }

//        val f = File(mCurrentPhotoPath)
//        MediaScannerConnection.scanFile(this, arrayOf(f.toString()), arrayOf(f.name), null)
    }*/


    // 이미지 크롭
    private fun cropCameraImage() {
        val cropImageIntent = Intent("com.android.camera.action.CROP")
        cropImageIntent.setDataAndType(uri, "image/*")

        //인자로 전달되는 인텐트에 맞는 실행가능한 액티비티들을 찾아 list에 담는다
        val list = packageManager.queryIntentActivities(cropImageIntent, 0)

        //파일에 대한 접근 허가
        grantUriPermission(
            list[0].activityInfo.packageName,
            uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val size = list.size
        if (size == 0) {
            Toast.makeText(this@SignupStep01Activity, R.string.notice_cancel_str, Toast.LENGTH_SHORT).show()
            return
        } else {
            cropImageIntent.putExtra("crop", "true")
            cropImageIntent.putExtra("aspectX", 1)  // crop 박스의 x축 비율
            cropImageIntent.putExtra("aspectY", 1)  // crop 박스의 y축 비율
            cropImageIntent.putExtra("scale", true)

            var croppedFileName: File? = null
            try {
                croppedFileName = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val tempFile = File(folder.toString(), croppedFileName!!.name)

            uri = FileProvider.getUriForFile(applicationContext, packageName, tempFile)

            cropImageIntent.putExtra("return-data", false)
            cropImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            cropImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()) //Bitmap 형태로 받기 위해 해당 작업 진행

            //다른 어플리케이션 상에 존재하는 Acitivity를 호출 (내장 crop기능 호출)
            cropImageIntent.component =
                ComponentName(list[0].activityInfo.packageName, list[0].activityInfo.name)
            startActivityForResult(cropImageIntent, REQUEST_IMAGE_CROP)
        }
    }


    //이미지 회전
    private fun rotateImageIfRequired(imagePath: String): Bitmap? {
        var degrees = 0
        try {

            //사진(imagePath)에 대한 정보를 가진 객체
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                //ExifInterface.TAG_로 가져올 수 있는 정보 중 회전값을 알수있는 TAG로 orientation를 반환받는다
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                //orientation 정보로 해당 사진의 각도를 degrees에 저장
                ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270
            }
        } catch (e: IOException) {
            Log.e(TAG, "ImageError in reading Exif data of $imagePath , $e")
        }

        //이미지 Resize
        val options: BitmapFactory.Options = BitmapFactory.Options()
        //임미지 크기만을 먼저 불러와 OutOfMemory Exception을 불러일으킬만한 큰 이미지를 선처리
        //메모리 할당을 피하면서 비트맵에 null을 반환하지만, outWidth, outHeight, outMimeType를 세팅할 수있음
        options.inJustDecodeBounds = true
        var bitmap: Bitmap?
        val numPixels: Int = options.outWidth * options.outHeight
        val maxPixels = 2048 * 1536 // requires 12 MB heap

        //inSampleSize : 이미지 decoding 시 얼마나 이미지를 줄여서 decoding 할지를 결정하는 옵션
        // inSampleSize = n -> n개의 픽셀을 1개의 픽셀로 decoding(1/n배크기로 만든다)
        options.inSampleSize = if (numPixels > maxPixels) 2 else 1
        bitmap = BitmapFactory.decodeFile(imagePath, options)
        if (bitmap == null) {
            return null
        }

        val matrix = Matrix()
        //회전각도셋팅
        matrix.setRotate(degrees.toFloat())
        //이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        bitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, true
        )
        return bitmap
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmssSSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        private const val RATIO_4_3_VALUE = 3.0 / 4.0
        private const val RATIO_16_9_VALUE = 9.0 / 16.0
    }

}

