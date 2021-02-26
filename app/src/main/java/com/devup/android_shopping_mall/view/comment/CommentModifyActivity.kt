package com.devup.android_shopping_mall.view.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.comments.model.AddCommentRequest
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import kotlinx.android.synthetic.main.activity_comment_modify.*
import org.koin.android.ext.android.inject

class CommentModifyActivity : AppCompatActivity() {
    private val TAG = "CommentModifyActivity"

    var resource_id = 0
    var comment_id = 0
    var parent_id = 0
    lateinit var content: String

    private val viewModel: BoardDetailsViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_modify)

        resource_id = intent.extras?.get("resource_id") as Int
        comment_id = intent.extras?.get("comment_id") as Int
        content = intent.extras?.get("content").toString()

        if (intent.hasExtra("parent_id")) {
            parent_id = intent.extras?.get("parent_id") as Int
        } else {
            //Toast.makeText(this,"no parent",Toast.LENGTH_SHORT).show()
        }

        if (content.isNotEmpty()) {
            etModifyComment.setText(content)
            etModifyComment.setSelection(etModifyComment.text.length)
        }

        btnCancel.setOnClickListener { finish() }

        btnOk.setOnClickListener {
            if (parent_id == 0) {
                //댓글수정, depth 1
                content = etModifyComment.text.toString()
                val request = AddCommentRequest(1, null, content)

                viewModel.modifyComment(resource_id, comment_id, request)

            } else {
                //대댓글수정, depth 2
                content = etModifyComment.text.toString()
                val request = AddCommentRequest(2, parent_id, content)

                viewModel.modifyComment(resource_id, comment_id, request)
            }
        }

        observeModifySuccess()
    }

    fun observeModifySuccess() {
        viewModel.isCommentModify.observe(this, Observer { success ->
            if (success) {
                finish()
            }
        })
    }



}