package com.devup.android_shopping_mall.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepository
import com.devup.android_shopping_mall.data.deviceInfo.repository.DeviceInfoRepository
import com.devup.android_shopping_mall.data.user.model.*
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.databinding.ActivityLoginBinding
import com.devup.android_shopping_mall.network.NetworkCheck
import com.devup.android_shopping_mall.util.DIFFERENT_CURRENT_PASSWORD
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import com.devup.android_shopping_mall.view.terms.TermsActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject


class LoginActivity : AppCompatActivity() {
    private val TAG: String = "LoginActivity"

    //데이터 DI
    val viewModel: LoginViewModel by inject()

    private val tokenRepository: TokenRepository by inject()
    private val userRepository: UserRepository by inject()
    private val deviceInfoRepository: DeviceInfoRepository by inject()
    private val categoriesRepository: CategoriesRepository by inject()


    lateinit var profile_email: String
    lateinit var password: String

    private lateinit var auth: FirebaseAuth
    val GOOGLE_REQUEST_CODE = 99
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.apply {
            lifecycleOwner = this@LoginActivity
        }

        setGoogleButtonText(btnSigninGoogle, "Google 계정으로 로그인")

        //카카오 콜백
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공 ${token.accessToken}")
                //사용자 정보 가져오기
                userInfoFromKakao()
            }
        }

        //Google 로그인을 적용하기 위한 Initialize Sequence
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


//----- 로그인
        btnSignin.setOnClickListener {
            profile_email = etProfileEmail.text.toString()
            password = etPassword.text.toString()
            Log.d(TAG, "btnSignin $profile_email , $password")
            getSigninToken("email", null, profile_email, password)
        }

        //구글 로그인
        btnSigninGoogle.setOnClickListener {
            signIn()
        }

        //카카오 로그인
        //카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        btnSigninKakao.setOnClickListener {
            Log.d(TAG, "onCreate: 카카오로그인")
            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }


//-----회원가입
        btnSignup.setOnClickListener {
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
            finish()
        }

        //비밀번호 찾기
        btnFindPassword.setOnClickListener {
            showDialog()

        }


    }
//-----onCreate종료

    private fun showDialog() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_find_password, null)

        val alertDialog = AlertDialog.Builder(this)
            .setPositiveButton(R.string.find_password_str) { dialog, which ->

                val etEmail: EditText = view.findViewById(R.id.etEmail)
                val userEmail = etEmail.text.toString()

                val request = FindPasswordRequest("email", userEmail)

                findPassword(request)
            }
            .setNeutralButton("취소", null)
            .create()
        //  여백 눌러도 창 안없어지게
        alertDialog.setCancelable(false)

        alertDialog.setView(view)
        alertDialog.show()
    }

    //비밀번호 찾기
    private fun findPassword(request: FindPasswordRequest) {
        userRepository.findPassword(request,
            onSuccess = {
                alert(R.string.password_find_info_str)
            },
            notSuccessStatus = { notSuccessStatus ->

                Log.d(TAG, "changPassword: $notSuccessStatus ")

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of changPassword $failureError")

            })
    }


    //구글로그인 버튼 text변경
    private fun setGoogleButtonText(loginButton: SignInButton, buttonText: String) {
        var i = 0
        while (i < loginButton.childCount) {
            val v = loginButton.getChildAt(i)
            if (v is TextView) {
                v.text = buttonText
                //v.gravity = Gravity.CENTER
                return
            }
            i++
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val uniqueId = account.id.toString()
                Log.d(TAG, "구글로그인 성공, account.id:" + account.id.toString())
                Log.d(TAG, "firebaseAuthWithGoogle,account.idToken:" + account.idToken.toString())
                firebaseAuthWithGoogle(uniqueId, account.idToken!!)


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(uniqueId: String, idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "로그인 성공")
                    val user = auth.currentUser
                    if (user != null) {
                        Log.d(TAG, "Google , 닉네임: ${user.displayName.toString()}")
                        Log.d(TAG, "Google , 프로필사진: ${user.photoUrl.toString()}")
                        Log.d(TAG, "Google , 이메일: ${user.email.toString()}")
                        Log.d(TAG, "Google ,: ${user.uid}")

                        val googleNickname = user.displayName.toString()
                        val googleProfileImage = user.photoUrl.toString()
                        val googleEmail = user.email ?: null

                        saveSimpleLoginResult(SimpleLoginUserProfile(uniqueId, googleNickname, googleProfileImage, googleEmail, null))
                        getSigninToken("gmail", uniqueId, null, null)
                    }
                    //loginSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    private fun userInfoFromKakao() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i(
                    TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                            "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                            "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                )
                val kakaoId = user.id.toString()
                val kakaoNickname = user.kakaoAccount?.profile?.nickname.toString()
                val kakaoProfileImage = user.kakaoAccount?.profile?.thumbnailImageUrl.toString()
                val kakaoEmail = user.kakaoAccount?.email ?: null
                val kakaoGender = makeGenderText(user.kakaoAccount?.gender?.toString() ?: "")

                saveSimpleLoginResult(SimpleLoginUserProfile(kakaoId, kakaoNickname, kakaoProfileImage, kakaoEmail, kakaoGender))

                getSigninToken("kakao", user.id.toString(), null, null)
            }
        }
    }

    private fun saveSimpleLoginResult(user: SimpleLoginUserProfile) {
        userRepository.saveSimpleLoginUserProfile(user)
    }

    private fun makeGenderText(kakaoGender: String): String? {
        return when (kakaoGender) {
            "MALE" -> "M"
            "FEMALE" -> "W"
            else -> null
        }
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(this@LoginActivity)
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun errorToast(message: String, e: Throwable) {
        toast(message + NetworkCheck.getErrorMessage(e))
    }

    private fun getSigninToken(type: String, unique_id: String?, email: String?, password: String?) {
        tokenRepository.getSigninToken(
            tokenExisted = { token ->
                login(type, token.signin_token, unique_id, email, password)
            },
            tokenNotExist = {
                checkUser(type, unique_id, email, password)
            }
        )

    }


    private fun checkUser(type: String, unique_id: String?, email: String?, password: String?) {
        userRepository.checkUser(CheckUserRequest(type, unique_id, email),
            onSuccess = { response ->
                when (response.message) {
                    getString(R.string.check_you_can_join_str) -> {
                        when (type) {
                            "email" -> {
                                //회원가입 첫 단계로 이동
                                Log.d(TAG, "onSuccess ${response.message}")
                                alert(R.string.notice_check_email_str)
                            }
                            else -> {
                                //카카오/구글 유저 정보 저장 -> 카카오/구글 로그인 완료후 저장완료상태
                                openTermsAgree(type)
                            }
                        }

                    }
                    getString(R.string.check_you_can_login_str) -> {
                        saveSigninToken(response.signin_token)
                        login(type, response.signin_token, unique_id, email, password)
                        Log.d(TAG, "onSuccess ${response.message}")
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
                Toast.makeText(this, NetworkCheck.getErrorMessage(failureError), Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    private fun saveSigninToken(token: String) {
        Log.d(TAG, "saveSigninToken")
        tokenRepository.saveSigninToken(SigninToken(token))
    }


    fun login(type: String, signin_token: String, unique_id: String?, email: String?, password: String?) {
        Log.d(TAG, "login")
        userRepository.login(loginRequest(type, signin_token, unique_id, email, password),
            onSuccess = { response ->
                //로그인 성공시 Access토큰 저장,Activity 종료
                NetworkCheck.setAccessToken(response.access_token)
                saveAccessToken(response.access_token)
                //saveUserProfile(response.user)
                //viewModelMore.loadProfile()
                getUserInfo()

            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loginError $notSuccessStatus")
                when (notSuccessStatus) {
                    //저장된 토큰값과 서버에서 보낸 토큰값이 다르다면, 저장된 토큰을 삭제하고 재발급한다.
                    TOKEN_EXPIRED -> deleteSigninToken()
                    else -> alert(R.string.notice_check_email_str)
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loginError $failureError")
            })
    }

    private fun loginRequest(type: String, signin_token: String, unique_id: String?, email: String?, password: String?): LoginRequest {
        val deviceInfo = deviceInfoRepository.getDeviceInfo()
        return LoginRequest(
            type,
            deviceInfo.getDeviceOs(),
            signin_token,
            deviceInfo.getAppVersion(),
            deviceInfo.getDeviceId(),
            deviceInfo.getDeviceModel(),
            unique_id,
            email,
            password
        )
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

    private fun saveAccessToken(token: String) {
        tokenRepository.saveAccessToken(AccessToken(token))

    }

    private fun saveUserProfile(user: User) {
        userRepository.saveUserProfile(user)
        finish()
    }

    private fun deleteSigninToken() {
        tokenRepository.deleteToken()
        profile_email = etProfileEmail.text.toString()
        password = etPassword.text.toString()
        Log.d(TAG, "deleteSigninToken() $profile_email , $password")
        getSigninToken("email", null, profile_email, password)
    }

    private fun openTermsAgree(type: String) {
        val intent = Intent(this, TermsActivity::class.java)
        val bundle = bundleOf("type" to type)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }


}