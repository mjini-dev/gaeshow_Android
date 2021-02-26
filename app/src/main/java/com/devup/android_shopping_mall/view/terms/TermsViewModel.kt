package com.devup.android_shopping_mall.view.terms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.deviceInfo.repository.DeviceInfoRepository
import com.devup.android_shopping_mall.data.user.model.*
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.network.NetworkCheck

open class TermsViewModel(
    private val userRepository: UserRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    private val TAG = "TermsViewModel"

    private val _terms = MutableLiveData<Terms>()
    val terms: LiveData<Terms> = _terms

    private val _isLoadClear = MutableLiveData<Boolean>().apply { value = false }
    val isLoadClear: LiveData<Boolean> = _isLoadClear

    // TermsAgree Buttons
    private val _allChecked = MutableLiveData<Boolean>().apply { value = false }
    val allChecked: LiveData<Boolean> = _allChecked
    private val _firstChecked = MutableLiveData<Boolean>().apply { value = false }
    val firstChecked: LiveData<Boolean> = _firstChecked
    private val _secondChecked = MutableLiveData<Boolean>().apply { value = false }
    val secondChecked: LiveData<Boolean> = _secondChecked
    private val _thirdChecked = MutableLiveData<Boolean>().apply { value = false }
    val thirdChecked: LiveData<Boolean> = _thirdChecked
    private val _fourthChecked = MutableLiveData<Boolean>().apply { value = false }
    val fourthChecked: LiveData<Boolean> = _fourthChecked

    private val _openStep01Activity = MutableLiveData<Boolean>().apply { value = false }
    val openStep01Activity: LiveData<Boolean> = _openStep01Activity

    private val _openMypageEditActivity = MutableLiveData<Boolean>().apply { value = false }
    val openMypageEditActivity: LiveData<Boolean> = _openMypageEditActivity

    private val _errorDialogMessage = MutableLiveData<String>().apply { value = "" }
    val errorDialogMessage: LiveData<String> = _errorDialogMessage

    private lateinit var simpleLoginUserProfile: SimpleLoginUserProfile

    init {
        getTerms()
    }

    private fun getTerms() {
        userRepository.getTerms(
            onSuccess = { response ->
                _terms.value = response
                Log.d(TAG, "getTerms,terms.value : ${terms.value}")
                _isLoadClear.value = true

            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of getTerms $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of getTerms $failureError")

            }
        )
    }


    fun onAllCheckButtonChanged() {
        if (allChecked.value == false) {
            _allChecked.value = true
            _firstChecked.value = true
            _secondChecked.value = true
            _thirdChecked.value = true
            _fourthChecked.value = true
        } else {
            _allChecked.value = false
            _firstChecked.value = false
            _secondChecked.value = false
            _thirdChecked.value = false
            _fourthChecked.value = false
        }
    }

    fun onCheckButtonChanged(buttonIndex: Int) {
        when (buttonIndex) {
            1 -> {
                _firstChecked.value = firstChecked.value != true
            }
            2 -> {
                _secondChecked.value = secondChecked.value != true
            }
            3 -> {
                _thirdChecked.value = thirdChecked.value != true
            }
            4 -> {
                _fourthChecked.value = fourthChecked.value != true
            }
        }

        _allChecked.value =
            firstChecked.value == true
                    && secondChecked.value == true
                    && thirdChecked.value == true
                    && fourthChecked.value == true
    }


    fun completeTermsAgree(type: String) {
        if (firstChecked.value == true && secondChecked.value == true) {
            Log.d(TAG, "completeTermsAgree: 타입확인 $type")
            when (type) {
                "kakao" -> {
                    //카카오유저정보 가져오기)로 회원가입 진행
                    join(type)
                }
                "gmail" -> {
                    Log.d(TAG, "onCreate: 카카오다")
                    //카카오유저정보 가져오기)로 회원가입 진행
                    join(type)
                }
                else -> {
                    startStep01Activity()
                }
            }
        } else {
            _errorDialogMessage.apply {
                value = "필수 약관 동의가 필요합니다."
                value = ""
            }
        }
    }

    private fun getSimpleLoginUserProfile() {
        userRepository.getSimpleLoginUserProfile(
            onProfileExist = { response ->
                simpleLoginUserProfile = response
            },
            onProfileNotExist = {
                Log.d(TAG, "getKakaoUserProfile: 카카오유저정보 가져오기 실패 ")

            }
        )
    }

    private fun joinRequest(type: String): JoinRequest {
        getSimpleLoginUserProfile()

        val deviceInfo = deviceInfoRepository.getDeviceInfo()
        return JoinRequest(
            type,
            simpleLoginUserProfile.profile_nickname,
            0,
            0,
            simpleLoginUserProfile.unique_id,
            null,
            null,
            simpleLoginUserProfile.profile_image_url,
            deviceInfo.getDeviceOs()
        )
    }

    fun join(type: String) {
        userRepository.join(joinRequest(type),
            onSuccess = { response ->
                Log.d(TAG, "join: 가입성공")
                //회원가입 성공하면, 저장한 카카오유저정보 석제,
                deleteProfile()
                deleteToken()
                login(type, response.signin_token)
            },
            notSuccessStatus = {
                Log.d(TAG, "join: 가입실패")

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of join $failureError")
            }
        )
    }

    fun login(type: String, signin_token: String) {
        Log.d(TAG, "login")
        userRepository.login(loginRequest(type, signin_token),
            onSuccess = { response ->
                //로그인 성공시 Access토큰 저장,Activity 종료
                NetworkCheck.setAccessToken(response.access_token)
                saveAccessToken(response.access_token)
                saveUserProfile(response.user)
                startMypageEditActivity()
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loginError $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loginError $failureError")
            })
    }

    private fun loginRequest(type: String, signin_token: String): LoginRequest {
        val deviceInfo = deviceInfoRepository.getDeviceInfo()
        return LoginRequest(
            type,
            deviceInfo.getDeviceOs(),
            signin_token,
            deviceInfo.getAppVersion(),
            deviceInfo.getDeviceId(),
            deviceInfo.getDeviceModel(),
            simpleLoginUserProfile.unique_id,
            null,
            null
        )
    }

    private fun deleteToken() {
        tokenRepository.deleteToken()
    }

    private fun deleteProfile() {
        userRepository.deleteProfile()
    }

    private fun saveAccessToken(token: String) {
        tokenRepository.saveAccessToken(AccessToken(token))
    }

    private fun saveUserProfile(user: User) {
        userRepository.saveUserProfile(user)
    }

    private fun startStep01Activity() {
        _openStep01Activity.apply {
            value = true
            value = false
        }
    }

    private fun startMypageEditActivity() {
        _openMypageEditActivity.apply {
            value = true
            value = false
        }
    }


}