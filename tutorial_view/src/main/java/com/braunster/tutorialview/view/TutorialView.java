package com.braunster.tutorialview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by braunster on 01/12/14.
 */
public class TutorialView extends AbstractTutorialView {

    public static final String TAG = TutorialView.class.getSimpleName();
    public static final boolean DEBUG = false;

    /**
     * Left and Right path's used to create the arcs around the view that need to be surrounded.
     * */
    private Path mLeftArcPath = new Path(), mRightArcPath = new Path();

    /**
     * The height of the tutorial view before the expand animation start. Used to handle animation issues.
     * */
    private int mBeforeAnimationHeight = -1;


    public TutorialView(Context context) {
        super(context);
    }

    public TutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode())
            return;

        mLeftArcPath.reset();
        mRightArcPath.reset();

        // This check for former height is needed so we wont draw anything until the view size is changed and ready for the animation.
        if (shouldDraw())
        {
            // The size of the view before the animation
            mBeforeAnimationHeight = -1;

            // Drawing the blocks around the view.
            drawSurroundingBlocks(canvas);

            // Drawing the circle around the view.
            drawSurroundingCircle(canvas);
        }
    }

    @Override
    public  void beforeFirstDraw(){
        if (DEBUG) Log.d(TAG, "FirstDraw");

        mBeforeAnimationHeight = ((View) getParent()).getMeasuredHeight();

        // To make sure the child order is ok.
        ((View) getParent()).invalidate();

        showing = true;

        setVisibility(INVISIBLE);

        removeAllRules((LayoutParams) getLayoutParams());
    }

    @Override
    public void closeTutorial(){
        if (DEBUG) Log.d(TAG, "onClick");

        if (closing || !showing)
            return;

        closing = true;

        // Disable click so the view would take on touch events from other views.
        setClickable(false);

        // Animating the views out
        removeTutorialInfo();

        hide();

        // Refresh the view
        invalidate();
    }

    /**
     * Drawing the blocks around the view.
     * */
    private void drawSurroundingBlocks(Canvas canvas){
        // If view is not in the top.
        if (mViewToSurroundCenterY > mViewToSurroundRadius - statusBarHeight - actionBarHeight)
        {
//            if (DEBUG) Log.i(TAG, "View not on the top");
            canvas.drawRect(0, 0, getMeasuredWidth(), mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight, mBackgroundPaint);
        }

        // If not on bottom
        if (mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight < getMeasuredHeight())
        {
//            if (DEBUG) Log.i(TAG, "View not on the bottom");
            canvas.drawRect(0, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight, getMeasuredWidth(), getMeasuredHeight(), mBackgroundPaint);
        }

        //if not on the left
        if (mViewToSurroundCenterX > mViewToSurroundRadius)
        {
//            if (DEBUG) Log.i(TAG, "View not on the left");
            canvas.drawRect(0,
                    mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight,
                    mViewToSurroundCenterX - mViewToSurroundRadius,
                    mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight,
                    mBackgroundPaint);
        }

        //if not on the right
        if (mViewToSurroundCenterX + mViewToSurroundRadius < getMeasuredWidth())
        {
//            if (DEBUG) Log.i(TAG, "View not on the right");
            canvas.drawRect(mViewToSurroundCenterX + mViewToSurroundRadius,
                    mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight,
                    getMeasuredWidth(),
                    mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight,
                    mBackgroundPaint);
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
        mRightArcPath.lineTo(mViewToSurroundCenterX + mViewToSurroundRadius, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);

//        // Going to the middle again.
        mRightArcPath.lineTo(mViewToSurroundCenterX, mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        mRightArcPath.arcTo(mViewBoundsInParent, 90, -180);

        canvas.drawPath(mRightArcPath, mBackgroundPaint);

        /**
         *  Drawing two semi transparent circles so the view would look better.
         * */
        drawInnerCircles(canvas);
    }

    @Override
    public Animation show(final Runnable onEnd) {
        if (DEBUG) Log.v(TAG, "expand");

        final int initialHeight = ((View) getParent()).getMeasuredHeight();
        final int initialWidth = ((View) getParent()).getMeasuredWidth();

        prepareForAnimation();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                switch (mTutorial.getAnimationType())
                {
                    case FROM_BOTTOM:
                    case FROM_TOP:
                        getLayoutParams().height = (int) (initialHeight * interpolatedTime);
                        break;

                    case FROM_RIGHT:
                    case FROM_LEFT:
                        getLayoutParams().width = (int) (initialWidth * interpolatedTime);
                        break;

                    case FROM_TOP_RIGHT:
                    case FROM_TOP_LEFT:
                    case FROM_BOTTOM_LEFT:
                    case FROM_BOTTOM_RIGHT:
                        getLayoutParams().width = (int) (initialWidth * interpolatedTime);
                        getLayoutParams().height = (int) (initialHeight * interpolatedTime);
                        break;
                }

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

        a.setDuration(getAnimationDuration());
        startAnimation(a);

        return a;
    }

    @Override
    public Animation hide(final Runnable onEnd) {

        final int initialHeight = ((View) getParent()).getMeasuredHeight();
        final int initialWidth = ((View) getParent()).getMeasuredWidth();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                switch (mTutorial.getAnimationType())
                {
                    case FROM_BOTTOM:
                    case FROM_TOP:
                        getLayoutParams().height = (int) (initialHeight - (initialHeight * interpolatedTime));
                        break;

                    case FROM_RIGHT:
                    case FROM_LEFT:
                        getLayoutParams().width = (int) (initialWidth- (initialWidth * interpolatedTime));
                        break;

                    case FROM_TOP_RIGHT:
                    case FROM_TOP_LEFT:
                    case FROM_BOTTOM_LEFT:
                    case FROM_BOTTOM_RIGHT:
                        getLayoutParams().width = (int) (initialWidth- (initialWidth * interpolatedTime));
                        getLayoutParams().height = (int) (initialHeight - (initialHeight * interpolatedTime));
                        break;
                }

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
        a.setDuration(getAnimationDuration());
        startAnimation(a);
        return a;
    }

    @Override
    protected boolean shouldDraw() {
        return super.shouldDraw() && showing &&
                ( mBeforeAnimationHeight != getMeasuredHeight() ||
                        (mTutorial.getAnimationType() == AnimationType.FROM_LEFT || mTutorial.getAnimationType() == AnimationType.FROM_RIGHT) );
    }

    /**
     *  Removing old rules that was applied to the tutorial view.
     * */
    private void removeAllRules(LayoutParams params){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            params.removeRule(ALIGN_PARENT_BOTTOM);
            params.removeRule(ALIGN_PARENT_TOP);
            params.removeRule(ALIGN_PARENT_LEFT);
            params.removeRule(ALIGN_PARENT_RIGHT);
        }
        else
        {
            params.addRule(ALIGN_PARENT_BOTTOM, 0);
            params.addRule(ALIGN_PARENT_TOP, 0);
            params.addRule(ALIGN_PARENT_LEFT, 0);
            params.addRule(ALIGN_PARENT_RIGHT, 0);
        }
    }

    private void prepareForAnimation(){
        if (mTutorial.getAnimationType() == AnimationType.RANDOM)
            mTutorial.setAnimationType(AnimationType.getRandom());

        switch (mTutorial.getAnimationType())
        {
            case RANDOM:
                if (DEBUG) Log.i(TAG, "Animating random");
                break;

            case FROM_TOP:
                if (DEBUG) Log.i(TAG, "Animating from top");
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
                getLayoutParams().height = 0;
                break;

            case FROM_BOTTOM:
                if (DEBUG) Log.i(TAG, "Animating from bottom");
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);
                getLayoutParams().height = 0;
                break;

            case FROM_LEFT:
                if (DEBUG) Log.i(TAG, "Animating from left");
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);

                getLayoutParams().width = 0;
                break;

            case FROM_RIGHT:
                if (DEBUG) Log.i(TAG, "Animating from right");
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
                // Setting the view height back to its original.
                getLayoutParams().height = ((View) getParent()).getLayoutParams().height;

                getLayoutParams().width = 0;
                break;

            case FROM_TOP_LEFT:
                if (DEBUG) Log.i(TAG, "Animating top left");
                getLayoutParams().width = 0;
                getLayoutParams().height = 0;
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
                break;

            case FROM_TOP_RIGHT:
                if (DEBUG) Log.i(TAG, "Animating top right");
                getLayoutParams().width = 0;
                getLayoutParams().height = 0;
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_TOP);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
                break;

            case FROM_BOTTOM_LEFT:
                if (DEBUG) Log.i(TAG, "Animating bottom left");
                getLayoutParams().width = 0;
                getLayoutParams().height = 0;
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);
                break;

            case FROM_BOTTOM_RIGHT:
                if (DEBUG) Log.i(TAG, "Animating bottom right");
                getLayoutParams().width = 0;
                getLayoutParams().height = 0;
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_BOTTOM);
                ((LayoutParams) getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
                break;
        }
    }

    @Override
    public void show() {
        show(new Runnable() {
            @Override
            public void run() {
                // Only if the tutorial is still showing.
                if (showing) {
                    inflateTutorialInfo();
                    setClickable(true);
                }
            }
        });
    }

    @Override
    public void hide() {
        hide(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) Log.d(TAG, "onEnd Collapse");

                restoreActionBar();

                closing = false;

                showing = false;

                // Setting the view height back to its original.
                getLayoutParams().height = ((View) getParent()).getLayoutParams().height;
                getLayoutParams().width = ((View) getParent()).getLayoutParams().width;

                requestLayout();

                dispatchTutorialClosed();
            }
        });
    }
}
