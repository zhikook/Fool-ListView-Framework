package zy.fool.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import zy.android.widget.EdgeEffect;
import zy.android.widget.OverScroller;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public abstract class FoolAbsView extends FoolAdapterView<ListAdapter> {

    private final static String TAG_STRING = "FoolAbsView";
	
	 /**
     * When set, this ViewGroup should not intercept touch events.
     * {@hide}
     */
    protected static final int FLAG_DISALLOW_INTERCEPT = 0x80000;
    
    /**
     * Indicates that this view was specifically invalidated, not just dirtied because some
     * child view was invalidated. The flag is used to determine when we need to recreate
     * a view's display list (as opposed to just returning a reference to its existing
     * display list).
     *
     * @hide
     */
    static final int PFLAG_INVALIDATED                 = 0x80000000;
    
	/**
     * When set, this ViewGroup tries to always draw its children using their drawing cache.
     */
    static final int FLAG_ALWAYS_DRAWN_WITH_CACHE = 0x4000;

    // When set, ViewGroup invalidates only the child's rectangle
    // Set by default
    static final int FLAG_CLIP_CHILDREN = 0x1;
    // When set, ViewGroup excludes the padding area from the invalidate rectangle
    // Set by default
    private static final int FLAG_CLIP_TO_PADDING = 0x2;
   
    // When set, there is either no layout animation on the ViewGroup or the layout
    // animation is over
    // Set by default
    static final int FLAG_ANIMATION_DONE = 0x10;
    
    // When set, this ViewGroup caches its children in a Bitmap before starting a layout animation
    // Set by default
    private static final int FLAG_ANIMATION_CACHE = 0x40;
    
    /**
     * When set, this ViewGroup will split MotionEvents to multiple child Views when appropriate.
     */
    private static final int FLAG_SPLIT_MOTION_EVENTS = 0x200000;

    /**
     * Indicates that we are not in the middle of a touch gesture
     */
    static final int TOUCH_MODE_REST = -1;

    /**
     * Indicates we just received the touch event and we are waiting to see if the it is a tap or a
     * scroll gesture.
     */
    static final int TOUCH_MODE_DOWN = 0;

    /**
     * Indicates the touch has been recognized as a tap and we are now waiting to see if the touch
     * is a longpress
     */
    static final int TOUCH_MODE_TAP = 1;

    /**
     * Indicates we have waited for everything we can wait for, but the user's finger is still down
     */
    static final int TOUCH_MODE_DONE_WAITING = 2;

    /**
     * Indicates the touch gesture is a scroll
     */
    static final int TOUCH_MODE_SCROLL = 3;

    /**
     * Indicates the view is in the process of being flung
     */
    static final int TOUCH_MODE_FLING = 4;

    /**
     * Indicates the touch gesture is an overscroll - a scroll beyond the beginning or end.
     */
    static final int TOUCH_MODE_OVERSCROLL = 5;

    /**
     * Indicates the view is being flung outside of normal content bounds
     * and will spring back.
     */
    static final int TOUCH_MODE_OVERFLING = 6; 
    
    /**
     * Indicates the view is being pulled outside of normal content bounds
     * and will spring back.
     */
    //static final int MULTI_TOUCH_PULLOUT = 7;
    
    /**
     * Indicates the view is being pulled into of normal content bounds
     * and will spring back.
     */
    //static final int MULTI_TOUCH_PULLIN = 8;
    
    /**
     * Indicates the view is being pulled into of normal content bounds
     * and will spring back.
     */
    static final int TOUCH_MODE_PULL = 7;
    
    /**
     * Indicates the view is being pulled into of normal content bounds
     * and will spring back.
     */
    static final int TOUCH_MODE_OVERPULL = 8;  
    
    /**
     * Indicates the view is being slided into of normal content bounds
     * and will spring back.
     */
    //static final int SPECIAL_TOUCH_SLIDE_LEFT = 10;
    
    /**
     * Indicates the view is being slided into of normal content bounds
     * and will spring back.
     */
   // static final int SPECIAL_TOUCH_SLIDE_RIGHT = 11;
   
    /**
     * Indicates the view is being slided into of normal content bounds
     * and will spring back.
     */
   /// static final int SPECIAL_TOUCH_SLIDE_UP = 12;
    
    /**
     * Indicates the view is being slided into of normal content bounds
     * and will spring back.
     */
  //  static final int SPECIAL_TOUCH_SLIDE_DOWN = 13;
   
    /**
     * Indicates the view is being pulled into of normal content bounds
     * and will spring back.
     */
    static final int TOUCH_MODE_SLIDE = 9;  
   
    /**
     * Indicates the view is being slided into of normal content bounds
     * and will spring back.
     */
    static final int TOUCH_MODE_OVERSLIDE = 10;

    static final int MULTI_TOUCH_PULLOUT = 1;

	static final int MULTI_TOUCH_PULLIN = 2;
	
	static final int TOUCH_SLIDE_LEFT = 1;
	
	static final int TOUCH_SLIDE_RIGHT = 2;
	
	static final int PULLING_TIME = 40; // milliseconds
	
	static final int SLIDING_TIME = 40; // milliseconds
	
	   /**
     * How many positions in either direction we will search to try to
     * find a checked item with a stable ID that moved position across
     * a data set change. If the item isn't found it will be unselected.
     */
    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;

	//no getLayoutOrientation 
    
    final int LAYOUT_ORIENTATION_HORIZONTAL = 2 ;    
	
	final int LAYOUT_ORIENTATION_VERTICAL = 3;     
  
	int mLayoutOrientation = LAYOUT_ORIENTATION_HORIZONTAL;

    /**
     * Regular layout - usually an unsolicited layout from the view system
     */
    static final int LAYOUT_NORMAL = 0;

    /**
     * Show the first item
     */
    static final int LAYOUT_FORCE_TOP = 1;

    /**
     * Force the selected item to be on somewhere on the screen
     */
    static final int LAYOUT_SET_SELECTION = 2;

    /**
     * Show the last item
     */
    static final int LAYOUT_FORCE_BOTTOM = 3;

    /**
     * Make a mSelectedItem appear in a specific location and build the rest of
     * the views from there. The top is specified by mSpecificTop.
     */
    static final int LAYOUT_SPECIFIC = 4;

    /**
     * Layout to sync as a result of a data change. Restore mSyncPosition to have its top
     * at mSpecificTop
     */
    static final int LAYOUT_SYNC = 5;

    /**
     * Layout as a result of using the navigation keys
     */
    static final int LAYOUT_MOVE_SELECTION = 6;
    
    static final int LAYOUT_SPOT = 7;
    
    static final int LAYOUT_SLIDE = 8;
    
    /**
     * Normal list that does not indicate choices
     */
    public static final int CHOICE_MODE_NONE = 0;

    /**
     * The list allows up to one choice
     */
    public static final int CHOICE_MODE_SINGLE = 1;

    /**
     * The list allows multiple choices
     */
    public static final int CHOICE_MODE_MULTIPLE = 2;

    /**
     * The list allows multiple choices in a modal selection mode
     */
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;

    /**
     * Controls if/how the user may choose/check items in the list
     */
    int mChoiceMode = CHOICE_MODE_NONE;

    /**
     * Controls CHOICE_MODE_MULTIPLE_MODAL. null when inactive.
     */
    ActionMode mChoiceActionMode;

    ViewConfiguration config;
	
	AdapterDataSetObserver mDataSetObserver;

	ListAdapter mAdapter;
	
	 /**
     * The remote adapter containing the data to be displayed by this view to be set
     */
    //private RemoteViewsAdapter mRemoteAdapter;

	
	int mWidthMeasureSpec = 0;

	/**
     * This view's padding
     */
    Rect mListPadding = new Rect();
    
    /**
     * The data set used to store unused views that should be reused during the next layout
     * to avoid creating new ones
     */
    final RecycleBin mRecycler = new RecycleBin();

    protected int mLeft;
   
    protected int mRight;
    
    protected int mTop;
   
    protected int mBottom; 
    
    protected int mPaddingLeft;
    
    protected int mPaddingRight;
    
    protected int mPaddingTop;
    
    protected int mPaddingBottom;
    
    protected int mScrollX;
    
    protected int mScrollY;
  
    protected int mGroupFlags;
    
	private int mLastHandledItemCount;
	
	int mSelectorPosition = INVALID_POSITION;
	
	private SavedState mPendingSync;

	//private boolean mTextFilterEnabled;

	boolean mIsAttached;

	boolean mCachingStarted;
	
	boolean mCachingActive;
	
    final boolean[] mIsMeasuredAndUnused = new boolean[1];
    
    /**
     * If mAdapter != null, whenever this is true the adapter has stable IDs.
     */
    boolean mAdapterHasStableIds;
	
    /**
     * Running count of how many items are currently checked
     */
    int mCheckedItemCount;

    /**
     * Running state of which positions are currently checked
     */
    SparseBooleanArray mCheckStates;

    /**
     * Running state of which IDs are currently checked.
     * If there is a value for a given key, the checked state for that ID is true
     * and the value holds the last known position in the adapter for that id.
     */
    LongSparseArray<Integer> mCheckedIdStates;
    
    /**
     * Controls how the next layout will happen
     */
    int mLayoutMode = LAYOUT_NORMAL;
    
    /**
     * Indicates whether the list is stacked from the bottom edge or
     * the top edge.
     */
    boolean mStackFromBottom;
    
    /**
     * The position to resurrect the selected position to.
     */
    int mResurrectToPosition = INVALID_POSITION;
    
    /**
     * Maximum distance to record overscroll
     */
    int mOverscrollMax;

    /**
     * Content height divided by this is the overscroll limit.
     */
    static final int OVERSCROLL_LIMIT_DIVISOR = 3;
    
    /**
     * The position of the view that received the down motion event
     */
    int mMotionPosition;

    /**
     * The offset to the top of the mMotionPosition view when the down motion event was received
     */
    int mMotionViewOriginalTop;

    /**
     * The desired offset to the top of the mMotionPosition view after a scroll
     */
    int mMotionViewNewTop;
    
    /**
     * Rectangle used for hit testing children
     */
    private Rect mTouchFrame;

    /**
     * Running state of which positions are currently checked
     */
    SparseBooleanArray mItemPullStates;

    /**
     * Running state of which IDs are currently checked.
     * If there is a value for a given key, the checked state for that ID is true
     * and the value holds the last known position in the adapter for that id.
     */
    LongSparseArray<Integer> mItemPulledIdStates;
	
  	
    /**
     * One of TOUCH_MODE_REST, TOUCH_MODE_DOWN, TOUCH_MODE_TAP, TOUCH_MODE_SCROLL, or
     * TOUCH_MODE_DONE_WAITING
     */
    int mTouchMode = TOUCH_MODE_REST;
    
    int mSwapPosition ;
	
    int mItemPulledItemCount; 

    /**
     * The X value associated with the the down motion event
     */
    int mMotionX;

    /**
     * The Y value associated with the the down motion event
     */
    int mMotionY;
    
    protected int mLastX;
    
    protected int mLastY;
    
    float mFirstX = 0, mFirstY = 0;
    
	float[][] mInitialKeyPointers = 
	{
			{-1.0F,-1.0F,-1.0F},
			{-1.0F,-1.0F,-1.0F},
	};
	
	float[][] mKeyPointers = 
	{
				{-1.0F,-1.0F,-1.0F},
				{-1.0F,-1.0F,-1.0F},
	};

    protected int mPulldeltaXs[] = {0,0,0};//
    
    protected int mPulldeltaYs[] = {0,0,0};//

	long mTouchDownTime = 0;
	
	int mActivePointsNum = 0;
	boolean isAllowedSlide = false;
	boolean isAllowedPull = false;
	boolean itemNeedAlpha = true;
	
	private RectF mTouchRectF =new RectF(0F, 0F, 1F, 1F);
	
	 /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;
 
    /**
     * Acts upon click
     */
    private PerformClick mPerformClick;

    /**
     * Delayed action for touch mode.
     */
    private Runnable mTouchModeReset;
   
    Runnable mPositionScrollAfterLayout;

    /**
     * Handles scrolling between positions within the list.
     */
    PositionScroller mPositionScroller;

    
    /**
     * The select child's view (from the adapter's getView) is enabled.
     */
    private boolean mIsChildViewEnabled;

    /**
     * The last scroll state reported to clients through {@link zy.fool.widget.FoolAbsView.OnScrollListener}.
     */
    private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    /**
     * Helper object that renders and controls the fast scroll thumb.
     */
    private FastScroller mFastScroller;

    /**
     * Whether or not to enable the fast scroll feature on this list
     */
    boolean mFastScrollEnabled;

    /**
     * How far the finger moved before we started scrolling
     */
    int mMotionCorrection;

    private int mTouchSlop;

    /**
     * When set to true, the list automatically discards the children's
     * bitmap cache after scrolling.
     */
    boolean mScrollingCacheEnabled;

    /**
     * Used for determining when to cancel out of overscroll.
     */
    private int mDirection = 0;

    private static final boolean PROFILE_SCROLLING = false;
    private boolean mScrollProfilingStarted = false;

    private static final boolean PROFILE_FLINGING = false;
    private boolean mFlingProfilingStarted = false;

    /**
     * The StrictMode "critical time span" objects to catch animation
     * stutters.  Non-null when a time-sensitive animation is
     * in-flight.  Must call finish() on them when done animating.
     * These are no-ops on user builds.
     */

    //private StrictMode.Span mScrollStrictSpan = null;
    //private StrictMode.Span mFlingStrictSpan = null;

    /**
     * The last CheckForLongPress runnable we posted, if any
     */
    private CheckForLongPress mPendingCheckForLongPress;

    /**
     * The last CheckForTap runnable we posted, if any
     */
    private Runnable mPendingCheckForTap;

    private Runnable mClearScrollingCache;

    /**
     * Maximum distance to overscroll by during edge effects
     */
    int mOverscrollDistance;

    /**
     * Maximum distance to overfling during edge effects
     */
    int mOverflingDistance;

    // These two EdgeGlows are always set and used together.
    // Checking one for null is as good as checking both.

    /**
     * Tracks the state of the top edge glow.
     */
    private EdgeEffect mEdgeGlowTop;

    /**
     * Tracks the state of the bottom edge glow.
     */
    private EdgeEffect mEdgeGlowBottom;
    /**
     * An estimate of how many pixels are between the top of the list and
     * the top of the first position in the adapter, based on the last time
     * we saw it. Used to hint where to draw edge glows.
     */
    private int mFirstPositionDistanceGuess;

    /**
     * An estimate of how many pixels are between the bottom of the list and
     * the bottom of the last position in the adapter, based on the last time
     * we saw it. Used to hint where to draw edge glows.
     */
    private int mLastPositionDistanceGuess;

    private int mPersistentDrawingCache;

    private OnPullListener mPullListener;

 	private OnPullOutListener mPullOutListener;

 	private OnPullInListener mPullInListener;

 	private OnSlideListener mSlideListener;

 	private OnSlideLeftListener mSlideLeftListener;

 	private OnSlideRightListener mSlideRightListener;

    /**
     * Defines the selector's location and dimension at drawing time
     */
    Rect mSelectorRect = new Rect();

    /**
     * The top scroll indicator
     */
    View mScrollUp;

    /**
     * The down scroll indicator
     */
    View mScrollDown;

    /**
     * Indicates whether the list selector should be drawn on top of the children or behind
     */
    boolean mDrawSelectorOnTop = false;

    /**
     * The drawable used to draw the selector
     */
    Drawable mSelector;

    /**
     * The selection's left padding
     */
    int mSelectionLeftPadding = 0;

    /**
     * The selection's top padding
     */
    int mSelectionTopPadding = 0;

    /**
     * The selection's right padding
     */
    int mSelectionRightPadding = 0;

    /**
     * The selection's bottom padding
     */
    int mSelectionBottomPadding = 0;
    
    int incrementalDeltaX = 0;


    /**
     * The offset in pixels form the top of the AdapterView to the top
     * of the currently selected view. Used to save and restore state.
     */
    int mSelectedTop = 0;

    private int mMinimumVelocity,mMaximumVelocity;

    private float mVelocityScale = 1.0f;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;

    /**
     * Used for smooth scrolling at a consistent rate
     */
    static final Interpolator sLinearInterpolator = new LinearInterpolator();

    /**
     * Handles one frame of a fling
     */
    private FlingRunnable mFlingRunnable;
    /**
     * Wrapper for the multiple choice mode callback; AbsListView needs to perform
     * a few extra actions around what application code does.
     */
    MultiChoiceModeWrapper mMultiChoiceModeCallback;

    private ContextMenuInfo mContextMenuInfo = null;

    private PerformItemSlide mPerformItemSlide;

    private PerformItemPull mPendingItemPull;

    private SpotSlide mSpotSlide;

    private SpotClick mSpotClick;

	private boolean ispull;

	private boolean isSliding;

	private int mPullMode;

	private int mSlideMode;

	private long m1TouchDownTime;

	private long m2TouchDownTime;
	
	int mSlidedPosition;

    public interface SelectionBoundsAdjuster {
        /**
         * Called to allow the list item to adjust the bounds shown for
         * its selection.
         *
         * @param bounds On call, this contains the bounds the list has
         * selected for the item (that is the bounds of the entire view).  The
         * values can be modified as desired.
         */
        public void adjustListItemSelectionBounds(Rect bounds);
    }

    public FoolAbsView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initFoolAbsView();
	}

	public FoolAbsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		initFoolAbsView();
	}

	public FoolAbsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		initFoolAbsView();
	}

	void initFoolAbsView(){

        //���娉����寰�ViewGroup mGroupFlags���
		mGroupFlags |= FLAG_CLIP_CHILDREN;
        mGroupFlags |= FLAG_CLIP_TO_PADDING;
        mGroupFlags |= FLAG_ANIMATION_DONE;
        mGroupFlags |= FLAG_ANIMATION_CACHE;
        mGroupFlags |= FLAG_ALWAYS_DRAWN_WITH_CACHE;

        if (getContext().getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB) {
            mGroupFlags |= FLAG_SPLIT_MOTION_EVENTS;
        }

		config = ViewConfiguration.get(getContext());

		this.mTop = this.getTop();
		this.mLeft = this.getLeft();
		this.mRight = this.getRight();
		this.mBottom = this.getBottom();

		this.mPaddingLeft = this.getPaddingLeft();
		this.mPaddingRight = this.getPaddingRight();
		this.mPaddingTop = this.getPaddingTop();
		this.mPaddingBottom = this.getPaddingBottom();

		this.mScrollX = this.getScrollX();
		this.mScrollY = this.getScrollY();

		mTouchSlop = config.getScaledTouchSlop();
		mMinimumVelocity = config.getScaledMinimumFlingVelocity();
        mMaximumVelocity = config.getScaledMaximumFlingVelocity();

		mPersistentDrawingCache = getPersistentDrawingCache();
		mSlidedPosition = INVALID_POSITION;

	}

	/**
	 * 璁剧疆LAYOUT NORMAL/SPOT,��ㄥ��寤哄��锛����琚�璋����
	 *
	 * @param isspot
	 */
	public void setSpotLayout(boolean isspot){
		if(isspot){
			mLayoutMode = LAYOUT_SPOT;
			//���濮����LAYOUT_SPOT
			initSpot();
		}else {
			mLayoutMode = LAYOUT_NORMAL;
		}
	}

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingTop()
     * @see #getSelector()
     *
     * @return The top list padding.
     */
    public int getListPaddingTop() {
        return mListPadding.top;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingBottom()
     * @see #getSelector()
     *
     * @return The bottom list padding.
     */
    public int getListPaddingBottom() {
        return mListPadding.bottom;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingLeft()
     * @see #getSelector()
     *
     * @return The left list padding.
     */
    public int getListPaddingLeft() {
        return mListPadding.left;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingRight()
     * @see #getSelector()
     *
     * @return The right list padding.
     */
    public int getListPaddingRight() {
        return mListPadding.right;
    }


    /**
     * The list is empty. Clear everything out.
     */
    void resetList() {
        removeAllViewsInLayout();
        mFirstPosition = 0;
        mDataChanged = false;
        mPositionScrollAfterLayout = null;
        mNeedSync = false;
        mPendingSync = null;
        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        setSelectedPositionInt(INVALID_POSITION);
        setNextSelectedPositionInt(INVALID_POSITION);
        mSelectedTop = 0;
        mSelectorPosition = INVALID_POSITION;
        mSelectorRect.setEmpty();
        invalidate();
    }

    @Override
    public void setOverScrollMode(int mode) {
        if (mode != OVER_SCROLL_NEVER) {
            if (mEdgeGlowTop == null) {
                Context context = getContext();
                mEdgeGlowTop = new EdgeEffect(context);
                mEdgeGlowBottom = new EdgeEffect(context);
            }
        } else {
            mEdgeGlowTop = null;
            mEdgeGlowBottom = null;
        }
        super.setOverScrollMode(mode);
    }

	@Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);

            // Data may have changed while we were detached. Refresh.
            mDataChanged = true;
            mOldItemCount = mItemCount;
            mItemCount = mAdapter.getCount();
        }

        mIsAttached = true;
        if(mLayoutMode!=LAYOUT_SPOT){
			mLayoutMode = LAYOUT_NORMAL;
		}
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // Detach any view left in the measuredAndUnused heap
        mRecycler.clear();

        if (mAdapter!= null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mIsAttached = false;
    }

    /**
     * Creates the ContextMenuInfo returned from {@link #getContextMenuInfo()}. This
     * methods knows the view, position and ID of the item that received the
     * long press.
     *
     * @param view The view that received the long press.
     * @param position The position of the item that received the long press.
     * @param id The ID of the item that received the long press.
     * @return The extra information that should be returned by
     *         {@link #getContextMenuInfo()}.
     */
    ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    /**
     * Indicates whether the content of this view is pinned to, or stacked from,
     * the bottom edge.
     *
     * @return true if the content is stacked from the bottom edge, false otherwise
     */
    public boolean isStackFromBottom() {
        return mStackFromBottom;
    }

    /**
     * When stack from bottom is set to true, the list fills its content starting from
     * the bottom of the view.
     *
     * @param stackFromBottom true to pin the view's content to the bottom edge,
     *        false to pin the view's content to the top edge
     */
    public void setStackFromBottom(boolean stackFromBottom) {
        if (mStackFromBottom != stackFromBottom) {
            mStackFromBottom = stackFromBottom;
            requestLayoutIfNecessary();
        }
    }

    void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    /**
     * Subclasses should NOT override this method but
     *  {@link #layoutChildren()} instead.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mInLayout = true;

        if (changed) {

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).forceLayout();
            }
            mRecycler.markChildrenDirty();
        }

        layoutChildren();
        mInLayout = false;

        mOverscrollMax = (b - t) / OVERSCROLL_LIMIT_DIVISOR;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSelector == null) {
            useDefaultSelector();
        }
        final Rect listPadding = mListPadding;
        listPadding.left = mSelectionLeftPadding + mPaddingLeft;
        listPadding.top = mSelectionTopPadding + mPaddingTop;
        listPadding.right = mSelectionRightPadding + mPaddingRight;
        listPadding.bottom = mSelectionBottomPadding + mPaddingBottom;

//        // Check if our previous measured size was at a point where we should scroll later.
//        if (mTranscriptMode == TRANSCRIPT_MODE_NORMAL) {
//            final int childCount = getChildCount();
//            final int listBottom = getHeight() - getPaddingBottom();
//            final View lastChild = getChildAt(childCount - 1);
//            final int lastBottom = lastChild != null ? lastChild.getBottom() : listBottom;
//            mForceTranscriptScroll = mFirstPosition + childCount >= mLastHandledItemCount &&
//                    lastBottom <= listBottom;
//        }
    }

	protected void layoutChildren() {
		// TODO Auto-generated method stub

	}

	void updateScrollIndicators() {
        if (mScrollUp != null) {
            boolean canScrollUp;
            // 0th element is not visible
            canScrollUp = mFirstPosition > 0;

            // ... Or top of 0th element is not visible
            if (!canScrollUp) {
                if (getChildCount() > 0) {
                    View child = getChildAt(0);
                    canScrollUp = child.getTop() < mListPadding.top;
                }
            }

            mScrollUp.setVisibility(canScrollUp ? View.VISIBLE : View.INVISIBLE);
        }

        if (mScrollDown != null) {
            boolean canScrollDown;
            int count = getChildCount();

            // Last item is not visible
            canScrollDown = (mFirstPosition + count) < mItemCount;

            // ... Or bottom of the last element is not visible
            if (!canScrollDown && count > 0) {
                View child = getChildAt(count - 1);
                canScrollDown = child.getBottom() > mBottom - mListPadding.bottom;
            }

            mScrollDown.setVisibility(canScrollDown ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter != null) {
            mAdapterHasStableIds = mAdapter.hasStableIds();

            if ( mAdapterHasStableIds && mCheckedIdStates == null) {
                        mCheckedIdStates = new LongSparseArray<Integer>();
            }
        }

        if (mCheckStates != null) {
            mCheckStates.clear();
        }

        if (mCheckedIdStates != null) {
            mCheckedIdStates.clear();
        }

        if (mItemPullStates != null) {
        	mItemPullStates.clear();
        }

        if (mItemPulledIdStates != null) {
        	mItemPulledIdStates.clear();
        }

    }

    @Override
    protected void handleDataChanged() {
        int count = mItemCount;
        int lastHandledItemCount = mLastHandledItemCount;
        mLastHandledItemCount = mItemCount;

        // TODO: In the future we can recycle these views based on stable ID instead.
        mRecycler.clearTransientStateViews();

        if (count > 0) {
            int newPos;
            int selectablePos;

            // Find the row we are supposed to sync to
            if (mNeedSync) {
                // Update this first, since setNextSelectedPositionInt inspects it
                mNeedSync = false;
                mPendingSync = null;


                final int childCount = getChildCount();
                final int listBottom = getHeight() - getPaddingBottom();
                final View lastChild = getChildAt(childCount - 1);
                final int lastBottom = lastChild != null ? lastChild.getBottom() : listBottom;

             }
        }

        // Nothing is selected. Give up and reset everything.
        mSelectedPosition = INVALID_POSITION;
        mSelectedRowId = INVALID_ROW_ID;
        mNextSelectedPosition = INVALID_POSITION;
        mNextSelectedRowId = INVALID_ROW_ID;
        mNeedSync = false;
        mPendingSync = null;
        mSelectorPosition = INVALID_POSITION;

        checkSelectionChanged();
    }

    void positionSelector(int position, View sel) {
        if (position != INVALID_POSITION) {
            mSelectorPosition = position;
        }

        final Rect selectorRect = mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster)sel).adjustListItemSelectionBounds(selectorRect);
        }
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right,
                selectorRect.bottom);

        final boolean isChildViewEnabled = mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            mIsChildViewEnabled = !isChildViewEnabled;
            if (getSelectedItemPosition() != INVALID_POSITION) {
                refreshDrawableState();
            }
        }
    }

    private void positionSelector(int l, int t, int r, int b) {
        mSelectorRect.set(l - mSelectionLeftPadding, t - mSelectionTopPadding, r
                + mSelectionRightPadding, b + mSelectionBottomPadding);
    }

    private void useDefaultSelector() {
        setSelector(getResources().getDrawable(
               android.R.drawable.list_selector_background));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = 0;
    	final boolean clipToPadding = (mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK;
        if(clipToPadding){
        	saveCount = canvas.save();
        	final int scrollX = mScrollX;
        	final int scrollY = mScrollY;

        	canvas.clipRect(scrollX+mPaddingLeft,scrollY+mPaddingTop,
        			scrollX+mRight -mLeft-mPaddingRight,
        			scrollY + mBottom -mTop-mPaddingBottom);
        	mGroupFlags &=~CLIP_TO_PADDING_MASK;
        }

        final boolean drawSelectorOnTop = mDrawSelectorOnTop;
        if(!drawSelectorOnTop){
        	drawSelector(canvas);
        }

        super.dispatchDraw(canvas);

        if(drawSelectorOnTop){
        	drawSelector(canvas);
        }

        if(clipToPadding){
        	canvas.restoreToCount(saveCount);
        	mGroupFlags |= CLIP_TO_PADDING_MASK;
        }
    }

    static class SavedState extends BaseSavedState {
        long selectedId;
        long firstId;
        int viewTop;
        int position;
        int height;
        String filter;
        boolean inActionMode;
        int checkedItemCount;
        SparseBooleanArray checkState;
        LongSparseArray<Integer> checkIdState;

        /**
         * Constructor called from {@link zy.fool.widget.FoolAbsView#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            selectedId = in.readLong();
            firstId = in.readLong();
            viewTop = in.readInt();
            position = in.readInt();
            height = in.readInt();
            filter = in.readString();
            inActionMode = in.readByte() != 0;
            checkedItemCount = in.readInt();
            checkState = in.readSparseBooleanArray();
            final int N = in.readInt();
            if (N > 0) {
                checkIdState = new LongSparseArray<Integer>();
                for (int i=0; i<N; i++) {
                    final long key = in.readLong();
                    final int value = in.readInt();
                    checkIdState.put(key, value);
                }
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(selectedId);
            out.writeLong(firstId);
            out.writeInt(viewTop);
            out.writeInt(position);
            out.writeInt(height);
            out.writeString(filter);
            out.writeByte((byte) (inActionMode ? 1 : 0));
            out.writeInt(checkedItemCount);
            out.writeSparseBooleanArray(checkState);
            final int N = checkIdState != null ? checkIdState.size() : 0;
            out.writeInt(N);
            for (int i=0; i<N; i++) {
                out.writeLong(checkIdState.keyAt(i));
                out.writeInt(checkIdState.valueAt(i));
            }
        }

        @Override
        public String toString() {
            return "FoolAbsView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " selectedId=" + selectedId
                    + " firstId=" + firstId
                    + " viewTop=" + viewTop
                    + " position=" + position
                    + " height=" + height
                    + " filter=" + filter
                    + " checkState=" + checkState + "}";
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        /*
         * This doesn't really make sense as the place to dismiss the
         * popups, but there don't seem to be any other useful hooks
         * that happen early enough to keep from getting complaints
         * about having leaked the window.
         */
        //dismissPopup();

        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        if (mPendingSync != null) {
            // Just keep what we last restored.
            ss.selectedId = mPendingSync.selectedId;
            ss.firstId = mPendingSync.firstId;
            ss.viewTop = mPendingSync.viewTop;
            ss.position = mPendingSync.position;
            ss.height = mPendingSync.height;
            ss.filter = mPendingSync.filter;
            ss.inActionMode = mPendingSync.inActionMode;
            ss.checkedItemCount = mPendingSync.checkedItemCount;
            ss.checkState = mPendingSync.checkState;
            ss.checkIdState = mPendingSync.checkIdState;
            return ss;
        }

        boolean haveChildren = getChildCount() > 0 && mItemCount > 0;
        long selectedId = getSelectedItemId();
        ss.selectedId = selectedId;
        ss.height = getHeight();

        if (selectedId >= 0) {
            // Remember the selection
            ss.viewTop = mSelectedTop;
            ss.position = getSelectedItemPosition();
            ss.firstId = INVALID_POSITION;
        } else {
            if (haveChildren && mFirstPosition > 0) {
                // Remember the position of the first child.
                // We only do this if we are not currently at the top of
                // the list, for two reasons:
                // (1) The list may be in the process of becoming empty, in
                // which case mItemCount may not be 0, but if we try to
                // ask for any information about position 0 we will crash.
                // (2) Being "at the top" seems like a special case, anyway,
                // and the user wouldn't expect to end up somewhere else when
                // they revisit the list even if its content has changed.
                View v = getChildAt(0);
                ss.viewTop = v.getTop();
                int firstPos = mFirstPosition;
                if (firstPos >= mItemCount) {
                    firstPos = mItemCount - 1;
                }
                ss.position = firstPos;
                ss.firstId = mAdapter.getItemId(firstPos);
            } else {
                ss.viewTop = 0;
                ss.firstId = INVALID_POSITION;
                ss.position = 0;
            }
        }

//        ss.filter = null;
//        if (mFiltered) {
//            final EditText textFilter = mTextFilter;
//            if (textFilter != null) {
//                Editable filterText = textFilter.getText();
//                if (filterText != null) {
//                    ss.filter = filterText.toString();
//                }
//            }
//        }

        ss.inActionMode = mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode != null;

        if (mCheckStates != null) {
            ss.checkState = mCheckStates.clone();
        }
        if (mCheckedIdStates != null) {
            final LongSparseArray<Integer> idState = new LongSparseArray<Integer>();
            final int count = mCheckedIdStates.size();
            for (int i = 0; i < count; i++) {
                idState.put(mCheckedIdStates.keyAt(i), mCheckedIdStates.valueAt(i));
            }
            ss.checkIdState = idState;
        }
        ss.checkedItemCount = mCheckedItemCount;

//        if (mRemoteAdapter != null) {
//            mRemoteAdapter.saveRemoteViewsCache();
//        }

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        mDataChanged = true;

        mSyncHeight = ss.height;

        if (ss.selectedId >= 0) {
            mNeedSync = true;
            mPendingSync = ss;
            mSyncRowId = ss.selectedId;
            mSyncPosition = ss.position;
            mSpecificTop = ss.viewTop;
            mSyncMode = SYNC_SELECTED_POSITION;
        } else if (ss.firstId >= 0) {
            setSelectedPositionInt(INVALID_POSITION);
            // Do this before setting mNeedSync since setNextSelectedPosition looks at mNeedSync
            setNextSelectedPositionInt(INVALID_POSITION);
            mSelectorPosition = INVALID_POSITION;
            mNeedSync = true;
            mPendingSync = ss;
            mSyncRowId = ss.firstId;
            mSyncPosition = ss.position;
            mSpecificTop = ss.viewTop;
            mSyncMode = SYNC_FIRST_POSITION;
        }

        //setFilterText(ss.filter);

        if (ss.checkState != null) {
            mCheckStates = ss.checkState;
        }

        if (ss.checkIdState != null) {
            mCheckedIdStates = ss.checkIdState;
        }

        mCheckedItemCount = ss.checkedItemCount;

        if (ss.inActionMode && mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL &&
                mMultiChoiceModeCallback != null) {
            mChoiceActionMode = startActionMode(mMultiChoiceModeCallback);
        }

        requestLayout();
    }

    @Override
    public View getSelectedView() {
        if (mItemCount > 0 && mSelectedPosition >= 0) {
            return getChildAt(mSelectedPosition - mFirstPosition);
        } else {
            return null;
        }
    }

    /**
     * Sets the recycler listener to be notified whenever a View is set aside in
     * the recycler for later reuse. This listener can be used to free resources
     * associated to the View.
     *
     * @param listener The recycler listener to be notified of views set aside
     *        in the recycler.
     *
     * @see zy.fool.widget.FoolAbsView.RecycleBin
     * @see android.widget.AbsListView.RecyclerListener
     */
    public void setRecyclerListener(RecyclerListener listener) {
        mRecycler.mRecyclerListener = listener;
    }

    class AdapterDataSetObserver extends FoolAdapterView<ListAdapter>.AdapterDataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            //
            if (mFastScroller != null) {
                mFastScroller.onSectionsChanged();
            }
        }

        @Override
        public void onInvalidated() {
        	super.onInvalidated();
            if (mFastScroller != null) {
                mFastScroller.onSectionsChanged();
            }
        }
    }

    /**
     * A RecyclerListener is used to receive a notification whenever a View is placed
     * inside the RecycleBin's measuredAndUnused heap. This listener is used to free resources
     * associated to Views placed in the RecycleBin.
     *
     * @see zy.fool.widget.FoolAbsView.RecycleBin
     * @see FoolAbsView#setRecyclerListener(zy.fool.widget.FoolAbsView.RecyclerListener)
     */
    public static interface RecyclerListener {
        /**
         * Indicates that the specified View was moved into the recycler's measuredAndUnused heap.
         * The view is not displayed on screen any more and any expensive resource
         * associated with the view should be discarded.
         *
         * @param view
         */
        void onMovedTomeasuredAndUnusedHeap(View view);
    }

    /**
     * The RecycleBin facilitates reuse of views across layouts. The RecycleBin has two levels of
     * storage: ActiveViews and measuredAndUnusedViews. ActiveViews are those views which were onscreen at the
     * start of a layout. By construction, they are displaying current information. At the end of
     * layout, all views in ActiveViews are demoted to measuredAndUnusedViews. measuredAndUnusedViews are old views that
     * could potentially be used by the adapter to avoid allocating views unnecessarily.
     *
     * @see android.widget.AbsListView#setRecyclerListener(android.widget.AbsListView.RecyclerListener)
     * @see android.widget.AbsListView.RecyclerListener
     */
    class RecycleBin {
        private RecyclerListener mRecyclerListener;

        /**
         * The position of the first view stored in mActiveViews.
         */
        private int mFirstActivePosition;

        /**
         * Views that were on screen at the start of layout. This array is populated at the start of
         * layout, and at the end of layout all view in mActiveViews are moved to mMeasuredAndUnusedViews.
         * Views in mActiveViews represent a contiguous range of Views, with position of the first
         * view store in mFirstActivePosition.
         */
        private View[] mActiveViews = new View[0];

        /**
         * Unsorted views that can be used by the adapter as a convert view.
         */
        private ArrayList<View>[] mMeasuredAndUnusedViews;

        private int mViewTypeCount;

        private ArrayList<View> mCurrentMeasuredAndUnused;

        private ArrayList<View> mSkippedMeasuredAndUnused;

        private SparseArray<View> mTransientStateViews;

        public void setViewTypeCount(int viewTypeCount) {

            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }

            //noinspection unchecked
            ArrayList<View>[] measuredAndUnusedViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
            	measuredAndUnusedViews[i] = new ArrayList<View>();
            }
            mViewTypeCount = viewTypeCount;
            mCurrentMeasuredAndUnused = measuredAndUnusedViews[0];
            mMeasuredAndUnusedViews = measuredAndUnusedViews;

        }

        public void markChildrenDirty() {

            if (mViewTypeCount == 1) {
                final ArrayList<View> measuredAndUnused = mCurrentMeasuredAndUnused;
                final int measuredAndUnusedCount = measuredAndUnused.size();
                for (int i = 0; i < measuredAndUnusedCount; i++) {
                    measuredAndUnused.get(i).forceLayout();
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> measuredAndUnused = mMeasuredAndUnusedViews[i];
                    final int measuredAndUnusedCount = measuredAndUnused.size();

                    for (int j = 0; j < measuredAndUnusedCount; j++) {
                        measuredAndUnused.get(j).forceLayout();
                        //Forces this view to be laid out during the next layout pass.
                        //This method does not call requestLayout() or forceLayout() on the parent.

                    }
                }
            }
            if (mTransientStateViews != null) {
                final int count = mTransientStateViews.size();
                for (int i = 0; i < count; i++) {
                    mTransientStateViews.valueAt(i).forceLayout();
                }
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
        	return viewType >= 0;
        }

        /**
         * Clears the measuredAndUnused heap.
         */
        void clear() {

            if (mViewTypeCount == 1) {
                final ArrayList<View> measuredAndUnused = mCurrentMeasuredAndUnused;
                final int measuredAndUnusedCount = measuredAndUnused.size();
                for (int i = 0; i < measuredAndUnusedCount; i++) {
                    removeDetachedView(measuredAndUnused.remove(measuredAndUnusedCount - 1 - i), false);
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> measuredAndUnused = mMeasuredAndUnusedViews[i];
                    final int measuredAndUnusedCount = measuredAndUnused.size();
                    for (int j = 0; j < measuredAndUnusedCount; j++) {
                        removeDetachedView(measuredAndUnused.remove(measuredAndUnusedCount - 1 - j), false);
                    }
                }
            }
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        /**
         * Fill ActiveViews with all of the children of the AbsListView.
         *
         * @param childCount The minimum number of views mActiveViews should hold
         * @param firstActivePosition The position of the first view that will be stored in
         *        mActiveViews
         */
        void fillActiveViews(int childCount, int firstActivePosition) {

            if (mActiveViews.length < childCount) {
                mActiveViews = new View[childCount];
            }
            mFirstActivePosition = firstActivePosition;

            final View[] activeViews = mActiveViews;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                // Don't put header or footer views into the measuredAndUnused heap
                if (lp != null && lp.viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                    // Note:  We do place AdapterView.ITEM_VIEW_TYPE_IGNORE in active views.
                    //        However, we will NOT place them into measuredAndUnused views.
                    activeViews[i] = child;
                 }
            }
        }

        /**
         * Get the view corresponding to the specified position. The view will be removed from
         * mActiveViews if it is found.
         *
         * @param position The position to look up in mActiveViews
         * @return The view if it is found, null otherwise
         */
        View getActiveView(int position) {

            int index = position - mFirstActivePosition;
            final View[] activeViews = mActiveViews;
            if (index >=0 && index < activeViews.length) {
                final View match = activeViews[index];
                activeViews[index] = null;
                return match;
            }
            return null;
        }

        View getTransientStateView(int position) {

            if (mTransientStateViews == null) {
                return null;
            }
            final int index = mTransientStateViews.indexOfKey(position);
            if (index < 0) {
                return null;
            }
            final View result = mTransientStateViews.valueAt(index);
            mTransientStateViews.removeAt(index);
            return result;
        }

        /**
         * Dump any currently saved views with transient state.
         */
        void clearTransientStateViews() {
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        /**
         * @return A view from the measuredAndUnusedViews collection. These are unordered.
         */
        View getMeasuredAndUnusedView(int position) {
        	if (mViewTypeCount == 1) {
                return retrieveFrommeasuredAndUnused(mCurrentMeasuredAndUnused, position);
            } else {
                int whichmeasuredAndUnused = mAdapter.getItemViewType(position);
                if (whichmeasuredAndUnused >= 0 && whichmeasuredAndUnused < mMeasuredAndUnusedViews.length) {
                    return retrieveFrommeasuredAndUnused(mMeasuredAndUnusedViews[whichmeasuredAndUnused], position);
                }
            }
            return null;
        }

        /**
         * Put a view into the measuredAndUnusedViews list. These views are unordered.
         *
         * @param measuredAndUnused The view to add
         */
        void addMeasuredAndUnusedView(View measuredAndUnused, int position) {
        	Log.i(TAG_STRING + " addmeasuredAndUnusedView position ", String.valueOf(position));

        	LayoutParams lp = (LayoutParams) measuredAndUnused.getLayoutParams();
            if (lp == null) {
                return;
            }

            lp.measuredAndUnusedFromPosition = position;


            // Don't put header or footer views or views that should be ignored
            // into the measuredAndUnused heap
            int viewType = lp.viewType;
            final boolean measuredAndUnusedHasTransientState = measuredAndUnused.hasTransientState();
            if (!shouldRecycleViewType(viewType) || measuredAndUnusedHasTransientState) {
                if (viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER || measuredAndUnusedHasTransientState) {
                    if (mSkippedMeasuredAndUnused == null) {
                        mSkippedMeasuredAndUnused = new ArrayList<View>();
                    }
                    mSkippedMeasuredAndUnused.add(measuredAndUnused);
                }
                if (measuredAndUnusedHasTransientState) {
                    if (mTransientStateViews == null) {
                        mTransientStateViews = new SparseArray<View>();
                    }
                    measuredAndUnused.onStartTemporaryDetach();
                    mTransientStateViews.put(position, measuredAndUnused);
                }
                return;
            }

            measuredAndUnused.onStartTemporaryDetach();

            if (mViewTypeCount == 1) {
                mCurrentMeasuredAndUnused.add(measuredAndUnused);
            } else {
                mMeasuredAndUnusedViews[viewType].add(measuredAndUnused);
            }

            //measuredAndUnused.setAccessibilityDelegate(null);

            if (mRecyclerListener != null) {
                mRecyclerListener.onMovedTomeasuredAndUnusedHeap(measuredAndUnused);
            }
        }

        /**
         * Finish the removal of any views that skipped the measuredAndUnused heap.
         */
        void removeSkippedMeasuredAndUnused() {

        	if (mSkippedMeasuredAndUnused == null) {
                return;
            }
            final int count = mSkippedMeasuredAndUnused.size();
            for (int i = 0; i < count; i++) {
                removeDetachedView(mSkippedMeasuredAndUnused.get(i), false);
            }
            mSkippedMeasuredAndUnused.clear();
        }

        /**
         * Move all views remaining in mActiveViews to mmeasuredAndUnused Views.
         */
        void moveMeasuredAndUnusedToActiveViews() {

            final View[] activeViews = mActiveViews;
            final boolean hasListener = mRecyclerListener != null;
            final boolean multiplemeasuredAndUnuseds = mViewTypeCount > 1;

            ArrayList<View> measuredAndUnusedViews = mCurrentMeasuredAndUnused;
            final int count = activeViews.length;
            for (int i = count - 1; i >= 0; i--) {
                final View victim = activeViews[i];
                if (victim != null) {
                    final LayoutParams lp
                            = (LayoutParams) victim.getLayoutParams();
                    int whichMeasuredAndUnused = lp.viewType;

                    activeViews[i] = null;

                    final boolean measuredAndUnusedHasTransientState = victim.hasTransientState();
                    if (!shouldRecycleViewType(whichMeasuredAndUnused) || measuredAndUnusedHasTransientState) {
                        // Do not move views that should be ignored
                        if (whichMeasuredAndUnused != ITEM_VIEW_TYPE_HEADER_OR_FOOTER ||
                                measuredAndUnusedHasTransientState) {
                            removeDetachedView(victim, false);
                        }
                        if (measuredAndUnusedHasTransientState) {
                            if (mTransientStateViews == null) {
                                mTransientStateViews = new SparseArray<View>();
                            }
                            mTransientStateViews.put(mFirstActivePosition + i, victim);
                        }
                        continue;
                    }

                    if (multiplemeasuredAndUnuseds) {
                        measuredAndUnusedViews = mMeasuredAndUnusedViews[whichMeasuredAndUnused];
                    }
                    victim.onStartTemporaryDetach();
                    lp.measuredAndUnusedFromPosition = mFirstActivePosition + i;
                    measuredAndUnusedViews.add(victim);

                    //victim.setAccessibilityDelegate(null);

                    if (hasListener) {
                        mRecyclerListener.onMovedTomeasuredAndUnusedHeap(victim);
                    }
                }
            }

            pruneMeasuredAndUnusedViews();
        }


        /**
         * Makes sure that the size of mmeasuredAndUnusedViews does not exceed the size of mActiveViews.
         * (This can happen if an adapter does not recycle its views).
         */
        private void pruneMeasuredAndUnusedViews() {

            final int maxViews = mActiveViews.length;
            final int viewTypeCount = mViewTypeCount;
            final ArrayList<View>[] measuredAndUnusedViews = mMeasuredAndUnusedViews;
            for (int i = 0; i < viewTypeCount; ++i) {
                final ArrayList<View> measuredAndUnusedPile = measuredAndUnusedViews[i];
                int size = measuredAndUnusedPile.size();
                final int extras = size - maxViews;
                size--;
                for (int j = 0; j < extras; j++) {
                	removeDetachedView(measuredAndUnusedPile.remove(size--), false);

                }
            }

            if (mTransientStateViews != null) {
                for (int i = 0; i < mTransientStateViews.size(); i++) {
                    final View v = mTransientStateViews.valueAt(i);
                    if (!v.hasTransientState()) {
                        mTransientStateViews.removeAt(i);
                        i--;
                    }
                }
            }
        }

        /**
         * Puts all views in the measuredAndUnused heap into the supplied list.
         */
        void reclaimMeasuredAndUnusedViews(List<View> views) {

            if (mViewTypeCount == 1) {
                views.addAll(mCurrentMeasuredAndUnused);
            } else {
                final int viewTypeCount = mViewTypeCount;
                final ArrayList<View>[] measuredAndUnusedViews = mMeasuredAndUnusedViews;
                for (int i = 0; i < viewTypeCount; ++i) {
                    final ArrayList<View> measuredAndUnusedPile = measuredAndUnusedViews[i];
                    views.addAll(measuredAndUnusedPile);
                }
            }
        }

        /**
         * Updates the cache color hint of all known views.
         *
         * @param color The new cache color hint.
         */
        void setCacheColorHint(int color) {

            if (mViewTypeCount == 1) {
                final ArrayList<View> measuredAndUnused = mCurrentMeasuredAndUnused;
                final int measuredAndUnusedCount = measuredAndUnused.size();
                for (int i = 0; i < measuredAndUnusedCount; i++) {
                    measuredAndUnused.get(i).setDrawingCacheBackgroundColor(color);
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> measuredAndUnused = mMeasuredAndUnusedViews[i];
                    final int measuredAndUnusedCount = measuredAndUnused.size();
                    for (int j = 0; j < measuredAndUnusedCount; j++) {
                        measuredAndUnused.get(j).setDrawingCacheBackgroundColor(color);
                    }
                }
            }

            // Just in case this is called during a layout pass
            final View[] activeViews = mActiveViews;
            final int count = activeViews.length;
            for (int i = 0; i < count; ++i) {
                final View victim = activeViews[i];
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }
    }

    static View retrieveFrommeasuredAndUnused(ArrayList<View> measuredAndUnusedViews, int position) {
        int size = measuredAndUnusedViews.size();
        if (size > 0) {
            // See if we still have a view for this position.
            for (int i=0; i<size; i++) {
                View view = measuredAndUnusedViews.get(i);
                if (((LayoutParams)view.getLayoutParams()).measuredAndUnusedFromPosition == position) {
                    measuredAndUnusedViews.remove(i);
                    return view;
                }
            }
            return measuredAndUnusedViews.remove(size - 1);
        } else {
            return null;
        }
    }

    /**
     * Get a view and have it show the data associated with the specified
     * position. This is called when we have already discovered that the view is
     * not available for reuse in the recycle bin. The only choices left are
     * converting an old view or making a new one.
     *
     * @param position The position to display
     * @param isMeasuredAndUnused Array of at least 1 boolean, the first entry will become true if
     *                the returned view was taken from the measuredAndUnused heap, false if otherwise.
     *
     * @return A view displaying the data associated with the specified position
     */
    View obtainMeasuredAndUnusedViewToUse(int position, boolean[] isMeasuredAndUnused) {

        isMeasuredAndUnused[0] = false;
        View measuredAndUnusedView;

        measuredAndUnusedView = mRecycler.getTransientStateView(position);
        if (measuredAndUnusedView != null) {
            return measuredAndUnusedView;
        }

        measuredAndUnusedView = mRecycler.getMeasuredAndUnusedView(position);

        View child;
        if (measuredAndUnusedView != null) {

            child = mAdapter.getView(position, measuredAndUnusedView, this);

            if (child.getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
                child.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            }

            if (child != measuredAndUnusedView) {
                mRecycler.addMeasuredAndUnusedView(measuredAndUnusedView, position);
                if (mCacheColorHint != 0) {
                    child.setDrawingCacheBackgroundColor(mCacheColorHint);
                }
            } else {
                isMeasuredAndUnused[0] = true;
                child.onStartTemporaryDetach();
            }
        } else {
            child = mAdapter.getView(position, null, this);

            if (child.getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
                child.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            }

            if (mCacheColorHint != 0) {
                child.setDrawingCacheBackgroundColor(mCacheColorHint);
            }
        }

        if (mAdapterHasStableIds) {
            final ViewGroup.LayoutParams vlp = child.getLayoutParams();
            LayoutParams lp;
            if (vlp == null) {
                lp = (LayoutParams) generateDefaultLayoutParams();
            } else if (!checkLayoutParams(vlp)) {
                lp = (LayoutParams) generateLayoutParams(vlp);
            } else {
                lp = (LayoutParams) vlp;
            }
            lp.itemId = mAdapter.getItemId(position);
            child.setLayoutParams(lp);
        }

        return child;
    }

    /**
     * Maps a point to a position in the list.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The position of the item which contains the specified point, or
     *         {@link #INVALID_POSITION} if the point does not intersect an item.
     */
    public int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            System.out.println(child.toString());

            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return mFirstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }

    /**
     * Maps a point to a the rowId of the item which intersects that point.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The rowId of the item which contains the specified point, or {@link #INVALID_ROW_ID}
     *         if the point does not intersect an item.
     */
    public long pointToRowId(int x, int y) {
        int position = pointToPosition(x, y);
        if (position >= 0) {
            return mAdapter.getItemId(position);
        }
        return INVALID_ROW_ID;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            // A disabled view that is clickable still consumes the touch
            // events, it just doesn't respond to them.

        	return isClickable() || isLongClickable();
        }

		return onTouchMultiSpecialHoverEvent(ev);
    }

    private boolean onTouchMultiSpecialHoverEvent(MotionEvent event) {

    	// TODO Auto-generated method stub
        final int action = event.getActionMasked();
        View view;
        float sumX = 0,sumY = 0;

		int motionPosition = mMotionPosition;

    	switch (action) {
    	case  MotionEvent.ACTION_DOWN:{
    		m1TouchDownTime = System.currentTimeMillis();
    		mActivePointsNum = event.getPointerCount();

    		mFirstX = mInitialKeyPointers[0][0] = event.getX();
    	    mFirstY = mInitialKeyPointers[1][0] = event.getY();

    		if (action != MotionEvent.ACTION_UP && action != MotionEvent.ACTION_CANCEL) {

    			int pointCount = event.getPointerCount();

    			if(pointCount>=mActivePointsNum){
    				final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;

    				final int skipIndex = pointerUp ?event.getActionIndex() : -1;
    				final int count = pointCount>3?3:pointCount;
    				//Determine focal point
    				for(int i = 0;i<count;i++){
    					if(skipIndex == i)
    						continue;
    					sumX += event.getX(i);
    					sumY += event.getY(i);
    				}

    				for(int j = 0;j<count;j++){
    					if(skipIndex == j)
    						continue;
    					mKeyPointers[0][j] = event.getX(j);
    					mKeyPointers[1][j] = event.getY(j);
    				}

    				final int div = pointerUp?pointCount-1:pointCount;

    				float x = sumX/div;
					float y = sumY/div;

    				motionPosition = pointToPosition((int)x, (int)y);
    			}
    	   }

    		switch (mTouchMode) {
    		case TOUCH_MODE_OVERFLING: {
    			mFlingRunnable.endFling();
    		    if (mPositionScroller != null) {
    		    	mPositionScroller.stop();
    		    }
                mTouchMode = TOUCH_MODE_OVERSCROLL;
                mMotionX = (int) event.getX();
                mMotionY = (int) (mLastY = (int)event.getY());
                mMotionCorrection = 0;
                mActivePointerId = event.getPointerId(0);
                mDirection = 0;
                break;
    		 }

    		 default: {
    		    mActivePointerId = event.getPointerId(0);
    		    if (!mDataChanged) {
    		       if ((mTouchMode != TOUCH_MODE_FLING) && (motionPosition >= 0) && (getAdapter().isEnabled(motionPosition))) {
    		           // User clicked on an actual view (and was not stopping a fling).
    		           // It might be a click or a scroll. Assume it is a click until
                       // proven otherwise

    		    	   mTouchMode = TOUCH_MODE_DOWN;

    		    	   // FIXME Debounce
    		           if (mPendingCheckForTap == null) {
    		        	   mPendingCheckForTap = new CheckForTap();
    		           }
                       postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
    		       } else {
    		           if (mTouchMode == TOUCH_MODE_FLING) {
    		        	   // Stopped a fling. It is a scroll.
    		               createScrollingCache();
                           mTouchMode = TOUCH_MODE_SCROLL;
                           mMotionCorrection = 0;
    		               motionPosition = findMotionRow((int) mMotionY);
    		               mFlingRunnable.flywheelTouch();
    		           }
    		       }
    		    }

    		    if (motionPosition >= 0) {

    		    	// Remember where the motion event started
    		        view = getChildAt(motionPosition - mFirstPosition);
    		        mMotionViewOriginalTop = view.getTop();
    	        }

                mMotionX = (int) mKeyPointers[0][mActivePointerId];
                mMotionY = (int) mKeyPointers[1][mActivePointerId];
                mMotionPosition = motionPosition;
                mLastY = Integer.MIN_VALUE;
                break;
                }
    		}

    		mTouchMode = TOUCH_MODE_DOWN;

    		break;
    	}

    	case MotionEvent.ACTION_MOVE:{

    		if (mDataChanged) {
    			// Re-sync everything if data has been changed
    			// since the scroll operation can query the adapter.
                layoutChildren();
            }

    		view = getChildAt(mMotionPosition);
    		int childHeight = view.getHeight();

  			int pointerIndex = event.findPointerIndex(mActivePointerId);

  			if (pointerIndex == -1) {
  				pointerIndex = 0;
  				mActivePointerId = event.getPointerId(pointerIndex);
  			}

  			final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
			final int skipIndex = pointerUp ?event.getActionIndex() : -1;

			for(int j = 0;j<mActivePointsNum;j++){
				if(skipIndex == j)
					continue;
				mKeyPointers[0][j] = event.getX(j);
				mKeyPointers[1][j] = event.getY(j);
			}

    		//if(mActivePointsNum>1&&ispull){
    		if(mActivePointsNum>1){
    			mPulldeltaXs[0] = (int) (mKeyPointers[0][0]-mInitialKeyPointers[0][0]);
    			mPulldeltaYs[0] = (int) (mKeyPointers[1][0]-mInitialKeyPointers[1][0]);

    			mPulldeltaXs[1] = (int) (mKeyPointers[0][1]-mInitialKeyPointers[0][1]);
    			mPulldeltaYs[1] = (int) (mKeyPointers[1][1]-mInitialKeyPointers[1][1]);
    			if(currentMotionIsPull())

    			mTouchMode = TOUCH_MODE_DONE_WAITING;
    		}

    		switch (mTouchMode) {
    			case TOUCH_MODE_DOWN:
    			case TOUCH_MODE_TAP:
    			case TOUCH_MODE_DONE_WAITING:
    				
    				if(startPullIfNeeded())break;

    				//check if we have moved far enough that it looks more like a scroll than a tap
    				//item scroll
    				else
    				if(startScrollItemIfNeeded((int)mKeyPointers[0][mActivePointerId],(int)mKeyPointers[1][mActivePointerId]))break;
    				else
    				startScrollIfNeeded((int)mKeyPointers[1][mActivePointerId]);

    				break;

    			case TOUCH_MODE_SCROLL:
    			case TOUCH_MODE_OVERSCROLL:
    				//((int)mKeyPointers[1][mActivePointerId]);
                    scrollIfNeeded((int) mKeyPointers[1][mActivePointerId]);
    				break;
    			case TOUCH_MODE_PULL:
    				if(mPendingItemPull != null){
    					postDelayed(mPendingItemPull, PULLING_TIME);
    				}
    				break;
    			case TOUCH_MODE_OVERPULL:
                    if(mPendingItemPull!=null){
                        mPendingItemPull.performPull();
    				}
    				break;
    			case TOUCH_MODE_SLIDE:
    			case TOUCH_MODE_OVERSLIDE:
    				scrollItemIfNeed((int)mKeyPointers[0][mActivePointerId]);
    				break;
    		}
    	break;
    	}

    	case MotionEvent.ACTION_UP:{
    		mActivePointsNum = 0;
    		switch (mTouchMode) {
	    		case TOUCH_MODE_DOWN:
	    		case TOUCH_MODE_TAP:
	  			case TOUCH_MODE_DONE_WAITING:
	  				final View childView = getChildAt(motionPosition-mFirstPosition);
	  				final float x0 = event.getX();

	  				final boolean inList = x0 > mListPadding.left && x0 < getWidth() - mListPadding.right;

	    			if (childView != null && !childView.hasFocusable() && inList) {
	    				if (mTouchMode != TOUCH_MODE_DOWN) {
	    		       	   childView.setPressed(false);
	    				}
	    				if (mPerformClick == null) {
	    	               mPerformClick = new PerformClick();
	    	            }

	    			    final PerformClick performClick = mPerformClick;
	    			       performClick.mClickMotionPosition = motionPosition;
	    			       performClick.rememberWindowAttachCount();
	    			       mResurrectToPosition = motionPosition;

	    			       if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_TAP) {
	    			    	   final Handler handler = getHandler();
	    			           if (handler != null) {
	    			           	  handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ?mPendingCheckForTap : mPendingCheckForLongPress);
	    			           }

	    			           if(mLayoutMode!=LAYOUT_SPOT){
	    							mLayoutMode = LAYOUT_NORMAL;
	    			           }

	    			           if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
	    			        	   mTouchMode = TOUCH_MODE_TAP;
	    			               setSelectedPositionInt(mMotionPosition);
	    			               layoutChildren();
	    			               childView.setPressed(true);
	    			               positionSelector(mMotionPosition, childView);
	    			               setPressed(true);
	    			               if (mSelector != null) {
	    			            	   Drawable d = mSelector.getCurrent();
	    			                   if (d != null && d instanceof TransitionDrawable) {
	    			                      ((TransitionDrawable) d).resetTransition();
	    			                    }
	    			               }
	    			               if (mTouchModeReset != null) {
	    			            	   	removeCallbacks(mTouchModeReset);
	    			               }

	    			               mTouchModeReset = new Runnable() {
	    			                  @Override
	    			                  public void run() {
	    	                              mTouchModeReset = null;
	    	                              mTouchMode = TOUCH_MODE_REST;
	    			                      childView.setPressed(false);
	    			                      setPressed(false);
	    			                      if (!mDataChanged) {
	    			                     	performClick.run();
	    			                      }
	    			                  }
	    			               };
	    			               postDelayed(mTouchModeReset, ViewConfiguration.getPressedStateDuration());

	    			           } else {
	    			               mTouchMode = TOUCH_MODE_REST;
	    			               updateSelectorState();
	    			           }
	    			           return true;

	    			        } else if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
	    			        	performClick.run();
	    			        }
	    			}

	    			mTouchMode = TOUCH_MODE_REST;
	                updateSelectorState();
					break;

	  			case TOUCH_MODE_SCROLL:
	  				final int childCount = getChildCount();
	    		    if (childCount > 0) {
		    		    final int firstChildTop = getChildAt(0).getTop();
		                final int lastChildBottom = getChildAt(childCount - 1).getBottom();
		                final int contentTop = mListPadding.top;
		    		    final int contentBottom = getHeight() - mListPadding.bottom;

		    		    if (mFirstPosition == 0 && firstChildTop >= contentTop &&
		    		                            mFirstPosition + childCount < mItemCount &&
		    		                            lastChildBottom <= getHeight() - contentBottom) {
		    		    	mTouchMode = TOUCH_MODE_REST;
		    		    	reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
		    		    } else {
		    		    	final VelocityTracker velocityTracker = mVelocityTracker;
		    		    	velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

		    		    	final int initialVelocity = (int)(velocityTracker.getYVelocity(mActivePointerId) * mVelocityScale);

		    		    	// Fling if we have enough velocity and we aren't at a boundary.
		    		    	// Since we can potentially overfling more than we can overscroll, don't
		    		    	// allow the weird behavior where you can scroll to a boundary then
		    		    	// fling further.

		    		    	if (Math.abs(initialVelocity) > mMinimumVelocity && !((mFirstPosition == 0 &&
		    		                                        firstChildTop == contentTop - mOverscrollDistance) ||
		    		                                  (mFirstPosition + childCount == mItemCount &&
		    		                                        lastChildBottom == contentBottom + mOverscrollDistance))) {
		    		    		if (mFlingRunnable == null) {
		    		    			mFlingRunnable = new FlingRunnable();
		    		            }
		    		    		reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
		    		    		mFlingRunnable.start(-initialVelocity);

		    		    	} else {
		    		            mTouchMode = TOUCH_MODE_REST;
		    		            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
		    		            if (mFlingRunnable != null) {
		    		            	mFlingRunnable.endFling();
		    		            }

		    		            if (mPositionScroller != null) {
		    		                mPositionScroller.stop();
		    		            }
		    		    	}
		    		    }
		    		}else {
		    		    mTouchMode = TOUCH_MODE_REST;
			            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			        }
	    		    break;

	  			case TOUCH_MODE_OVERSCROLL:
	    		    if (mFlingRunnable == null) {
	    		    	mFlingRunnable = new FlingRunnable();
	    		    }
	    		    final VelocityTracker velocityTracker = mVelocityTracker;
	    		    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
	    		    final int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);

	    		    reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);

	    		    if (Math.abs(initialVelocity) > mMinimumVelocity) {
	    		        mFlingRunnable.startOverfling(-initialVelocity);
	    		    } else {
	    		    	mFlingRunnable.startSpringback();
	    		    }
	    		    break;
	  			case TOUCH_MODE_PULL:
                    if(mPendingItemPull!=null){
                        postDelayed(mPendingItemPull, PULLING_TIME);;
                    }
                    break;
	  			case TOUCH_MODE_OVERPULL:{
                    if(mPendingItemPull!=null)
                    mPendingItemPull.endPull();
                    removeCallbacks(mPendingItemPull);

	  				mTouchModeReset = new Runnable() {
		                  @Override
		                  public void run() {
                          mTouchModeReset = null;
                          mTouchMode = TOUCH_MODE_REST;
		                      setPressed(false);
		                      if (!mDataChanged) {
		                    	  if(mSpotClick!=null)
		                    	  mSpotClick.run();
		                      }
		                  }
		            };
		            postDelayed(mTouchModeReset, ViewConfiguration.getPressedStateDuration());
	  				break;
                }
	  			case TOUCH_MODE_SLIDE:
	  			case TOUCH_MODE_OVERSLIDE:{
	  				if(mPerformItemSlide!=null)
                        mPerformItemSlide.setSlideState(ItemSlide.SLIDE_DONE);
                        removeCallbacks(mPerformItemSlide);
                    //mTouchMode = TOUCH_MODE_REST;

                    mTouchModeReset = new Runnable() {
		                  @Override
		                  public void run() {
                          mTouchModeReset = null;
                          mTouchMode = TOUCH_MODE_REST;
		                      setPressed(false);
		                      if (!mDataChanged&&mSpotSlide!=null) {
		                    	  mSpotSlide.run();
		                      }
		                  }
		            };
		            postDelayed(mTouchModeReset,SLIDING_TIME);
	  				break;
                }
    		}

    		setPressed(false);
    		if (mEdgeGlowTop != null) {
    		    mEdgeGlowTop.onRelease();
    		    mEdgeGlowBottom.onRelease();
    		}
            // Need to redraw since we probably aren't drawing the selector anymore
    		invalidate();
    		final Handler handler = getHandler();
    		if (handler != null) {
                handler.removeCallbacks(mPendingCheckForLongPress);
            }
    		recycleVelocityTracker();
    		mActivePointerId = INVALID_POINTER;

    		if (PROFILE_SCROLLING) {
    		    if (mScrollProfilingStarted) {
    		        mScrollProfilingStarted = false;
                }
    		}

//    		            if (mScrollStrictSpan != null) {
//    		                mScrollStrictSpan.finish();
//    		                mScrollStrictSpan = null;
//    		            }
    		 break;
    	}
    	case MotionEvent.ACTION_POINTER_UP:{

//    				for(int i = 0;i<3;i++){
//    						mKeyPointers[0][i] = -1;
//    						mKeyPointers[1][i] = -1;
//    					}
//    				}
    		onSecondaryPointerUp(event);
    		if(!isAllowedPull)
    		{
    			motionPosition = pointToPosition(mMotionX, mMotionY);
    		}

    		if (motionPosition >= 0) {
    			// Remember where the motion event started
    			view= getChildAt(motionPosition - mFirstPosition);
    			mMotionViewOriginalTop = view.getTop();
    			mMotionPosition = motionPosition;
    		}
    		mLastY = mMotionY;
    	break;
    	}

    	case MotionEvent.ACTION_POINTER_DOWN:{
    		m2TouchDownTime = System.currentTimeMillis();

    		int pcount = event.getPointerCount();
    		final int count = pcount>3?3:pcount;
    		final int index = event.getActionIndex();
    		final int activeId = event.getPointerId(index)>2?2:event.getPointerId(index);

    		mActivePointsNum = count;
    		for(int i = 0;i<count;i++){
    			mInitialKeyPointers[0][i] = event.getX(i);
    			mInitialKeyPointers[1][i] = event.getY(i);
    		}
    		//  no pull, New pointers take over dragging duties

    			mMotionCorrection = 0;
                mActivePointerId = activeId;
                mMotionX = (int) (mKeyPointers[0][mActivePointerId]=(int)event.getX(activeId));
                mMotionY = (int)(mKeyPointers[1][mActivePointerId]=(int)event.getY(activeId));

                motionPosition = pointToPosition(mMotionX, mMotionY);
        		if (motionPosition >= 0) {
        			// Remember where the motion event started
        			view = getChildAt(motionPosition - mFirstPosition);
        			mMotionViewOriginalTop = view.getTop();
        			mMotionPosition = motionPosition;
        		}
                mLastY = mMotionY;
    		break;
    	}

    	case MotionEvent.ACTION_CANCEL: {
    		for(int i = 0;i<3;i++){
   				mKeyPointers[0][i] = -1;
   				mKeyPointers[1][i] = -1;
    		}

//    					final Handler handler = getHandler();
//    		            if (handler != null) {
//    		                handler.removeCallbacks(mPendingItemPull);
//    		            }
    		switch (mTouchMode) {
    		case TOUCH_MODE_OVERSCROLL:
    			if (mFlingRunnable == null) {
    				mFlingRunnable = new FlingRunnable();
    		    }
    			mFlingRunnable.startSpringback();
    			break;
    		case TOUCH_MODE_OVERFLING:
    		    // Do nothing - let it play out.
    			break;
    		default:
    			mTouchMode = TOUCH_MODE_REST;
    		    setPressed(false);
    		    View motionView = this.getChildAt(mMotionPosition - mFirstPosition);
    		    if (motionView != null) {
    		    	motionView.setPressed(false);
    		    }

    		    clearScrollingCache();
    		    final Handler handler = getHandler();
    		    if (handler != null) {
    		        handler.removeCallbacks(mPendingCheckForLongPress);
    		    }

    		    recycleVelocityTracker();
    		 }

    		 if (mEdgeGlowTop != null) {
    			 mEdgeGlowTop.onRelease();
    		     mEdgeGlowBottom.onRelease();
    		 }

    		 mActivePointerId = INVALID_POINTER;
    		 break;
    		 }
    	        	//add case Touch_mode_OverPull and touch_mode_overslide
    	        	//clear cache and remove callback
    	}

    	return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	 int action = ev.getAction();
         View v;
         long touchDownTime = 0;

         if (mPositionScroller != null) {
             mPositionScroller.stop();
         }

         if (!mIsAttached) {
             // Something isn't right.
             // Since we rely on being attached to get data set change notifications,
             // don't risk doing anything where we might try to resync and find things
             // in a bogus state.
             return false;
         }

         if (mFastScroller != null) {
             boolean intercepted = mFastScroller.onInterceptTouchEvent(ev);
             if (intercepted) {
                 return true;
             }
         }

         switch (action & MotionEvent.ACTION_MASK) {
         case MotionEvent.ACTION_DOWN: {
             int touchMode = mTouchMode;
             if (touchMode == TOUCH_MODE_OVERFLING || touchMode == TOUCH_MODE_OVERSCROLL) {
                 mMotionCorrection = 0;
                 return true;
             }

             final int x = (int) ev.getX();
             final int y = (int) ev.getY();
             mActivePointerId = ev.getPointerId(0);
             touchDownTime =ev.getDownTime();

             int motionPosition = findMotionRow(y);
             if (touchMode != TOUCH_MODE_FLING && motionPosition >= 0) {
                 // User clicked on an actual view (and was not stopping a fling).
                 // Remember where the motion event started
                 v = getChildAt(motionPosition - mFirstPosition);
                 mMotionViewOriginalTop = v.getTop();
                 mMotionX = x;
                 mMotionY = y;
                 mMotionPosition = motionPosition;
                 mTouchMode = TOUCH_MODE_DOWN;
                 clearScrollingCache();
             }
             mLastY = Integer.MIN_VALUE;
             initOrResetVelocityTracker();
             mVelocityTracker.addMovement(ev);
             if (touchMode == TOUCH_MODE_FLING) {
                 return true;
             }
             break;
         }

         case MotionEvent.ACTION_MOVE: {
             switch (mTouchMode) {
             case TOUCH_MODE_DOWN:
                 int pointerIndex = ev.findPointerIndex(mActivePointerId);
                 if (pointerIndex == -1) {
                     pointerIndex = 0;
                     mActivePointerId = ev.getPointerId(pointerIndex);
                 }
                 final int x = (int) ev.getX(pointerIndex);
                 final int y = (int) ev.getY(pointerIndex);

                 initVelocityTrackerIfNotExists();
                 mVelocityTracker.addMovement(ev);
                 if(mActivePointsNum>1){
                	 if(startPullIfNeeded())
                		 return true;

                 }else if(startScrollItemIfNeeded(x,y)){
                	 return true;
                 }
                 if (startScrollIfNeeded(y)) {
                     return true;
                 }
                 break;
             }
             break;
         }

         case MotionEvent.ACTION_CANCEL:
         case MotionEvent.ACTION_UP: {
             mTouchMode = TOUCH_MODE_REST;
             mActivePointerId = INVALID_POINTER;
             recycleVelocityTracker();
             reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
             break;
         }

         case MotionEvent.ACTION_POINTER_UP: {
        	 mActivePointsNum = ev.getPointerCount();
        	 onSecondaryPointerUp(ev);
             break;
         }

         }

         return false;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);

        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mMotionX = (int) ev.getX(newPointerIndex);
            mMotionY = (int) ev.getY(newPointerIndex);
            mMotionCorrection = 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }

        if(mTouchMode == TOUCH_MODE_PULL&&mActivePointerId<1){
            mTouchMode = TOUCH_MODE_OVERPULL;
        }
    }

    /**
     * @return A position to select. First we try mSelectedPosition. If that has been clobbered by
     * entering touch mode, we then try mResurrectToPosition. Values are pinned to the range
     * of items available in the adapter
     */
    int reconcileSelectedPosition() {
        int position = mSelectedPosition;
        if (position < 0) {
            position = mResurrectToPosition;
        }
        position = Math.max(0, position);
        position = Math.min(position, mItemCount - 1);
        return position;
    }


    /**
     * Find the row closest to y. This row will be used as the motion row when scrolling
     *
     * @param y Where the user touched
     * @return The position of the first (or only) item in the row containing y
     */
    abstract int findMotionRow(int y);

    /**
     * Find the row closest to y. This row will be used as the motion row when scrolling.
     *
     * @param y Where the user touched
     * @return The position of the first (or only) item in the row closest to y
     */
    int findClosestMotionRow(int y) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return INVALID_POSITION;
        }

        final int motionRow = findMotionRow(y);
        return motionRow != INVALID_POSITION ? motionRow : mFirstPosition + childCount - 1;
    }

    /**
     * Returns the checked state of the specified position. The result is only
     * valid if the choice mode has been set to {@link #CHOICE_MODE_SINGLE}
     * or {@link #CHOICE_MODE_MULTIPLE}.
     *
     * @param position The item whose checked state to return
     * @return The item's checked state or <code>false</code> if choice mode
     *         is invalid
     */
    public boolean isItemChecked(int position) {
    	//if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
    	if (mCheckStates != null) {
    	    return mCheckStates.get(position);
        }

        return false;
    }

    void confirmCheckedPositionsById() {
        // Clear out the positional check states, we'll rebuild it below from IDs.
        mCheckStates.clear();

        boolean checkedCountChanged = false;
        for (int checkedIndex = 0; checkedIndex < mCheckedIdStates.size(); checkedIndex++) {
            final long id = mCheckedIdStates.keyAt(checkedIndex);
            final int lastPos = mCheckedIdStates.valueAt(checkedIndex);
            final long lastPosId = mAdapter.getItemId(lastPos);
            if (id != lastPosId) {
                // Look around to see if the ID is nearby. If not, uncheck it.
                final int start = Math.max(0, lastPos - CHECK_POSITION_SEARCH_DISTANCE);
                final int end = Math.min(lastPos + CHECK_POSITION_SEARCH_DISTANCE, mItemCount);
                boolean found = false;
                for (int searchPos = start; searchPos < end; searchPos++) {
                    final long searchId = mAdapter.getItemId(searchPos);
                    if (id == searchId) {
                        found = true;
                        mCheckStates.put(searchPos, true);
                        mCheckedIdStates.setValueAt(checkedIndex, searchPos);
                        break;
                    }
                }

                if (!found) {
                    mCheckedIdStates.delete(id);
                    checkedIndex--;
                    mCheckedItemCount--;
                    checkedCountChanged = true;
                    if (mChoiceActionMode != null && mMultiChoiceModeCallback != null) {
                        mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode, lastPos, id, false);
                    }
                }
            } else {
                mCheckStates.put(lastPos, true);
            }
        }

        if (checkedCountChanged && mChoiceActionMode != null) {
            mChoiceActionMode.invalidate();
        }
    }

    /**
	 * @param xy0Time
	 * @return boolean
	 *
	 */
	private boolean isEffectiveMotion(long xy0Time){
		long now = System.currentTimeMillis();
		if(now<ViewConfiguration.getTapTimeout()+xy0Time&&now<ViewConfiguration.getJumpTapTimeout()+xy0Time){
			return true;
		}else{
            return false;
        }
	}

	private boolean startPullIfNeeded(){
		//杩�婊ゅ�ㄤ��
		boolean ispull = false;

		if(mActivePointsNum>1){
			int ix0 = (int)mInitialKeyPointers[0][0];
			int iy0 = (int)mInitialKeyPointers[1][0];
			int ix1 = (int)mInitialKeyPointers[0][1];
			int iy1 = (int)mInitialKeyPointers[1][1];

			int x0 = (int)mKeyPointers[0][0];
			int y0 = (int)mKeyPointers[1][0];
			int x1 = (int)mKeyPointers[0][1];
			int y1 = (int)mKeyPointers[1][1];

			int ixmax = Math.max(ix0 , ix1);
			int ixmin = Math.min(ix0, ix1);
			int iymax = Math.max(iy0, iy1);
			int iymin = Math.min(iy0, iy1);

			if((isEffectiveMotion(m1TouchDownTime)|| isEffectiveMotion(m2TouchDownTime))){

                    ispull = true;

					//----------------------------------------------------------

					if((x0>ixmax&&x1<ixmin)||(x1>ixmax&&x0<ixmin)){
						//x-out
						mPullMode= MULTI_TOUCH_PULLOUT;

					}else if((x0>ixmin&&x0<ixmax&&x1<ixmax&&x1>ixmin)){
						//x-in
						mPullMode= MULTI_TOUCH_PULLIN;
					}

					if((y0>iymax&&y1<iymin)||(y1>iymax&&y0<iymin)){
						//x-out
						mPullMode= MULTI_TOUCH_PULLOUT;

					}else if((y0>iymin&&y0<iymax&&y1<iymax&&y1>iymin)){
						//x-in
						mPullMode= MULTI_TOUCH_PULLIN;
					}

					//----------------------------------------------------------

					final Handler handler = getHandler();

					// Handler should not be null unless the FoolAbsView is not attached to a
					// window, which would make it very hard to scroll it... but the monkeys
					// say it's possible.
			        if (handler != null) {
			            handler.removeCallbacks(mPendingCheckForLongPress);
			        }
			        setPressed(false);
			        View motionView = getChildAt(mMotionPosition - mFirstPosition);
			        if (motionView != null) {
			            motionView.setPressed(false);
			        }

			        if(mPendingItemPull == null){
			        	mPendingItemPull = new PerformItemPull();
			        }

			        mTouchMode  = TOUCH_MODE_PULL;
			}

		}else{
            mTouchMode  = TOUCH_MODE_REST;
            ispull = false;
        }

		return ispull;

	}

	/**
     * Interface definition for a callback to be invoked when the list or grid
     * has been scrolled.
     */
    public interface OnScrollListener {

        /**
         * The view is not scrolling. Note navigating the list using the trackball counts as
         * being in the idle state since these transitions are not animated.
         */
        public static int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen
         */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed a fling. The
         * animation is now coasting to a stop
         */
        public static int SCROLL_STATE_FLING = 2;

        /**
         * Callback method to be invoked while the list view or grid view is being scrolled. If the
         * view is being scrolled, this method will be called before the next frame of the scroll is
         * rendered. In particular, it will be called before any calls to
         * {@link android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)}.
         *
         * @param view The view whose scroll state is being reported
         *
         * @param scrollState The current scroll state. One of {@link #SCROLL_STATE_IDLE},
         * {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}.
         */
        public void onScrollStateChanged(FoolAbsView view, int scrollState);

        /**
         * Callback method to be invoked when the list or grid has been scrolled. This will be
         * called after the scroll has completed
         * @param view The view whose scroll state is being reported
         * @param firstVisibleItem the index of the first visible cell (ignore if
         *        visibleItemCount == 0)
         * @param visibleItemCount the number of visible cells
         * @param totalItemCount the number of items in the list adaptor
         */
        public void onScroll(FoolAbsView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount);
    }

    /**
     * Set the listener that will receive notifications every time the list scrolls.
     *
     * @param l the scroll listener
     */
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
        invokeOnItemScrollListener();
    }

    private OnScrollListener mOnScrollListener ;

    /**
     * Notify our scroll listener (if there is one) of a change in scroll state
     */
    void invokeOnItemScrollListener() {
        if (mFastScroller != null) {
            mFastScroller.onScroll(this, mFirstPosition, getChildCount(), mItemCount);
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, mFirstPosition, getChildCount(), mItemCount);
        }
        onScrollChanged(0, 0, 0, 0); // dummy values, View's implementation does not use these.
    }

	public interface OnPullListener{
		public boolean onPull();
	}

	public interface OnPullOutListener{
		public boolean onPullOut();
	}

	public interface OnPullInListener{
		public boolean onPullIn();
	}

	public interface OnSlideListener{
		public boolean onSlide();
	}

	public interface OnSlideLeftListener{
		public boolean onSlideLeft();
	}

	public interface OnSlideRightListener{
		public boolean onSlideRight();
	}

	public void setOnPullOutListener(OnPullOutListener onPullOutListener){
		this.mPullOutListener = onPullOutListener;
	}

	public void setOnPullInListener(OnPullInListener mListener){
		this.mPullInListener = mListener;
	}

	public void setOnSlideListener(OnSlideListener mListener){
		this.mSlideListener = mListener;
	}

	public void setOnSlideLeftListener(OnSlideLeftListener mListener){
		this.mSlideLeftListener = mListener;
	}

	public void setOnSlideRightListener(OnSlideRightListener mListener){
		this.mSlideRightListener = mListener;
	}

   /**
	* FoolAbsView extends LayoutParams to provide a place to hold the view type.
	*/
	public static class LayoutParams extends ViewGroup.LayoutParams {
		/**
	     * View type for this view, as returned by
	     * {@link android.widget.Adapter#getItemViewType(int) }
	     */
	    int viewType;

	    /**
	    * When this boolean is set, the view has been added to the AbsListView
	    * at least once. It is used to know whether headers/footers have already
	    * been added to the list view and whether they should be treated as
	    * recycled views or not.
	    */
	    boolean recycledHeaderFooter;

	    /**
	     * When an AbsListView is measured with an AT_MOST measure spec, it needs
	     * to obtain children views to measure itself. When doing so, the children
	     * are not attached to the window, but put in the recycler which assumes
	     * they've been attached before. Setting this flag will force the reused
	     * view to be attached to the window rather than just attached to the
	     * parent.
	     */
	    boolean forceAdd;

	    /**
	     * The position the view was removed from when pulled out of the
	     * measuredAndUnused heap.
	     * @hide
	     */
	    int measuredAndUnusedFromPosition;

	    /**
	     * The ID the view represents
	     */
	    long itemId = -1;

	    public LayoutParams(Context c, AttributeSet attrs) {
	       super(c, attrs);
	    }

	    public LayoutParams(int w, int h) {
	       super(w, h);
        }

	    public LayoutParams(int w, int h, int viewType) {
	       super(w, h);
	       this.viewType = viewType;
	    }

	    public LayoutParams(ViewGroup.LayoutParams source) {
	       super(source);
	    }
	}

	  /**
     * A MultiChoiceModeListener receives events for {@link android.widget.AbsListView#CHOICE_MODE_MULTIPLE_MODAL}.
     * It acts as the {@link android.view.ActionMode.Callback} for the selection mode and also receives
     * {@link #onItemCheckedStateChanged(android.view.ActionMode, int, long, boolean)} events when the user
     * selects and deselects list items.
     */
    public interface MultiChoiceModeListener extends ActionMode.Callback {
        /**
         * Called when an item is checked or unchecked during selection mode.
         *
         * @param mode The {@link android.view.ActionMode} providing the selection mode
         * @param position Adapter position of the item that was checked or unchecked
         * @param id Adapter ID of the item that was checked or unchecked
         * @param checked <code>true</code> if the item is now checked, <code>false</code>
         *                if the item is now unchecked.
         */
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id, boolean checked);
    }

	class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        private MultiChoiceModeListener mWrapped;

        public void setWrapped(MultiChoiceModeListener wrapped) {
            mWrapped = wrapped;
        }

        public boolean hasWrappedCallback() {
            return mWrapped != null;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (mWrapped.onCreateActionMode(mode, menu)) {
                // Initialize checked graphic state?
                setLongClickable(false);
                return true;
            }
            return false;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return mWrapped.onPrepareActionMode(mode, menu);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            mWrapped.onDestroyActionMode(mode);
            mChoiceActionMode = null;

            // Ending selection mode means deselecting everything.
            clearChoices();

            mDataChanged = true;
            rememberSyncState();
            requestLayout();

            setLongClickable(true);
        }

        public void onItemCheckedStateChanged(ActionMode mode,
                int position, long id, boolean checked) {
            mWrapped.onItemCheckedStateChanged(mode, position, id, checked);

            // If there are no items selected we no longer need the selection mode.
            if (getCheckedItemCount() == 0) {
                mode.finish();
            }
        }
    }

	 /**
     * Indicates that this list is always drawn on top of a solid, single-color, opaque
     * background
     */
    private int mCacheColorHint;

    /**
     * When set to a non-zero value, the cache color hint indicates that this list is always drawn
     * on top of a solid, single-color, opaque background.
     *
     * Zero means that what's behind this object is translucent (non solid) or is not made of a
     * single color. This hint will not affect any existing background drawable set on this view (
     * typically set via {@link #setBackgroundDrawable(android.graphics.drawable.Drawable)}).
     *
     * @param color The background color
     */
    public void setCacheColorHint(int color) {
        if (color != mCacheColorHint) {
            mCacheColorHint = color;
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).setDrawingCacheBackgroundColor(color);
            }
            mRecycler.setCacheColorHint(color);
        }
    }

    /**
     * When set to a non-zero value, the cache color hint indicates that this list is always drawn
     * on top of a solid, single-color, opaque background
     *
     * @return The cache color hint
     */
    public int getCacheColorHint() {
        return mCacheColorHint;
    }

    /**
     * Fires an "on scroll state changed" event to the registered
     * {@link android.widget.AbsListView.OnScrollListener}, if any. The state change
     * is fired only if the specified state is different from the previously known state.
     *
     * @param newState The new scroll state.
     */
    void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            if (mOnScrollListener != null) {
                mLastScrollState = newState;
                mOnScrollListener.onScrollStateChanged(this, newState);
            }
        }
    }

    /**
    * Smoothly scroll to the specified adapter position. The view will
    * scroll such that the indicated position is displayed.
    * @param position Scroll to this adapter position.
    */
    public void smoothScrollToPosition(int position) {
       if (mPositionScroller == null) {
           mPositionScroller = new PositionScroller();
       }
       mPositionScroller.start(position);
    }

    /**
     * Smoothly scroll to the specified adapter position. The view will
     * scroll such that the indicated position is displayed, but it will
     * stop early if scrolling further would scroll boundPosition out of
     * view.
     * @param position Scroll to this adapter position.
     * @param boundPosition Do not scroll if it would move this adapter
     *          position out of view.
     */
    public void smoothScrollToPosition(int position, int boundPosition) {
        if (mPositionScroller == null) {
            mPositionScroller = new PositionScroller();
        }
        mPositionScroller.start(position, boundPosition);
    }

    /**
     * Smoothly scroll by distance pixels over duration milliseconds.
     * @param distance Distance to scroll in pixels.
     * @param duration Duration of the scroll animation in milliseconds.
     */
    public void smoothScrollBy(int distance, int duration) {
        smoothScrollBy(distance, duration, false);
    }

    void smoothScrollBy(int distance, int duration, boolean linear) {
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable();
        }

        // No sense starting to scroll if we're not going anywhere
        final int firstPos = mFirstPosition;
        final int childCount = getChildCount();
        final int lastPos = firstPos + childCount;
        final int topLimit = getPaddingTop();
        final int bottomLimit = getHeight() - getPaddingBottom();

        if (distance == 0 || mItemCount == 0 || childCount == 0 ||
                (firstPos == 0 && getChildAt(0).getTop() == topLimit && distance < 0) ||
                (lastPos == mItemCount &&
                        getChildAt(childCount - 1).getBottom() == bottomLimit && distance > 0)) {
           // mFlingRunnable.endFling();
            if (mPositionScroller != null) {
                mPositionScroller.stop();
            }
        } else {
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
            mFlingRunnable.startScroll(distance, duration, linear);
        }
    }

    /**
     * Allows RemoteViews to scroll relatively to a position.
     */
    void smoothScrollByOffset(int position) {
        int index = -1;
        if (position < 0) {
            index = getFirstVisiblePosition();
        } else if (position > 0) {
            index = getLastVisiblePosition();
        }

        if (index > -1) {
            View child = getChildAt(index - getFirstVisiblePosition());
            if (child != null) {
                Rect visibleRect = new Rect();
                if (child.getGlobalVisibleRect(visibleRect)) {
                    // the child is partially visible
                    int childRectArea = child.getWidth() * child.getHeight();
                    int visibleRectArea = visibleRect.width() * visibleRect.height();
                    float visibleArea = (visibleRectArea / (float) childRectArea);
                    final float visibleThreshold = 0.75f;
                    if ((position < 0) && (visibleArea < visibleThreshold)) {
                        // the top index is not perceivably visible so offset
                        // to account for showing that top index as well
                        ++index;
                    } else if ((position > 0) && (visibleArea < visibleThreshold)) {
                        // the bottom index is not perceivably visible so offset
                        // to account for showing that bottom index as well
                        --index;
                    }
                }
                smoothScrollToPosition(Math.max(0, Math.min(getCount(), index + position)));
            }
        }
    }

    private boolean startScrollIfNeeded(int y) {
        // Check if we have moved far enough that it looks more like a
        // scroll than a tap
        final int deltaY = y - mMotionY;
        final int distance = Math.abs(deltaY);
        final boolean overscroll = mScrollY != 0;

        boolean multiscroll = false;
       
        if(mActivePointsNum>1){
        	if(currentMotionIsPull()){
        		mTouchMode = TOUCH_MODE_PULL;
        		return false;
        	}
        	else{
        		multiscroll = true;
        	}
        }

        if (overscroll || distance > mTouchSlop) {
            createScrollingCache();
            if (overscroll) {
                mTouchMode = TOUCH_MODE_OVERSCROLL;
                mMotionCorrection = 0;
            } else {
                mTouchMode = TOUCH_MODE_SCROLL;
                mMotionCorrection = deltaY > 0 ? mTouchSlop : -mTouchSlop;
            }
            final Handler handler = getHandler();
            // Handler should not be null unless the AbsListView is not attached to a
            // window, which would make it very hard to scroll it... but the monkeys
            // say it's possible.
            if (handler != null) {
                handler.removeCallbacks(mPendingCheckForLongPress);
            }
            setPressed(false);
            View motionView = getChildAt(mMotionPosition - mFirstPosition);
            if (motionView != null) {
                motionView.setPressed(false);
            }
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            // Time to start stealing events! Once we've stolen them, don't let anyone
            // steal from us
            final ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            scrollIfNeeded(y);
            return true;
        }

        return false;
    }

    private void scrollIfNeeded(int y) {
        final int rawDeltaY = y - mMotionY;
        final int deltaY = rawDeltaY - mMotionCorrection;
        int incrementalDeltaY = mLastY != Integer.MIN_VALUE ? y - mLastY : deltaY;

        if (mTouchMode == TOUCH_MODE_SCROLL) {
            if (PROFILE_SCROLLING) {
                if (!mScrollProfilingStarted) {
                    mScrollProfilingStarted = true;
                }
            }

//            if (true) {
//                // If it's non-null, we're already in a scroll.
//            	// mScrollStrictSpan = StrictMode.enterCriticalSpan("AbsListView-scroll");
//            	StrictMode.enterCriticalSpan("AbsListView-scroll");
//            }

            if (y != mLastY) {
                // We may be here after stopping a fling and continuing to scroll.
                // If so, we haven't disallowed intercepting touch events yet.
                // Make sure that we do so in case we're in a parent that can intercept.
                if ((mGroupFlags & FLAG_DISALLOW_INTERCEPT) == 0 &&
                        Math.abs(rawDeltaY) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                final int motionIndex;
                if (mMotionPosition >= 0) {
                    motionIndex = mMotionPosition - mFirstPosition;
                } else {
                    // If we don't have a motion position that we can reliably track,
                    // pick something in the middle to make a best guess at things below.
                    motionIndex = getChildCount() / 2;
                }

                int motionViewPrevTop = 0;
                View motionView = this.getChildAt(motionIndex);
                if (motionView != null) {
                    motionViewPrevTop = motionView.getTop();
                }

                // No need to do all this work if we're not going to move anyway
                boolean atEdge = false;
                if (incrementalDeltaY != 0) {
                    atEdge = trackMotionScroll(deltaY, incrementalDeltaY);
                }

                // Check to see if we have bumped into the scroll limit
                motionView = this.getChildAt(motionIndex);
                if (motionView != null) {
                    // Check if the top of the motion view is where it is
                    // supposed to be
                    final int motionViewRealTop = motionView.getTop();
                    if (atEdge) {
                        // Apply overscroll

                        int overscroll = -incrementalDeltaY -
                                (motionViewRealTop - motionViewPrevTop);
                        overScrollBy(0, overscroll, 0, mScrollY, 0, 0,
                                0, mOverscrollDistance, true);
                        if (Math.abs(mOverscrollDistance) == Math.abs(mScrollY)) {
                            // Don't allow overfling if we're at the edge.
                            if (mVelocityTracker != null) {
                                mVelocityTracker.clear();
                            }
                        }

                        final int overscrollMode = getOverScrollMode();
                        if (overscrollMode == OVER_SCROLL_ALWAYS ||
                                (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS &&
                                        !contentFits())) {
                            mDirection = 0; // Reset when entering overscroll.
                            mTouchMode = TOUCH_MODE_OVERSCROLL;
                            if (rawDeltaY > 0) {
                                mEdgeGlowTop.onPull((float) overscroll / getHeight());
                                if (!mEdgeGlowBottom.isFinished()) {
                                    mEdgeGlowBottom.onRelease();
                                }
                                invalidate(mEdgeGlowTop.getBounds(false));
                            } else if (rawDeltaY < 0) {
                                mEdgeGlowBottom.onPull((float) overscroll / getHeight());
                                if (!mEdgeGlowTop.isFinished()) {
                                    mEdgeGlowTop.onRelease();
                                }
                                invalidate(mEdgeGlowBottom.getBounds(true));
                            }
                        }
                    }
                    mMotionY = y;
                }
                mLastY = y;
            }
        } else if (mTouchMode == TOUCH_MODE_OVERSCROLL) {
            if (y != mLastY) {
                final int oldScroll = mScrollY;
                final int newScroll = oldScroll - incrementalDeltaY;
                int newDirection = y > mLastY ? 1 : -1;

                if (mDirection == 0) {
                    mDirection = newDirection;
                }

                int overScrollDistance = -incrementalDeltaY;
                if ((newScroll < 0 && oldScroll >= 0) || (newScroll > 0 && oldScroll <= 0)) {
                    overScrollDistance = -oldScroll;
                    incrementalDeltaY += overScrollDistance;
                } else {
                    incrementalDeltaY = 0;
                }

                if (overScrollDistance != 0) {
                    overScrollBy(0, overScrollDistance, 0, mScrollY, 0, 0,
                            0, mOverscrollDistance, true);
                    final int overscrollMode = getOverScrollMode();
                    if (overscrollMode == OVER_SCROLL_ALWAYS ||
                            (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS &&
                                    !contentFits())) {
                        if (rawDeltaY > 0) {
                            mEdgeGlowTop.onPull((float) overScrollDistance / getHeight());
                            if (!mEdgeGlowBottom.isFinished()) {
                                mEdgeGlowBottom.onRelease();
                            }
                            invalidate(mEdgeGlowTop.getBounds(false));
                        } else if (rawDeltaY < 0) {
                            mEdgeGlowBottom.onPull((float) overScrollDistance / getHeight());
                            if (!mEdgeGlowTop.isFinished()) {
                                mEdgeGlowTop.onRelease();
                            }
                            invalidate(mEdgeGlowBottom.getBounds(true));
                        }
                    }
                }

                if (incrementalDeltaY != 0) {
                    // Coming back to 'real' list scrolling
                    if (mScrollY != 0) {
                        mScrollY = 0;
                        invalidateParentIfNeeded();
                    }

                    trackMotionScroll(incrementalDeltaY, incrementalDeltaY);

                    mTouchMode = TOUCH_MODE_SCROLL;

                    // We did not scroll the full amount. Treat this essentially like the
                    // start of a new touch scroll
                    final int motionPosition = findClosestMotionRow(y);

                    mMotionCorrection = 0;
                    View motionView = getChildAt(motionPosition - mFirstPosition);
                    mMotionViewOriginalTop = motionView != null ? motionView.getTop() : 0;
                    mMotionY = y;
                    mMotionPosition = motionPosition;
                }
                mLastY = y;
                mDirection = newDirection;
            }
        }
    }


    class PositionScroller implements Runnable {
        private static final int SCROLL_DURATION = 200;

        private static final int MOVE_DOWN_POS = 1;
        private static final int MOVE_UP_POS = 2;
        private static final int MOVE_DOWN_BOUND = 3;
        private static final int MOVE_UP_BOUND = 4;
        private static final int MOVE_OFFSET = 5;

        private int mMode;
        private int mTargetPos;
        private int mBoundPos;
        private int mLastSeenPos;
        private int mScrollDuration;
        private final int mExtraScroll;

        private int mOffsetFromTop;

        PositionScroller() {
            mExtraScroll = ViewConfiguration.get(getContext()).getScaledFadingEdgeLength();
        }

        void start(final int position) {
            stop();

            if (mDataChanged) {
                // Wait until we're back in a stable state to try this.
                mPositionScrollAfterLayout = new Runnable() {
                    @Override public void run() {
                        start(position);
                    }
                };
                return;
            }

            final int childCount = getChildCount();
            if (childCount == 0) {
                // Can't scroll without children.
                return;
            }

            final int firstPos = mFirstPosition;
            final int lastPos = firstPos + childCount - 1;

            int viewTravelCount;
            int clampedPosition = Math.max(0, Math.min(getCount() - 1, position));
            if (clampedPosition < firstPos) {
                viewTravelCount = firstPos - clampedPosition + 1;
                mMode = MOVE_UP_POS;
            } else if (clampedPosition > lastPos) {
                viewTravelCount = clampedPosition - lastPos + 1;
                mMode = MOVE_DOWN_POS;
            } else {
                scrollToVisible(clampedPosition, INVALID_POSITION, SCROLL_DURATION);
                return;
            }

            if (viewTravelCount > 0) {
                mScrollDuration = SCROLL_DURATION / viewTravelCount;
            } else {
                mScrollDuration = SCROLL_DURATION;
            }
            mTargetPos = clampedPosition;
            mBoundPos = INVALID_POSITION;
            mLastSeenPos = INVALID_POSITION;

            postOnAnimation(this);
        }

        void start(final int position, final int boundPosition) {
            stop();

            if (boundPosition == INVALID_POSITION) {
                start(position);
                return;
            }

            if (mDataChanged) {
                // Wait until we're back in a stable state to try this.
                mPositionScrollAfterLayout = new Runnable() {
                    @Override public void run() {
                        start(position, boundPosition);
                    }
                };
                return;
            }

            final int childCount = getChildCount();
            if (childCount == 0) {
                // Can't scroll without children.
                return;
            }

            final int firstPos = mFirstPosition;
            final int lastPos = firstPos + childCount - 1;

            int viewTravelCount;
            int clampedPosition = Math.max(0, Math.min(getCount() - 1, position));
            if (clampedPosition < firstPos) {
                final int boundPosFromLast = lastPos - boundPosition;
                if (boundPosFromLast < 1) {
                    // Moving would shift our bound position off the screen. Abort.
                    return;
                }

                final int posTravel = firstPos - clampedPosition + 1;
                final int boundTravel = boundPosFromLast - 1;
                if (boundTravel < posTravel) {
                    viewTravelCount = boundTravel;
                    mMode = MOVE_UP_BOUND;
                } else {
                    viewTravelCount = posTravel;
                    mMode = MOVE_UP_POS;
                }
            } else if (clampedPosition > lastPos) {
                final int boundPosFromFirst = boundPosition - firstPos;
                if (boundPosFromFirst < 1) {
                    // Moving would shift our bound position off the screen. Abort.
                    return;
                }

                final int posTravel = clampedPosition - lastPos + 1;
                final int boundTravel = boundPosFromFirst - 1;
                if (boundTravel < posTravel) {
                    viewTravelCount = boundTravel;
                    mMode = MOVE_DOWN_BOUND;
                } else {
                    viewTravelCount = posTravel;
                    mMode = MOVE_DOWN_POS;
                }
            } else {
                scrollToVisible(clampedPosition, boundPosition, SCROLL_DURATION);
                return;
            }

            if (viewTravelCount > 0) {
                mScrollDuration = SCROLL_DURATION / viewTravelCount;
            } else {
                mScrollDuration = SCROLL_DURATION;
            }
            mTargetPos = clampedPosition;
            mBoundPos = boundPosition;
            mLastSeenPos = INVALID_POSITION;

            postOnAnimation(this);
        }

        void startWithOffset(int position, int offset) {
            startWithOffset(position, offset, SCROLL_DURATION);
        }

        void startWithOffset(final int position, int offset, final int duration) {
            stop();

            if (mDataChanged) {
                // Wait until we're back in a stable state to try this.
                final int postOffset = offset;
                mPositionScrollAfterLayout = new Runnable() {
                    @Override public void run() {
                        startWithOffset(position, postOffset, duration);
                    }
                };
                return;
            }

            final int childCount = getChildCount();
            if (childCount == 0) {
                // Can't scroll without children.
                return;
            }

            offset += getPaddingTop();

            mTargetPos = Math.max(0, Math.min(getCount() - 1, position));
            mOffsetFromTop = offset;
            mBoundPos = INVALID_POSITION;
            mLastSeenPos = INVALID_POSITION;
            mMode = MOVE_OFFSET;

            final int firstPos = mFirstPosition;
            final int lastPos = firstPos + childCount - 1;

            int viewTravelCount;
            if (mTargetPos < firstPos) {
                viewTravelCount = firstPos - mTargetPos;
            } else if (mTargetPos > lastPos) {
                viewTravelCount = mTargetPos - lastPos;
            } else {
                // On-screen, just scroll.
                final int targetTop = getChildAt(mTargetPos - firstPos).getTop();
                smoothScrollBy(targetTop - offset, duration, true);
                return;
            }

            // Estimate how many screens we should travel
            final float screenTravelCount = (float) viewTravelCount / childCount;
            mScrollDuration = screenTravelCount < 1 ?
                    duration : (int) (duration / screenTravelCount);
            mLastSeenPos = INVALID_POSITION;

            postOnAnimation(this);
        }

        /**
         * Scroll such that targetPos is in the visible padded region without scrolling
         * boundPos out of view. Assumes targetPos is onscreen.
         */
        void scrollToVisible(int targetPos, int boundPos, int duration) {
            final int firstPos = mFirstPosition;
            final int childCount = getChildCount();
            final int lastPos = firstPos + childCount - 1;
            final int paddedTop = mListPadding.top;
            final int paddedBottom = getHeight() - mListPadding.bottom;

            if (targetPos < firstPos || targetPos > lastPos) {
                Log.w(TAG_STRING, "scrollToVisible called with targetPos " + targetPos +
                        " not visible [" + firstPos + ", " + lastPos + "]");
            }
            if (boundPos < firstPos || boundPos > lastPos) {
                // boundPos doesn't matter, it's already offscreen.
                boundPos = INVALID_POSITION;
            }

            final View targetChild = getChildAt(targetPos - firstPos);
            final int targetTop = targetChild.getTop();
            final int targetBottom = targetChild.getBottom();
            int scrollBy = 0;

            if (targetBottom > paddedBottom) {
                scrollBy = targetBottom - paddedBottom;
            }
            if (targetTop < paddedTop) {
                scrollBy = targetTop - paddedTop;
            }

            if (scrollBy == 0) {
                return;
            }

            if (boundPos >= 0) {
                final View boundChild = getChildAt(boundPos - firstPos);
                final int boundTop = boundChild.getTop();
                final int boundBottom = boundChild.getBottom();
                final int absScroll = Math.abs(scrollBy);

                if (scrollBy < 0 && boundBottom + absScroll > paddedBottom) {
                    // Don't scroll the bound view off the bottom of the screen.
                    scrollBy = Math.max(0, boundBottom - paddedBottom);
                } else if (scrollBy > 0 && boundTop - absScroll < paddedTop) {
                    // Don't scroll the bound view off the top of the screen.
                    scrollBy = Math.min(0, boundTop - paddedTop);
                }
            }

            smoothScrollBy(scrollBy, duration);
        }

        void stop() {
            removeCallbacks(this);
        }

        public void run() {
            final int listHeight = getHeight();
            final int firstPos = mFirstPosition;

            switch (mMode) {
            case MOVE_DOWN_POS: {
                final int lastViewIndex = getChildCount() - 1;
                final int lastPos = firstPos + lastViewIndex;

                if (lastViewIndex < 0) {
                    return;
                }

                if (lastPos == mLastSeenPos) {
                    // No new views, let things keep going.
                    postOnAnimation(this);
                    return;
                }

                final View lastView = getChildAt(lastViewIndex);
                final int lastViewHeight = lastView.getHeight();
                final int lastViewTop = lastView.getTop();
                final int lastViewPixelsShowing = listHeight - lastViewTop;
                final int extraScroll = lastPos < mItemCount - 1 ?
                        Math.max(mListPadding.bottom, mExtraScroll) : mListPadding.bottom;

                final int scrollBy = lastViewHeight - lastViewPixelsShowing + extraScroll;
                smoothScrollBy(scrollBy, mScrollDuration, true);

                mLastSeenPos = lastPos;
                if (lastPos < mTargetPos) {
                    postOnAnimation(this);
                }
                break;
            }

            case MOVE_DOWN_BOUND: {
                final int nextViewIndex = 1;
                final int childCount = getChildCount();

                if (firstPos == mBoundPos || childCount <= nextViewIndex
                        || firstPos + childCount >= mItemCount) {
                    return;
                }
                final int nextPos = firstPos + nextViewIndex;

                if (nextPos == mLastSeenPos) {
                    // No new views, let things keep going.
                    postOnAnimation(this);
                    return;
                }

                final View nextView = getChildAt(nextViewIndex);
                final int nextViewHeight = nextView.getHeight();
                final int nextViewTop = nextView.getTop();
                final int extraScroll = Math.max(mListPadding.bottom, mExtraScroll);
                if (nextPos < mBoundPos) {
                    smoothScrollBy(Math.max(0, nextViewHeight + nextViewTop - extraScroll),
                            mScrollDuration, true);

                    mLastSeenPos = nextPos;

                    postOnAnimation(this);
                } else  {
                    if (nextViewTop > extraScroll) {
                        smoothScrollBy(nextViewTop - extraScroll, mScrollDuration, true);
                    }
                }
                break;
            }

            case MOVE_UP_POS: {
                if (firstPos == mLastSeenPos) {
                    // No new views, let things keep going.
                    postOnAnimation(this);
                    return;
                }

                final View firstView = getChildAt(0);
                if (firstView == null) {
                    return;
                }
                final int firstViewTop = firstView.getTop();
                final int extraScroll = firstPos > 0 ?
                        Math.max(mExtraScroll, mListPadding.top) : mListPadding.top;

                smoothScrollBy(firstViewTop - extraScroll, mScrollDuration, true);

                mLastSeenPos = firstPos;

                if (firstPos > mTargetPos) {
                    postOnAnimation(this);
                }
                break;
            }

            case MOVE_UP_BOUND: {
                final int lastViewIndex = getChildCount() - 2;
                if (lastViewIndex < 0) {
                    return;
                }
                final int lastPos = firstPos + lastViewIndex;

                if (lastPos == mLastSeenPos) {
                    // No new views, let things keep going.
                    postOnAnimation(this);
                    return;
                }

                final View lastView = getChildAt(lastViewIndex);
                final int lastViewHeight = lastView.getHeight();
                final int lastViewTop = lastView.getTop();
                final int lastViewPixelsShowing = listHeight - lastViewTop;
                final int extraScroll = Math.max(mListPadding.top, mExtraScroll);
                mLastSeenPos = lastPos;
                if (lastPos > mBoundPos) {
                    smoothScrollBy(-(lastViewPixelsShowing - extraScroll), mScrollDuration, true);
                    postOnAnimation(this);
                } else {
                    final int bottom = listHeight - extraScroll;
                    final int lastViewBottom = lastViewTop + lastViewHeight;
                    if (bottom > lastViewBottom) {
                        smoothScrollBy(-(bottom - lastViewBottom), mScrollDuration, true);
                    }
                }
                break;
            }

            case MOVE_OFFSET: {
                if (mLastSeenPos == firstPos) {
                    // No new views, let things keep going.
                    postOnAnimation(this);
                    return;
                }

                mLastSeenPos = firstPos;

                final int childCount = getChildCount();
                final int position = mTargetPos;
                final int lastPos = firstPos + childCount - 1;

                int viewTravelCount = 0;
                if (position < firstPos) {
                    viewTravelCount = firstPos - position + 1;
                } else if (position > lastPos) {
                    viewTravelCount = position - lastPos;
                }

                // Estimate how many screens we should travel
                final float screenTravelCount = (float) viewTravelCount / childCount;

                final float modifier = Math.min(Math.abs(screenTravelCount), 1.f);
                if (position < firstPos) {
                    final int distance = (int) (-getHeight() * modifier);
                    final int duration = (int) (mScrollDuration * modifier);
                    smoothScrollBy(distance, duration, true);
                    postOnAnimation(this);
                } else if (position > lastPos) {
                    final int distance = (int) (getHeight() * modifier);
                    final int duration = (int) (mScrollDuration * modifier);
                    smoothScrollBy(distance, duration, true);
                    postOnAnimation(this);
                } else {
                    // On-screen, just scroll.
                    final int targetTop = getChildAt(position - firstPos).getTop();
                    final int distance = targetTop - mOffsetFromTop;
                    final int duration = (int) (mScrollDuration *
                            ((float) Math.abs(distance) / getHeight()));
                    smoothScrollBy(distance, duration, true);
                }
                break;
            }

            default:
                break;
            }
        }
    }

    /**
     * Responsible for fling behavior. Use {@link #start(int)} to
     * initiate a fling. Each frame of the fling is handled in {@link #run()}.
     * A FlingRunnable will keep re-posting itself until the fling is done.
     *
     */
    private class FlingRunnable implements Runnable {
        /**
         * Tracks the decay of a fling scroll
         */
        private final OverScroller mScroller;

        /**
         * Y value reported by mScroller on the previous fling
         */
        private int mLastFlingY;

        private final Runnable mCheckFlywheel = new Runnable() {
            public void run() {
                final int activeId = mActivePointerId;
                final VelocityTracker vt = mVelocityTracker;
                final OverScroller scroller = mScroller;
                if (vt == null || activeId == INVALID_POINTER) {
                    return;
                }

                vt.computeCurrentVelocity(1000, mMaximumVelocity);
                final float yvel = -vt.getYVelocity(activeId);

                if (Math.abs(yvel) >= mMinimumVelocity
                        && scroller.isScrollingInDirection(0, yvel)) {
                    // Keep the fling alive a little longer
                    postDelayed(this, FLYWHEEL_TIMEOUT);
                } else {
                    endFling();
                    mTouchMode = TOUCH_MODE_SCROLL;
                    reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                }
            }
        };

        private static final int FLYWHEEL_TIMEOUT = 40; // milliseconds

        FlingRunnable() {
            mScroller = new OverScroller(getContext());
        }

        void start(int initialVelocity) {
            int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingY = initialY;
            mScroller.setInterpolator(null);
            mScroller.fling(0, initialY, 0, initialVelocity,
                    0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            mTouchMode = TOUCH_MODE_FLING;
            postOnAnimation(this);

            if (PROFILE_FLINGING) {
                if (!mFlingProfilingStarted) {
                    //Debug.startMethodTracing("AbsListViewFling");
                    mFlingProfilingStarted = true;
                }
            }

//            if (mFlingStrictSpan == null) {
//                mFlingStrictSpan = StrictMode.enterCriticalSpan("AbsListView-fling");
//            }
        }

        void startSpringback() {
            if (mScroller.springBack(0, mScrollY, 0, 0, 0, 0)) {
                mTouchMode = TOUCH_MODE_OVERFLING;
                invalidate();
                postOnAnimation(this);
            } else {
                mTouchMode = TOUCH_MODE_REST;
                reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            }
        }

        void startOverfling(int initialVelocity) {
            mScroller.setInterpolator(null);
            mScroller.fling(0, mScrollY, 0, initialVelocity, 0, 0,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, 0, getHeight());
            mTouchMode = TOUCH_MODE_OVERFLING;
            invalidate();
            postOnAnimation(this);
        }

        void edgeReached(int delta) {
            mScroller.notifyVerticalEdgeReached(mScrollY, 0, mOverflingDistance);
            final int overscrollMode = getOverScrollMode();
            if (overscrollMode == OVER_SCROLL_ALWAYS ||
                    (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits())) {
                mTouchMode = TOUCH_MODE_OVERFLING;
                final int vel = (int) mScroller.getCurrVelocity();
                if (delta > 0) {
                    mEdgeGlowTop.onAbsorb(vel);
                } else {
                    mEdgeGlowBottom.onAbsorb(vel);
                }
            } else {
                mTouchMode = TOUCH_MODE_REST;
                if (mPositionScroller != null) {
                    mPositionScroller.stop();
                }
            }
            invalidate();
            postOnAnimation(this);
        }

        void startScroll(int distance, int duration, boolean linear) {
            int initialY = distance < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingY = initialY;
            mScroller.setInterpolator(linear ? sLinearInterpolator : null);
            mScroller.startScroll(0, initialY, 0, distance, duration);
            mTouchMode = TOUCH_MODE_FLING;
            postOnAnimation(this);
        }

        void endFling() {
            mTouchMode = TOUCH_MODE_REST;

            removeCallbacks(this);
            removeCallbacks(mCheckFlywheel);

            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            clearScrollingCache();
            mScroller.abortAnimation();
//
//            if (mFlingStrictSpan != null) {
//                mFlingStrictSpan.finish();
//                mFlingStrictSpan = null;
//            }
        }

        void flywheelTouch() {
            postDelayed(mCheckFlywheel, FLYWHEEL_TIMEOUT);
        }

        public void run() {
            switch (mTouchMode) {
            default:
                endFling();
                return;

            case TOUCH_MODE_SCROLL:
                if (mScroller.isFinished()) {
                    return;
                }
                // Fall through
            case TOUCH_MODE_FLING: {
                if (mDataChanged) {
                    layoutChildren();
                }

                if (mItemCount == 0 || getChildCount() == 0) {
                    endFling();
                    return;
                }

                final OverScroller scroller = mScroller;
                boolean more = scroller.computeScrollOffset();
                final int y = scroller.getCurrY();

                // Flip sign to convert finger direction to list items direction
                // (e.g. finger moving down means list is moving towards the top)
                int delta = mLastFlingY - y;

                // Pretend that each frame of a fling scroll is a touch scroll
                if (delta > 0) {
                    // List is moving towards the top. Use first view as mMotionPosition
                    mMotionPosition = mFirstPosition;
                    final View firstView = getChildAt(0);
                    mMotionViewOriginalTop = firstView.getTop();

                    // Don't fling more than 1 screen
                    delta = Math.min(getHeight() - mPaddingBottom - mPaddingTop - 1, delta);
                } else {
                    // List is moving towards the bottom. Use last view as mMotionPosition
                    int offsetToLast = getChildCount() - 1;
                    mMotionPosition = mFirstPosition + offsetToLast;

                    final View lastView = getChildAt(offsetToLast);
                    mMotionViewOriginalTop = lastView.getTop();

                    // Don't fling more than 1 screen
                    delta = Math.max(-(getHeight() - mPaddingBottom - mPaddingTop - 1), delta);
                }

                // Check to see if we have bumped into the scroll limit
                View motionView = getChildAt(mMotionPosition - mFirstPosition);
                int oldTop = 0;
                if (motionView != null) {
                    oldTop = motionView.getTop();
                }

                // Don't stop just because delta is zero (it could have been rounded)
                final boolean atEdge = trackMotionScroll(delta, delta);
                final boolean atEnd = atEdge && (delta != 0);
                if (atEnd) {
                    if (motionView != null) {
                        // Tweak the scroll for how far we overshot
                        int overshoot = -(delta - (motionView.getTop() - oldTop));
                        overScrollBy(0, overshoot, 0, mScrollY, 0, 0,
                                0, mOverflingDistance, false);
                    }
                    if (more) {
                        edgeReached(delta);
                    }
                    break;
                }

                if (more && !atEnd) {
                    if (atEdge) invalidate();
                    mLastFlingY = y;
                    postOnAnimation(this);
                } else {
                    endFling();

                    if (PROFILE_FLINGING) {
                        if (mFlingProfilingStarted) {
                            //Debug.stopMethodTracing();
                            mFlingProfilingStarted = false;
                        }

//                        if (mFlingStrictSpan != null) {
//                            mFlingStrictSpan.finish();
//                            mFlingStrictSpan = null;
//                        }
                    }
                }
                break;
            }

            case TOUCH_MODE_OVERFLING: {
                final OverScroller scroller = mScroller;
                if (scroller.computeScrollOffset()) {
                    final int scrollY = mScrollY;
                    final int currY = scroller.getCurrY();
                    final int deltaY = currY - scrollY;
                    if (overScrollBy(0, deltaY, 0, scrollY, 0, 0,
                            0, mOverflingDistance, false)) {
                        final boolean crossDown = scrollY <= 0 && currY > 0;
                        final boolean crossUp = scrollY >= 0 && currY < 0;
                        if (crossDown || crossUp) {
                            int velocity = (int) scroller.getCurrVelocity();
                            if (crossUp) velocity = -velocity;

                            // Don't flywheel from this; we're just continuing things.
                            scroller.abortAnimation();
                            start(velocity);
                        } else {
                            startSpringback();
                        }
                    } else {
                        invalidate();
                        postOnAnimation(this);
                    }
                } else {
                    endFling();
                }
                break;
            }
            }
        }
    }

    /**
     * Enables fast scrolling by letting the user quickly scroll through lists by
     * dragging the fast scroll thumb. The adapter attached to the list may want
     * to implement {@link android.widget.SectionIndexer} if it wishes to display alphabet preview and
     * jump between sections of the list.
     * @see android.widget.SectionIndexer
     * @see #isFastScrollEnabled()
     * @param enabled whether or not to enable fast scrolling
     */
    public void setFastScrollEnabled(boolean enabled) {
        mFastScrollEnabled = enabled;
        if (enabled) {
            if (mFastScroller == null) {
                mFastScroller = new FastScroller(getContext(), this);
            }
        } else {
            if (mFastScroller != null) {
                mFastScroller.stop();
                mFastScroller = null;
            }
        }
    }

    /**
     * Returns true if the fast scroller is enabled.
     *
     * @see #setFastScrollEnabled(boolean)
     * @return true if fast scroll is enabled, false otherwise
     */
    @ViewDebug.ExportedProperty
    public boolean isFastScrollEnabled() {
        if (mFastScroller == null) {
            return mFastScrollEnabled;
        } else {
            return mFastScroller.isVisible();
        }
    }

    private void createScrollingCache() {
        if (mScrollingCacheEnabled && !mCachingStarted && !isHardwareAccelerated()) {
            setChildrenDrawnWithCacheEnabled(true);
            setChildrenDrawingCacheEnabled(true);
            mCachingStarted = mCachingActive = true;
        }
    }

    private void clearScrollingCache() {
        if (!isHardwareAccelerated()) {
            if (mClearScrollingCache == null) {
                mClearScrollingCache = new Runnable() {

					public void run() {
                        if (mCachingStarted) {
                            mCachingStarted = mCachingActive = false;
                            setChildrenDrawnWithCacheEnabled(false);
                            if ((mPersistentDrawingCache & PERSISTENT_SCROLLING_CACHE) == 0) {
                                setChildrenDrawingCacheEnabled(false);
                            }
                            if (!isAlwaysDrawnWithCacheEnabled()) {
                                invalidate();
                            }
                        }
                    }
                };
            }
            post(mClearScrollingCache);
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Track a motion scroll
     *
     * @param deltaY Amount to offset mMotionView. This is the accumulated delta since the motion
     *        began. Positive numbers mean the user's finger is moving down the screen.
     * @param incrementalDeltaY Change in deltaY from the previous event.
     * @return true if we're already at the beginning/end of the list and have nothing to do.
     */
    boolean trackMotionScroll(int deltaY, int incrementalDeltaY) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }

        final int firstTop = getChildAt(0).getTop();
        final int lastBottom = getChildAt(childCount - 1).getBottom();

        final Rect listPadding = mListPadding;

        // "effective padding" In this case is the amount of padding that affects
        // how much space should not be filled by items. If we don't clip to padding
        // there is no effective padding.
        int effectivePaddingTop = 0;
        int effectivePaddingBottom = 0;
        if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
            effectivePaddingTop = listPadding.top;
            effectivePaddingBottom = listPadding.bottom;
        }

         // FIXME account for grid vertical spacing too?
        final int spaceAbove = effectivePaddingTop - firstTop;
        final int end = getHeight() - effectivePaddingBottom;
        final int spaceBelow = lastBottom - end;

        final int height = getHeight() - mPaddingBottom - mPaddingTop;
        if (deltaY < 0) {
            deltaY = Math.max(-(height - 1), deltaY);
        } else {
            deltaY = Math.min(height - 1, deltaY);
        }

        if (incrementalDeltaY < 0) {
            incrementalDeltaY = Math.max(-(height - 1), incrementalDeltaY);
        } else {
            incrementalDeltaY = Math.min(height - 1, incrementalDeltaY);
        }

        final int firstPosition = mFirstPosition;

        // Update our guesses for where the first and last views are
        if (firstPosition == 0) {
            mFirstPositionDistanceGuess = firstTop - listPadding.top;
        } else {
            mFirstPositionDistanceGuess += incrementalDeltaY;
        }
        if (firstPosition + childCount == mItemCount) {
            mLastPositionDistanceGuess = lastBottom + listPadding.bottom;
        } else {
            mLastPositionDistanceGuess += incrementalDeltaY;
        }

        final boolean cannotScrollDown = (firstPosition == 0 &&
                firstTop >= listPadding.top && incrementalDeltaY >= 0);
        final boolean cannotScrollUp = (firstPosition + childCount == mItemCount &&
                lastBottom <= getHeight() - listPadding.bottom && incrementalDeltaY <= 0);

        if (cannotScrollDown || cannotScrollUp) {
            return incrementalDeltaY != 0;
        }

        final boolean down = incrementalDeltaY < 0;

        final boolean inTouchMode = isInTouchMode();
        if (inTouchMode) {
            hideSelector();
        }

        final int headerViewsCount = getHeaderViewsCount();
        final int footerViewsStart = mItemCount - getFooterViewsCount();

        int start = 0;
        int count = 0;

        if (down) {
            int top = -incrementalDeltaY;
            if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
                top += listPadding.top;
            }
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (child.getBottom() >= top) {
                    break;
                } else {
                    count++;
                    int position = firstPosition + i;
                    if (position >= headerViewsCount && position < footerViewsStart) {
                        mRecycler.addMeasuredAndUnusedView(child, position);
                    }
                }
            }
        } else {
            int bottom = getHeight() - incrementalDeltaY;
            if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
                bottom -= listPadding.bottom;
            }
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                if (child.getTop() <= bottom) {
                    break;
                } else {
                    start = i;
                    count++;
                    int position = firstPosition + i;
                    if (position >= headerViewsCount && position < footerViewsStart) {
                        mRecycler.addMeasuredAndUnusedView(child, position);
                    }
                }
            }
        }

        mMotionViewNewTop = mMotionViewOriginalTop + deltaY;

        mBlockLayoutRequests = true;

        if (count > 0) {
            detachViewsFromParent(start, count);
            mRecycler.removeSkippedMeasuredAndUnused();
        }

        // invalidate before moving the children to avoid unnecessary invalidate
        // calls to bubble up from the children all the way to the top
        if (!awakenScrollBars()) {
           invalidate();
        }

        offsetTopAndBottom(incrementalDeltaY);

        if (down) {
            mFirstPosition += count;
        }

        final int absIncrementalDeltaY = Math.abs(incrementalDeltaY);
        if (spaceAbove < absIncrementalDeltaY || spaceBelow < absIncrementalDeltaY) {
            fillGap(down);
        }

        if (!inTouchMode && mSelectedPosition != INVALID_POSITION) {
            final int childIndex = mSelectedPosition - mFirstPosition;
            if (childIndex >= 0 && childIndex < getChildCount()) {
                positionSelector(mSelectedPosition, getChildAt(childIndex));
            }
        } else if (mSelectorPosition != INVALID_POSITION) {
            final int childIndex = mSelectorPosition - mFirstPosition;
            if (childIndex >= 0 && childIndex < getChildCount()) {
                positionSelector(INVALID_POSITION, getChildAt(childIndex));
            }
        } else {
            mSelectorRect.setEmpty();
        }

        mBlockLayoutRequests = false;

        invokeOnItemScrollListener();

        return false;
    }

    /**
     * Returns the number of header views in the list. Header views are special views
     * at the top of the list that should not be recycled during a layout.
     *
     * @return The number of header views, 0 in the default implementation.
     */
    int getHeaderViewsCount() {
        return 0;
    }

    /**
     * Returns the number of footer views in the list. Footer views are special views
     * at the bottom of the list that should not be recycled during a layout.
     *
     * @return The number of footer views, 0 in the default implementation.
     */
    int getFooterViewsCount() {
        return 0;
    }

    /**
     * Fills the gap left open by a touch-scroll. During a touch scroll, children that
     * remain on screen are shifted and the other ones are discarded. The role of this
     * method is to fill the gap thus created by performing a partial layout in the
     * empty space.
     *
     * @param down true if the scroll is going down, false if it is going up
     */
    abstract void fillGap(boolean down);

    void hideSelector() {
        if (mSelectedPosition != INVALID_POSITION) {
            if (mLayoutMode != LAYOUT_SPECIFIC) {
                mResurrectToPosition = mSelectedPosition;
            }
            if (mNextSelectedPosition >= 0 && mNextSelectedPosition != mSelectedPosition) {
                mResurrectToPosition = mNextSelectedPosition;
            }
            setSelectedPositionInt(INVALID_POSITION);
            setNextSelectedPositionInt(INVALID_POSITION);
            mSelectedTop = 0;
        }
    }

    public boolean isLayoutRtl() {
        return this.getLayoutDirection() == LAYOUT_DIRECTION_RTL;
    }

    public boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    /**
     * Used to indicate that the parent of this view should clear its caches. This functionality
     * is used to force the parent to rebuild its display list (when hardware-accelerated),
     * which is necessary when various parent-managed properties of the view change, such as
     * alpha, translationX/Y, scrollX/Y, scaleX/Y, and rotation/X/Y. This method only
     * clears the parent caches and does not causes an invalidate event.
     *
     * @hide
     */
	//    protected void invalidateParentCaches() {
	//    	ViewParent mParent = getParent();
	//
	//        if (mParent instanceof View) {
	//            ((View) mParent).mPrivateFlags |= PFLAG_INVALIDATED;
	//        }
	//    }


    /**
     * Used to indicate that the parent of this view should be invalidated. This functionality
     * is used to force the parent to rebuild its display list (when hardware-accelerated),
     * which is necessary when various parent-managed properties of the view change, such as
     * alpha, translationX/Y, scrollX/Y, scaleX/Y, and rotation/X/Y. This method will propagate
     * an invalidation event to the parent.
     *
     * @hide
     */
    protected void invalidateParentIfNeeded() {

    	ViewParent mParent = this.getParent();

        if (isHardwareAccelerated() && mParent instanceof View) {
            ((View) mParent).invalidate();
        }
    }

    final class CheckForTap implements Runnable {
        public void run() {
            if (mTouchMode == TOUCH_MODE_DOWN) {
                mTouchMode = TOUCH_MODE_TAP;
                final View child = getChildAt(mMotionPosition - mFirstPosition);
                if (child != null && !child.hasFocusable()) {
                    if(mLayoutMode!=LAYOUT_SPOT)
                	mLayoutMode = LAYOUT_NORMAL;

                    if (!mDataChanged) {
                        child.setPressed(true);
                        setPressed(true);
                        layoutChildren();
                        positionSelector(mMotionPosition, child);
                        refreshDrawableState();

                        final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        final boolean longClickable = isLongClickable();

                        if (mSelector != null) {
                            Drawable d = mSelector.getCurrent();
                            if (d != null && d instanceof TransitionDrawable) {
                                if (longClickable) {
                                    ((TransitionDrawable) d).startTransition(longPressTimeout);
                                } else {
                                    ((TransitionDrawable) d).resetTransition();
                                }
                            }
                        }

                        if (longClickable) {
                            if (mPendingCheckForLongPress == null) {
                                mPendingCheckForLongPress = new CheckForLongPress();
                            }
                            mPendingCheckForLongPress.rememberWindowAttachCount();
                            postDelayed(mPendingCheckForLongPress, longPressTimeout);
                        } else {
                            mTouchMode = TOUCH_MODE_DONE_WAITING;
                        }
                    } else {
                        mTouchMode = TOUCH_MODE_DONE_WAITING;
                    }
                }
            }
        }
    }

    /**
     * A base class for Runnables that will check that their view is still attached to
     * the original window as when the Runnable was created.
     *
     */
    private class WindowRunnnable {
        private int mOriginalAttachCount;

        public void rememberWindowAttachCount() {
            mOriginalAttachCount = getWindowAttachCount();
        }

        public boolean sameWindow() {
            return hasWindowFocus() && getWindowAttachCount() == mOriginalAttachCount;
        }
    }

    private class PerformClick extends WindowRunnnable implements Runnable {
        int mClickMotionPosition;

        public void run() {
            // The data has changed since we posted this action in the event queue,
            // bail out before bad things happen
            if (mDataChanged) return;

            final ListAdapter adapter = mAdapter;
            final int motionPosition = mClickMotionPosition;
            if (adapter != null && mItemCount > 0 &&
                    motionPosition != INVALID_POSITION &&
                    motionPosition < adapter.getCount() && sameWindow()) {
                final View view = getChildAt(motionPosition - mFirstPosition);
                // If there is no view, something bad happened (the view scrolled off the
                // screen, etc.) and we should cancel the click
                if (view != null) {
                    performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
                }
            }
        }
    }


    @Override
    protected ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    private class CheckForLongPress extends WindowRunnnable implements Runnable {
        public void run() {
            final int motionPosition = mMotionPosition;
            final View child = getChildAt(motionPosition - mFirstPosition);
            if (child != null) {
                final int longPressPosition = mMotionPosition;
                final long longPressId = mAdapter.getItemId(mMotionPosition);

                boolean handled = false;
                if (sameWindow() && !mDataChanged) {
                    handled = performLongPress(child, longPressPosition, longPressId);
                }
                if (handled) {
                    mTouchMode = TOUCH_MODE_REST;
                    setPressed(false);
                    child.setPressed(false);
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }
            }
        }
    }

    boolean performLongPress(final View child,
            final int longPressPosition, final long longPressId) {
        // CHOICE_MODE_MULTIPLE_MODAL takes over long press.
        if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
            if (mChoiceActionMode == null &&
                    (mChoiceActionMode = startActionMode(mMultiChoiceModeCallback)) != null) {
                setItemChecked(longPressPosition, true);
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
            return true;
        }

        boolean handled = false;
        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(FoolAbsView.this, child,
                    longPressPosition, longPressId);
        }
        if (!handled) {
            mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            handled = super.showContextMenuForChild(FoolAbsView.this);
        }
        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return handled;
    }

    /**
     * Sets the checked state of the specified position. The is only valid if
     * the choice mode has been set to {@link #CHOICE_MODE_SINGLE} or
     * {@link #CHOICE_MODE_MULTIPLE}.
     *
     * @param position The item whose checked state is to be checked
     * @param value The new checked state for the item
     */
    public void setItemChecked(int position, boolean value) {
        if (mChoiceMode == CHOICE_MODE_NONE) {
            return;
        }

        // Start selection mode if needed. We don't need to if we're unchecking something.
        if (value && mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode == null) {
            if (mMultiChoiceModeCallback == null ||
                    !mMultiChoiceModeCallback.hasWrappedCallback()) {
                throw new IllegalStateException("AbsListView: attempted to start selection mode " +
                        "for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was " +
                        "supplied. Call setMultiChoiceModeListener to set a callback.");
            }
            mChoiceActionMode = startActionMode(mMultiChoiceModeCallback);
        }

        if (mChoiceMode == CHOICE_MODE_MULTIPLE || mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
            boolean oldValue = mCheckStates.get(position);
            mCheckStates.put(position, value);
            if (mCheckedIdStates != null && mAdapter.hasStableIds()) {
                if (value) {
                    mCheckedIdStates.put(mAdapter.getItemId(position), position);
                } else {
                    mCheckedIdStates.delete(mAdapter.getItemId(position));
                }
            }
            if (oldValue != value) {
                if (value) {
                    mCheckedItemCount++;
                } else {
                    mCheckedItemCount--;
                }
            }
            if (mChoiceActionMode != null) {
                final long id = mAdapter.getItemId(position);
                mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode,
                        position, id, value);
            }
        } else {
            boolean updateIds = mCheckedIdStates != null && mAdapter.hasStableIds();
            // Clear all values if we're checking something, or unchecking the currently
            // selected item
            if (value || isItemChecked(position)) {
                mCheckStates.clear();
                if (updateIds) {
                    mCheckedIdStates.clear();
                }
            }
            // this may end up selecting the value we just cleared but this way
            // we ensure length of mCheckStates is 1, a fact getCheckedItemPosition relies on
            if (value) {
                mCheckStates.put(position, true);
                if (updateIds) {
                    mCheckedIdStates.put(mAdapter.getItemId(position), position);
                }
                mCheckedItemCount = 1;
            } else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)) {
                mCheckedItemCount = 0;
            }
        }

        // Do not generate a data change while we are in the layout phase
        if (!mInLayout && !mBlockLayoutRequests) {
            mDataChanged = true;
            rememberSyncState();
            requestLayout();
        }
    }

    public void setScrollIndicators(View up, View down) {
        mScrollUp = up;
        mScrollDown = down;
    }

    void updateSelectorState() {
        if (mSelector != null) {
            if (shouldShowSelector()) {
                mSelector.setState(getDrawableState());
            } else {
                mSelector.setState(StateSet.NOTHING);
            }
        }
    }

    /**
     * @return True if the current touch mode requires that we draw the selector in the pressed
     *         state.
     */
    boolean touchModeDrawsInPressedState() {
        // FIXME use isPressed for this
        switch (mTouchMode) {
        case TOUCH_MODE_TAP:
        case TOUCH_MODE_DONE_WAITING:
            return true;
        default:
            return false;
        }
    }

    /**
     * Indicates whether this view is in a state where the selector should be drawn. This will
     * happen if we have focus but are not in touch mode, or we are in the middle of displaying
     * the pressed state for an item.
     *
     * @return True if the selector should be shown
     */
    boolean shouldShowSelector() {
        return (hasFocus() && !isInTouchMode()) || touchModeDrawsInPressedState();
    }

    private void drawSelector(Canvas canvas) {
        if (!mSelectorRect.isEmpty()) {
            final Drawable selector = mSelector;
            selector.setBounds(mSelectorRect);
            selector.draw(canvas);
        }
    }

    /**
     * Controls whether the selection highlight drawable should be drawn on top of the item or
     * behind it.
     *
     * @param onTop If true, the selector will be drawn on the item it is highlighting. The default
     *        is false.
     *
     * @attr ref android.R.styleable#AbsListView_drawSelectorOnTop
     */
    public void setDrawSelectorOnTop(boolean onTop) {
        mDrawSelectorOnTop = onTop;
    }

    /**
     * Set a Drawable that should be used to highlight the currently selected item.
     *
     * @param resID A Drawable resource to use as the selection highlight.
     *
     * @attr ref android.R.styleable#AbsListView_listSelector
     */
    public void setSelector(int resID) {
        setSelector(getResources().getDrawable(resID));
    }

    public void setSelector(Drawable sel) {
        if (mSelector != null) {
            mSelector.setCallback(null);
            unscheduleDrawable(mSelector);
        }
        mSelector = sel;
        Rect padding = new Rect();
        sel.getPadding(padding);
        mSelectionLeftPadding = padding.left;
        mSelectionTopPadding = padding.top;
        mSelectionRightPadding = padding.right;
        mSelectionBottomPadding = padding.bottom;
        sel.setCallback(this);
        updateSelectorState();
    }

    /**
     * Returns the selector {@link android.graphics.drawable.Drawable} that is used to draw the
     * selection in the list.
     *
     * @return the drawable used to display the selector
     */
    public Drawable getSelector() {
        return mSelector;
    }

    /**
     * Clear any choices previously set
     */
    public void clearChoices() {
        if (mCheckStates != null) {
            mCheckStates.clear();
        }
        if (mCheckedIdStates != null) {
            mCheckedIdStates.clear();
        }
        mCheckedItemCount = 0;
    }

    /**
     * Returns the number of items currently selected. This will only be valid
     * if the choice mode is not {@link #CHOICE_MODE_NONE} (default).
     *
     * <p>To determine the specific items that are currently selected, use one of
     * the <code>getChecked*</code> methods.
     *
     * @return The number of items currently selected
     *
     */
    public int getCheckedItemCount() {
        return mCheckedItemCount;
    }

    /**
     * Set a {@link zy.fool.widget.FoolAbsView.MultiChoiceModeListener} that will manage the lifecycle of the
     * selection {@link android.view.ActionMode}. Only used when the choice mode is set to
     * {@link #CHOICE_MODE_MULTIPLE_MODAL}.
     *
     * @param listener Listener that will manage the selection mode
     *
     */
    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        if (mMultiChoiceModeCallback == null) {
            mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
        }
        mMultiChoiceModeCallback.setWrapped(listener);
    }

    /**
     * @return true if all list content currently fits within the view boundaries
     */
    private boolean contentFits() {
        final int childCount = getChildCount();
        if (childCount == 0) return true;
        if (childCount != mItemCount) return false;

        return getChildAt(0).getTop() >= mListPadding.top &&
                getChildAt(childCount - 1).getBottom() <= getHeight() - mListPadding.bottom;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (mScrollY != scrollY) {
            onScrollChanged(mScrollX, scrollY, mScrollX, mScrollY);
            mScrollY = scrollY;
            invalidateParentIfNeeded();

            awakenScrollBars();
        }
    }
    
    /**
     * What is the distance between the source and destination rectangles given the direction of
     * focus navigation between them? The direction basically helps figure out more quickly what is
     * self evident by the relationship between the rects...
     *
     * @param source the source rectangle
     * @param dest the destination rectangle
     * @param direction the direction
     * @return the distance between the rectangles
     */
    static int getDistance(Rect source, Rect dest, int direction) {
        int sX, sY; // source x, y
        int dX, dY; // dest x, y
        switch (direction) {
        case View.FOCUS_RIGHT:
            sX = source.right;
            sY = source.top + source.height() / 2;
            dX = dest.left;
            dY = dest.top + dest.height() / 2;
            break;
        case View.FOCUS_DOWN:
            sX = source.left + source.width() / 2;
            sY = source.bottom;
            dX = dest.left + dest.width() / 2;
            dY = dest.top;
            break;
        case View.FOCUS_LEFT:
            sX = source.left;
            sY = source.top + source.height() / 2;
            dX = dest.right;
            dY = dest.top + dest.height() / 2;
            break;
        case View.FOCUS_UP:
            sX = source.left + source.width() / 2;
            sY = source.top;
            dX = dest.left + dest.width() / 2;
            dY = dest.bottom;
            break;
        case View.FOCUS_FORWARD:
        case View.FOCUS_BACKWARD:
            sX = source.right + source.width() / 2;
            sY = source.top + source.height() / 2;
            dX = dest.left + dest.width() / 2;
            dY = dest.top + dest.height() / 2;
            break;
        default:
            throw new IllegalArgumentException("direction must be one of "
                    + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, "
                    + "FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
        int deltaX = dX - sX;
        int deltaY = dY - sY;
        return deltaY * deltaY + deltaX * deltaX;
    }
    

    /**
     * 璁板��琚�pulled item position
     * @param position
     * @param value
     */
	public void setItemPulledChecked(int position, boolean value) {
        // Start selection mode if needed. We don't need to if we're unchecking something.

        if (true) {
            boolean oldValue = mItemPullStates.get(position);
            mItemPullStates.put(position, value);
            if (mItemPulledIdStates != null && mAdapter.hasStableIds()) {
                if (value) {
                	mItemPulledIdStates.put(mAdapter.getItemId(position), position);
                } else {
                	mItemPulledIdStates.delete(mAdapter.getItemId(position));
                }
            }
            if (oldValue != value) {
                if (value) {
                    mItemPulledItemCount++;
                } else {
                    mItemPulledItemCount--;
                }
            }
            //if (mChoiceActionMode != null) {
            if (true) {
                final long id = mAdapter.getItemId(position);
                mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode, position, id, value);
            }
        } else {
            boolean updateIds = mItemPulledIdStates != null && mAdapter.hasStableIds();
            // Clear all values if we're checking something, or unchecking the currently
            // selected item
            if (value || isItemChecked(position)) {
                mCheckStates.clear();
                if (updateIds) {
                    mCheckedIdStates.clear();
                }
            }
            
            // this may end up selecting the value we just cleared but this way
            // we ensure length of mCheckStates is 1, a fact getCheckedItemPosition relies on
            if (value) {
                mCheckStates.put(position, true);
                if (updateIds) {
                    mCheckedIdStates.put(mAdapter.getItemId(position), position);
                }
                mCheckedItemCount = 1;
            } else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)) {
                mCheckedItemCount = 0;
            }
        }

        // Do not generate a data change while we are in the layout phase
        if (!mInLayout && !mBlockLayoutRequests) {
            mDataChanged = true;
            rememberSyncState();
            requestLayout();
        }
    }
  
	/**
	 * 杩����pulled item��绘��
	 * @return
	 */
    public int getItemPulledItemCount(){
    	return mItemPulledItemCount;
    }
    
    /**
     * ���濮������芥��UI������澧�������
     */
    private void initSpot(){
    		
    	if(mItemPullStates ==null){
    		mItemPullStates = new SparseBooleanArray();	
    	}
    		
    	//if(mItemPulledIdStates == null&&mAdapter!=null&&mAdapter.hasStableIds()){
    	if(mItemPulledIdStates == null&&mAdapter!=null){
    	    	mItemPulledIdStates = new LongSparseArray<Integer>();
    	}

    }
    

    protected boolean isCheckSpottedPosition(int pos){
    	boolean hasACheck = false;
    	
    	for(int i = 0;i<mItemPullStates.size();i++){
    		if(mItemPullStates.get(pos))
    			hasACheck = true; 
    	}   	
    	return hasACheck;
    }
    
    /**
     * 杩����all pulled items������澶�position
     * @return
     */
    private int getMaxItemPulledFromIdStates(){
    	int y = 0;
    	for(int i = 0;i<mItemPulledIdStates.size();i++){
    		y =Math.max(mItemPulledIdStates.valueAt(i), y);
    	}   	
    	return y;
    }  
    
    /**
     * 杩����all pulled items������灏�position
     * @return
     */
    private int getMinItemPulledPosition(){
    	int y = 0;
    	for(int i = 0;i<mItemPulledIdStates.size();i++){
    		y =Math.min(mItemPulledIdStates.valueAt(i), y);
    	}   	
    	return y;
    }
    
    /**
     * @return
     */
    private boolean startScrollItemIfNeeded(int x,int y){
	
    	mScrollX = getScrollX();
		final int deltaX = x -mMotionX;
		final int deltaY = y -mMotionY;
		final int distanceX = Math.abs(deltaX);
		final int distanceY = Math.abs(deltaY);
		final boolean overscroll = mScrollX!=0;
		
		mMotionCorrection = deltaX>0?mTouchSlop:-mTouchSlop;
		if(overscroll){ mMotionCorrection = 0;}
				
		if(overscroll||(distanceX>mTouchSlop&&(distanceY<=mTouchSlop))){
			
			mTouchMode = TOUCH_MODE_SLIDE;
			isAllowedSlide =true;
			if(deltaX>0){
				this.mSlideMode = TOUCH_SLIDE_LEFT;
			}else if(deltaX <0) {
				this.mSlideMode = TOUCH_SLIDE_RIGHT;
			}
				
			scrollItemIfNeed(x);
			System.out.println("Scroll" +x);
			return true;
		}else{
			mTouchMode = TOUCH_MODE_REST;
			return false;
		}
	} 

 
    protected int getIncrementalDeltaX(){
        return incrementalDeltaX;
    }

    protected void setIncrementalDeltaX(int x){
        this.incrementalDeltaX = x;
    }

    /**
     * child view scroll x
     * @param x
     */
	private void scrollItemIfNeed(int x){
		final int rawDeltaX = x - mMotionX;
		final int deltaX3 = rawDeltaX - mMotionCorrection;
		int incrementalDeltaX = mLastX!=Integer.MIN_VALUE?x - mLastX:deltaX3;
		if(!mDataChanged){
            if(mTouchMode == TOUCH_MODE_SLIDE){
                final Handler handler = getHandler();

                setIncrementalDeltaX(incrementalDeltaX);
                
                // Handler should not be null unless the AbsListView is not attached to a
                // window, which would make it very hard to scroll it... but the monkeys
                // say it's possible.
                if (handler != null) {
                    handler.removeCallbacks(mPendingCheckForLongPress);
                }
                setPressed(false);

                if (mMotionPosition >= 0) {
                    int	motionIndex = mMotionPosition - mFirstPosition;
                    View motionView = getChildAt(motionIndex);

                    if (motionView != null) {
                        motionView.setPressed(false);
                    }
                    
                    if(mPerformItemSlide==null){
                    	mPerformItemSlide = new PerformItemSlide();
                    	mPerformItemSlide.setSlideState(ItemSlide.SLIDE_ENTER);
                    	mPerformItemSlide.run();
                    }
                }

                mPerformItemSlide.setSlideState(ItemSlide.SLIDE_IN);
				               
            }else if(mTouchMode == TOUCH_MODE_OVERSLIDE){
                if (x != mLastX) {
                    final int oldScroll = mScrollX;
                    final int newScroll = oldScroll - incrementalDeltaX;
                    int newDirection = x > mLastX ? 1 : -1;

                    if (mDirection  == 0) {
                        mDirection = newDirection;
                    }

                    int overScrollDistance = -incrementalDeltaX;
                    if ((newScroll < 0 && oldScroll >= 0) || (newScroll > 0 && oldScroll <= 0)) {
                        overScrollDistance = -oldScroll;
                        incrementalDeltaX += overScrollDistance;
                    } else {
                        incrementalDeltaX = 0;
                    }
                    
                    setIncrementalDeltaX(incrementalDeltaX);
                    
                }
            }
            
            if(mPerformItemSlide!=null){
              	 postDelayed(mPerformItemSlide, SLIDING_TIME);
            }

        }else{
            mTouchMode = TOUCH_MODE_DONE_WAITING;
        }
	}
  
    private class SpotClick implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mLayoutMode!=LAYOUT_SPOT){
				mLayoutMode = LAYOUT_NORMAL;
			}
			
			boolean handle = performSpotClick();
			if(handle){
				mTouchMode = TOUCH_MODE_REST;
				requestLayout();
				removeCallbacks(this);
			}else{
				mTouchMode = TOUCH_MODE_DONE_WAITING;
			}
		}
    }
    
    private boolean performSpotClick() {
		// TODO Auto-generated method stub
        //PULL�����垮�ㄤ��缁����
    	if(!mDataChanged&&mTouchMode == TOUCH_MODE_OVERPULL){
    		switch (mPullMode) {
			case MULTI_TOUCH_PULLIN:
				if(mPullInListener!=null){
					mPullInListener.onPullIn();
				}
				break;
			case MULTI_TOUCH_PULLOUT:
				if(mPullOutListener!=null){
					mPullOutListener.onPullOut();
				}
				break;
			default:
				if(mPullListener!=null){
					mPullListener.onPull();
				}
				break;
			}
    		
    		mItemPullStates.put(mMotionPosition, true);
    		return true;
    	}
		return false;
	}
	
    private class SpotSlide implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mLayoutMode!=LAYOUT_SPOT){
				mLayoutMode = LAYOUT_NORMAL;
			}
			
			boolean handle = performSpotSlide();
			if(handle){
				mTouchMode = TOUCH_MODE_REST;
				requestLayout();
				removeCallbacks(this);
			}else{
				mTouchMode = TOUCH_MODE_DONE_WAITING;
			}
		}
    }

    /**
     * ���SLIDE��ㄤ��缁�������������璁告�ц�����璋���芥�版�ュ��
     * @return
     */
    private boolean performSpotSlide() {
		// TODO Auto-generated method stub
    	if(!mDataChanged&&mTouchMode == TOUCH_MODE_OVERSLIDE){
            //��ц�����璋���芥��
    		switch (mSlideMode) {
			case TOUCH_SLIDE_LEFT:
				if(mSlideLeftListener!=null){
					mSlideLeftListener.onSlideLeft();
				}
				break;
			case TOUCH_SLIDE_RIGHT:
				if(mSlideRightListener!=null){
					mSlideRightListener.onSlideRight();
				}
				break;
			default:
				if(mSlideListener!=null){
					mSlideListener.onSlide();
				}
				break;
			}
    		
    		//mItemPullStates.put(mMotionPosition, true);
    		return true;
    	}
		return false;
	}

    /**
     * 2014.02.24
     * @author davidlau
     */
    
    class PerformItemPull implements Runnable{
    	
        private static final int STATE_NONE = 0;
        private static final int STATE_PULL = 1;
        private static final int STATE_EXIT = 2;
        
    	private static final long PULLING_TIME = 10;
    	 
    	private View pullView;
    	private float mOriginalAlpha;
    	private Handler mHandler = new Handler();
        private ItemViewAlphaRunnable mItemAlphaRunnable; //PullFade���澶����瀵瑰�����View���������搴�
        
        private int mState;
        
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		if(!mDataChanged&&mIsAttached){
    			
    			if(itemNeedAlpha){
                    pullView = getChildAt(mMotionPosition-mFirstPosition);
        			if (pullView!=null){
                        mHandler.postDelayed(mPrepareFadeRunnable, PULLING_TIME);
                        mSpotClick = new SpotClick();
                    }
                }else{
                    mSpotClick = new SpotClick();
                }
                mTouchMode = TOUCH_MODE_OVERPULL;
            }else{
    			mTouchMode = TOUCH_MODE_DONE_WAITING;
    		}
    	}	
    	
        private Runnable mPrepareFadeRunnable = new Runnable() {
    		
    		@Override
    		public void run() {
    			
    			//�����ュ��瑁����涓轰��涓�initFade��芥��
    			if(!mDataChanged){
    				if(mItemAlphaRunnable!=null){
    	    			setState(STATE_NONE);
    	    			mHandler.removeCallbacks(mPrepareFadeRunnable);
    	    		}
    	    		
    	    		if(mItemAlphaRunnable == null&&pullView!=null){
    	    			//
    	    			mOriginalAlpha = pullView.getAlpha();
                        mItemAlphaRunnable = new ItemViewAlphaRunnable();
    	    			//濡����澧����pull������overpull
    	    			mHandler.postDelayed(mItemAlphaRunnable, 10);
    	    		}
    			}
    		}
    	};

    	public void endPull(){
    		isAllowedPull = false;
			mTouchMode = TOUCH_MODE_REST;
			
    		setState(STATE_EXIT);
    		mHandler.removeCallbacks(this);
    		mHandler.removeCallbacks(mPrepareFadeRunnable);
    	}
    		
    	public boolean performPull(){
            //榛�璁よ����� mItemAlphaRunnable 瀛�绾跨��锛���存��Alpha
            if(mItemAlphaRunnable!=null){
    			//杩���ョ嚎绋�绛�寰�
//    			if(mState == STATE_PULL){
//    				mHandler.removeCallbacks(mItemAlpha);
//    			}
//    			
    			setState(STATE_PULL);
    			
    			return true;
    		}else {
                //do nothing
    			return false;
    		}
    	}
    	

        private void setState(int state) {
        	
        	if(itemNeedAlpha)
        	{
	        	switch (state) {
	                case STATE_NONE:
	                	mHandler.removeCallbacks(mItemAlphaRunnable);
	                	pullView.invalidate();
	                    break;
	               
	                case STATE_PULL:
	                	//mHandler.removeCallbacks(mItemAlpha);
	                	float x = (float)mItemAlphaRunnable.getAlpha()/255F;
	                	pullView.setAlpha(x);
	                	pullView.invalidate();
	                	
	                    break;
	                case STATE_EXIT:
	                	mHandler.removeCallbacks(mItemAlphaRunnable);
	                	//��㈠��ItemView
	                	pullView.setAlpha(mOriginalAlpha);
	                	pullView.invalidate();
	    	            
	                	break;
	            }
            mState = state;
            }
        }
        
        public int getState() {
            return mState;
        }
        
    	class ItemViewAlphaRunnable implements Runnable {

    		long mStartTime;
    	    long mItemAlphaDuration;
    	    static final int ALPHA_MAX = 255;
    	    static final long ALPHA_DURATION = 30000;
    	        
    	    void initAlpha() {
    	    	mItemAlphaDuration = ALPHA_DURATION;
    	        mStartTime = SystemClock.uptimeMillis(); 
    	    }
    	        
    	    int getAlpha() {
    	    	if (getState() != STATE_PULL) {
    	            return ALPHA_MAX;
    	        }
    	            
    	    	int alpha;
    	        long now = SystemClock.uptimeMillis();
    	        if (now > mStartTime + mItemAlphaDuration) {
    	        	alpha = 0;
    	        } else {
    	        	alpha = (int) (ALPHA_MAX - ((now - mStartTime) * ALPHA_MAX) / mItemAlphaDuration); 
    	        }
    	        System.out.println(alpha);
    	        return alpha;
    	    }
    	        
    	    public void run() { 
    	    	if(!mDataChanged){

                    //寮�濮����濮����瀛�绾跨��
                    if(mStartTime==0)
                    initAlpha();
        	        
        	        if (getAlpha() > 0) {
        	            pullView.invalidate();
        	            setState(STATE_PULL);
        	            } else {
        	            setState(STATE_NONE);
        	        }

    	    	}
    	     }
    	 }
    }
    
    public void setIsPullItemViewAlpha(boolean itemNeedAlpha){
    	this.itemNeedAlpha = itemNeedAlpha;
    }
    
	/**
	 * �����跺����ゆ��Y��瑰�������ㄦ��,������绗�涓����绗�浜�瑙���哥��
	 * 	
	 * 2014.2.24
	 * @author davidlau
	 * @return boolean
	 */
	private boolean currentMotionIsPull(){
		
		boolean ispull = false;
		if (mPulldeltaYs[0]==0||mPulldeltaYs[1]==0) {
			ispull = false;			
		}
		
		if((mPulldeltaYs[0]>0&&mPulldeltaYs[1]<0)||(mPulldeltaYs[0]<0&&mPulldeltaYs[1]>0)){
			isAllowedPull = ispull = true;
		}

      
		return ispull;
	}

	
	private interface ItemSlide{
		static final int SLIDE_ENTER= 0;
		static final int SLIDE_IN = 1;
		static final int SLIDE_DONE = 2;
	}
    /**
     * @author davidlau
     * 2014 2 27 03锛�08
     */
	class PerformItemSlide  extends WindowRunnnable implements Runnable {
      
        private int mSlideState = ItemSlide.SLIDE_ENTER;
        private int deltaX;
        private int width;
        private int MaxDeltaX = 0;

        View mSlideItemView;

		@Override
		public void run() {
			// TODO Auto-generated method stub

            deltaX = getIncrementalDeltaX();

            if(!mDataChanged&&mIsAttached){
                switch (mSlideState) {
                    case ItemSlide.SLIDE_ENTER:
                       // mTouchMode = TOUCH_MODE_OVERSLIDE;
                        init();
                        isSliding = true;
                        break;
                    case ItemSlide.SLIDE_IN:
                        slide();
                        break;
                    case ItemSlide.SLIDE_DONE:
                    	endSlide();
                        removeCallbacks(this);
                        isSliding = false;
                        break;
                }
            }else{
                mTouchMode = TOUCH_MODE_DONE_WAITING;
            }
		}

        /**
         * @see .slide()
         * 2014.02.27 14:01
         */
		private void init(){

		   	if(mSlideItemView == null)
		    
		   	mSlideItemView = getChildAt(mMotionPosition-mFirstPosition);
            width = mSlideItemView.getWidth();
            MaxDeltaX = width/3; 

        }
	    
	    private void slide(){
	    	
	    	if(Math.abs(incrementalDeltaX)>MaxDeltaX){
	    		incrementalDeltaX = incrementalDeltaX>0?MaxDeltaX:-MaxDeltaX;
	    	}
	    	
	    	mSlidedPosition = mMotionPosition;
	    	mLayoutMode = LAYOUT_SLIDE;
            layoutChildren();
           
            mTouchMode = TOUCH_MODE_OVERSLIDE;            
        }

		private void endSlide() {
			mSlidedPosition = INVALID_POSITION;
            mLayoutMode = LAYOUT_NORMAL;
            layoutChildren();
            
            if(mSpotSlide== null){
                mSpotSlide = new SpotSlide();
            }
            postDelayed(mSpotSlide,SLIDING_TIME);//SLIDEOVERTIME
        }
		
		void setSlideState(int state){
			this.mSlideState = state;
		}
	}
	    
}
