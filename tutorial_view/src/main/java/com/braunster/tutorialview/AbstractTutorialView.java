package com.braunster.tutorialview;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by braunster on 03/12/14.
 */
public abstract class AbstractTutorialView extends RelativeLayout implements TutorialInterface{

    // FIXME debug draw is overlayd by the regular draw
    protected static final boolean DEBUG_DRAW = true;
    public static final String TAG = AbstractTutorialView.class.getSimpleName();
    public static final boolean DEBUG = true;

    protected static final int DEFAULT_PRECAUTION_ABOVE_DISTANCE = 5, DEFAULT_CIRCLE_PADDING = 40, INNER_CIRCLE_DEFAULT_ALPHA = 180, DEFAULT_STROKE_WIDTH = 13;

    /**
     * Default animation duration that is used to expand and collapse the tutorial.
     * */
    protected static final int DEFAULT_ANIM_DURATION  = 600;


    protected int mViewToSurroundX = -1, mViewToSurroundY = -1, mViewToSurroundCenterX = 0, mViewToSurroundCenterY = 0, mViewToSurroundRadius = 0, mViewToSurroundWidth = -1, mViewToSurroundHeight = -1;

    protected int statusBarHeight = 0, actionBarHeight = 0;

    protected View mViewToSurround, mTutorialInfoView, mGotItButton;

    /**
     * The paint that will be used to draw the tutorial background color.
     * */
    protected Paint mBackgroundPaint;

    /**
     * Used for debug drawing to find problems with calculations.
     * */
    protected Paint debugPaint;

    /**
     * The paint that will be used to draw the inner circles.
     * */
    protected Paint mInnerCirclePaint;

    /**
     * true if the tutorial is visible.
     * */
    protected boolean showing = false;

    /** true if the tutorial is animating closing of the tutorial.
     *
     * Good for checking checking if we should do close operation or we are in the middle of was.
     **/
    protected boolean closing = false;

    /**
     * The layout id that will be used to inflate the tutorial info view.
     * */
    protected int mTutorialInfoLayoutId = -1;

    protected ActionBar mActionBar;

    /**
     * If true when the tutorial view will be visible the action bar color will be change to the tutorial background color
     * */
    protected boolean mChangeActionBarColor = false;

    /**
     *  The background color of the tutorial view
     * */
    protected int mTutorialBackgroundColor = Color.BLUE;

    /**
     * The default color of the action bar,
     * This is used combine with mChangeActionBarColor
     * to set the background of the action bar to the tutorial color.
     * */
    protected int mActionBarRestoreColor = -1;

    /**
     * The title of the action bar before we changed it to the tutorial title,
     * Used for restoring the action bar state after tutorial is closed.
     * */
    protected String mActionBarOldTitle = "";

    /**
     * Holds the size of the default info view text view.
     * */
    protected int mTutorialTextSize = -1;

    /**
     * Holds the text that will be shown in the default info view
     * */
    protected String mTutorialText = "";

    /**
     * Holds the color for the default info view text view.
     * */
    protected int mTutorialTextColor = Color.WHITE;

    /**
     * Holds the name of the typeface that will be used for the default info view text view
     * */
    protected Typeface mTutorialTextTypeFace = null;

    /**
     * Listener for the tutorial closing.
     * */
    protected TutorialClosedListener tutorialClosedListener;

    /**
     * Holds the view bounds on screen,
     * This is used for DEBUG_DRAW and for creating the arcs surrounding the view.
     * */
    protected RectF mViewBoundsInParent;

    /**
     * Holds the animation type value that will be preformed on opening and closing the tutorial.
     * */
    protected AnimationType mAnimationType = AnimationType.RANDOM;

    /**
     * Animation type that are supported by the tutorial view.
     * */
    public enum AnimationType{
        RANDOM,
        FROM_TOP,
        FROM_BOTTOM,
        FROM_RIGHT,
        FROM_LEFT,
        FROM_TOP_RIGHT,
        FROM_TOP_LEFT,
        FROM_BOTTOM_RIGHT,
        FROM_BOTTOM_LEFT;

        private static final Random random = new Random();

        public static AnimationType getRandom(){
            // Skipping random
            return values()[random.nextInt(values().length -1) + 1];
        }
    }


    public AbstractTutorialView(Context context) {
        super(context);
        init();
    }

    public AbstractTutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractTutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface TutorialClosedListener{
        public void onClosed();
    }

    public abstract void closeTutorial();

    /**
     * This function will be called before the view needs to be draw.
     *
     * This is the place to place the entering animation code.
     * */
    public abstract void beforeFirstDraw();


    private void init(){
        setWillNotDraw(false);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mTutorialBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(mTutorialBackgroundColor);
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA);
        mInnerCirclePaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);


        if (DEBUG_DRAW){
            debugPaint = new Paint();
            debugPaint.setColor(Color.BLACK);
        }
    }

    /**
     * Surround a given view by a visual tutorial with text.
     * @param mViewToSurround the view that need to be surrounded with the tutorial.
     * */
    @Override
    public void setViewToSurround(View mViewToSurround) {
        setViewToSurround(mViewToSurround, null);
    }

    /**
     * Surround a given view by a visual tutorial with text and title.
     *
     * @param title the text of the title that will be used for the tutorial, If an action bar is assign the title will be in the action bar title.
     * @param mViewToSurround the view that need to be surrounded with the tutorial.
     * */
    @Override
    public void setViewToSurround(View mViewToSurround, String title) {

        if (showing)
            return;

        this.mViewToSurround = mViewToSurround;

        // Hide the action bar if given
        if (mActionBar != null)
        {
            if (mChangeActionBarColor)
                mActionBar.setBackgroundDrawable(new ColorDrawable(mTutorialBackgroundColor));

            if (title != null && !title.isEmpty())
            {
                mActionBarOldTitle = mActionBar.getTitle().toString();
                mActionBar.setTitle(title);
            }
        }

        int[] loc = new int[2];
        mViewToSurround.getLocationOnScreen(loc);

        mViewToSurroundX = loc[0];
        mViewToSurroundY = loc[1];

        mViewToSurroundWidth = mViewToSurround.getMeasuredWidth();
        mViewToSurroundHeight = mViewToSurround.getMeasuredHeight();

        calcViewBoundsRadiusAndCenter();

        beforeFirstDraw();

        // Moving the tutorial to the front so np other view will be visible
        bringToFront();
    }

    /**
     *  Used mostly for the tutorial activity, Instead of passing a view to surround we pass the view coordination and dimensions.
     *
     *  @param positionToSurroundX The x position of the area that should be surrounded, This could be obtained by using getX() on the view you want to surround.
     *  @param positionToSurroundY The y position of the area that should be surrounded, This could be obtained by using getY() on the view you want to surround.
     *  @param positionToSurroundWidth The width of the area that should be surrounded, This could be obtained by using getMeasuredWidth() on the view you want to surround.
     *  @param positionToSurroundHeight The height of the area that should be surrounded, This could be obtained by using getMeasuredHeight() on the view you want to surround.
     *
     *  */
    @Override
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight){
        setPositionToSurround(positionToSurroundX, positionToSurroundY, positionToSurroundWidth, positionToSurroundHeight, null);
    }

    /**
     *  Used mostly for the tutorial activity, Instead of passing a view to surround we pass the view coordination and dimensions.
     *
     *  @param title The title text that will be used for this tutorial.
     *  @param positionToSurroundX The x position of the area that should be surrounded, This could be obtained by using getX() on the view you want to surround.
     *  @param positionToSurroundY The y position of the area that should be surrounded, This could be obtained by using getY() on the view you want to surround.
     *  @param positionToSurroundWidth The width of the area that should be surrounded, This could be obtained by using getMeasuredWidth() on the view you want to surround.
     *  @param positionToSurroundHeight The height of the area that should be surrounded, This could be obtained by using getMeasuredHeight() on the view you want to surround.
     *
     *  */
    @Override
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight, String title){
        if (showing)
            return;

        // Hide the action bar if given
        if (mActionBar != null)
        {
            if (mChangeActionBarColor)
                mActionBar.setBackgroundDrawable(new ColorDrawable(mTutorialBackgroundColor));

            if (title != null && !title.isEmpty())
            {
                mActionBarOldTitle = mActionBar.getTitle().toString();
                mActionBar.setTitle(title);
            }
        }

        mViewToSurroundX = (int) positionToSurroundX;
        mViewToSurroundY = (int) positionToSurroundY;

        mViewToSurroundWidth = positionToSurroundWidth;
        mViewToSurroundHeight = positionToSurroundHeight;

        calcViewBoundsRadiusAndCenter();

        beforeFirstDraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode())
            return;

        // No need to draw if does not have any view to surround,
        // Or the view size is null (in case we do not use a view but only its coordinates).
        if ( !showing || (mViewToSurround == null && mViewToSurroundHeight == -1 && mViewToSurroundWidth == -1) )
            return;

        /*if (DEBUG_DRAW)
        {
            canvas.drawCircle(mViewToSurroundCenterX, mViewToSurroundCenterY - actionBarHeight - statusBarHeight, 5 * getResources().getDisplayMetrics().density, debugPaint);

            // The rectCorners.
            //TOP - LEFT
            canvas.drawCircle(mViewBoundsInParent.left, mViewBoundsInParent.top, 5 * getResources().getDisplayMetrics().density, debugPaint);
            // TOP - RIGHT
            canvas.drawCircle(mViewBoundsInParent.right, mViewBoundsInParent.top, 5 * getResources().getDisplayMetrics().density, debugPaint);
            // BOTTOM - LEFT
            canvas.drawCircle(mViewBoundsInParent.left, mViewBoundsInParent.bottom, 5 * getResources().getDisplayMetrics().density, debugPaint);
            // BOTTOM - RIGHT
            canvas.drawCircle(mViewBoundsInParent.right, mViewBoundsInParent.bottom, 5 * getResources().getDisplayMetrics().density, debugPaint);
        }*/
    }

    /**
     * Calculating the view position variables and the circle radius.
     * */
    protected void calcViewBoundsRadiusAndCenter(){
        /* Radius of the view*/
        mViewToSurroundRadius = Math.max(mViewToSurroundWidth, mViewToSurroundHeight);

//        if (DEBUG) Log.i(TAG, "Width: " + viewHeight + ", Height: " + viewHeight);

        // Get the diagonal length of the square that need to be surrounded, And add padding to it.
        mViewToSurroundRadius = (int) Math.sqrt(Math.pow(mViewToSurroundWidth, 2) + Math.pow(mViewToSurroundHeight, 2)) / 2 + DEFAULT_CIRCLE_PADDING;

        /* The center of the view that need to be surrounded*/
        mViewToSurroundCenterX = mViewToSurroundX + mViewToSurroundWidth / 2;
        mViewToSurroundCenterY = mViewToSurroundY + mViewToSurroundHeight / 2;

        /* THis rect will be used to draw the arcs.*/
        mViewBoundsInParent = new RectF(mViewToSurroundCenterX - mViewToSurroundRadius,
                mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight,
                mViewToSurroundCenterX + mViewToSurroundRadius,
                mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        if (DEBUG) Log.i(TAG, "X: " + mViewToSurroundCenterX + ", Y: " + mViewToSurroundCenterY + ", R: " + mViewToSurroundRadius);
    }

    /**
     * Inflating the layout by given id that will explain about the view that has been surrounded.
     *
     * If no id was assigned the default layout will be used, <b>see</b> {@link com.braunster.tutorialview.TutorialView#setTutorialText(String) setTutorialText(String)}
     * for setting the text for the explanation.
     *
     * */
    protected void inflateTutorialInfo(){

        // If no info was assigned we use the default
        if (mTutorialInfoLayoutId == -1)
        {
            mTutorialInfoLayoutId = R.layout.tutorial_text;
        }

        mTutorialInfoView = inflate(getContext(), mTutorialInfoLayoutId, null);

        // When the default view is used it should be combined with the tutorial text variable.
        if (mTutorialInfoLayoutId == R.layout.tutorial_text)
        {
            TextView txtTutorial = ((TextView) mTutorialInfoView.findViewById(R.id.tutorial_info_text));
            txtTutorial.setText(mTutorialText);

            if (mTutorialTextSize != -1)
                txtTutorial.setTextSize(mTutorialTextSize);

            txtTutorial.setTextColor(mTutorialTextColor);


            if (mTutorialTextTypeFace != null)
            {
                txtTutorial.setTypeface(mTutorialTextTypeFace);
            }
        }

        mTutorialInfoView.setVisibility(INVISIBLE);

        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_LEFT);
        params.addRule(ALIGN_PARENT_RIGHT);

        addView(mTutorialInfoView, params);

        // Inflating the "Got It" button.
        mGotItButton = inflate(getContext(), R.layout.got_it_button_view, null);
        ((TextView) mGotItButton).setTextColor(mTutorialTextColor);

        mTutorialInfoView.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.addRule(CENTER_HORIZONTAL);

                // The view to surround is in the top half of the screen so the info will be below it.
                if (mViewToSurroundCenterY < getMeasuredHeight() / 2)
                {
                    mTutorialInfoView.setY(mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);
                    params.addRule(ALIGN_PARENT_BOTTOM);
                }
                // The view to surround is in the bottom half of the screen.
                else {
                    mTutorialInfoView.setY(mViewToSurroundCenterY - mViewToSurroundRadius -
                            mTutorialInfoView.getMeasuredHeight() - mTutorialInfoView.getPaddingTop()
                            - mTutorialInfoView.getPaddingBottom() - statusBarHeight - actionBarHeight
                            - DEFAULT_PRECAUTION_ABOVE_DISTANCE * getResources().getDisplayMetrics().density);

                    params.addRule(ALIGN_PARENT_TOP);
                }

                addView(mGotItButton, params);

                mGotItButton.setOnClickListener(closeTutorialClickListener);

                // Animating the views in
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.tutorial_info_view_fade_in);

                mTutorialInfoView.setAnimation(animation);
                mGotItButton.setAnimation(animation);

                animation.start();

            }
        });
    }

    protected void dispatchTutorialClosed(){
        if (tutorialClosedListener!=null)
            tutorialClosedListener.onClosed();
    }


    /**
     * OnClickListener for hiding the tutorial when clicked.
     * */
    private OnClickListener closeTutorialClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            closeTutorial();
        }
    };


    /**
     * The Layout id of the view that will be used to show the tutorial text or information.
     * */
    @Override
    public void setTutorialInfoLayoutId(int tutorialInfoLayoutId) {
        this.mTutorialInfoLayoutId = tutorialInfoLayoutId;
    }

    @Override
    public void setActionBar(ActionBar actionBar) {
        this.mActionBar = actionBar;
    }

    @Override
    public void setTutorialClosedListener(TutorialClosedListener tutorialClosedListener) {
        this.tutorialClosedListener = tutorialClosedListener;
    }

    /**
     * The background color for the tutorial.
     * */
    @Override
    public void setTutorialBackgroundColor(int mTutorialBackgroundColor) {
        this.mTutorialBackgroundColor = mTutorialBackgroundColor;
        mBackgroundPaint.setColor(mTutorialBackgroundColor);
        mInnerCirclePaint.setColor(mTutorialBackgroundColor);
    }

    /**
     * If true and an action bar is given to the view the action bar background color will be changed to the tutorial color.
     * You <b>should</b> also set {@link com.braunster.tutorialview.TutorialView#mActionBarRestoreColor mActionBarRestoreColor} so when the tutorial is closed the action bar color will ber restored.
     *
     * @see com.braunster.tutorialview.TutorialView#setActionBarRestoreColor(int) */
    @Override
    public void changeActionBarColor(boolean mChangeActionBarColor) {
        this.mChangeActionBarColor = mChangeActionBarColor;
    }

    /**
     * The of the color that will be set as the action bar color when the tutorial view will be closed.
     *
     * @see
     *  com.braunster.tutorialview.TutorialView#changeActionBarColor(boolean)
     *  */
    @Override
    public void setActionBarRestoreColor(int mActionBarColor) {
        this.mActionBarRestoreColor = mActionBarColor;
    }

    /**
     *  Get the height of the status bar.
     * */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     *  Set true if the application has status bar.
     * */
    @Override
    public void setHasStatusBar(boolean hasStatusBar){
        statusBarHeight = hasStatusBar ? getStatusBarHeight() : 0;
    }

    /**
     *  Set true if the application has the activity that hold this view has action bar..
     * */
    @Override
    public void setHasActionBar(boolean hasActionBar){
        actionBarHeight = hasActionBar ? getActionBarHeight() : 0;
    }

    /**
     * The text that will be used for the {@link com.braunster.tutorialview.TutorialView#mTutorialInfoView mTutorialInfoView}.
     * <b>Only</b> if the layout that is used is the default layout ({@link com.braunster.tutorialview.R.layout#tutorial_text tutorial_text}).
     *
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextTypeFace(String)
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextColor(int)
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextSize(int)
     *
     * */
    @Override
    public void setTutorialText(String tutorialText) {
        this.mTutorialText = tutorialText;
    }

    private int getActionBarHeight(){
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        return actionBarHeight;
    }

    /**
     * Set the size that will be used for the default info view text view.
     *
     * @see com.braunster.tutorialview.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextColor(int)
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    @Override
    public void setTutorialTextSize(int mTutorialTextSize) {
        this.mTutorialTextSize = mTutorialTextSize;
    }

    /**
     * Set the color that will be used for the default info view text view.
     *
     * This color will also be used for the "Got It" button.
     *
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextSize(int)
     * @see com.braunster.tutorialview.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    @Override
    public void setTutorialTextColor(int mTutorialTextColor) {
        this.mTutorialTextColor = mTutorialTextColor;
    }

    @Override
    public void setTutorialTextTypeFace(String tutorialTextTypeFaceName) {

        if (tutorialTextTypeFaceName.isEmpty())
            return;

        this.mTutorialTextTypeFace = Typeface.createFromAsset(getResources().getAssets(), tutorialTextTypeFaceName);
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void setAnimationType(AnimationType mAnimationType) {
        this.mAnimationType = mAnimationType;
    }

    protected int getParentWidth(){
        return ((View) getParent()).getMeasuredWidth();
    }

    protected int getParentHeight(){
        return ((View) getParent()).getMeasuredHeight();
    }
}
