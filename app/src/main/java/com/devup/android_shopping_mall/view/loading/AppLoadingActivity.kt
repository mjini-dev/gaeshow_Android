package com.devup.android_shopping_mall.view.loading

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.categories.model.CategoriesRequest
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepository
import com.devup.android_shopping_mall.data.user.model.User
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import com.devup.android_shopping_mall.view.home.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_app_loading.*
import org.koin.android.ext.android.inject
import java.security.MessageDigest


class AppLoadingActivity : AppCompatActivity() {
    private val TAG: String = "AppLoadingActivity"
    private val categoriesRepository: CategoriesRepository by inject()
    private val userRepository: UserRepository by inject()
    private val tokenRepository: TokenRepository by inject()

    private lateinit var googleSignInClient: GoogleSignInClient

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_loading)

        getPostCategories()
        getServiceCategories()
        getJobCategories()
        getIdeCategories()
        getLanguageCategories()
        //loadProfile()
        getUserInfo()
        //getHashkey()
        //kakaoTokenInfo()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        

        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "onCreate: Google 계정 로그아웃")
        }
        //keyHash
        //Log.d("Hash", Utility.getKeyHash(this))

        val DURATION: Long = 6000

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }, DURATION)
    }

    //카카오 자동로그인
    private fun kakaoTokenInfo() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            }
            else if (tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getHashkey() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = packageInfo.signingInfo.apkContentsSigners

            for (signature in signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())

                val key = String(Base64.encode(md.digest(), Base64.DEFAULT))

                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                Log.d("Hashkey:", key)

            }

        }catch(e: Exception) {
            Log.e("name not found", e.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        imgLogo.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.blink)
    }

    private fun categoriesRequest(type: String): CategoriesRequest {
        return CategoriesRequest(
            type, null, null, null
        )
    }

    private fun getPostCategories() {
        categoriesRepository.getCategories(categoriesRequest("post"),
            onSuccess = { response ->
                categoriesRepository.savePostCategories(response)
                //Log.d(TAG, "response of getPostCategories $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of getPostCategoriesError $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of getPostCategoriesError $failureError")
            }
        )
    }

    private fun getStoreCategories() {
    }

    private fun getServiceCategories() {
        categoriesRepository.getCategories(categoriesRequest("service"),
            onSuccess = { response ->
                categoriesRepository.saveServiceCategories(response)
                //Log.d(TAG, "response of getServiceCategories $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of getServiceCategoriesError $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of getServiceCategoriesError $failureError")
            }
        )
    }

    private fun getJobCategories() {
        categoriesRepository.getCategories(categoriesRequest("job"),
            onSuccess = { response ->
                categoriesRepository.saveJobCategories(response)
                //Log.d(TAG, "response of getJobCategories $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of getJobCategoriesError $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of getJobCategoriesError $failureError")
            }
        )
    }

    private fun getIdeCategories() {
        categoriesRepository.getCategories(categoriesRequest("ide"),
            onSuccess = { response ->
                categoriesRepository.saveIdeCategories(response)
                //Log.d(TAG, "response of getIdeCategories $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of getIdeCategoriesError $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of getIdeCategoriesError $failureError")
            }
        )
    }

    private fun getLanguageCategories() {
        categoriesRepository.getCategories(categoriesRequest("language"),
            onSuccess = { response ->
                categoriesRepository.saveLanguageCategories(response)
                //Log.d(TAG, "response of getLanguageCategories $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of getLanguageCategoriesError $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of getLanguageCategoriesError $failureError")
            }
        )
    }

    //앱 로딩시, 내 정보 조회진행 -> 저장된 토큰을 사용한다.
    //토큰이 아직 유효하면 사용자정보 다시 저장(웹으로 사용자 정보를 변경했을경우, 정보갱신목적)
    //토큰만료가 되면. 저장되어있는 토큰,정보 삭제
    fun getUserInfo() {
        Log.d(TAG, "getUserInfo: ")
        userRepository.getUserInfo(
            onSuccess = { response ->
                saveUserProfile(response)
                Log.d(TAG, "getUserInfo: response $response")
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    //저장된 토큰값과 서버에서 보낸 토큰값이 다르다면, 저장된 토큰을 삭제
                    TOKEN_EXPIRED -> {
                        deleteAccessToken()
                        deleteProfile()
                    }
                    else ->  Log.d(TAG, "getUserInfo,getUserInfo : $notSuccessStatus ")
                }
            },
            onFailure = { failureError -> Log.d(TAG, "getUserInfo,getUserInfo: $failureError") })

    }

    private fun saveUserProfile(user: User) {
        userRepository.saveUserProfile(user)
    }


 /*  //로컬에 저장된 회원정보를 가져와
    private fun loadProfile() {
        userRepository.getUserProfile(
            onProfileExist = { userProfile ->
                getUserInfo(userProfile.user_id)
            },
            onProfileNotExist = {
                Log.d(TAG, "loadProfile: onProfileNotExist")
            }
        )
    }

    fun getUserInfo(userId: Int) {
        userRepository.getOtherUserInfo(userId,
            onSuccess = { response ->
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    //저장된 토큰값과 서버에서 보낸 토큰값이 다르다면, 저장된 토큰을 삭제
                    TOKEN_EXPIRED -> {
                        deleteAccessToken()
                        deleteProfile()
                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadServicesPosts $notSuccessStatus")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "getUserInfo,getUserInfo: $failureError")
            }
        )
    }
*/   private fun deleteAccessToken() {
        tokenRepository.deleteToken()
    }

    private fun deleteProfile() {
        userRepository.deleteProfile()
    }
}