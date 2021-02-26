package com.devup.android_shopping_mall.view.mypage

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.user.model.ModifyUserInfoRequest
import com.devup.android_shopping_mall.databinding.FragmentMypageEditBinding
import com.devup.android_shopping_mall.util.NICKNAME_REG_EXP
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.fragment_mypage_edit.*
import kotlinx.android.synthetic.main.fragment_mypage_edit.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MypageEditFragment : Fragment() {

    private val TAG = "MypageEditFragment"

    private val viewModel: MoreFragmentViewModel by sharedViewModel()

    lateinit var navController: NavController

    //닉네임, 프사, 생년월일, 직종, 분야, 성별, 총경력, 지역, 지역상세, 급여, 근속정보, 언어, ide

    //닉네임
    var nicknameRegexp = NICKNAME_REG_EXP.toRegex()
    var isNicknameRegexp = false //

    //프로필 이미지
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val REQUEST_IMAGE_GALLERY = 200

    var photoFile: File? = null
    lateinit var mCurrentPhotoPath: String
    lateinit var uri: Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMypageEditBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mypage_edit, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@MypageEditFragment
            viewModel.getJobTypeList()

            rvDevelopmentLanguages.let {
                it.layoutManager = getFlexBoxLayoutManager()
                it.adapter = MypageEditLanguageIdeAdapter(object :
                    MypageEditLanguageIdeAdapter.OnItemListener {
                    override fun footerClick() {
                        openIdeSearch("language")
                    }

                    override fun onItemClick(str: String) {
                        viewModel.deleteResult("lang", "choice", str)
                    }
                })
            }
            rvDevelopmentIdes.let {
                it.layoutManager = getFlexBoxLayoutManager()
                it.adapter = MypageEditLanguageIdeAdapter(object :
                    MypageEditLanguageIdeAdapter.OnItemListener {
                    override fun footerClick() {
                        //
                        openIdeSearch("ide")
                    }

                    override fun onItemClick(str: String) {
                        viewModel.deleteResult("ide", "choice", str)
                    }
                })
            }
        }


        //닉네임 중복검사(현재 프로필장의 닉네임과 다를경우에만 중복검사 진행)
        view.etNickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etNickname.text.toString().matches(nicknameRegexp)) {
                    if (view.etNickname.text.toString() != viewModel.userProfile.value?.profile_nickname.toString()) {
                        viewModel.checkNickname(etNickname.text.toString())
                    } else {
                        //viewModel._isNicknamePossible.value = null
                        view.tvNicknameValidate.visibility = View.GONE
                    }

                } else if (!etNickname.text.toString().matches(nicknameRegexp)) {
                    //view.tvNicknameValidate.visibility = View.GONE
                    view.tvNicknameValidate.visibility = View.VISIBLE
                    view.tvNicknameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
                    view.tvNicknameValidate.setText(R.string.nickname_info_str)
                    isNicknameRegexp = false

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        //----- 프로필 사진 등록
        view.ivProfile.setOnClickListener {
            if (allPermissionsGranted()) {
                //다이얼로그 띄우기기
                /*var alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("")
                    //setMessage("")
                    setNeutralButton(R.string.gallery_str) { dialog, which ->
                        openGalleryForImage()
                    }
                    setPositiveButton(R.string.camera_str) { dialog, which -> captureCamera() }
                }
                alertDialog.show()*/

                showDialog()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }
        // Set up the listener for take photo button
        view.btnCameraCapture.setOnClickListener { takePhoto() }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()


        //생년월일등록
        view.etBirthday.setOnClickListener {
            showDatePicker()
        }

        view.radioGroupProfileGender.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, id ->
            when (id) {
                R.id.radioButtonProfileGenderMan -> {//profile_gender = "M"
                    viewModel._isGenderMan.value = true
                    viewModel._isGenderWoman.value = false
                }
                R.id.radioButtonProfileGenderWoman -> {//profile_gender = "W"
                    viewModel._isGenderMan.value = false
                    viewModel._isGenderWoman.value = true
                }
            }
        })


        if (viewModel.check == 0) {
            //닉네임
            viewModel._myNickname.value = viewModel.userProfile.value?.profile_nickname.toString()

            //생년월일
            viewModel._myBirthDay.value = viewModel.userProfile.value?.profile_birthday

            //직종 _ 직종에 따라 분야의 스피너 아이템 변경
            viewModel.myJobType.value = viewModel.userProfile.value?.job_type.toString()
            Log.d(TAG, "선택전,viewModel.myJobType.value: ${viewModel.myJobType.value}")
            view.spJobType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    viewModel.myJobType.value = viewModel.jobTypeSpinnerItem.value?.get(p2)
                    Log.d(TAG, "onItemSelected,viewModel.myJobType.value: ${viewModel.myJobType.value}")
                    viewModel.clearJobItem()
                    viewModel.getJobTypeList()
                }
            }
            viewModel._myJobTypeIndex.value = viewModel.jobTypeSpinnerItem.value?.indexOf(viewModel.myJobType.value.toString())

            //분야
            viewModel.myJobField.value = viewModel.userProfile.value?.job_field.toString()
            Log.d(TAG, "선택전,viewModel.myJobField.value: ${viewModel.myJobField.value}")

            view.spJobField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel.myJobField.value = viewModel.jobFieldSpinnerItem.value?.get(position)
                    //Log.d(TAG, "onItemSelected,viewModel.myJobField.value: ${viewModel.myJobField.value.toString()}")
                }
            }
            //viewModel._myJobFieldIndex.value = viewModel.jobFieldSpinnerItemEn.value?.indexOf(viewModel.myJobField.value.toString())
            // viewModel._myJobFieldIndex.value = viewModel.jobFieldSpinnerItem.value?.indexOf(viewModel.myJobField.value.toString())?.plus(1)
            // Log.d(TAG, "선택전,viewModel.myJobFieldIndex.value: ${viewModel.myJobFieldIndex.value}")

            //성별
            viewModel._isGenderMan.value = viewModel.userProfile.value?.profile_gender.equals("남")
            viewModel._isGenderWoman.value = viewModel.userProfile.value?.profile_gender.equals("여")

            //총경력
            val experienceArray: Array<String> = resources.getStringArray(R.array.periodSelector)
            viewModel._myExperienceIndex.value = experienceArray.indexOf(viewModel.userProfile.value?.experience_years)
            view.spExperienceYears.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel._myExperienceIndex.value = position
                }
            }

            //근무지역
            val areaArray: Array<String> = resources.getStringArray(R.array.workingPlaceSelector)
            viewModel._myWorkingAreaIndex.value = areaArray.indexOf(viewModel.userProfile.value?.working_area)
            view.spWorkingArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    //Log.d(TAG, "onItemSelected: p2 $position")
                    viewModel._myWorkingAreaIndex.value = position
                    //viewModel._myWorkingArea.value = areaArray[position].toString()
                    viewModel._myWorkingArea.value = view.spWorkingArea.selectedItem.toString()
                    //Log.d(TAG, "onItemSelected: ${viewModel.myWorkingAreaIndex.value}")

                    val area = view.spWorkingArea.selectedItem.toString()
                    //Log.d(TAG, "onCreateView: $area")
                }
            }

            //근무지역 상세
            val areaDetailArray: Array<String> = resources.getStringArray(R.array.workingDetailPlaceSelector)
            viewModel._myWorkingAreaDetailIndex.value = areaDetailArray.indexOf(viewModel.userProfile.value?.working_area_detail)
            view.spWorkingAreaDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel._myWorkingAreaDetailIndex.value = position
                    viewModel._myWorkingAreaDetail.value = view.spWorkingAreaDetail.selectedItem.toString()
                }
            }

            //급여
            viewModel._myBasicSalary.value = viewModel.userProfile.value?.basic_salary
            view.etBasicSalary.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (view.etBasicSalary.text.toString().isNotEmpty()) {
                        viewModel._myBasicSalary.value = Integer.parseInt(view.etBasicSalary.text.toString())
                    } else viewModel._myBasicSalary.value = null

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })


            //근속년수
            viewModel._mylongevityIndex.value = viewModel.userProfile.value?.longevity?.plus(1)
            view.spLongevity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel._mylongevityIndex.value = position
                }
            }

            //git
            viewModel._myGithubUrl.value = viewModel.userProfile.value?.github_url
            view.etGithubUrl.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel._myGithubUrl.value = view.etGithubUrl.text.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            //포폴
            viewModel._myPortfolioUrl.value = viewModel.userProfile.value?.portfolio_url
            view.etPortfolioUrl.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel._myPortfolioUrl.value = view.etPortfolioUrl.text.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            //정보공개여부, 미동의 : 0 , 동의 : 1
            viewModel._isInformationOpen.value = viewModel.userProfile.value?.is_information_open == 1
            view.cbIsInformationOpen.setOnCheckedChangeListener { compoundButton, checked ->
                viewModel._isInformationOpen.value = checked
            }

            //푸쉬알림동의
            viewModel._isPushStatus.value = viewModel.userProfile.value?.push_status == 1
            view.cbIsPush.setOnCheckedChangeListener { compoundButton, checked ->
                viewModel._isPushStatus.value = checked
            }

            if (!viewModel.userProfile.value?.languages.isNullOrEmpty()) {
                for (language in viewModel.userProfile.value?.languages!!) {
                    viewModel._languages.value = viewModel.languages.value?.plus(language.name) ?: listOf(language.name)
                }
            } else viewModel._languages.value = null

            if (!viewModel.userProfile.value?.ides.isNullOrEmpty()) {
                for (ide in viewModel.userProfile.value?.ides!!) {
                    viewModel._ides.value = viewModel.ides.value?.plus(ide.name) ?: listOf(ide.name)
                }
            } else viewModel._ides.value = null

        } else {
            //닉네임
            //viewModel._myNickname.value = view.etNickname.text.toString()
            //생년월일일
            //viewModel._myBirthDay.value = view.etBirthday.text.toString()

            //직종 _ 직종에 따라 분야의 스피너 아이템 변경
            view.spJobType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    viewModel.myJobType.value = viewModel.jobTypeSpinnerItem.value?.get(p2)
                    //Log.d(TAG, "onItemSelected,viewModel.myJobType.value: ${viewModel.myJobType.value}")
                    viewModel.clearJobItem()
                    viewModel.getJobTypeList()
                }
            }
            viewModel._myJobTypeIndex.value = viewModel.jobTypeSpinnerItem.value?.indexOf(viewModel.myJobType.value.toString())

            //분야
            view.spJobField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel.myJobField.value = viewModel.jobFieldSpinnerItem.value?.get(position)
                    //Log.d(TAG, "onItemSelected,viewModel.myJobField.value: ${viewModel.myJobField.value.toString()}")
                }
            }
            //viewModel._myJobFieldIndex.value = viewModel.jobFieldSpinnerItem.value?.indexOf(viewModel.myJobField.value.toString())

            //성별
            /*view.radioGroupProfileGender.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, id ->
                when (id) {
                    R.id.radioButtonProfileGenderMan -> {//profile_gender = "M"
                        viewModel._isGenderMan.value = true
                    }
                    R.id.radioButtonProfileGenderWoman -> {//profile_gender = "W"
                        viewModel._isGenderMan.value = true
                    }
                }
            })*/

            //총경력
            view.spExperienceYears.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel._myExperienceIndex.value = position
                }
            }

            //근무지역
            view.spWorkingArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    //Log.d(TAG, "onItemSelected: p2 $position")
                    viewModel._myWorkingAreaIndex.value = position
                    //Log.d(TAG, "onItemSelected: ${viewModel.myWorkingAreaIndex.value}")

                    val area = view.spWorkingArea.selectedItem.toString()

                    viewModel._myWorkingArea.value = view.spWorkingArea.selectedItem.toString()
                    //Log.d(TAG, "onCreateView: $area")
                }
            }

            //근무지역 상세
            view.spWorkingAreaDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel._myWorkingAreaDetailIndex.value = position

                    viewModel._myWorkingAreaDetail.value = view.spWorkingAreaDetail.selectedItem.toString()
                }
            }

            //급여
            view.etBasicSalary.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (view.etBasicSalary.text.toString().isNotEmpty()) {
                        viewModel._myBasicSalary.value = Integer.parseInt(view.etBasicSalary.text.toString())
                    } else viewModel._myBasicSalary.value = null

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            //근속년수
            view.spLongevity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    viewModel._mylongevityIndex.value = position
                }
            }

            //git
            view.etGithubUrl.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel._myGithubUrl.value = view.etGithubUrl.text.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            //포폴
            view.etPortfolioUrl.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel._myPortfolioUrl.value = view.etPortfolioUrl.text.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            //정보공개여부
            view.cbIsInformationOpen.setOnCheckedChangeListener { compoundButton, checked ->
                viewModel._isInformationOpen.value = checked
            }

            //푸쉬알림동의
            view.cbIsPush.setOnCheckedChangeListener { compoundButton, checked ->
                viewModel._isPushStatus.value = checked
            }

        }

        //근무지역이 서울이면 근무상세는 null로 보낸다


        observeViewModel()


        //수정하기 버튼클릭
        view.btnModify.setOnClickListener {
            Log.d(TAG, "myNickname : ${viewModel.myNickname.value.toString()}")
            Log.d(TAG, "profileImageUrl : ${viewModel.profileImageUrl.value.toString()}")
            Log.d(TAG, "myBirthDay : ${viewModel.myBirthDay.value.toString()}")

            var myJobTypeCategoryIndex: Int? = null
            var myJobFieldCategoryIndex: Int? = null
            if (viewModel.myJobType.value != null) {
                for (type in viewModel.jobCategory.value!!) {
                    if (type.ko_name == viewModel.myJobType.value.toString()) {
                        myJobTypeCategoryIndex = type.id

                        for (field in type.job_field) {
                            if (field.ko_name == viewModel.myJobField.value.toString()) {
                                myJobFieldCategoryIndex = field.id
                            }
                        }
                    }
                }
            } else {
                myJobTypeCategoryIndex = null
                myJobFieldCategoryIndex = null
            }

            Log.d(TAG, "myJobType : ${viewModel.myJobType.value.toString()}")
            Log.d(TAG, "myJobTypeCategoryIndex : ${myJobTypeCategoryIndex.toString()}")

            Log.d(TAG, "myJobField : ${viewModel.myJobField.value.toString()}")
            Log.d(TAG, "myJobFieldCategoryIndex : ${myJobFieldCategoryIndex.toString()}")

            if (viewModel.isGenderMan.value!!) {
                viewModel._myGender.value = "M"
            } else if (viewModel.isGenderWoman.value!!) {
                viewModel._myGender.value = "W"
            } else if (viewModel.isGenderMan.value == null && viewModel.isGenderWoman.value == null) {
                viewModel._myGender.value = null
            }
            Log.d(TAG, "myGender : ${viewModel.myGender.value.toString()}")


            val myExperienceYear: Int? = if (viewModel.myExperienceIndex.value == 0) {
                null
            } else {
                viewModel.myExperienceIndex.value?.minus(1)
            }

            //Log.d(TAG, "myExperienceIndex : ${viewModel.myExperienceIndex.value}")
            Log.d(TAG, "myExperienceYear : ${myExperienceYear.toString()}")  //보낼값은 Int이지만, 값이 null일 수 있다.

            if (viewModel.myWorkingAreaIndex.value != 1) {
                //지역이 서울이 아니면, 상세는 null이다
                viewModel._myWorkingAreaDetailIndex.value = null
                viewModel._myWorkingAreaDetail.value = null

            }
            if (viewModel.myWorkingAreaIndex.value == 0) {
                viewModel._myWorkingArea.value = null
            }

            Log.d(TAG, "myWorkingArea : ${viewModel._myWorkingArea.value.toString()}")
            //Log.d(TAG, "myWorkingAreaIndex : ${viewModel.myWorkingAreaIndex.value}")

            //서운인데 구 가 null일 경우
            if (viewModel.myWorkingAreaIndex.value == 1 && viewModel.myWorkingAreaDetailIndex.value == null) {
                alert(R.string.working_area_detail_alert_str)
                view.svMypageEdit.scrollTo(0, view.tvJobField.bottom)  //스크롤 이동
                return@setOnClickListener
            }
            if (viewModel.myWorkingAreaDetailIndex.value == 0) {
                //지역상세 선택해주세요 경고창, 클릭 되돌리기
                alert(R.string.working_area_detail_alert_str)
                view.svMypageEdit.scrollTo(0, view.tvJobField.bottom)  //스크롤 이동
                return@setOnClickListener
            }
            Log.d(TAG, "myWorkingAreaDetail : ${viewModel.myWorkingAreaDetail.value.toString()}")
            //Log.d(TAG, "myWorkingAreaDetailIndex : ${viewModel.myWorkingAreaDetailIndex.value.toString()}")

            Log.d(TAG, "myBasicSalary : ${viewModel.myBasicSalary.value.toString()}")

            val mylongevity: Int? = if (viewModel.mylongevityIndex.value == 0) {
                null
            } else {
                viewModel.mylongevityIndex.value?.minus(1)
            }
            //Log.d(TAG, "mylongevityIndex : ${viewModel.mylongevityIndex.value}")
            Log.d(TAG, "mylongevity : ${mylongevity.toString()}")

            Log.d(TAG, "myGithubUrl : ${viewModel.myGithubUrl.value.toString()}")
            Log.d(TAG, "myPortfolioUrl : ${viewModel.myPortfolioUrl.value.toString()}")


            val infomation: Int = if (viewModel.isInformationOpen.value!!) {
                1
            } else {
                0
            }
            Log.d(TAG, "infomation : $infomation")

            val push: Int = if (viewModel.isPushStatus.value!!) {
                1
            } else {
                0
            }
            Log.d(TAG, "push : $push")

            Log.d(TAG, "deviceOs : ${viewModel.deviceOs}")

            Log.d(TAG, "languages: ${viewModel.languages.value}")

            if (!viewModel.languages.value.isNullOrEmpty()) {
                viewModel.getLanguageList()
            }
            if (!viewModel.ides.value.isNullOrEmpty()) {
                viewModel.getIdeList()
            }
            val myLanguages = viewModel.requestLanguageList()
            val myIdes = viewModel.requestIdeList()

            Log.d(TAG, "onCreateView, myLanguages: $myLanguages")
            Log.d(TAG, "onCreateView, myIdes: $myIdes")


            val modifyUserInfoRequest = ModifyUserInfoRequest(
                viewModel.myNickname.value.toString(),
                viewModel.profileImageUrl.value,
                viewModel.myBirthDay.value,
                myJobTypeCategoryIndex,
                myJobFieldCategoryIndex,
                viewModel.myGender.value,
                myExperienceYear,
                viewModel._myWorkingArea.value,
                viewModel.myWorkingAreaDetail.value,
                viewModel.myBasicSalary.value,
                mylongevity,
                myLanguages,
                myIdes,
                viewModel.myGithubUrl.value,
                viewModel.myPortfolioUrl.value,
                infomation,
                push,
                viewModel.deviceOs,
                0
            )
            Log.d(TAG, "onCreateView,modifyUserInfoRequest : $modifyUserInfoRequest ")

            viewModel.modifyUserInfo(modifyUserInfoRequest)

        }

        view.btnCloseToolbar.setOnClickListener {
            requireActivity().finish()
        }





        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")

    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: ")
    }


    private fun observeViewModel() {
        observeNicknamePossible()
        observePickImage()
        observeFinishActivity()
        observeAlert()
    }

    private fun showDialog() {
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

    //닉네임중복채크
    private fun observeNicknamePossible() {
        viewModel.isNicknamePossible.observe(viewLifecycleOwner, androidx.lifecycle.Observer { isValidate ->
            if (view?.etNickname?.text.toString() != viewModel.userProfile.value?.profile_nickname.toString()) {
                viewModel._myNickname.value = view?.etNickname?.text.toString()
                //Log.d(TAG, "observeNicknamePossible,viewModel._profileNickname.value: ${viewModel._myNickname.value.toString()}")
                if (isValidate!!) {
                    viewModel._myNickname.value = view?.etNickname?.text.toString()
                    view?.tvNicknameValidate?.text = "사용 가능한 닉네임 입니다"
                    view?.tvNicknameValidate?.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_delete))
                    view?.tvNicknameValidate?.visibility = View.VISIBLE
                    //tvNicknameInfo.visibility = View.GONE
                    isNicknameRegexp = true

                } else {
                    view?.tvNicknameValidate?.text = "이미 사용중인 닉네임 입니다."
                    view?.tvNicknameValidate?.visibility = View.VISIBLE
                    view?.tvNicknameValidate?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))

                    isNicknameRegexp = false
                }
            }

        })
    }

    private fun observeFinishActivity() {
        viewModel.finishActivity.observe(viewLifecycleOwner, androidx.lifecycle.Observer { isFinish ->
            if (isFinish) {
                requireActivity().finish()
            }
        })
    }

    private fun observeAlert() {
        viewModel.alertMessage.observe(viewLifecycleOwner, androidx.lifecycle.Observer { message ->
            if (message.isNotBlank()) {
                alertText(message)
                viewModel._alertMessage.value = ""
            }
        })
    }

    private fun alertText(message: String) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(message)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }


    fun alert(strId: Int) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }


    fun openIdeSearch(type: String) {
        when (type) {
            "language" -> {
                navController!!.navigate(R.id.action_mypageEditFragment_to_languageSearchFragment)

            }
            "ide" -> {
                navController!!.navigate(R.id.action_mypageEditFragment_to_ideSearchFragment)
            }
        }
    }


    private fun getFlexBoxLayoutManager(): FlexboxLayoutManager {
        return FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val currentTime = cal.timeInMillis // 현재 시간
        DatePickerDialog(
            requireContext(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)

                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.KOREA)

                view?.etBirthday?.text = sdf.format(cal.time)
                viewModel._myBirthDay.value = view?.etBirthday?.text.toString()

            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DATE)
        ).apply {
            //오늘 이후 날짜는 선택할 수 없다
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
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
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
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
                //카메라로찍을것인지, 갤러리 선택인지
                /*var alertDialog = AlertDialog.Builder(requireContext())
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
        view?.svMypageEdit?.visibility = View.GONE
        startCamera()
    }


    //카메라 사진 퇄영 후 저장된 이미지 존재 여부
    //촬영한 이미지가 저장되었다면 실행한 카메라 종료
    fun observeExistImage() {
        viewModel.isExistImage.observe(this, androidx.lifecycle.Observer { exist ->
            if (exist) {
                cameraExecutor.shutdown()
                view?.cameraView?.visibility = View.GONE
                view?.svMypageEdit?.visibility = View.VISIBLE
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