package com.zejian.myapplication.layoutmanager;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private int mVerticalOffset;//竖直偏移量 每次换行时，要根据这个offset判断
    private int mFirstVisiPos;//屏幕可见的第一个View的Position
    private int mLastVisiPos;//屏幕可见的最后一个View的Position

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        //空
        if(getItemCount() == 0){
            //回收所有view
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
            return;
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler);
        //初始化
        mVerticalOffset = 0;
        mFirstVisiPos = 0;
        mLastVisiPos = getItemCount();

        //初始化时调用 填充childView
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        int topOffset = getPaddingTop();//布局时的上偏移
//        int leftOffset = getPaddingLeft();//布局时的左偏移
//        int lineMaxHeight = 0;//每一行最大的高度
//        int minPos = mFirstVisiPos;//初始化时，我们不清楚究竟要layout多少个子View，所以就假设从0~itemcount-1
//        mLastVisiPos = getItemCount() - 1;
//        //顺序addChildView
//        for (int i = minPos; i <= mLastVisiPos; i++) {
//            //找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
//            View child = recycler.getViewForPosition(i);
//            addView(child);
//            measureChildWithMargins(child, 0, 0);
//            //计算宽度 包括margin
//            if (leftOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()) {//当前行还排列的下
//                layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));
//
//                //改变 left  lineHeight
//                leftOffset += getDecoratedMeasurementHorizontal(child);
//                lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
//            } else {//当前行排列不下
//                //改变top  left  lineHeight
//                leftOffset = getPaddingLeft();
//                topOffset += lineMaxHeight;
//                lineMaxHeight = 0;
//
//                //新起一行的时候要判断一下边界
//                if (topOffset - dy > getHeight() - getPaddingBottom()) {
//                    //越界了 就回收
//                    removeAndRecycleView(child, recycler);
//                    mLastVisiPos = i - 1;
//                } else {
//                    layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));
//
//                    //改变 left  lineHeight
//                    leftOffset += getDecoratedMeasurementHorizontal(child);
//                    lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
//                }
//            }
//        }
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


}
