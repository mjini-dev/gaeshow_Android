package com.devup.android_shopping_mall.view.comment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.databinding.ActivityCommentDepthBinding
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import kotlinx.android.synthetic.main.activity_comment_depth.btnAddComment
import kotlinx.android.synthetic.main.activity_comment_depth.etComment
import org.koin.android.ext.android.inject

class CommentDepthActivity : AppCompatActivity() {
    private val TAG = "CommentSingleActivity"

    private val viewModel: BoardDetailsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_comment_depth)
        val binding: ActivityCommentDepthBinding = DataBindingUtil.setContentView(this,R.layout.activity_comment_depth)
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@CommentDepthActivity
        }

        val comment = intent.extras?.get("comment")
        val resource_id = intent.extras?.get("resource_id")

        Log.d(TAG, "onCreate comment: $comment , resource_id: $resource_id")

        viewModel._commentSingle.value = comment as Comment?
        Log.d(TAG, "onCreate: ${viewModel.commentSingle.value.toString()}")
        val commentId = viewModel.commentSingle.value?.comment_id
        btnAddComment.setOnClickListener {
            if (!etComment.text.isNullOrBlank()) {
                viewModel.addCommentDepth(resource_id as Int,commentId!!,etComment.text.toString())
            }
            etComment.text.clear()
            hideSoftInput()
            finish()
        }
    }

    private fun hideSoftInput() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etComment.windowToken, 0);
    }
}