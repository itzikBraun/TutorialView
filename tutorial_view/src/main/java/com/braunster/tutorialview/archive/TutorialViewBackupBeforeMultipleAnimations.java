//package com.braunster.tutorialview.archive;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.drawable.ColorDrawable;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.animation.Transformation;
//
//import com.braunster.tutorialview.view.AbstractTutorialView;
//import com.braunster.tutorialview.R;
//
///**
//* Created by braunster on 01/12/14.
//*/
//public class TutorialViewBackupBeforeMultipleAnimations extends AbstractTutorialView {
//
//    // TODO option to change the inner circle color and alpha or disabling it.
//
//    // TODO nest the tutorial view inside a relative layout so more complex animation could be created.
//
//    public static final String TAG = TutorialViewBackupBeforeMultipleAnimations.class.getSimpleName();
//    public static final boolean DEBUG = true;
//
//    /**
//     * Left and Right path's used to create the arcs around the view that need to be surrounded.
//     * */
//    private Path mLeftArcPath = new Path(), mRightArcPath = new Path();
//
//    /**
//     * The height of the tutorial view before the expand animation start. Used to handle animation issues.
//     * */
//    private int mBeforeAnimationHeight = -1;
//
//
//    public TutorialViewBackupBeforeMultipleAnimations(Context context) {
//        super(context);
//    }
//
//    public TutorialViewBackupBeforeMultipleAnimations(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public TutorialViewBackupBeforeMultipleAnimations(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (isInEditMode())
//
//        if (DEBUG) Log.d(TAG, "onDraw, Height: " + getMeasuredHeight());
//
//        // No need to draw if does not have any view to surround,
//        // Or the view size is null (in case we do not use a view but only its coordinates).
//        if (mViewToSurround == null && mViewToSurroundHeight == -1 && mViewToSurroundWidth == -1)
//            return;
//
//        mLeftArcPath.reset();
//        mRightArcPath.reset();
//
//        // This check for former height is needed so we wont draw anything until the view size is changed and ready for the animation.
//        if (showing && mBeforeAnimationHeight != getMeasuredHeight())
//        {
//            // The size of the view before the animation
//            mBeforeAnimationHeight = -1;
//
//            // Drawing the blocks around the view.
//            drawSurroundingBlocks(canvas);
//
//            // Drawing the circle around the view.
//            drawSurroundingCircle(canvas);
//        }
//    }
//
//    @Override
//    public  void beforeFirstDraw(){
//        if (DEBUG) Log.d(TAG, "FirstDraw");
//
//        calcViewBoundsRadiusAndCenter();
//
//        // Bringing the surrounding view to front so it will be visible.
//        if (mViewToSurround != null)
//            mViewToSurround.bringToFront();
//
//        mBeforeAnimationHeight = getMeasuredHeight();
//
//        // To make sure the child order is ok.
//        ((View) getParent()).invalidate();
//
//        showing = true;
//
//        setVisibility(INVISIBLE);
//
//        expand(this, new Runnable() {
//            @Override
//            public void run() {
//                // Only if the tutorial is still showing.
//                if (showing) {
//                    inflateTutorialInfo();
//                    setClickable(true);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void closeTutorial(){
//        if (DEBUG) Log.d(TAG, "onClick");
//
//        if (closing || !showing)
//            return;
//
//        closing = true;
//
//        // Disable click so the view would take on touch events from other views.
//        setClickable(false);
//
//        // Animating the views out
//        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.tutorial_info_view_fade_out);
//
//        mTutorialInfoView.setAnimation(animation);
//        mGotItButton.setAnimation(animation);
//
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // Removing the info
//                removeView(mTutorialInfoView);
//                mTutorialInfoView = null;
//
//                // Removing the "got it" button
//                removeView(mGotItButton);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        animation.start();
//
//        collapse(TutorialViewBackupBeforeMultipleAnimations.this, new Runnable() {
//            @Override
//            public void run() {
//                if (DEBUG) Log.d(TAG, "onEnd Collapse");
//
//                if (mActionBar != null)
//                {
//                    mViewToSurround = null;
//
//                    mViewToSurroundHeight = -1;
//                    mViewToSurroundWidth = -1;
//
//                    if (mChangeActionBarColor)
//                        mActionBar.setBackgroundDrawable(new ColorDrawable(mActionBarRestoreColor));
//
//                    if (mActionBarOldTitle != null && !mActionBarOldTitle.isEmpty())
//                        mActionBar.setTitle(mActionBarOldTitle);
//                }
//
//                closing = false;
//
//                showing = false;
//
//                // Setting the view height back to its original.
//                getLayoutParams().height = ((View) getParent()).getLayoutParams().height;
//
//                requestLayout();
//
//                dispatchTutorialClosed();
//            }
//        });
//
//        // Refresh the view
//        invalidate();
//    }
//
//    /**
//     * Drawing the blocks around the view.
//     * */
//    private void drawSurroundingBlocks(Canvas canvas){
//        // If view is not in the top.
//        if (mViewToSurroundCenterY > mViewToSurroundRadius - statusBarHeight - actionBarHeight)
//        {
//            if (DEBUG) Log.i(TAG, "View not on the top");
//            canvas.drawRect(0, 0, getMeasuredWidth(), mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight, mBackgroundPaint);
//        }
//
//        // If not on bottom
//        if (mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight < getMeasuredHeight())
//        {
//            if (DEBUG) Log.i(TAG, "View not on the bottom");
//            canvas.drawRect(0, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight, getMeasuredWidth(), getMeasuredHeight(), mBackgroundPaint);
//        }
//
//        //if not on the left
//        if (mViewToSurroundCenterX > mViewToSurroundRadius)
//        {
//            if (DEBUG) Log.i(TAG, "View not on the left");
//            canvas.drawRect(0,
//                    mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight,
//                    mViewToSurroundCenterX - mViewToSurroundRadius,
//                    mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight,
//                    mBackgroundPaint);
//        }
//
//        //if not on the right
//        if (mViewToSurroundCenterX + mViewToSurroundRadius < getMeasuredWidth())
//        {
//            if (DEBUG) Log.i(TAG, "View not on the right");
//            canvas.drawRect(mViewToSurroundCenterX + mViewToSurroundRadius,
//                    mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight,
//                    getMeasuredWidth(),
//                    mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight,
//                    mBackgroundPaint);
//        }
//    }
//
//    /**
//     * Drawing the circle around the view.
//     * */
//    private void drawSurroundingCircle(Canvas canvas){
//
//        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        mBackgroundPaint.setStrokeWidth(1f);
//
//        /**
//         *  Left side of the circle
//         * */
//        // Starting the path from the middle of x of the view and the top y.
//        mLeftArcPath.moveTo(mViewToSurroundCenterX, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
//        // Going to the left
//        mLeftArcPath.lineTo(mViewToSurroundCenterX - mViewToSurroundRadius, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
//        // Going to the bottom
//        mLeftArcPath.lineTo(mViewToSurroundCenterX - mViewToSurroundRadius, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
//        // Going to the middle again.
//        mLeftArcPath.lineTo(mViewToSurroundCenterX, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
//        mLeftArcPath.arcTo(mViewBoundsInParent, 90, 180);
//
//        canvas.drawPath(mLeftArcPath, mBackgroundPaint);
//
//        /**
//         *  Right side of the circle
//         * */
//        // Starting the path from the middle of x of the view and the top y.
//        mRightArcPath.moveTo(mViewToSurroundCenterX, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
//        // Going to the left
//        mRightArcPath.lineTo(mViewToSurroundCenterX + mViewToSurroundRadius, mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
//        // Going to the bottom
//        mRightArcPath.lineTo(mViewToSurroundCenterX + mViewToSurroundRadius, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
////        // Going to the middle again.
//        mRightArcPath.lineTo(mViewToSurroundCenterX, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);
//
//        mRightArcPath.arcTo(mViewBoundsInParent, 90, -180);
//
//        canvas.drawPath(mRightArcPath, mBackgroundPaint);
//
//        /**
//         *  Drawing two semi transparent circles so the view would look better.
//         * */
//        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA);
//        canvas.drawCircle(mViewToSurroundCenterX,
//                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
//                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() / 2,
//                mInnerCirclePaint);
//
//        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA/2);
//        canvas.drawCircle(mViewToSurroundCenterX,
//                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
//                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() - mInnerCirclePaint.getStrokeWidth() / 2,
//                mInnerCirclePaint);
//     }
//
//
//    public Animation expand(final View v, final Runnable onEnd) {
//        final int initialHeight = ((View) v.getParent()).getMeasuredHeight();
//
//        if (DEBUG) Log.d(TAG, "initialHeight: " + initialHeight);
//
//        v.getLayoutParams().height = 0;
//
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                final int newHeight = (int)(initialHeight * interpolatedTime);
//                getLayoutParams().height = newHeight;
//                requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        a.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                setVisibility(VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (onEnd!= null)
//                    onEnd.run();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        a.setDuration(DEFAULT_ANIM_DURATION);
//        v.startAnimation(a);
//        return a;
//    }
//
//    public Animation collapse(final View v, final Runnable onEnd) {
//
//        final int initialHeight = ((View) v.getParent()).getMeasuredHeight();
//
//        if (DEBUG) Log.d(TAG, "initialHeight: " + initialHeight);
//
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                getLayoutParams().height = (int) (initialHeight - (initialHeight * interpolatedTime));
//                requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        a.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                setVisibility(VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (onEnd!= null)
//                    onEnd.run();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        a.setDuration(DEFAULT_ANIM_DURATION);
//        v.startAnimation(a);
//        return a;
//    }
//}
//
//
//
//
//            /*
//
//            Convert color to be visible if background is dark or bright.
//
//            int y = (int) (0.2126 * Color.red(mTutorialBackgroundColor)
//                                + 0.7152 * Color.green(mTutorialBackgroundColor)
//                                + 0.0722 * Color.blue(mTutorialBackgroundColor));
//            txtTutorial.setTextColor(y < 128 ? Color.WHITE: Color.BLACK);*/
