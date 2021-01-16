package com.zejian.myapplication.widget.pyq

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.lang.ref.WeakReference


class ImageGridView(context: Context) : ViewGroup(context), OnHierarchyChangeListener{

    private var mAdapter: NineGridAdapter<*>? = null
    private var mListener: OnImageClickListener? = null
    private var mRows = 0
    private var mColumns = 0
    private var mSpace = 0
    private var IMAGE_POOL: WeakObjectPool<View>? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setOnHierarchyChangeListener(this)
        IMAGE_POOL = WeakObjectPool(5)
        mSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            4f, context.resources.displayMetrics).toInt()
    }

    private var mSingleWidth = 0
    private var mSingleHeight = 0

    fun setSingleImageSize(width: Int, height: Int) {
        mSingleWidth = width
        mSingleHeight = height
    }

    fun setAdapter(adapter: NineGridAdapter<*>?) {
        if (adapter == null || adapter.getCount() <= 0) {
            removeAllViews()
            return
        }
        if (mImageViews == null) {
            mImageViews = ArrayList()
        } else {
            mImageViews!!.clear()
        }
        mAdapter = adapter
        val oldCount: Int = childCount
        val newCount = adapter.getCount()
        initMatrix(newCount)
        removeScrapViews(oldCount, newCount)
        addChildrenData(adapter)
        requestLayout()
    }

    private fun removeScrapViews(oldCount: Int, newCount: Int) {
        if (newCount < oldCount) {
            removeViewsInLayout(newCount, oldCount - newCount)
        }
    }

    private fun initMatrix(length: Int) {
        if (length <= 3) {
            mRows = 1
            mColumns = length
        } else if (length <= 6) {
            mRows = 2
            mColumns = 3 // 因为length <=6 所以实际Columns<3也是不会导致计算出问题的
            if (length == 4) {
                mColumns = 2
            }
        } else {
            mRows = 3
            mColumns = 3
        }
    }

    private fun addChildrenData(adapter: NineGridAdapter<*>) {
        val childCount: Int = childCount
        val count = adapter.getCount()
        for (i in 0 until count) {
            val hasChild = i < childCount
            // 简单的回收机制,主要是为ListView/RecyclerView做优化
            var recycleView: View? = if (hasChild) getChildAt(i) else null
            if (recycleView == null) {
                recycleView = IMAGE_POOL?.get()
                val child = adapter.getView(i, recycleView)
                addViewInLayout(child, i, child.layoutParams, true)
                mImageViews?.add(child as ImageView)
            } else {
                adapter.getView(i, recycleView)
                mImageViews?.add(recycleView as ImageView)
            }
        }
    }


    private var mImageViews: MutableList<ImageView>? = null

    fun getImageViews(): List<ImageView>? {
        return mImageViews
    }

    protected override fun addViewInLayout(
        child: View?,
        index: Int,
        params: ViewGroup.LayoutParams?,
        preventRequestLayout: Boolean
    ): Boolean {
        if (child !is ImageView) {
            throw ClassCastException("addView(View child) 只能放ImageView")
        }
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }



    var mChildWidth = 0
    var mChildHeight = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childCount: Int = childCount
        if (childCount <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if ((mRows == 0 || mColumns == 0) && mAdapter == null) {
            initMatrix(childCount)
        }
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width = View.resolveSizeAndState(minW, widthMeasureSpec, 1)
        val availableWidth: Int = width - paddingLeft - paddingRight
        if (childCount <= 1) {
            mChildWidth = if (mSingleWidth == 0) {
                availableWidth * 2 / 5
            } else {
                availableWidth / 2
            }
            mChildHeight = if (mSingleHeight == 0) {
                mChildWidth
            } else {
                (mSingleHeight / mSingleWidth.toFloat() * mChildWidth).toInt()
            }
        } else {
            mChildWidth = (availableWidth - mSpace * (mColumns - 1)) / 3
            mChildHeight = mChildWidth
        }
        val height = mChildHeight * mRows + mSpace * (mRows - 1)
        setMeasuredDimension(width, height + paddingTop + paddingBottom)
    }

    fun getChildHeight(): Int {
        return mChildHeight
    }

    fun getChildWidth(): Int {
        return mChildWidth
    }

    protected override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutChildren()
    }



    protected fun layoutChildren() {
        if (mRows <= 0 || mColumns <= 0) {
            return
        }
        val childCount: Int = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i) as ImageView
            val row = i / mColumns
            val col = i % mColumns
            val left: Int = (mChildWidth + mSpace) * col + paddingLeft
            val top: Int = (mChildHeight + mSpace) * row + paddingTop
            val right = left + mChildWidth
            val bottom = top + mChildHeight
            view.layout(left, top, right, bottom)
            view.setOnClickListener { v: View? ->
                if (mListener != null) {
                    mListener!!.onImageClick(i, view)
                }
            }
        }
    }


    fun setOnImageClickListener(listener: OnImageClickListener?) {
        mListener = listener
    }

    fun setSpace(space: Int) {
        mSpace = space
    }

    fun getSpace(): Int {
        return mSpace
    }

    override fun onChildViewAdded(parent: View?, child: View?) {}

    override fun onChildViewRemoved(parent: View, child: View) {
        IMAGE_POOL?.put(child)
    }

    fun getMaxSize(): Int {
        return 9
    }


    interface NineGridAdapter<T> {
        fun getCount(): Int
        fun getItem(position: Int): T
        fun getView(position: Int, itemView: View?): View
    }

    interface OnImageClickListener {
        fun onImageClick(position: Int, view: View?)
    }


    class NineImageAdapter(private val mContext: Context, private val mRequestOptions: RequestOptions,
                           private val mImageBeans: List<MomentImage>?) :
        NineGridAdapter<String?> {


        override fun getCount(): Int {
            return mImageBeans?.size ?: 0
        }

        override fun getItem(position: Int): String? {
            return if (mImageBeans == null) null else if (position < mImageBeans.size) mImageBeans[position].thumbnail else null
        }

        override fun getView(position: Int, itemView: View?): View {
            val imageView: ImageView
            if (itemView == null) {
                imageView = ImageView(mContext)
                imageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            } else {
                imageView = itemView as ImageView
            }
            mImageBeans?.isNotEmpty()?.let {
                val url = mImageBeans[position].thumbnail
                val requestOptions = RequestOptions().dontAnimate().dontTransform()
                Glide.with(mContext).asDrawable().apply(requestOptions).load(url)
                    .placeholder(android.R.color.transparent)
                    .transform(CenterCrop(), RoundedCorners(MomentImage.RADIUS))
                    .into(imageView)
            }
            return imageView
        }

    }



    class WeakObjectPool<T> @JvmOverloads constructor(private val size: Int = 5) {
        private val objsPool: Array<WeakReference<T>?>?
        private var curPointer = -1
        @Synchronized
        fun get(): T? {
            if (curPointer == -1 || curPointer > objsPool!!.size) return null
            val obj = objsPool[curPointer]!!.get()
            objsPool[curPointer] = null
            curPointer--
            return obj
        }

        @Synchronized
        fun put(t: T): Boolean {
            if (curPointer == -1 || curPointer < objsPool!!.size - 1) {
                curPointer++
                objsPool!![curPointer] = WeakReference(t)
                return true
            }
            return false
        }

        fun clearPool() {
            for (i in objsPool!!.indices) {
                objsPool[i]!!.clear()
                objsPool[i] = null
            }
            curPointer = -1
        }

        fun size(): Int {
            return objsPool?.size ?: 0
        }

        init {
            objsPool = java.lang.reflect.Array.newInstance(
                WeakReference::class.java,
                size
            ) as Array<WeakReference<T>?>
        }
    }


}