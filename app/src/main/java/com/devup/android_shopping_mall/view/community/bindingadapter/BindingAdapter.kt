package com.devup.android_shopping_mall.view.community.bindingadapter

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.data.comments.model.CommentDepth
import com.devup.android_shopping_mall.data.community.model.*
import com.devup.android_shopping_mall.data.tag.Tag
import com.devup.android_shopping_mall.view.comment.BoardCommentAdapter
import com.devup.android_shopping_mall.view.comment.BoardCommentDepthAdapter
import com.devup.android_shopping_mall.view.comment.BoardIdeCommentAdapter
import com.devup.android_shopping_mall.view.comment.CommentModifyActivity
import com.devup.android_shopping_mall.view.community.adapter.BoardWriteTagAddAdapter
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.adapter.TagAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import com.devup.android_shopping_mall.view.service.ReportWriteActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import jp.wasabeef.richeditor.RichEditor
import java.util.concurrent.atomic.AtomicBoolean

@BindingAdapter("setPost")
fun setPost(view: RecyclerView, item: List<Post>?) {
    (view.adapter as? ListAdapter)?.run {
        item?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}

@BindingAdapter("setCategory")
fun setCategory(view: RecyclerView, item: List<Category>?) {
    (view.adapter as? ListAdapter)?.run {
        item?.let { updateCategorys(it) }
        notifyDataSetChanged()
    }
}

@BindingAdapter("setSalaryTop")
fun setSalaryTop(view: RecyclerView, item: List<Salaries>?) {
    (view.adapter as? ListAdapter)?.run {
        item?.let { updateSalaryTop(it) }
        notifyDataSetChanged()
    }
}

@BindingAdapter("setSalaryTopLanguage")
fun setSalaryTopLanguage(view: TextView, item: List<Laguages>?) {
    if (!item.isNullOrEmpty()) {
        Log.d("TAG", "setSalaryTopLanguage: $item")
        for (lan in item) {
            if (lan==null) {
                view.text = "미선택"
            } else {
                view.text = lan.name
            }
//            if (!lan.name.isNullOrEmpty()) {
//                view.text = lan.name
//            } else if (lan.name.isNullOrEmpty()) {
//                Log.d("TAG", "setSalaryTopLanguage: errrrrrrrrrrrrrr ")
//            }
        }
    } else {
        view.text = "미선택"
    }
}

//board
@BindingAdapter("setBoardPostTag")
fun setBoardPostTag(view: RecyclerView, tags: List<Tag>?) {
    (view.adapter as? TagAdapter)?.run {
        tags?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}


@BindingAdapter("setBoardComment")
fun setBoardComment(view: RecyclerView, item: List<Comment>?) {
    (view.adapter as? BoardCommentAdapter)?.run {
        item?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}

@BindingAdapter("setBoardIdeComment")
fun setBoardIdeComment(view: RecyclerView, item: List<Comment>?) {
    (view.adapter as? BoardIdeCommentAdapter)?.run {
        item?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}

//@BindingAdapter("setBoardCommentDepth")
@BindingAdapter(value = ["setBoardCommentDepth", "vm","position"])
fun RecyclerView.setBoardCommentDepth(item: List<CommentDepth>?, vm: BoardDetailsViewModel, position:Int) {
    if (item != null) {
        val commentDepthAdapter = BoardCommentDepthAdapter(vm, object : BoardCommentDepthAdapter.ItemClickListener {
            override fun onModifyClick(commentId: Int, content: String) {
                Log.d("BindingAdapter", "부모 인덱스, id: $position")
                Log.d("BindingAdapter", "대댓 인덱스, id: $commentId")
                Log.d("BindingAdapter", "onPostItemClick, con: $content")

                val intent = Intent(context, CommentModifyActivity::class.java)
                val bundle = bundleOf("resource_id" to vm.postId.value!!, "comment_id" to commentId, "content" to content,"parent_id" to position)
                intent.putExtras(bundle)
                Log.d("BindingAdapter", "bundle: $bundle")
                startActivity(context,intent,bundle)
            }

            override fun onDeleteClick(commentId: Int) {
                vm.deleteComment(vm.postId.value!!,commentId)
            }

            override fun onRatingClick(ratingId: Int,commentId: Int) {
                if (ratingId !=0) {
                    //좋아요 삭제
                    vm.ratingsDelete("comment", ratingId)
                } else {
                    vm.ratingsAdd("comment",commentId)
                }
                vm.clearComments()
            }

            override fun onReportClick(comment_id: Int) {
                val intent = Intent(context, ReportWriteActivity::class.java)
                val bundle = bundleOf("uniqueId" to comment_id, "reportType" to "POST_COMMENT")
                intent.putExtras(bundle)
                startActivity(context,intent,bundle)
            }

        })
        commentDepthAdapter.updateItems(item)
        adapter = commentDepthAdapter
    }
}


@BindingAdapter("onPostItemClickListener")
fun View.onPostItemClickListener(clickListener: View.OnClickListener?) {
    clickListener?.also {
        setOnClickListener(OnClickListener(it))
    } ?: setOnClickListener(null)
}

//중복클릭 방지
class OnClickListener(
    private val clickListener: View.OnClickListener,
    private val intervalMs: Long = 1000
) : View.OnClickListener {
    private var canClick = AtomicBoolean(true)

    override fun onClick(v: View?) {
        if (canClick.getAndSet(false)) {
            v?.run {
                postDelayed({
                    canClick.set(true)
                }, intervalMs)
                clickListener.onClick(v)
            }
        }
    }
}

@BindingAdapter("setProfileImage")
fun setProfileImage(view: de.hdodenhof.circleimageview.CircleImageView, profile_image_url: String?) {
    if (profile_image_url.isNullOrEmpty()) {
        view.visibility = View.VISIBLE
        view.setBackgroundResource(R.drawable.ic_add)
    } else {
        view.visibility = View.VISIBLE
        Glide.with(view.context).load(profile_image_url).error(R.drawable.default_img_3x).into(view)
    }
}

@BindingAdapter("setThumbnail")
fun setThumbnail(view: ImageView, thumbnail: String?) {
    if (thumbnail.isNullOrEmpty()) {
        view.visibility = View.INVISIBLE
    } else {
        view.visibility = View.VISIBLE
        Glide.with(view.context).load(thumbnail).error(R.drawable.default_img_3x).into(view)
    }

    /*  Glide.with(view.context).asBitmap().load(thumbnail).error(R.drawable.default_img_3x).override(500, 500).transform(CenterCrop(), RoundedCorners(100))
          .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(view)*/
}

@BindingAdapter("setPostDetailProfile")
fun setPostDetailProfile(view: ImageView, url: String) {
    //activity_comment_depth 이미지 바인딩
}

@BindingAdapter("setTagAddItem")
fun setTagAddItem(view: RecyclerView, items: List<String>?) {
    (view.adapter as? BoardWriteTagAddAdapter)?.run {
        items?.let { addTags(it) }
        notifyDataSetChanged()
    }
}

@BindingAdapter("preferenceCareer")
fun PieChart.preferenceCareer(PreferenceCareer: List<PreferenceCareer>?) {
    PreferenceCareer?.let {
        description.isEnabled = false
        //setExtraOffsets(5f, 10f, 5f, 5f)

        //파이 차트 가운데 빈 구멍
        isDrawHoleEnabled = true
        setHoleColor(Color.WHITE)
        transparentCircleRadius = 61f

        //Legend는 차트의 범례를 의미
        //범례가 표시될 위치를 설정
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.textSize = 15f


        val values: ArrayList<PieEntry> = ArrayList()
        with(values) {
            for (item in PreferenceCareer) {
                add(PieEntry(item.rate, item.experience_years))
            }
        }
        animateY(1000, Easing.EaseInOutCubic)
        //animate()

        val dataSet: PieDataSet = PieDataSet(values, "")
        with(dataSet) {
            //슬라이스 간격
            sliceSpace = 1f
            selectionShift = 5f

            // add a lot of colors
            val colorsItems = ArrayList<Int>()
            for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
            for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
            for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
            for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
            colorsItems.add(ColorTemplate.getHoloBlue())

            colors = colorsItems
            //setColors(*ColorTemplate.JOYFUL_COLORS)
            //value 위치, 크기 지정
            //yValuePosition=PieDataSet.ValuePosition.OUTSIDE_SLICE

            //차트 내부에 퍼센트값, 라벨 안보이게
            setDrawValues(false)
            //setDrawSliceText(false)
            setDrawEntryLabels(false)
        }

        val pieData: PieData = PieData(dataSet)
        with(pieData) {
            //퍼센트 표시
            //setValueTextSize(10f)
            //setValueTextColor(Color.BLACK)
            //val des = Description()
            //des.text = "경력별"
            //description = des
        }
        data = pieData
    }
}


@BindingAdapter("preferenceDeveloper")
fun BarChart.preferenceDeveloper(PreferenceDeveloper: List<PreferenceDeveloper>?) {
    PreferenceDeveloper?.let {

        setExtraOffsets(5f, 10f, 5f, 25f)

        description.isEnabled = false  //차트 옆에 별도로 표기되는 description
        //setMaxVisibleValueCount(3) // 최대 보이는 그래프 개수
        setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
        setDrawBarShadow(false) //그래프의 그림자
        setDrawGridBackground(false) //격자구조 넣을건지
        setDrawBorders(false)
        legend.isEnabled = false
        setTouchEnabled(false)
        axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
        axisLeft.isEnabled = false // 왼쪽 Y축을 안보이게 해줌.
        isDoubleTapToZoomEnabled = false
        animateY(1000)

        val values: ArrayList<BarEntry> = ArrayList()
        val type = ArrayList<String>()
        var i = 1.0f
        with(values) {
            for (item in PreferenceDeveloper) {
                add(BarEntry(i, item.rate))
                i += 1f
                type.add(item.name)
            }
        }

        //축 라벨값 설정
        class MyXAxisFormatter : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return type.getOrNull(value.toInt() - 1) ?: value.toString()
            }
        }

        val xAxis = xAxis //X축
        xAxis.apply {
            isEnabled = true
            position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다
            granularity = 1f // 1 단위만큼 간격 두기
            disableGridDashedLine()
            setDrawAxisLine(true) // 축 ---그림
            setDrawGridLines(false) // 격자
            isGranularityEnabled = true
            //valueFormatter = IndexAxisValueFormatter(type)
            valueFormatter = MyXAxisFormatter()
            textSize = 15f
        }

        val set = BarDataSet(values, "")//데이터셋 초기화 하기
        set.color = ContextCompat.getColor(context!!, R.color.color_bar_chart_rating)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)

        val barData = BarData(dataSet)
        with(barData) {
            barWidth = 0.3f//막대 너비 설정하기
            setValueTextSize(10f)
            setValueFormatter(PercentFormatter()) //value값 %로 표기
        }

        data = barData
        setFitBars(true)
        invalidate()
    }
}


@BindingAdapter("salary")
fun BarChart.salary(Salaries: List<Salaries>?) {
    Salaries?.let {

        setExtraOffsets(5f, 15f, 5f, 25f)

        description.isEnabled = true  //차트 옆에 별도로 표기되는 description
        //setMaxVisibleValueCount(3) // 최대 보이는 그래프 개수
        setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
        setDrawBarShadow(false) //그래프의 그림자
        setDrawGridBackground(false) //격자구조 넣을건지
        setDrawBorders(false)
        legend.isEnabled = false
        setTouchEnabled(false)
        axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
        axisLeft.isEnabled = true
        isDoubleTapToZoomEnabled = false
        animateY(1000)

        setDrawValueAboveBar(false)

        val values: ArrayList<BarEntry> = ArrayList()
        val type = ArrayList<String>()
        var i = 1.0f
        with(values) {
            for (item in Salaries) {
                add(BarEntry(i, item.basic_salary.toFloat()))
                i += 1f
                val label = "${item.name}(${item.count}명)"
                type.add(label)
            }
        }

        //축 라벨값 설정
        class MyXAxisFormatter : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return type.getOrNull(value.toInt() - 1) ?: value.toString()
            }
        }

        val xAxis = xAxis //X축
        xAxis.apply {
            isEnabled = true
            position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다
            granularity = 1f // 1 단위만큼 간격 두기
            disableGridDashedLine()
            setDrawAxisLine(true) // 축 ---그림
            setDrawGridLines(false) // 격자
            isGranularityEnabled = true
            //valueFormatter = IndexAxisValueFormatter(type)
            valueFormatter = MyXAxisFormatter()
            textSize = 15f
        }

        axisLeft.run { //왼쪽 Y축
            axisMinimum = 1000f // 최소값
            axisMaximum = 10000f // 최대값
            granularity = 1000f // 값 만큼 라인선 설정
            setDrawAxisLine(true)
            labelCount = 10
            description.text = "(만원)"
            description.setPosition(100f, 10f)
            description.textSize = 10f
            description.yOffset = 25f
        }

        val set = BarDataSet(values, "")//데이터셋 초기화 하기
        set.color = ContextCompat.getColor(context!!, R.color.color_bar_chart_rating)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)

        val barData = BarData(dataSet)
        with(barData) {
            barWidth = 0.3f//막대 너비 설정하기
            //setValueTextSize(10f)
            setDrawValues(false)
        }

        data = barData
        setFitBars(true)
        invalidate()
    }
}

@BindingAdapter("setContent")
fun setContent(editor: RichEditor, text: String?) {

    val DURATION: Long = 500

    Handler(Looper.getMainLooper()).postDelayed({
        if (!text.isNullOrEmpty()) {
            editor.html = text
        } else {
            editor.html = ""
        }
    }, DURATION)

    /*  if(!text.isNullOrEmpty()) {
          editor.html = text
      } else {
          editor.html = ""
      }
  */
}




