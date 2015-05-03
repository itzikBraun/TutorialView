package com.braunster.tutorialview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by braunster on 01/12/14.
 */
public class RippleTutorialView extends AbstractTutorialView {

    private static final String TAG = RippleTutorialView.class.getSimpleName();
    private static final boolean DEBUG = false;

    private int mAnimatedRadius = -1;

    private int mAnimationRadiusMax = -1;

    private static final PorterDuffXfermode sXfermodeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private static final PorterDuffXfermode sXfermodeNormal = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

    public RippleTutorialView(Context context) {
        super(context);
    }

    public RippleTutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleTutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode())
            return;

        // This check for former height is needed so we wont draw anything until the view size is changed and ready for the animation.
        if (shouldDraw())
        {
            canvas.drawCircle(mViewToSurroundCenterX,
                    mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
                    mAnimatedRadius, // Radius
                    mBackgroundPaint);

            mBackgroundPaint.setXfermode(sXfermodeClear);
            canvas.drawCircle(mViewToSurroundCenterX,
                    mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
                    mViewToSurroundRadius, // Radius
                    mBackgroundPaint);

            mBackgroundPaint.setColor(mTutorial.getTutorialBackgroundColor());
            mBackgroundPaint.setXfermode(sXfermodeNormal);

            /**
             * Draw the semi transparent circle only when the ripple start.
             * */
            if (mAnimatedRadius < mViewToSurroundRadius)
                return;

            drawInnerCircles(canvas);
        }
    }

    @Override
    protected boolean shouldDraw() {
        return super.shouldDraw() && showing && mAnimatedRadius != -1;
    }

    // To remove a warning regrading using maxHeight as an x parameter.
    @SuppressWarnings("all")
    @Override
    public  void beforeFirstDraw(){
        if (DEBUG) Log.d(TAG, "FirstDraw, Width: " + getMeasuredWidth() + ", Height: " + getMeasuredHeight());

        // Getting the longest distance from the screen size so that will be the max of the radius.
        int maxHeight, maxWidth;
        if (mViewToSurroundCenterY > getMeasuredHeight()/2)
            maxHeight = mViewToSurroundCenterY;
        else
            maxHeight = getMeasuredHeight() - mViewToSurroundCenterY;

        if (mViewToSurroundCenterX > getMeasuredWidth()/2)
            maxWidth = mViewToSurroundCenterX;
        else
            maxWidth = getMeasuredWidth() - mViewToSurroundCenterX;

        maxHeight += actionBarHeight + statusBarHeight;

        maxHeight *= 2;
        maxWidth *= 2;

        mAnimationRadiusMax = (int) Math.sqrt(Math.pow(maxWidth, 2) + Math.pow(maxHeight, 2)) / 2 /*+ DEFAULT_CIRCLE_PADDING*/;

        if (DEBUG) Log.i(TAG,
                "mAnimationRadiusMax: " + mAnimationRadiusMax
                        + " maxWidth: " + maxWidth
                        + " maxHeight: " + maxHeight);

        // To make sure the child order is ok.
        ((View) getParent()).invalidate();

        setVisibility(INVISIBLE);
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
    }

    @Override
    public Animation show(final Runnable onEnd) {
        if (DEBUG) Log.v(TAG, "expand");

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                mAnimatedRadius = (int) (mAnimationRadiusMax * interpolatedTime);

                invalidate();
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

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mAnimatedRadius = (int) (mAnimationRadiusMax - (mAnimationRadiusMax * interpolatedTime));

                invalidate();
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
    public void show() {

        showing = true;

        if (DEBUG) Log.v(TAG, "show");

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

        if (DEBUG) Log.v(TAG, "hide");

        hide(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) Log.d(TAG, "onEnd Collapse");

                restoreActionBar();

                closing = false;

                showing = false;

                dispatchTutorialClosed();
            }
        });
    }
}
