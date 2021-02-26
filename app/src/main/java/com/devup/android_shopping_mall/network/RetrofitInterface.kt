package com.devup.android_shopping_mall.network

import com.devup.android_shopping_mall.data.categories.model.CategoriesResponse
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.data.comments.model.*
import com.devup.android_shopping_mall.data.community.model.*
import com.devup.android_shopping_mall.data.notification.model.NotificationsResponse
import com.devup.android_shopping_mall.data.post.model.*
import com.devup.android_shopping_mall.data.services.model.ServicesPostsResponse
import com.devup.android_shopping_mall.data.services.model.ServicesPost
import com.devup.android_shopping_mall.data.uploadfile.UploadFileResponse
import com.devup.android_shopping_mall.data.user.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    // 이용약관
    @GET("users/terms")
    fun getTerms(): Call<Terms>

    //가입여부체크
    @POST("users/check")
    fun checkUser(@Body request: CheckUserRequest): Call<CheckUserResponse>

    //닉네임 중복 체크
    @POST("users/profile-nickname")
    fun checkNickname(@Body request: CheckNicknameRequest): Call<ResponseBody>

    // 회원가입
    @POST("users/join")
    fun join(@Body request: JoinRequest): Call<JoinResponse>

    // 로그인
    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 로그아웃
    @POST("users/logout")
    fun logout(): Call<ResponseBody>

    // 로그인유저 정보 조회 (내정보 조회)
    @GET("users")
    fun getUserInfo(): Call<User>

    // 내 정보 수정
    @PUT("users")
    fun modifyUserInfo(@Body request: ModifyUserInfoRequest): Call<ResponseBody>

    // 비밀번호 변경
    @PUT("users/password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ResponseBody>

    // 비밀번호 찾기
    @POST("users/password")
    fun findPassword(@Body request: FindPasswordRequest): Call<ResponseBody>

    // 유저 정보 조회
    @GET("users/{user_id}")
    fun getOtherUserInfo(@Path("user_id") user_id: Int): Call<UserInfo>

    // 유저 정보 조회_로그인O
    @GET("users/{user_id}/auth")
    fun getOtherUserInfoAuth(@Path("user_id") user_id: Int): Call<UserInfo>

    //팔로우 추가
    @POST("users/{user_id}/follows")
    fun addFollows(@Path("user_id") user_id: Int): Call<FollowAddResponse>

    //언팔로우
    @DELETE("users/follows/{resource_id}")
    fun deleteFollows(@Path("resource_id") resource_id: Int): Call<ResponseBody>

    // 카테고리 조회
    @GET("categories")
    fun getCategories(@Query("type") type: String): Call<List<Category>>

    // 카테고리 조회
    @GET("categories")
    fun getCategoriesSearch(
        @Query("type") type: String,
        @Query("search_word") search_word: String?,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?
    ): Call<CategoriesResponse>

    //게시글 목록 조회
    @GET("posts")
    fun getPosts(
        @Query("category_id") category_id: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type") type: String?,
        @Query("user_id") user_id: Int?,
        @Query("search_type") search_type: String?,
        @Query("search_word") search_word: String?
    ): Call<PostsResponse>

    // 게시글 상세 조회_로그인X
    @GET("posts/{resource_id}")
    fun getPostDetails(@Path("resource_id") resource_id: Int): Call<Post>

    // 게시글 상세 조회_로그인O
    @GET("posts/{resource_id}/auth")
    fun getPostDetailsAuth(@Path("resource_id") resource_id: Int): Call<Post>

    // 게시글 상세 조회(ide, language)
    @GET("posts/recommends")
    fun getPostRecommendsDetails(
        @Query("type") type: String,
        @Query("unique_id") unique_id: Int
    ): Call<PostsRecommendsResponse>

    // 게시글 상세 조회(연봉)
    @GET("posts/salaries")
    fun getPostSalariesDetails(
        @Query("job_filter") job_filter: String,
        @Query("experience_years_filter") experience_years_filter: Int?
    ): Call<PostsSalaryResponse>

    // 연봉 TOP3 조회
    @GET("posts/salaries/top")
    fun getSalariesTop(): Call<PostsSalaryTopResponse>

    // 게시글 작성
    @POST("posts")
    fun addPost(
        @Body request: PostAddRequest
    ): Call<PostAddResponse>

    // 게시글 수정
    @PUT("posts/{resource_id}")
    fun modifyPost(
        @Path("resource_id") resource_id: Int,
        @Body request: PostAddRequest
    ): Call<ResponseBody>

    // 게시글 삭제
    @DELETE("posts/{resource_id}")
    fun deletePost(
        @Path("resource_id") resource_id: Int
    ): Call<ResponseBody>

    // 댓글 목록 조회
    @GET("posts/{resource_id}/comments")
    fun getComments(
        @Path("resource_id") resource_id: Int
//        ,
//        @Query("page") page: Int,
//        @Query("limit") limit: Int
    ): Call<Comments>

    // 댓글 목록 조회_로그인O
    @GET("posts/{resource_id}/comments/auth")
    fun getCommentsAuth(@Path("resource_id") resource_id: Int): Call<Comments>

    // 댓글 목록 조회(ide, language)
    @GET("posts/recommends/comments")
    fun getPostRecommendsComments(
        @Query("type") type: String,
        @Query("unique_id") unique_id: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<Comments>

    // 댓글 추가
    @POST("posts/{resource_id}/comments")
    fun addComment(@Path("resource_id") resource_id: Int, @Body request: AddCommentRequest): Call<AddCommentResponse>

    // 댓글 수정
    @PUT("posts/{resource_id}/comments/{comment_id}")
    fun modifyComment(
        @Path("resource_id") resource_id: Int, //게시글 인덱스
        @Path("comment_id") comment_id: Int, //댓글 인덱스
        @Body request: AddCommentRequest
    ): Call<ResponseBody>

    // 댓글 삭제
    @DELETE("posts/{resource_id}/comments/{comment_id}")
    fun deleteComment(
        @Path("resource_id") resource_id: Int, //게시글 인덱스
        @Path("comment_id") comment_id: Int //댓글 인덱스
    ): Call<ResponseBody>

    // 댓글 추가(ide, language)
    @POST("posts/recommends/comments")
    fun addPostRecommendsComment(@Body request: AddRecommendCommentRequest): Call<AddCommentResponse>

    // 댓글 삭제(ide, language)
    @DELETE("posts/recommends/comments/{comment_id}")
    fun deletePostRecommendsComment(
        @Path("comment_id") comment_id: Int //댓글 인덱스
    ): Call<ResponseBody>

    // 댓글 수정(ide, language)
    @PUT("posts/recommends/comments/{comment_id}")
    fun modifyPostRecommendsComment(
        @Path("comment_id") comment_id: Int,
        @Body request: AddRecommendCommentRequest
    ): Call<ResponseBody>

    //평가(좋아요) 추가
    @POST("ratings")
    fun ratingsAddPost(@Body request: PostRatingsAddRequest): Call<PostRatingsAddResponse>

    //평가(좋아요) 삭제
    @DELETE("ratings/{resource_id}")
    fun ratingsDeletePost(@Path("resource_id") resource_id: Int): Call<ResponseBody>

    //북마크 추가
    @POST("bookmarks")
    fun bookmarksAddPost(@Body request: PostBookmarksAddRequest): Call<PostBookmarksAddResponse>

    //북마크 삭제
    @DELETE("bookmarks/{resource_id}")
    fun bookmarksDeletePost(@Path("resource_id") resource_id: Int): Call<ResponseBody>

    //고객센터 게시글 목록 조회 _ 공지사항, 자주하는질문
    @GET("services")
    fun getCsPosts(
        @Query("category_id") category_id: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search_type") search_type: String?,
        @Query("search_word") search_word: String?
    ): Call<ServicesPostsResponse>

    //고객센터 게시글 목록 조회 _ 문의, 신고,
    @GET("services/auth")
    fun getCsAuthPosts(
        @Query("category_id") category_id: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search_type") search_type: String?,
        @Query("search_word") search_word: String?
    ): Call<ServicesPostsResponse>

    // 고객센터_문의하기 게시글 작성
    @POST("services")
    fun addServicesPost(
        @Body request: ServicesPostAddRequest
    ): Call<PostAddResponse>

    //고객센터 게시글 상세 조회(문의하기, 신고하기)
    @GET("services/{resource_id}")
    fun getServicesPostDetails(@Path("resource_id") resource_id: Int): Call<ServicesPost>

    // 고객센터 댓글 목록 조회 (문의하기, 신고하기)
    @GET("services/{resource_id}/comments/auth")
    fun getServicesComments(
        @Path("resource_id") resource_id: Int
    ): Call<ServiceComments>

    // 댓글 추가
    @POST("services/{resource_id}/comments")
    fun addServicesComment(@Path("resource_id") resource_id: Int, @Body request: AddCommentRequest): Call<AddCommentResponse>

    // 파일 업로드
    @Multipart
    @POST("files/images")
    fun uploadFile(
        @Part("type") request: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<List<UploadFileResponse>>

    //알림 목록 조회
    @GET("notifications")
    fun getNotifications(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<NotificationsResponse>

    //알림 수정- 읽음 처리
    @PUT("notifications/{resource_id}")
    fun modifyNotifications(
        @Path("resource_id") resource_id: Int
    ): Call<ResponseBody>


}