package com.devup.android_shopping_mall.view.community.ide

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.comments.model.AddRecommendCommentRequest
import com.devup.android_shopping_mall.databinding.FragmentIdeRecommendViewBinding
import com.devup.android_shopping_mall.databinding.FragmentIdeRecommendWriteBinding
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ide_recommend_view.view.*
import kotlinx.android.synthetic.main.fragment_ide_recommend_write.view.*
import org.koin.android.ext.android.inject

class IdeRecommendWriteFragment : Fragment() {
    private val TAG = "IdeRecommendWrite"

    private val viewModel: BoardDetailsViewModel by inject()

    private var uniqueId: Int = 0
    lateinit var type: String
    private var rate: Float = 0f

    private var commentId: Int = 0
    lateinit var advantage: String
    lateinit var disadvantage: String

    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uniqueId = it.getInt("uniqueId")
            type = it.getString("type", "")
            commentId = it.getInt("commentId")
            viewModel._isRateModify.value = commentId != 0
            if (commentId != 0) {
                // rate, advantage, disadvantage
                rate = it.getInt("rate").toFloat()
                viewModel._rate.value = rate

                advantage = it.getString("advantage", "")
                viewModel._advantage.value = advantage

                disadvantage = it.getString("disadvantage", "")
                viewModel._disadvantage.value = disadvantage

            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentIdeRecommendWriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ide_recommend_write, container, false)
        val view = binding.root
        //val view = inflater.inflate(R.layout.fragment_ide_recommend_write, container, false)
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@IdeRecommendWriteFragment
        }

        if (type == "language") {
            viewModel.getLanguageList()
        } else if (type == "ide") {
            viewModel.getIdeList()
        }

        view.btnRatingAdd.setOnClickListener {
            if (view.ratingBarRating.rating == 0f && !viewModel.isZeroRateCheck.value!!) {
                alert(R.string.rating_score_zero_info_str)
            }
            rate = view.ratingBarRating.rating
            //Log.d(TAG, "onCreateView, rate: $rate")
            val advantage_content = view.etAdvantage.text.toString()
            val disadvantage_content = view.etDisadvantage.text.toString()

            val request = AddRecommendCommentRequest(type, uniqueId, rate, advantage_content, disadvantage_content)
            val requestMody = AddRecommendCommentRequest(null, uniqueId, rate, advantage_content, disadvantage_content)



            if (commentId != 0) {
                viewModel.modifyPostRecommendsComment(commentId, requestMody)
            } else {
                viewModel.addRecommendComment(request)
            }



            Log.d(TAG, "onCreateView: $request")
        }


        val main = activity as AppCompatActivity
        main.imageBtnBell.isEnabled = false


        observeStartDetailActivity()

        return view
    }

    fun alert(strId: Int) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str) { dial, which ->
            viewModel._isZeroRateCheck.value = true
        }
        dialog.show()
    }

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(viewLifecycleOwner, Observer { viewFragment ->
            if (viewFragment) {
                val postId = viewModel.postId.value
                if (postId == null) {
                    Log.e(TAG, "postId is null observeStartDetailActivity")
                } else {
                    navigateAction(postId)
                }
            }
        })
    }

    fun navigateAction(postId: Int) {
        val name = viewModel.idesLanguages.value?.get(uniqueId - 1)?.en_name
        val bundle = bundleOf("resource_id" to uniqueId, "type" to type, "name" to name)
        navController.navigate(R.id.action_ideRecommendWriteFragment_to_ideRecommendViewFragment, bundle)
        Log.d(TAG, "bundle Of resource_id $bundle")
    }

}