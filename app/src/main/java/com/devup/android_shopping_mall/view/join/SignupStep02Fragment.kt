package com.devup.android_shopping_mall.view.join

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.user.model.ModifyUserInfoRequest
import com.devup.android_shopping_mall.databinding.FragmentSignupStep02Binding
import com.devup.android_shopping_mall.view.mypage.MypageEditLanguageIdeAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_signup_step02.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class SignupStep02Fragment : Fragment() {
    private val TAG = "SignupStep02Fragment"
    private val viewModel: SignupViewModel by sharedViewModel()

    lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentSignupStep02Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_step02, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@SignupStep02Fragment
            viewModel.loadProfile()
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

        //생년월일등록
        view.etBirthday.setOnClickListener {
            showDatePicker()
        }

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
        viewModel._myJobFieldIndex.value = viewModel.jobFieldSpinnerItem.value?.indexOf(viewModel.myJobField.value.toString())

        //성별
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

        //수정하기 버튼클릭
        view.btnModify.setOnClickListener {
            val myNickname = viewModel.userProfile.value?.profile_nickname.toString()
            val profileImageUrl = viewModel.userProfile.value?.profile_image_url

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


            /*if (viewModel.isGenderMan.value != null) {
                if (viewModel.isGenderMan.value as Boolean) {
                    viewModel._myGender.value = "M"
                } else {
                    viewModel._myGender.value = "W"
                }
            }*/

            if (viewModel.isGenderMan.value!!) {
                viewModel._myGender.value = "M"
            } else if (viewModel.isGenderWoman.value!!){
                viewModel._myGender.value = "W"
            } else if (viewModel.isGenderMan.value==null && viewModel.isGenderWoman.value==null) {
                viewModel._myGender.value = null
            }


            val myExperienceYear: Int? = if (viewModel.myExperienceIndex.value == 0) {
                null
            } else {
                viewModel.myExperienceIndex.value?.minus(1)
            }


            if (viewModel.myWorkingAreaIndex.value != 1) {
                //지역이 서울이 아니면, 상세는 null이다
                viewModel._myWorkingAreaDetailIndex.value = null
                viewModel._myWorkingAreaDetail.value = null

            }
            if (viewModel.myWorkingAreaIndex.value == 0) {
                viewModel._myWorkingArea.value = null
            }


            //서운인데 구 가 null일 경우
            if (viewModel.myWorkingAreaIndex.value == 1 && viewModel.myWorkingAreaDetailIndex.value == null) {
                alert(R.string.working_area_detail_alert_str)
                view.moreInfo.scrollTo(0, view.tvJobField.bottom)  //스크롤 이동
                return@setOnClickListener
            }
            if (viewModel.myWorkingAreaDetailIndex.value == 0) {
                //지역상세 선택해주세요 경고창, 클릭 되돌리기
                alert(R.string.working_area_detail_alert_str)
                view.moreInfo.scrollTo(0, view.tvJobField.bottom)  //스크롤 이동
                return@setOnClickListener
            }

            val myLongevity: Int? = if (viewModel.mylongevityIndex.value == 0) {
                null
            } else {
                viewModel.mylongevityIndex.value?.minus(1)
            }

            val infomation: Int = if (viewModel.isInformationOpen.value!!) {
                1
            } else {
                0
            }

            val push: Int = if (viewModel.isPushStatus.value!!) {
                1
            } else {
                0
            }

            if (!viewModel.languages.value.isNullOrEmpty()) {
                viewModel.getLanguageList()
            }
            if (!viewModel.ides.value.isNullOrEmpty()) {
                viewModel.getIdeList()
            }
            val myLanguages = viewModel.requestLanguageList()
            val myIdes = viewModel.requestIdeList()


            val modifyUserInfoRequest = ModifyUserInfoRequest(
                myNickname,
                viewModel.profileImageUrl.value,
                viewModel.myBirthDay.value,
                myJobTypeCategoryIndex,
                myJobFieldCategoryIndex,
                viewModel.myGender.value,
                myExperienceYear,
                viewModel._myWorkingArea.value,
                viewModel.myWorkingAreaDetail.value,
                viewModel.myBasicSalary.value,
                myLongevity,
                myLanguages,
                myIdes,
                viewModel.myGithubUrl.value,
                viewModel.myPortfolioUrl.value,
                infomation,
                push,
                viewModel.deviceOs,
                0
            )

            Log.d(TAG, "onCreateView, modifyUserInfoRequest : $modifyUserInfoRequest ")

            viewModel.modifyUserInfo(modifyUserInfoRequest)

        }


        observeFinishActivity()
        observeAlert()
        return view
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
                navController!!.navigate(R.id.action_signupStep02Fragment_to_signupLanguageSearchFragment)

            }
            "ide" -> {
                navController!!.navigate(R.id.action_signupStep02Fragment_to_signupIdeSearchFragment)
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


}