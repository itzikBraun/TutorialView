//package com.braunster.tutorialview;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.animation.Transformation;
//
//import java.util.Random;
//
///**
// * Created by braunster on 01/12/14.
// */
//public class TutorialViewNewDrawFail extends AbstractTutorialView {
//
//    // TODO option to change the inner circle color and alpha or disabling it.
//
//    // TODO nest the tutorial view inside a relative layout so more complex animation could be created.
//
//    public static final String TAG = TutorialViewNewDrawFail.class.getSimpleName();
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
//    public enum AnimationType{
//        RANDOM,
//        FROM_TOP,
//        FROM_BOTTOM,
//        FROM_RIGHT,
//        FROM_LEFT,
//        FROM_TOP_RIGHT,
//        FROM_TOP_LEFT,
//        FROM_BOTTOM_RIGHT,
//        FROM_BOTTOM_LEFT;
//
//        private static final Random random = new Random();
//
//        public static AnimationType getRandom(){
//            // Skipping random
//            return values()[random.nextInt(values().length -1) + 1];
//        }
//    }
//
//    private AnimationType mAnimationType = AnimationType.RANDOM;
//
//    public TutorialViewNewDrawFail(Context context) {
//        super(context);
//    }
//
//    public TutorialViewNewDrawFail(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public TutorialViewNewDrawFail(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (isInEditMode())
//            return;
//
//        if (DEBUG) Log.d(TAG, "onDraw, Height: " + getMeasuredHeight() + ", Width: " + getMeasuredWidth() + ", X: " + getX() + ", Y: " + getY() + "curX: " + curX + "curY: " + curY);
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
//        if (showing &&
//                ( mBeforeAnimationHeight != getMeasuredHeight() ||
//                        (mAnimationType == AnimationType.FROM_LEFT || mAnimationType == AnimationType.FROM_RIGHT) ))
//        {
//
////            if (DEBUG) Log.d(TAG, "Actual Drawing");
//
//            // The size of the view before the animation
//            mBeforeAnimationHeight = -1;
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
//        // Bringing the surrounding view to front so it will be visible.
//        if (mViewToSurround != null)
//            mViewToSurround.bringToFront();
//
//        mBeforeAnimationHeight = ((View) getParent()).getMeasuredHeight();
//
//        // To make sure the child order is ok.
//        ((View) getParent()).invalidate();
//
//        showing = true;
//
////        setVisibility(INVISIBLE);
//
//        removeAllRules((LayoutParams) getLayoutParams());
//
//        expand(new Runnable() {
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
//        if (mTutorialInfoView != null && mGotItButton != null)
//        {
//
//            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.tutorial_info_view_fade_out);
//            mTutorialInfoView.setAnimation(animation);
//            mGotItButton.setAnimation(animation);
//            animation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    // Removing the info
//                    removeView(mTutorialInfoView);
//                    mTutorialInfoView = null;
//
//                    // Removing the "got it" button
//                    removeView(mGotItButton);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//
//            animation.start();
//        }
//
//        collapse(new Runnable() {
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
//                getLayoutParams().width = ((View) getParent()).getLayoutParams().width;
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
//     * Drawing the circle around the view.
//     * */
//     int mostLeftDrawX;
//     private void drawSurroundingCircle(Canvas canvas){
//
//        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        mBackgroundPaint.setStrokeWidth(1f);
//
//        boolean shouldDrawCircle = shouldDrawCircle();
//
//        Log.i(TAG, shouldDrawCircle ? " Should Draw Circle " : " Should Not Draw Circle!");
//
//        switch (mAnimationType)
//        {
//            case FROM_RIGHT:
//
//                mostLeftDrawX = Math.max(getMeasuredWidth() - curX, mViewToSurroundCenterX);
//
//
//                if (shouldDrawCircle)
//                {
//                    // Move to the top - left
//                    mLeftArcPath.moveTo(mostLeftDrawX, 0);
//
//                    // Line to top - right
//                    mLeftArcPath.lineTo(getMeasuredWidth(), 0);
//
//                    // Line to bottom - right
//                    mLeftArcPath.lineTo(getMeasuredWidth(), getParentHeight());
//
//                    // Line to bottom - left
//                    mLeftArcPath.lineTo(mostLeftDrawX, getParentHeight());
//
//
//                    // Going up to the until we reach the circle
//
//                    double rPow = Math.pow(mViewToSurroundRadius , 2) ;
//                    double xPow = Math.pow(mostLeftDrawX - mViewToSurroundCenterX, 2);
//                    Double y =  Math.sqrt( ( rPow - xPow ));
//
//
//                    mLeftArcPath.lineTo(mostLeftDrawX, Math.abs(y.floatValue()) + mViewToSurroundCenterY - actionBarHeight - statusBarHeight);
//
//
//                    Double radStartAngle = Math.atan2(y.floatValue()  - actionBarHeight - statusBarHeight, mostLeftDrawX - mViewToSurroundCenterX);
//
//                    mLeftArcPath.arcTo(mViewBoundsInParent, 90, -180);
//
//                    if (DEBUG) Log.d(TAG, "Y:: " + (y.floatValue() + mViewToSurroundCenterY - actionBarHeight - statusBarHeight) + ", rPow: " + rPow + ", xPow: " + xPow + ", Angel: " + radStartAngle.floatValue());
//
//                    mLeftArcPath.lineTo(mostLeftDrawX, 0);
//
//                    canvas.drawPath(mLeftArcPath, mBackgroundPaint);
//
//                    canvas.drawCircle(mostLeftDrawX, y.floatValue() + mViewToSurroundCenterY - actionBarHeight - statusBarHeight, 10 * getResources().getDisplayMetrics().density, debugPaint);
//                    canvas.drawCircle(mostLeftDrawX, -y.floatValue() + mViewToSurroundCenterY - actionBarHeight - statusBarHeight, 10 * getResources().getDisplayMetrics().density, debugPaint);
//
//                    //TOP - LEFT
//                    canvas.drawCircle(mViewBoundsInParent.left, mViewBoundsInParent.top, 5 * getResources().getDisplayMetrics().density, debugPaint);
//                    // TOP - RIGHT
//                    canvas.drawCircle(mViewBoundsInParent.right, mViewBoundsInParent.top, 5 * getResources().getDisplayMetrics().density, debugPaint);
//                    // BOTTOM - LEFT
//                    canvas.drawCircle(mViewBoundsInParent.left, mViewBoundsInParent.bottom, 5 * getResources().getDisplayMetrics().density, debugPaint);
//                    // BOTTOM - RIGHT
//                    canvas.drawCircle(mViewBoundsInParent.right, mViewBoundsInParent.bottom, 5 * getResources().getDisplayMetrics().density, debugPaint);
//                }
//                else
//                {
//                    // Move to the top - left
//                    mLeftArcPath.moveTo(mostLeftDrawX, 0);
//
//                    // Line to top - right
//                    mLeftArcPath.lineTo(getMeasuredWidth(), 0);
//
//                    // Line to bottom - right
//                    mLeftArcPath.lineTo(getMeasuredWidth(), getParentHeight());
//
//                    // Line to bottom - left
//                    mLeftArcPath.lineTo(mostLeftDrawX, getParentHeight());
//
//                    // Closing
//                    mLeftArcPath.close();
//
//                    canvas.drawPath(mLeftArcPath, mBackgroundPaint);
//                }
//
//                break;
//
//            /*case FROM_BOTTOM:
//            case FROM_TOP:
//                break;
//
//            case FROM_LEFT:
//                break;
//
//            case FROM_TOP_RIGHT:
//            case FROM_TOP_LEFT:
//            case FROM_BOTTOM_LEFT:
//            case FROM_BOTTOM_RIGHT:
//                break;*/
//        }
//        /**
//         *  Left side of the circle
//         * */
//       /* // Starting the path from the middle of x of the view and the top y.
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
//*/
//        /**
//         *  Right side of the circle
//         * */
///*        // Starting the path from the middle of x of the view and the top y.
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
//        canvas.drawPath(mRightArcPath, mBackgroundPaint);*/
//
//        /**
//         *  Drawing two semi transparent circles so the view would look better.
//         * */
//  /*      mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA);
//        canvas.drawCircle(mViewToSurroundCenterX,
//                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
//                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() / 2,
//                mInnerCirclePaint);
//
//        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA/2);
//        canvas.drawCircle(mViewToSurroundCenterX,
//                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
//                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() - mInnerCirclePaint.getStrokeWidth() / 2,
//                mInnerCirclePaint);*/
//     }
//
//    private boolean shouldDrawCircle(){
//        switch (mAnimationType)
//        {
//            case FROM_RIGHT:
//                // If X is smaller the the most right part of the circle.
//                return getMeasuredWidth() - curX + 10 < mViewToSurroundCenterX + mViewToSurroundRadius;
//
//            /*case FROM_BOTTOM:
//            case FROM_TOP:
//                break;
//
//            case FROM_LEFT:
//                break;
//
//            case FROM_TOP_RIGHT:
//            case FROM_TOP_LEFT:
//            case FROM_BOTTOM_LEFT:
//            case FROM_BOTTOM_RIGHT:
//                break;*/
//        }
//
//        return false;
//    }
//
//    private int curX = -1, curY = -1;
//    public Animation expand(final Runnable onEnd) {
//        if (DEBUG) Log.v(TAG, "expand");
//
//        final int initialHeight = ((View) getParent()).getMeasuredHeight();
//        final int initialWidth = ((View) getParent()).getMeasuredWidth();
//
////        prepareForAnimation();
//
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//
//                switch (mAnimationType)
//                {
//                    case FROM_BOTTOM:
//                    case FROM_TOP:
//                        curY = (int) (initialHeight * interpolatedTime);
////                        getLayoutParams().height = (int) (initialHeight * interpolatedTime);
//                        break;
//
//                    case FROM_RIGHT:
//                    case FROM_LEFT:
////                        getLayoutParams().width = (int) (initialWidth * interpolatedTime);
//                        curX = (int) (initialWidth * interpolatedTime);
//                        break;
//
//                    case FROM_TOP_RIGHT:
//                    case FROM_TOP_LEFT:
//                    case FROM_BOTTOM_LEFT:
//                    case FROM_BOTTOM_RIGHT:
//                        curX = (int) (initialWidth * interpolatedTime);
//                        curY = (int) (initialHeight * interpolatedTime);
////                        getLayoutParams().width = (int) (initialWidth * interpolatedTime);
////                        getLayoutParams().height = (int) (initialHeight * interpolatedTime);
//                        break;
//                }
//
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
//
//        a.setDuration(DEFAULT_ANIM_DURATION);
//        startAnimation(a);
//
//        return a;
//    }
//
//    public Animation collapse(final Runnable onEnd) {
//
//        final int initialHeight = ((View) getParent()).getMeasuredHeight();
//        final int initialWidth = ((View) getParent()).getMeasuredWidth();
//
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//
//                switch (mAnimationType)
//                {
//                    case FROM_BOTTOM:
//                    case FROM_TOP:
//                        getLayoutParams().height = (int) (initialHeight - (initialHeight * interpolatedTime));
//                        break;
//
//                    case FROM_RIGHT:
//                    case FROM_LEFT:
//                        getLayoutParams().width = (int) (initialWidth- (initialWidth * interpolatedTime));
//                        break;
//
//                    case FROM_TOP_RIGHT:
//                    case FROM_TOP_LEFT:
//                    case FROM_BOTTOM_LEFT:
//                    case FROM_BOTTOM_RIGHT:
//                        getLayoutParams().width = (int) (initialWidth- (initialWidth * interpolatedTime));
//                        getLayoutParams().height = (int) (initialHeight - (initialHeight * interpolatedTime));
//                        break;
//                }
//
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
//        startAnimation(a);
//        return a;
//    }
//
//    public void setAnimationType(AnimationType mAnimationType) {
//        if (DEBUG) Log.v(TAG, "setAnimationType");
//        this.mAnimationType = mAnimationType;
//    }
//
//    /**
//     *  Removing old rules that was applied to the tutorial view.
//     * */
//    private void removeAllRules(LayoutParams params){
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
//            params.removeRule(ALIGN_PARENT_BOTTOM);
//            params.removeRule(ALIGN_PARENT_TOP);
//            params.removeRule(ALIGN_PARENT_LEFT);
//            params.removeRule(ALIGN_PARENT_RIGHT);
//        }
//        else
//        {
//            params.addRule(ALIGN_PARENT_BOTTOM, 0);
//            params.addRule(ALIGN_PARENT_TOP, 0);
//            params.addRule(ALIGN_PARENT_LEFT, 0);
//            params.addRule(ALIGN_PARENT_RIGHT, 0);
//        }
//    }
//
//    private void prepareForAnimation(){
//        if (mAnimationType == AnimationType.RANDOM)
//            mAnimationType = AnimationType.getRandom();
//
//        switch (mAnimationType)
//        {
//            case RANDOM:
//                if (DEBUG) Log.i(TAG, "Animating random");
//                break;
//
//            case FROM_TOP:
//                if (DEBUG) Log.i(TAG, "Animating from top");
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
//                getLayoutParams().height = 0;
//                break;
//
//            case FROM_BOTTOM:
//                if (DEBUG) Log.i(TAG, "Animating from bottom");
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);
//                getLayoutParams().height = 0;
//                break;
//
//            case FROM_LEFT:
//                if (DEBUG) Log.i(TAG, "Animating from left");
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);
//
//                getLayoutParams().width = 0;
//                break;
//
//            case FROM_RIGHT:
//                if (DEBUG) Log.i(TAG, "Animating from right");
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
//                // Setting the view height back to its original.
//                getLayoutParams().height = ((View) getParent()).getLayoutParams().height;
//
//                getLayoutParams().width = 0;
//                break;
//
//            case FROM_TOP_LEFT:
//                if (DEBUG) Log.i(TAG, "Animating top left");
//                getLayoutParams().width = 0;
//                getLayoutParams().height = 0;
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
//                break;
//
//            case FROM_TOP_RIGHT:
//                if (DEBUG) Log.i(TAG, "Animating top right");
//                getLayoutParams().width = 0;
//                getLayoutParams().height = 0;
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
//                break;
//
//            case FROM_BOTTOM_LEFT:
//                if (DEBUG) Log.i(TAG, "Animating bottom left");
//                getLayoutParams().width = 0;
//                getLayoutParams().height = 0;
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);
//                break;
//
//            case FROM_BOTTOM_RIGHT:
//                if (DEBUG) Log.i(TAG, "Animating bottom right");
//                getLayoutParams().width = 0;
//                getLayoutParams().height = 0;
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);
//                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
//                break;
//        }
//    }
//}
