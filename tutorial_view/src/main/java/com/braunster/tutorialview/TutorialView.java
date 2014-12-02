package com.braunster.tutorialview;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by braunster on 01/12/14.
 */
public class TutorialView extends RelativeLayout {

    // TODO option to change the inner circle color and alpha or disabling it.

    public static final String TAG = TutorialView.class.getSimpleName();
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_DRAW = false;

    private int mViewToSurroundX = -1, mViewToSurroundY = -1, mViewToSurroundCenterX = 0, mViewToSurroundCenterY = 0, mViewToSurroundRadius = 0, mViewToSurroundWidth = -1, mViewToSurroundHeight = -1;

    private Paint mBackgroundPaint, debugPaint, mInnerCirclePaint;

    private int statusBarHeight = 0, actionBarHeight = 0;

    private View mViewToSurround, mTutorialInfoView, mGotItButton;

    /**
     * The layout id that will be used to inflate the tutorial info view.
     * */
    private int mTutorialInfoLayoutId = -1;

    private ActionBar mActionBar;

    private static final int DEFAULT_PRECAUTION_ABOVE_DISTANCE = 5, DEFAULT_CIRCLE_PADDING = 40, INNER_CIRCLE_DEFAULT_ALPHA = 180, DEFAULT_STROKE_WIDTH = 13;

    /**
     * Holds the text that will be shown in the default info view
     * */
    private String mTutorialText = "";

    /**
     * Holds the size of the default info view text view.
     * */
    private int mTutorialTextSize = -1;

    /**
     * Holds the color for the default info view text view.
     * */
    private int mTutorialTextColor = Color.WHITE;

    /**
     * Holds the name of the typeface that will be used for the default info view text view
     * */
    private Typeface mTutorialTextTypeFace = null;

    /**
     * Default animation duration that is used to expand and collapse the tutorial.
     * */
    private static final int DEFAULT_ANIM_DURATION  = 700;

    /**
     * Left and Right path's used to create the arcs around the view that need to be surrounded.
     * */
    private Path mLeftArcPath = new Path(), mRightArcPath = new Path();

    private RectF mViewBoundsInParent;

    /**
     * true if the tutorial is visible.
     * */
    private boolean showing = false;

    /**
     * The height of the tutorial view before the expand animation start. Used to handle animation issues.
     * */
    private int mBeforeAnimationHeight = -1;

    /**
     * If true when the tutorial view will be visible the action bar color will be change to the tutorial background color
     * */
    private boolean mChangeActionBarColor = false;

    /**
     *  The background color of the tutorial view
     * */
    private int mTutorialBackgroundColor = Color.BLUE;

    /**
     * The default color of the action bar,
     * This is used combine with mChangeActionBarColor
     * to set the background of the action bar to the tutorial color.
     * */
    private int mActionBarRestoreColor = -1;

    /**
     * The title of the action bar before we changed it to the tutorial title,
     * Used for restoring the action bar state after tutorial is closed.
     * */
    private String mActionBarOldTitle = "";


    /**
     * Listener for the tutorial closing.
     * */
    private TutorialClosedListener tutorialClosedListener;

    public TutorialView(Context context) {
        super(context);
        init();
    }

    public TutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

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
     * @param mViewToSurround - the view that need to be surrounded with the tutorial.
     * */
    public void setViewToSurround(View mViewToSurround) {
        setViewToSurround(mViewToSurround, null);
    }

    /**
     * Surround a given view by a visual tutorial with text and title.
     *
     * @param title - the text of the title that will be used for the tutorial, If an action bar is assign the title will be in the action bar title.
     * @param mViewToSurround - the view that need to be surrounded with the tutorial.
     * */
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

        // Moving the tutorial to the front so np other view will be visible
        bringToFront();

        firstDraw();

        invalidate();
    }

    /**
     *  Used mostly for the tutorial activity, Instead of passing a view to surround we pass the view coordination and dimensions.
     *
     *  @param positionToSurroundX - The x position of the area that should be surrounded, This could be obtained by using getX() on the view you want to surround.
     *  @param positionToSurroundY - The y position of the area that should be surrounded, This could be obtained by using getY() on the view you want to surround.
     *  @param positionToSurroundWidth - The width of the area that should be surrounded, This could be obtained by using getMeasuredWidth() on the view you want to surround.
     *  @param positionToSurroundHeight - The height of the area that should be surrounded, This could be obtained by using getMeasuredHeight() on the view you want to surround.
     *
     *  */
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight){
        setPositionToSurround(positionToSurroundX, positionToSurroundY, positionToSurroundWidth, positionToSurroundHeight, null);
    }

    /**
     *  Used mostly for the tutorial activity, Instead of passing a view to surround we pass the view coordination and dimensions.
     *
     *  @param title - The title text that will be used for this tutorial.
     *  @param positionToSurroundX - The x position of the area that should be surrounded, This could be obtained by using getX() on the view you want to surround.
     *  @param positionToSurroundY - The y position of the area that should be surrounded, This could be obtained by using getY() on the view you want to surround.
     *  @param positionToSurroundWidth - The width of the area that should be surrounded, This could be obtained by using getMeasuredWidth() on the view you want to surround.
     *  @param positionToSurroundHeight - The height of the area that should be surrounded, This could be obtained by using getMeasuredHeight() on the view you want to surround.
     *
     *  */
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight, String title){
        if (showing)
            return;

        mViewToSurroundX = (int) positionToSurroundX;
        mViewToSurroundY = (int) positionToSurroundY;

        mViewToSurroundWidth = positionToSurroundWidth;
        mViewToSurroundHeight = positionToSurroundHeight;

        firstDraw();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode())

        if (DEBUG) Log.d(TAG, "onDraw, Height: " + getMeasuredHeight());

        // No need to draw if does not have any view to surround
        if (mViewToSurround == null && mViewToSurroundHeight == -1 && mViewToSurroundWidth == -1)
            return;

        calcViewPosition();

        // This check for former height is needed so we wont draw anything until the view size is changed and ready for the animation.
        if (showing && mBeforeAnimationHeight != getMeasuredHeight())
        {
            // The size of the view before the animation
            mBeforeAnimationHeight = -1;

            // Drawing the blocks around the view.
            drawSurroundingBlocks(canvas);

            // Drawing the circle around the view.
            drawSurroundingCircle(canvas);
        }

        if (DEBUG_DRAW)
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
        }

    }

    /**
     * The first draw of the tutorial view for the given viewToSurround.
     * */
    private void firstDraw(){
        if (DEBUG) Log.d(TAG, "FirstDraw");

        // Bringing the surrounding view to front so it will be visible.
        if (mViewToSurround != null)
            mViewToSurround.bringToFront();

        mBeforeAnimationHeight = getMeasuredHeight();

        // To make sure the child order is ok.
        ((View) getParent()).invalidate();

        showing = true;

        setVisibility(INVISIBLE);

        expand(this, new Runnable() {
            @Override
            public void run() {
                // Only if the tutorial is still showing.
                if (showing) inflateTutorialInfo();
            }
        });
    }

    /**
     * Calculating the view position variables and the circle radius.
     * */
    private void calcViewPosition(){

        mLeftArcPath = new Path();
        mRightArcPath = new Path();

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

//        if (DEBUG) Log.i(TAG, "X: " + mViewToSurroundCenterX + ", Y: " + mViewToSurroundCenterY + ", R: " + mViewToSurroundRadius);

        setClickable(true);
    }

    /**
     * Drawing the blocks around the view.
     * */
    private void drawSurroundingBlocks(Canvas canvas){
        // If view is not in the top.
        if (mViewToSurroundCenterY > mViewToSurroundRadius)
        {
            if (DEBUG) Log.i(TAG, "View not on the top");
            canvas.drawRect(0, 0, getMeasuredWidth(), mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight, mBackgroundPaint);
        }

        // If not on bottom
        if (mViewToSurroundCenterY + mViewToSurroundRadius < getMeasuredHeight())
        {
            if (DEBUG) Log.i(TAG, "View not on the bottom");
            canvas.drawRect(0, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight, getMeasuredWidth(), getMeasuredHeight(), mBackgroundPaint);
        }

        //if not on the left
        if (mViewToSurroundCenterX > mViewToSurroundRadius)
        {
            if (DEBUG) Log.i(TAG, "View not on the left");
            canvas.drawRect(0, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight, mViewToSurroundCenterX - mViewToSurroundRadius, mViewToSurroundCenterY + mViewToSurroundRadius, mBackgroundPaint);
        }

        //if not on the right
        if (mViewToSurroundCenterX + mViewToSurroundRadius < getMeasuredWidth())
        {
            if (DEBUG) Log.i(TAG, "View not on the right");
            canvas.drawRect(mViewToSurroundCenterX + mViewToSurroundRadius, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight, getMeasuredWidth(), mViewToSurroundCenterY + mViewToSurroundRadius, mBackgroundPaint);
        }
    }

    /**
     * Drawing the circle around the view.
     * */
    private void drawSurroundingCircle(Canvas canvas){
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setStrokeWidth(1f);

        /**
         *  Left side of the circle
         * */
        // Starting the path from the middle of x of the view and the top y.
        mLeftArcPath.moveTo(mViewToSurroundCenterX, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        // Going to the left
        mLeftArcPath.lineTo(mViewToSurroundCenterX - mViewToSurroundRadius, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        // Going to the bottom
        mLeftArcPath.lineTo(mViewToSurroundCenterX - mViewToSurroundRadius, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        // Going to the middle again.
        mLeftArcPath.lineTo(mViewToSurroundCenterX, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        mLeftArcPath.arcTo(mViewBoundsInParent, 90, 180);

        canvas.drawPath(mLeftArcPath, mBackgroundPaint);

        /**
         *  Right side of the circle
         * */
        // Starting the path from the middle of x of the view and the top y.
        mRightArcPath.moveTo(mViewToSurroundCenterX, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        // Going to the left
        mRightArcPath.lineTo(mViewToSurroundCenterX + mViewToSurroundRadius, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        // Going to the bottom
        mRightArcPath.lineTo(mViewToSurroundCenterX + mViewToSurroundRadius, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight + actionBarHeight);

//        // Going to the middle again.
        mRightArcPath.lineTo(mViewToSurroundCenterX, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        mRightArcPath.arcTo(mViewBoundsInParent, 90, -180);

        canvas.drawPath(mRightArcPath, mBackgroundPaint);

        /**
         *  Drawing two semi transparent circles so the view would look better.
         * */
        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA);
        canvas.drawCircle(mViewToSurroundCenterX,
                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() / 2,
                mInnerCirclePaint);

        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA/2);
        canvas.drawCircle(mViewToSurroundCenterX,
                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() - mInnerCirclePaint.getStrokeWidth() / 2,
                mInnerCirclePaint);
     }

    /**
     * Inflating the layout by given id that will explain about the view that has been surrounded.
     *
     * If no id was assigned the default layout will be used, <b>see</b> {@link com.braunster.tutorialview.TutorialView#setTutorialText(String) setTutorialText(String)}
     * for setting the text for the explanation.
     *
     * */
    private void inflateTutorialInfo(){

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

    /**
     * OnClickListener for hiding the tutorial when clicked.
     * */
    private OnClickListener closeTutorialClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            closeTutorial();
        }
    };

    public void closeTutorial(){
        if (DEBUG) Log.d(TAG, "onClick");

        // Disable click so the view would take on touch events from other views.
        setClickable(false);

        // Animating the views out
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.tutorial_info_view_fade_out);

        mTutorialInfoView.setAnimation(animation);
        mGotItButton.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Removing the info
                removeView(mTutorialInfoView);
                mTutorialInfoView = null;

                // Removing the "got it" button
                removeView(mGotItButton);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation.start();

        collapse(TutorialView.this, new Runnable() {
            @Override
            public void run() {
                if (DEBUG) Log.d(TAG, "onEnd Collapse");

                if (mActionBar != null)
                {
                    mViewToSurround = null;

                    mViewToSurroundHeight = -1;
                    mViewToSurroundWidth = -1;

                    if (mChangeActionBarColor)
                        mActionBar.setBackgroundDrawable(new ColorDrawable(mActionBarRestoreColor));

                    if (mActionBarOldTitle != null && !mActionBarOldTitle.isEmpty())
                        mActionBar.setTitle(mActionBarOldTitle);
                }

                showing = false;

                // Setting the view height back to its original.
                getLayoutParams().height = ((View) getParent()).getLayoutParams().height;

                requestLayout();

                dispatchTutorialClosed();
            }
        });

        // Refresh the view
        invalidate();
    }

    public Animation expand(final View v, final Runnable onEnd) {
        final int initialHeight = ((View) v.getParent()).getMeasuredHeight();

        if (DEBUG) Log.d(TAG, "initialHeight: " + initialHeight);

        v.getLayoutParams().height = 0;

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                final int newHeight = (int)(initialHeight * interpolatedTime);
                getLayoutParams().height = newHeight;
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onEnd!= null)
                    onEnd.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(DEFAULT_ANIM_DURATION);
        v.startAnimation(a);
        return a;
    }

    public Animation collapse(final View v, final Runnable onEnd) {

        final int initialHeight = ((View) v.getParent()).getMeasuredHeight();

        if (DEBUG) Log.d(TAG, "initialHeight: " + initialHeight);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                getLayoutParams().height = (int) (initialHeight - (initialHeight * interpolatedTime));
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onEnd!= null)
                    onEnd.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(DEFAULT_ANIM_DURATION);
        v.startAnimation(a);
        return a;
    }

    private void dispatchTutorialClosed(){
        if (tutorialClosedListener!=null)
            tutorialClosedListener.onClosed();
    }

    public interface TutorialClosedListener{
        public void onClosed();
    }


    /**
     * The Layout id of the view that will be used to show the tutorial text or information.
     * */
    public void setTutorialInfoLayoutId(int tutorialInfoLayoutId) {
        this.mTutorialInfoLayoutId = tutorialInfoLayoutId;
    }

    public void setActionBar(ActionBar actionBar) {
        this.mActionBar = actionBar;
    }

    public void setTutorialClosedListener(TutorialClosedListener tutorialClosedListener) {
        this.tutorialClosedListener = tutorialClosedListener;
    }

    /**
     * The background color for the tutorial.
     * */
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
    public void changeActionBarColor(boolean mChangeActionBarColor) {
        this.mChangeActionBarColor = mChangeActionBarColor;
    }

    /**
     * The of the color that will be set as the action bar color when the tutorial view will be closed.
     *
     * @see
     *  com.braunster.tutorialview.TutorialView#changeActionBarColor(boolean)
     *  */
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
    public void setHasStatusBar(boolean hasStatusBar){
        statusBarHeight = hasStatusBar ? getStatusBarHeight() : 0;
    }

    /**
     *  Set true if the application has the activity that hold this view has action bar..
     * */
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
    public void setTutorialTextColor(int mTutorialTextColor) {
        this.mTutorialTextColor = mTutorialTextColor;
    }

    /**
     * Set the typeface that will be used for the default info view text view.
     *
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextSize(int)
     * @see com.braunster.tutorialview.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.TutorialView#setTutorialTextColor(int)
     *
     * */
    public void setTutorialTextTypeFace(String tutorialTextTypeFaceName) {

        if (tutorialTextTypeFaceName.isEmpty())
            return;

        this.mTutorialTextTypeFace = Typeface.createFromAsset(getResources().getAssets(), tutorialTextTypeFaceName);
    }
}




            /*

            Convert color to be visible if background is dark or bright.

            int y = (int) (0.2126 * Color.red(mTutorialBackgroundColor)
                                + 0.7152 * Color.green(mTutorialBackgroundColor)
                                + 0.0722 * Color.blue(mTutorialBackgroundColor));
            txtTutorial.setTextColor(y < 128 ? Color.WHITE: Color.BLACK);*/
