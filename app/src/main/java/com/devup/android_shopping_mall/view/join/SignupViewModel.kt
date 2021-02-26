package com.devup.android_shopping_mall.view.join

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.categories.model.CategoriesRequest
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepository
import com.devup.android_shopping_mall.data.deviceInfo.repository.DeviceInfoRepository
import com.devup.android_shopping_mall.data.post.source.repository.PostRepository
import com.devup.android_shopping_mall.data.user.model.*
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.network.NetworkCheck
import com.devup.android_shopping_mall.util.CANNOT_USE_NICKNAME
import com.devup.android_shopping_mall.util.CAN_USE_NICKNAME
import com.devup.android_shopping_mall.util.SingleLiveEvent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class SignupViewModel(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val tokenRepository: TokenRepository,
    private val categoriesRepository: CategoriesRepository,
    private val deviceInfoRepository: DeviceInfoRepository
) : ViewModel() {
    private val TAG = "SignupViewModel"

    private val _isJoinPossible = MutableLiveData<Boolean>()
    val isJoinPossible: LiveData<Boolean> = _isJoinPossible

    private val _isNicknamePossible = MutableLiveData<Boolean>()
    val isNicknamePossible: LiveData<Boolean> = _isNicknamePossible

    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    private val _isExistImage = MutableLiveData<Boolean>().apply { value = false }
    val isExistImage: LiveData<Boolean> = _isExistImage

    private val _openDetailActivity = MutableLiveData<Boolean>().apply { value = false }
    val openDetailActivity: LiveData<Boolean> = _openDetailActivity

    lateinit var imageUri: Uri
    private val _pickImage = SingleLiveEvent<Unit>()
    val pickImage: LiveData<Unit> get() = _pickImage

    //회원정보 추가
    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> = _userProfile

    //생년월일
    var _myBirthDay = MutableLiveData<String>()
    val myBirthDay: LiveData<String> = _myBirthDay

    //직종, 업무분야
    private val _jobCategory = MutableLiveData<List<Category>>()
    val jobCategory: LiveData<List<Category>> = _jobCategory

    private val _jobTypeSpinnerItem = MutableLiveData<List<String>>()
    val jobTypeSpinnerItem: LiveData<List<String>> = _jobTypeSpinnerItem

    val _jobFieldSpinnerItem = MutableLiveData<List<String>>()
    val jobFieldSpinnerItem: LiveData<List<String>> = _jobFieldSpinnerItem

    val _jobFieldSpinnerItemEn = MutableLiveData<List<String>>()
    val jobFieldSpinnerItemEn: LiveData<List<String>> = _jobFieldSpinnerItemEn

    var myJobType = MutableLiveData<String>()
    var _myJobTypeIndex = MutableLiveData<Int>().apply { value = 0 }
    val myJobTypeIndex: LiveData<Int> = _myJobTypeIndex
    var myJobTypeCategoryIndex = MutableLiveData<Int?>()

    var myJobField = MutableLiveData<String>()
    var _myJobFieldIndex = MutableLiveData<Int>().apply { value = 0 }
    val myJobFieldIndex: LiveData<Int> = _myJobFieldIndex
    var myJobFieldCategoryIndex = MutableLiveData<Int?>()

    //성별
    var _isGenderMan = MutableLiveData<Boolean?>().apply { value = null }
    val isGenderMan: LiveData<Boolean?> = _isGenderMan

    var _isGenderWoman = MutableLiveData<Boolean?>().apply { value = null }
    val isGenderWoman: LiveData<Boolean?> = _isGenderWoman

    var _myGender = MutableLiveData<String?>().apply { value = null }
    var myGender: LiveData<String?> = _myGender

    private var page = 1
    private val limit = 10

    //언어
    private val _languageCategory = MutableLiveData<List<Category>>()
    val languageCategory: LiveData<List<Category>> = _languageCategory

    var _languages = MutableLiveData<List<String?>>()
    val languages: LiveData<List<String?>> = _languages

    var _languagesSearchResult = MutableLiveData<List<String?>>()
    val languagesSearchResult: LiveData<List<String?>> = _languagesSearchResult

    private val _languagesExist = MutableLiveData<List<String?>>()
    val languagesExist: LiveData<List<String?>> = _languagesExist

    private val _existLanguagesCategoryId = MutableLiveData<List<Int?>>()
    val existLanguagesCategoryId: LiveData<List<Int?>> = _existLanguagesCategoryId

    private val _languagesAdded = MutableLiveData<List<String?>>()
    val languagesAdded: LiveData<List<String?>> = _languagesAdded

    //ide
    private val _ideCategory = MutableLiveData<List<Category>>()
    val ideCategory: LiveData<List<Category>> = _ideCategory

    var _ides = MutableLiveData<List<String?>>()
    val ides: LiveData<List<String?>> = _ides

    var _idesSearchResult = MutableLiveData<List<String?>>()
    val idesSearchResult: LiveData<List<String?>> = _idesSearchResult

    private val _existIdesCategoryId = MutableLiveData<List<Int?>>()
    val existIdesCategoryId: LiveData<List<Int?>> = _existIdesCategoryId

    private val _idesExist = MutableLiveData<List<String?>>()
    val idesExist: LiveData<List<String?>> = _idesExist

    private val _idesAdded = MutableLiveData<List<String?>>()
    val idesAdded: LiveData<List<String?>> = _idesAdded

    //총경력
    var _myExperienceIndex = MutableLiveData<Int?>()
    val myExperienceIndex: LiveData<Int?> = _myExperienceIndex

    //근무지역
    var _myWorkingArea = MutableLiveData<String?>()
    val myWorkingArea: LiveData<String?> = _myWorkingArea
    var _myWorkingAreaIndex = MutableLiveData<Int?>()
    val myWorkingAreaIndex: LiveData<Int?> = _myWorkingAreaIndex

    //지역상세
    var _myWorkingAreaDetail = MutableLiveData<String?>()
    val myWorkingAreaDetail: LiveData<String?> = _myWorkingAreaDetail
    var _myWorkingAreaDetailIndex = MutableLiveData<Int?>()
    val myWorkingAreaDetailIndex: LiveData<Int?> = _myWorkingAreaDetailIndex

    //급여정보
    var _myBasicSalary = MutableLiveData<Int?>()
    val myBasicSalary: LiveData<Int?> = _myBasicSalary

    //근속
    var _mylongevityIndex = MutableLiveData<Int?>()
    val mylongevityIndex: LiveData<Int?> = _mylongevityIndex

    //github_url
    var _myGithubUrl = MutableLiveData<String?>()
    val myGithubUrl: LiveData<String?> = _myGithubUrl

    //portfolio_url
    var _myPortfolioUrl = MutableLiveData<String?>()
    val myPortfolioUrl: LiveData<String?> = _myPortfolioUrl


    var _isInformationOpen = MutableLiveData<Boolean>().apply { value = false }
    val isInformationOpen: LiveData<Boolean> = _isInformationOpen

    var _isPushStatus = MutableLiveData<Boolean>().apply { value = false }
    val isPushStatus: LiveData<Boolean> = _isPushStatus

    val deviceOs = deviceInfoRepository.getDeviceInfo().getDeviceOs()

    private val _finishActivity = MutableLiveData<Boolean>().apply { value = false }
    val finishActivity: LiveData<Boolean> = _finishActivity

    val _alertMessage = MutableLiveData<String>().apply { value = "" }
    val alertMessage: LiveData<String> = _alertMessage

    val _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage: LiveData<String> = _toastMessage

    fun pickImage() {
        _pickImage.call()
    }

    //-----가입여부확인
    fun checkUser(email: String) {
        userRepository.checkUser(
            CheckUserRequest("email", null, email),
            onSuccess = { response ->
                when (response.message) {
                    "you_can_join" -> {
                        _isJoinPossible.value = true
                    }
                    "you_can_login" -> {
                        _isJoinPossible.value = false
                    }
                    else -> {
                        //가입여부확인 실패
                        Log.d(TAG, "checkUserError of onSuccess ${response.message}")
                    }
                }
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of checkUserError $notSuccessStatus")

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of checkUserError $failureError")
                //Toast.makeText(this, NetworkCheck.getErrorMessage(failureError), Toast.LENGTH_LONG).show()
            }
        )
    }

    //닉네임 중복체크
    fun checkNickname(profile_nickname: String) {
        userRepository.checkNickname(
            CheckNicknameRequest(profile_nickname),
            onSuccess = {
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of checkUserError $notSuccessStatus")
                when (notSuccessStatus) {
                    CAN_USE_NICKNAME -> {
                        //사용가능 닉네임
                        _isNicknamePossible.value = true
                        Log.d(TAG, "checkNickname: 사용가능? ${isNicknamePossible.value}")
                    }

                    CANNOT_USE_NICKNAME -> {
                        //사용불가능 닉네임
                        _isNicknamePossible.value = false
                        Log.d(TAG, "checkNickname: $notSuccessStatus ")
                        Log.d(TAG, "checkNickname: 사용불가 ${isNicknamePossible.value}")
                    }

                    else -> Log.d(TAG, "checkNickname: $notSuccessStatus ")

                }

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of checkUserError $failureError")
                //Toast.makeText(this, NetworkCheck.getErrorMessage(failureError), Toast.LENGTH_LONG).show()
            }
        )
    }

    //프로필이미지 업로드
    fun upLoadImage(file: File) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("files", file.name, requestFile)

        //val request: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),"user")
        val request: RequestBody = "user".toRequestBody("text/plain".toMediaTypeOrNull())

        postRepository.uploadFile(request, part,
            onSuccess = { response ->
                //업로드 성공
                Log.d(TAG, "upLoadImage, response: $response")
                _profileImageUrl.value = response[0].file_path
                _isExistImage.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "upLoadImage,notSuccessStatus: $notSuccessStatus")

            },
            onFailure = { failureError ->
                Log.d(TAG, "upLoadImage, 파일크기: ${file.length()}Bytes")
                Log.d(TAG, "upLoadImage, failureError: $failureError")

            }
        )
    }

    fun join(nickname: String, email: String?, password: String?, profile_image_url: String?) {
        userRepository.join(joinRequest(nickname, email, password, profile_image_url),
            onSuccess = { response ->
                login(response.signin_token, email, password)
            },
            notSuccessStatus = {

            },
            onFailure = {

            }
        )
    }

    fun joinRequest(nickname: String, email: String?, password: String?, profile_image_url: String?): JoinRequest {
        val deviceInfo = deviceInfoRepository.getDeviceInfo()
        val joinRequest = JoinRequest(
            "email", nickname, 0, 0, null, email, password, profile_image_url, deviceInfo.getDeviceOs()
        )
        return joinRequest
    }

    fun login(signin_token: String, email: String?, password: String?) {
        Log.d(TAG, "login")
        userRepository.login(loginRequest(signin_token, email, password),
            onSuccess = { response ->
                //로그인 성공시 Access토큰 저장,Activity 종료
                NetworkCheck.setAccessToken(response.access_token)
                saveAccessToken(response.access_token)
                getUserInfo()
                startDetailActivity()
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loginError $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loginError $failureError")
            })
    }

    fun loginRequest(signin_token: String, email: String?, password: String?): LoginRequest {
        val deviceInfo = deviceInfoRepository.getDeviceInfo()
        val loginRequest = LoginRequest(
            "email",
            deviceInfo.getDeviceOs(),
            signin_token,
            deviceInfo.getAppVersion(),
            deviceInfo.getDeviceId(),
            deviceInfo.getDeviceModel(),
            null,
            email,
            password
        )
        return loginRequest
    }

    fun getUserInfo() {
        Log.d(TAG, "getUserInfo: ")
        userRepository.getUserInfo(
            onSuccess = { response ->
                saveUserProfile(response)
                Log.d(TAG, "getUserInfo: response $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "getUserInfo,getUserInfo : $notSuccessStatus ")
            },
            onFailure = { failureError -> Log.d(TAG, "getUserInfo,getUserInfo: $failureError") })
    }

    private fun getUserInfoFinish() {
        Log.d(TAG, "getUserInfo: ")
        userRepository.getUserInfo(
            onSuccess = { response ->
                saveUserProfileFinish(response)
                Log.d(TAG, "getUserInfo: response $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "getUserInfo,getUserInfo : $notSuccessStatus ")
            },
            onFailure = { failureError -> Log.d(TAG, "getUserInfo,getUserInfo: $failureError") })

    }

    fun loadProfile() {
        userRepository.getUserProfile(
            onProfileExist = { userProfile ->
                _userProfile.value = userProfile
                Log.d(TAG, "loadProfile: $userProfile")
                _profileImageUrl.value = userProfile.profile_image_url

            },
            onProfileNotExist = {
                Log.d(TAG, "loadProfile: onProfileNotExist")
            }
        )
    }

    fun getJobTypeList() {
        //clearJobItem()
        categoriesRepository.getJobCategories(
            onSuccess = { jobCategories ->
                _jobCategory.value = jobCategories
                _jobTypeSpinnerItem.value = mutableListOf("재직하고 계신 직종을 선택해 주세요")
                _jobFieldSpinnerItem.value = mutableListOf("업무분야를 선택해 주세요")
                for (sp_item in jobCategories) {
                    _jobTypeSpinnerItem.value = jobTypeSpinnerItem.value?.plus(sp_item.ko_name) ?: mutableListOf(sp_item.ko_name)
                    if (sp_item.ko_name == myJobType.value.toString()) {
                        for (jobField in sp_item.job_field) {
                            _jobFieldSpinnerItem.value = jobFieldSpinnerItem.value?.plus(jobField.ko_name) ?: mutableListOf(jobField.ko_name)
                            _jobFieldSpinnerItemEn.value = jobFieldSpinnerItemEn.value?.plus(jobField.en_name) ?: mutableListOf(jobField.en_name)
                        }

                       // _myJobFieldIndex.value = jobFieldSpinnerItem.value?.indexOf(myJobField.value.toString())
                    }
                }
            },
            onFailure = {
                Log.d(TAG, "getJobTypeList: onFailure")
            })
    }

    fun clearJobItem() {
        _jobTypeSpinnerItem.value = null
        _jobFieldSpinnerItem.value = null
        _jobFieldSpinnerItemEn.value = null
    }

    fun deleteResult(type: String, typeResult: String, str: String) {
        when (type) {
            "lang" -> {
                when (typeResult) {
                    "searchResult" -> {
                        _languagesSearchResult.value = languagesSearchResult.value?.filterNot { it == str }
                        //Log.d(TAG, "deleteResult,languagesSearchResult.value: ${languagesSearchResult.value}")
                    }
                    "choice" -> {
                        _languages.value = _languages.value?.filterNot { it == str }
                        //Log.d(TAG, "deleteResult,_languages.value: ${_languages.value}")
                    }
                }
            }

            "ide" -> {
                when (typeResult) {
                    "searchResult" -> {
                        _idesSearchResult.value = idesSearchResult.value?.filterNot { it == str }
                        //Log.d(TAG, "deleteResult,idesSearchResult.value: ${idesSearchResult.value}")
                    }
                    "choice" -> {
                        _ides.value = _ides.value?.filterNot { it == str }
                        //Log.d(TAG, "deleteResult,_ides.value: ${_ides.value}")
                    }
                }
            }
        }
    }

    fun getLanguageList() {
        _languagesExist.value = null
        categoriesRepository.getLanguageCategories(
            onSuccess = { languageCategories ->
                _languageCategory.value = languageCategories
                for (lang in languageCategories) {
                    for (myLang in languages.value!!) {
                        if (myLang == lang.en_name) {
                            _languagesExist.value = languagesExist.value?.plus(myLang) ?: mutableListOf(myLang)
                            _existLanguagesCategoryId.value = existLanguagesCategoryId.value?.plus(lang.id) ?: mutableListOf(lang.id)
                        }
                    }
                }
                for (myLang in languages.value!!) {
                    _languagesAdded.value = languagesAdded.value?.plus(myLang) ?: mutableListOf(myLang)
                }

                if (!languagesExist.value.isNullOrEmpty()) {
                    for (exist in languagesExist.value!!) {
                        _languagesAdded.value = languagesAdded.value?.filterNot { it == exist }
                    }
                }
            },
            onFailure = {
                Log.d(TAG, "getLanguageList: onFailure")
            })
    }

    fun getIdeList() {
        _idesExist.value = null
        categoriesRepository.getIdeCategories(
            onSuccess = { ideCategories ->
                _ideCategory.value = ideCategories
                for (ide in ideCategories) {
                    for (myIde in ides.value!!) {
                        if (myIde == ide.en_name) {
                            _idesExist.value = idesExist.value?.plus(myIde) ?: mutableListOf(myIde)
                            _existIdesCategoryId.value = existIdesCategoryId.value?.plus(ide.id) ?: mutableListOf(ide.id)
                        }
                    }
                }
                for (myIde in ides.value!!) {
                    _idesAdded.value = idesAdded.value?.plus(myIde) ?: mutableListOf(myIde)
                }

                if (!idesExist.value.isNullOrEmpty()) {
                    for (exist in idesExist.value!!) {
                        _idesAdded.value = idesAdded.value?.filterNot { it == exist }
                    }
                }
            },
            onFailure = {
                Log.d(TAG, "getIdeList: onFailure")
            })
    }

    private fun categoriesRequest(type: String, search: String): CategoriesRequest {
        return CategoriesRequest(
            type, search, page, limit
        )
    }

    fun getLanguageIdeCategories(type: String, search: String) {
        categoriesRepository.getCategoriesSearch(categoriesRequest(type, search),
            onSuccess = { response ->
                //_languagesSearchResult.value = languagesSearchResult.value?.plus(response.categories) ?: response.categories
                for (it in response.categories) {
                    when (type) {
                        "language" -> {
                            _languagesSearchResult.value = languagesSearchResult.value?.plus(it.en_name) ?: listOf(it.en_name)

                            //이미 선택된 항목은 검색결과에서 뺀다다
                            if (!_languages.value.isNullOrEmpty()) {
                                for (str in _languages.value!!) {
                                    _languagesSearchResult.value = languagesSearchResult.value?.filterNot { it == str }
                                }
                            }
                        }

                        "ide" -> {
                            _idesSearchResult.value = idesSearchResult.value?.plus(it.en_name) ?: listOf(it.en_name)

                            //이미 선택된 항목은 검색결과에서 뺀다다
                            if (!_ides.value.isNullOrEmpty()) {
                                for (str in _ides.value!!) {
                                    _idesSearchResult.value = idesSearchResult.value?.filterNot { it == str }
                                }
                            }
                        }
                    }
                }
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of getLanguageCategoriesError $notSuccessStatus")
                _toastMessage.value = "검색결과가 없습니다.\n직접입력해 주세요."
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of getLanguageCategoriesError $failureError")
            }
        )
    }

    fun addChoice(type: String, str: String) {
        when (type) {
            "lang" -> {
                _languages.value = languages.value?.plus(str) ?: listOf(str)
                //Log.d(TAG, "addChoice,languages.value: ${languages.value}")
            }
            "ide" -> {
                _ides.value = ides.value?.plus(str) ?: listOf(str)
                //Log.d(TAG, "addChoice,languages.value: ${ides.value}")
            }
        }
    }

    fun requestLanguageList(): List<UserLanguageIde> {
        return mutableListOf<UserLanguageIde>().apply {
            if (!existLanguagesCategoryId.value.isNullOrEmpty()) {
                _existLanguagesCategoryId.value = existLanguagesCategoryId.value?.distinct()
                for (exist in existLanguagesCategoryId.value ?: listOf()) {
                    this.add(UserLanguageIde("existing", exist, null, null))
                }
            }
            if (!languagesAdded.value.isNullOrEmpty()) {
                for (added in languagesAdded.value ?: listOf()) {
                    this.add(UserLanguageIde("added", null, null, added))
                }
            }
        }
    }

    fun requestIdeList(): List<UserLanguageIde> {
        return mutableListOf<UserLanguageIde>().apply {
            if (!existIdesCategoryId.value.isNullOrEmpty()) {
                for (exist in existIdesCategoryId.value ?: listOf()) {
                    this.add(UserLanguageIde("existing", null, exist, null))
                }
            }
            if (!idesAdded.value.isNullOrEmpty()) {
                for (added in idesAdded.value ?: listOf()) {
                    this.add(UserLanguageIde("added", null, null, added))
                }
            }
        }
    }

    fun modifyUserInfo(request: ModifyUserInfoRequest) {
        userRepository.modifyUserInfo(request,
            onSuccess = {
                //로컬에 저장할 사용자 정보 갱신
                getUserInfoFinish()
                //현재프래그 먼트 & 액티비티 종료..

            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    CANNOT_USE_NICKNAME -> {
                        //사용불가능 닉네임
                        _alertMessage.value = "이미 사용중인 닉네임 입니다."
                        Log.d(TAG, "modifyUserInfo,CANNOT_USE_NICKNAME: $notSuccessStatus ")
                    }
                    else -> Log.d(TAG, "modifyUserInfo: $notSuccessStatus ")
                }

            },
            onFailure = {

            })

    }



    private fun saveAccessToken(token: String) {
        tokenRepository.saveAccessToken(AccessToken(token))
    }

    private fun saveUserProfile(user: User) {
        userRepository.saveUserProfile(user)
    }

    private fun saveUserProfileFinish(user: User) {
        userRepository.saveUserProfile(user)
        finishActivity()
    }

    private fun finishActivity() {
        _finishActivity.value = true
    }

    private fun startDetailActivity() {
        _openDetailActivity.value = true
    }


}