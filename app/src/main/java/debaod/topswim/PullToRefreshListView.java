package debaod.topswim;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by debaod on 4/8/2015.
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener
{

            // 状态　
            private static final int TAP_TO_REFRESH = 1;
            private static final int PULL_TO_REFRESH = 2;
            private static final int RELEASE_TO_REFRESH = 3;
            private static final int REFRESHING = 4;
            private OnRefreshListener mOnRefreshListener;
            // 监听对listview的滑动动作　
            private OnScrollListener mOnScrollListener;
            private LayoutInflater mInflater;
            //顶部刷新时出现的控件　
            private RelativeLayout mRefreshView;
            private RelativeLayout mLoadView;
            private TextView mRefreshViewText;
            private ImageView mRefreshViewImage;
            private ProgressBar mRefreshViewProgress;
            private TextView mRefreshViewLastUpdated;
            private TextView mLoadViewText;
            private ImageView mLoadViewImage;
            private ProgressBar mLoadViewProgress;
            private TextView mLoadViewLastUpdated;
            // 当前滑动状态
            private int mCurrentScrollState;
            // 当前刷新状态
            private int mRefreshState;
            // 箭头动画效果
            private RotateAnimation mFlipAnimation;
            private RotateAnimation mReverseFlipAnimation;
            private int mRefreshViewHeight;
            private int mLoadViewHeight;
            private int mRefreshOriginalTopPadding;
            private int mLoadOriginalBottomPadding;
            private int mLastMotionY;
            private boolean mBounceHack;
            private boolean mBounceHackLoad;
            public PullToRefreshListView(Context context)
            {
                super(context);
                init(context);
            }
            public PullToRefreshListView(Context context, AttributeSet attrs)
            {
                super(context, attrs);
                init(context);
            }
            public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle)
            {
               super(context, attrs, defStyle);
               init(context);
            }
            /**
            * 初始化控件和箭头动画（这里直接在代码中初始化动画而不是通过xml）
            */
            private void init(Context context)
            {
                mFlipAnimation = new RotateAnimation(0, -180,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                mFlipAnimation.setInterpolator(new LinearInterpolator());
                mFlipAnimation.setDuration(250);
                mFlipAnimation.setFillAfter(true);
                mReverseFlipAnimation = new RotateAnimation(-180, 0,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
                mReverseFlipAnimation.setDuration(250);
                mReverseFlipAnimation.setFillAfter(true);
                mInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                mRefreshView = (RelativeLayout) mInflater.inflate(
                        R.layout.pull_to_refresh_header, this, false);
                mLoadView = (RelativeLayout)mInflater.inflate(R.layout.pull_to_load_header, this, false);
                mRefreshViewText =
                        (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_text);
                mRefreshViewImage =
                        (ImageView) mRefreshView.findViewById(R.id.pull_to_refresh_image);
                mRefreshViewProgress =
                        (ProgressBar) mRefreshView.findViewById(R.id.pull_to_refresh_progress);
                mRefreshViewLastUpdated =
                        (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_updated_at);
                mLoadViewText = (TextView)mLoadView.findViewById(R.id.pull_to_load_text);
                mLoadViewImage = (ImageView)mLoadView.findViewById(R.id.pull_to_load_image);
                mLoadViewProgress = (ProgressBar)mLoadView.findViewById(R.id.pull_to_load_progress);
                mLoadViewLastUpdated = (TextView)mLoadView.findViewById(R.id.pull_to_load_updated_at);
                mRefreshViewImage.setMinimumHeight(50);
                mLoadViewImage.setMinimumHeight(50);
                mRefreshOriginalTopPadding = mRefreshView.getPaddingTop();
                mLoadOriginalBottomPadding = mLoadView.getPaddingBottom();
                mRefreshState = TAP_TO_REFRESH;
                //为listview头部增加一个view　
                addHeaderView(mRefreshView);
                addFooterView(mLoadView);
                super.setOnScrollListener(this);
                measureView(mRefreshView);
                measureView(mLoadView);
                mRefreshViewHeight = mRefreshView.getMeasuredHeight();
                mLoadViewHeight = mLoadView.getMeasuredHeight();

            }
            @Override
            protected void onAttachedToWindow()
            {
                setSelection(1);
            }
            @Override
            public void setAdapter(ListAdapter adapter)
            {
                super.setAdapter(adapter);
                setSelection(1);
            }
            /**
     　　    * 设置滑动监听器
     　　    *
     　　   */
            @Override
            public void setOnScrollListener(AbsListView.OnScrollListener l)
            {
                mOnScrollListener = l;
            }
            /**
         　　 * 注册一个list需要刷新时的回调接口
         　　 *
            */
            public void setOnRefreshListener(OnRefreshListener onRefreshListener)
            {
                mOnRefreshListener = onRefreshListener;
            }
            /**
            * 设置标签显示何时最后被刷新
            *
            * @param lastUpdated
            *　　　　　　Last updated at.
            */
            public void setLastUpdated(CharSequence lastUpdated)
            {
                if (lastUpdated != null)
                {
                    mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
                    mRefreshViewLastUpdated.setText(lastUpdated);
                }
                else
                {
                    mRefreshViewLastUpdated.setVisibility(View.GONE);
                }
            }
            // 实现该方法处理触摸　
            @Override
            public boolean onTouchEvent(MotionEvent event)
            {
                final int y = (int) event.getY();
                mBounceHack = false;
                mBounceHackLoad = false;
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_UP:  //press gesture is finished.
                        if (!isVerticalScrollBarEnabled())
                        {
                            setVerticalScrollBarEnabled(true);
                        }
                        if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING)
                        {
                            // 拖动距离达到刷新需要　
                            if ((mRefreshView.getBottom() >= mRefreshViewHeight
                            || mRefreshView.getTop() >= 0)
                            && mRefreshState == RELEASE_TO_REFRESH)
                            {
                                // 把状态设置为正在刷新
                                mRefreshState = REFRESHING;
                                // 准备刷新　
                                prepareForRefresh();
                                // 刷新　
                                onRefresh();
                            }
                            else if (mRefreshView.getBottom() < mRefreshViewHeight
                                    || mRefreshView.getTop() <= 0)
                            {
                                // 中止刷新
                                resetHeader();
                                setSelection(1);
                            }
                        }
                        if(getLastVisiblePosition() == (getCount()-1)&& mRefreshState != REFRESHING)
                        {
                            if((mLoadView.getBottom() < getBottom()
                            || (getBottom()-mLoadView.getTop()) >= mLoadViewHeight)
                            && mRefreshState == RELEASE_TO_REFRESH)
                            {
                                mRefreshState = REFRESHING;
                                prepareForLoad();
                                onLoad();
                            }
                            else if(mLoadView.getBottom() > getBottom()
                                    || (getBottom() - mLoadView.getTop()) < mLoadViewHeight)
                            {
                                Log.i("OnTouch.HeadView.bottom",Integer.toString(mLoadView.getBottom()));
                                Log.i("OnTouch.View.bottom",Integer.toString(getBottom()));
                                Log.i("OnTouch.HeadView.Top",Integer.toString(mLoadView.getTop()));
                                Log.i("OnTouch.mRefreshHeight",Integer.toString(mLoadViewHeight));
                                resetFooter();
                                setSelection(getCount()-2);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:  //press gesture is started.
                        // 获得按下y轴位置　
                        mLastMotionY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                       // 计算边距　
                        applyHeaderPadding(event);
                        applyFooterPadding(event);
                        break;
                }
                return super.onTouchEvent(event);
            }
            // 获得header的边距　
            private void applyHeaderPadding(MotionEvent ev)
            {
                int pointerCount = ev.getHistorySize();
                for (int p = 0; p < pointerCount; p++)
                {
                   if (mRefreshState == RELEASE_TO_REFRESH)
                   {
                       if (isVerticalFadingEdgeEnabled())
                       {
                          setVerticalScrollBarEnabled(false);
                       }
                       int historicalY = (int) ev.getHistoricalY(p);
                       // 计算申请的边距，除以1.7使得拉动效果更好　
                       int topPadding = (int) (((historicalY - mLastMotionY)
                            - mRefreshViewHeight) / 1.7);
                       mRefreshView.setPadding(
                                    mRefreshView.getPaddingLeft(),
                                    topPadding,
                                    mRefreshView.getPaddingRight(),
                                    mRefreshView.getPaddingBottom());
                   }
                }
           }
            private void applyFooterPadding(MotionEvent ev)
            {
                int pointerCount = ev.getHistorySize();
                for (int p = 0; p < pointerCount; p++)
                {
                    if (mRefreshState == RELEASE_TO_REFRESH)
                    {
                        if (isVerticalFadingEdgeEnabled())
                        {
                            setVerticalScrollBarEnabled(false);
                        }
                        int historicalY = (int) ev.getHistoricalY(p);
                        // 计算申请的边距，除以1.7使得拉动效果更好　
                        int bottomPadding = (int) (((mLastMotionY - historicalY)
                                - mLoadViewHeight) / 1.7);
                        mLoadView.setPadding(
                                mLoadView.getPaddingLeft(),
                                mLoadView.getPaddingTop(),
                                mLoadView.getPaddingRight(),
                                bottomPadding);
                    }
                }
            }
            /**
     　　 * 将head的边距重置为初始的数值
     　　 */
            private void resetHeaderPadding()
            {
                    mRefreshView.setPadding(
                    mRefreshView.getPaddingLeft(),
                    mRefreshOriginalTopPadding,
                    mRefreshView.getPaddingRight(),
                    mRefreshView.getPaddingBottom());
            }

            private void resetFooterPadding()
            {
                mLoadView.setPadding(
                        mLoadView.getPaddingLeft(),
                        mLoadView.getPaddingTop(),
                        mLoadView.getPaddingRight(),
                        mLoadOriginalBottomPadding);
            }
            /**
     　　 * 重置header为之前的状态
     　　 */
            private void resetHeader()
            {
                if (mRefreshState != TAP_TO_REFRESH)
                {
                    mRefreshState = TAP_TO_REFRESH;
                    resetHeaderPadding();
                    // 将刷新图标换成箭头　
                    mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
                    // 清除动画　
                    mRefreshViewImage.clearAnimation();
                    // 隐藏图标和进度条　
                    mRefreshViewImage.setVisibility(View.GONE);
                    mRefreshViewProgress.setVisibility(View.GONE);
                }
            }
            private void resetFooter()
            {
                if (mRefreshState != TAP_TO_REFRESH)
                {
                    mRefreshState = TAP_TO_REFRESH;
                    resetFooterPadding();
                    // 将刷新图标换成箭头　
                    mLoadViewImage.setImageResource(R.drawable.ic_pulltoload_arrow);
                    if(MainPage.isLastPageofTopic)
                    {
                        mLoadViewText.setText("已是最后一页") ;
                    }
                    else {
                        mLoadViewText.setText("上拉翻页...");
                    }
                    // 清除动画　
                    //mLoadViewText.setText("上拉翻页...");
                    mLoadViewImage.clearAnimation();
                    // 隐藏图标和进度条　
                    mLoadViewImage.setVisibility(View.GONE);
                    mLoadViewProgress.setVisibility(View.GONE);
                }
            }
            // 估算headview的width和height　
            private void measureView(View child)
            {
                ViewGroup.LayoutParams p = child.getLayoutParams();
                if (p == null)
                {
                   p = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                       0 + 0, p.width);
                int lpHeight = p.height;
                int childHeightSpec;
                if (lpHeight > 0)
                {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
                }
                else
                {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                }
                child.measure(childWidthSpec, childHeightSpec);
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                        int visibleItemCount, int totalItemCount)
            {
                // 在refreshview完全可见时，设置文字为松开刷新，同时翻转箭头　
                if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
                    && mRefreshState != REFRESHING)
                {
                    if (firstVisibleItem == 0)
                    {
                        mRefreshViewImage.setVisibility(View.VISIBLE);
                        if ((mRefreshView.getBottom() >= mRefreshViewHeight + 20
                            || mRefreshView.getTop() >= 0)
                            && mRefreshState != RELEASE_TO_REFRESH)
                        {
                            mRefreshViewText.setText(myApplication.getAppContext().getString(R.string.release_load));
                            mRefreshViewImage.clearAnimation();
                            mRefreshViewImage.startAnimation(mFlipAnimation);
                            mRefreshState = RELEASE_TO_REFRESH;
                        }
                        else if (mRefreshView.getBottom() < mRefreshViewHeight + 20
                            && mRefreshState != PULL_TO_REFRESH)
                        {
                            mRefreshViewText.setText("下拉前页...");
                            if (mRefreshState != TAP_TO_REFRESH)
                            {
                                mRefreshViewImage.clearAnimation();
                                mRefreshViewImage.startAnimation(mReverseFlipAnimation);
                            }
                            mRefreshState = PULL_TO_REFRESH;
                        }
                    }
                    else if(firstVisibleItem == (totalItemCount-visibleItemCount))
                    {
                        mLoadViewImage.setVisibility(View.VISIBLE);
                        if((getBottom()-mLoadView.getTop() >= mLoadViewHeight + 20
                            || mLoadView.getBottom() <= getBottom())
                            && mRefreshState != RELEASE_TO_REFRESH)
                        {
                            mLoadViewText.setText("松开加载...");
                            mLoadViewImage.clearAnimation();
                            mLoadViewImage.startAnimation(mFlipAnimation);
                            mRefreshState = RELEASE_TO_REFRESH;
                        }
                        else if(getBottom()-mLoadView.getTop() < mLoadViewHeight + 20
                                && mLoadView.getBottom() > getBottom()
                                && mRefreshState != PULL_TO_REFRESH)
                        {
                            mLoadViewText.setText(myApplication.getAppContext().getString(R.string.poll_up_next));
                            if (mRefreshState != TAP_TO_REFRESH)
                            {
                                mLoadViewImage.clearAnimation();
                                mLoadViewImage.startAnimation(mReverseFlipAnimation);
                            }
                            mRefreshState = PULL_TO_REFRESH;
                        }

                    }
                    else
                    {
                        mRefreshViewImage.setVisibility(View.GONE);
                        mLoadViewImage.setVisibility(View.GONE);
                        resetHeader();
                        resetFooter();
                    }
            }
            else if (mCurrentScrollState == SCROLL_STATE_FLING
                    && firstVisibleItem == 0
                    && mRefreshState != REFRESHING)
                {
                    setSelection(1);
                    mBounceHack = true;
                }
            else if(mCurrentScrollState == SCROLL_STATE_FLING
                    && firstVisibleItem == (totalItemCount-visibleItemCount)
                        && mRefreshState != REFRESHING)
                {
                    setSelection(totalItemCount-2);
                    mBounceHackLoad = true;
                }
            else if (mBounceHack && mCurrentScrollState == SCROLL_STATE_FLING)
                {
                    setSelection(1);
                }
            else if (mBounceHackLoad && mCurrentScrollState == SCROLL_STATE_FLING)
                {
                    setSelection(totalItemCount-2);
                }
                if (mOnScrollListener != null)
                {
                    mOnScrollListener.onScroll(view, firstVisibleItem,
                            visibleItemCount, totalItemCount);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                mCurrentScrollState = scrollState;
                if (mCurrentScrollState == SCROLL_STATE_IDLE)
                {
                    mBounceHack = false;
                    mBounceHackLoad = false;
                }
                if (mOnScrollListener != null)
                {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            public void prepareForRefresh()
            {
                resetHeaderPadding();// 恢复header的边距　
                mRefreshViewImage.setVisibility(View.GONE);
                // 注意加上，否则仍然显示之前的图片　
                mRefreshViewImage.setImageDrawable(null);
                mRefreshViewProgress.setVisibility(View.VISIBLE);
               // 设置文字　
                mRefreshViewText.setText("加载中...");
                mRefreshState = REFRESHING;
            }
            public void prepareForLoad()
            {
                resetFooterPadding();// 恢复footer的边距　
                mLoadViewImage.setVisibility(View.GONE);
                // 注意加上，否则仍然显示之前的图片　
                mLoadViewImage.setImageDrawable(null);
                mLoadViewProgress.setVisibility(View.VISIBLE);
                // 设置文字　
                mLoadViewText.setText("加载中...");
                mRefreshState = REFRESHING;
            }

            public void onRefresh()
            {
                if (mOnRefreshListener != null)
                {
                    mOnRefreshListener.onRefresh();
                }
            }

            public void onLoad()
            {
                if (mOnRefreshListener != null)
                {
                    mOnRefreshListener.onLoad();
                }
            }
            /**
     　　 * 重置listview为普通的listview，该方法设置最后更新时间
     　　 *
     　　 * @param lastUpdated
     　　 *　　　　　　Last updated at.
     　　 */
           public void onRefreshComplete(CharSequence lastUpdated)
           {
                setLastUpdated(lastUpdated);
                onRefreshComplete();
           }
            /**
     　　 * 重置listview为普通的listview，不设置最后更新时间
     　　 */
           public void onRefreshComplete()
           {
                resetHeader();
        // 如果refreshview在加载结束后可见，下滑到下一个条目　
                if (mRefreshView.getBottom() > 0)
                {
                    invalidateViews();
                    setSelection(1);
                }
           }
            public void onLoadComplete()
            {
                resetFooter();
                WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
                setSelectionFromTop((MainPage.currentPage - 1) * 15 + 2, wm.getDefaultDisplay().getHeight() - DensityUtil.dip2px(myApplication.getAppContext(),50));
                // 如果refreshview在加载结束后可见，下滑到下一个条目　
                if (mLoadView.getTop() < getBottom())
                {
                    invalidateViews();
                    setSelectionFromTop((MainPage.currentPage - 1) * 15 + 2, wm.getDefaultDisplay().getHeight() - DensityUtil.dip2px(myApplication.getAppContext(),50));
                }
            }
            /**
     　　 * 刷新监听器接口
     　　 */
           public interface OnRefreshListener
           {
            /**
     　　　　 * list需要被刷新时调用
     　　　　 */
                public void onRefresh();
                public void onLoad();
           }
}
