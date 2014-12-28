package com.braunster.tutorialview.view;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.braunster.tutorialview.TutorialInterface;
import com.braunster.tutorialview.WalkThroughInterface;
import com.braunster.tutorialview.object.Tutorial;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by braunster on 04/12/14.
 */
public class TutorialLayout extends RelativeLayout implements TutorialInterface, WalkThroughInterface {

    public static final String TAG = TutorialLayout.class.getSimpleName();
    public static final boolean DEBUG = true;

    private AbstractTutorialView mTutorialView;

    public TutorialLayout(Context context) {
        super(context);
        init();
    }

    public TutorialLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TutorialLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mTutorialView = new RippleTutorialView(getContext());

        addView(mTutorialView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public AbstractTutorialView getTutorialView() {
        return mTutorialView;
    }

    private WalkThroughListener walkThroughListener;

    private ArrayList<Tutorial> tutorialList;

    private Iterator<Tutorial> tutorialIterator;

    @Override
    public void setViewToSurround(View mViewToSurround) {
        mTutorialView.setViewToSurround(mViewToSurround);
    }

    @Override
    public void setViewToSurround(View mViewToSurround, String title) {
        mTutorialView.setViewToSurround(mViewToSurround, title);
    }

    @Override
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight) {
        mTutorialView.setPositionToSurround(positionToSurroundX, positionToSurroundY, positionToSurroundWidth, positionToSurroundHeight);
    }

    @Override
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight, String title) {
        mTutorialView.setPositionToSurround(positionToSurroundX, positionToSurroundY, positionToSurroundWidth, positionToSurroundHeight, title);
    }

    @Override
    public void setTutorialInfoLayoutId(int tutorialInfoLayoutId) {
        mTutorialView.setTutorialInfoLayoutId(tutorialInfoLayoutId);
    }

    @Override
    public void setActionBar(ActionBar actionBar) {
        mTutorialView.setActionBar(actionBar);
    }

    /**
     * Set the listener on the tutorial view, If walk through is used this will be overriden and you should set a {@link com.braunster.tutorialview.view.TutorialLayout.WalkThroughListener WalkThroughListener} instead.
     *
     * @see #setWalkThroughListener(com.braunster.tutorialview.view.TutorialLayout.WalkThroughListener)
     * */
    @Override
    public void setTutorialClosedListener(AbstractTutorialView.TutorialClosedListener tutorialClosedListener) {
        if (isWalkThrough())
            return;

        mTutorialView.setTutorialClosedListener(tutorialClosedListener);
    }

    @Override
    public void setTutorialBackgroundColor(int mTutorialBackgroundColor) {
        mTutorialView.setTutorialBackgroundColor(mTutorialBackgroundColor);
    }

    @Override
    public void changeActionBarColor(boolean mChangeActionBarColor) {
        mTutorialView.changeActionBarColor(mChangeActionBarColor);
    }

    @Override
    public void setActionBarRestoreColor(int mActionBarColor) {
        mTutorialView.setActionBarRestoreColor(mActionBarColor);
    }

    @Override
    public void setHasStatusBar(boolean hasStatusBar) {
        mTutorialView.setHasStatusBar(hasStatusBar);
    }

    @Override
    public void setHasActionBar(boolean hasActionBar) {
        mTutorialView.setHasActionBar(hasActionBar);
    }

    @Override
    public void setTutorialText(String tutorialText) {
        mTutorialView.setTutorialText(tutorialText);
    }

    @Override
    public void setTutorialTextSize(int mTutorialTextSize) {
        mTutorialView.setTutorialTextSize(mTutorialTextSize);
    }

    @Override
    public void setTutorialTextColor(int mTutorialTextColor) {
        mTutorialView.setTutorialTextColor(mTutorialTextColor);
    }

    @Override
    public void setTutorialTextTypeFace(String tutorialTextTypeFaceName) {
        mTutorialView.setTutorialTextTypeFace(tutorialTextTypeFaceName);
    }

    @Override
    public void setAnimationType(AbstractTutorialView.AnimationType mAnimationType) {
        mTutorialView.setAnimationType(mAnimationType);
    }

    @Override
    public void setAnimationDuration(long duration) {
        mTutorialView.setAnimationDuration(duration);
    }

    @Override
    public boolean isShowing() {
        return mTutorialView.isShowing();
    }

    public void closeTutorial(){
        mTutorialView.closeTutorial();
    }

    public void setWalkThroughListener(WalkThroughListener walkThroughListener) {
        this.walkThroughListener = walkThroughListener;
    }

    @Override
    public void nextTutorial(Tutorial tutorial) {
        mTutorialView.setTutorial(tutorial, null);
        dispatchNextTutorialShown();
    }

    @Override
    public void skip() {

        // Setting a listener on the tutorial for the last closing before skipping the rest.
        mTutorialView.setTutorialClosedListener(new AbstractTutorialView.TutorialClosedListener() {
            @Override
            public void onClosed() {
                // Removing the listener from the tutorial view
                mTutorialView.setTutorialClosedListener(null);

                dispatchWalkThroughSkipped();
            }
        });

        mTutorialView.closeTutorial();
    }

    @Override
    public void setWalkThroughData(final ArrayList<Tutorial> tutorials) {
        if (DEBUG) Log.v(TAG, "setWalkThroughData");

        if (tutorials == null || tutorials.size() == 0)
        {
            if (DEBUG) Log.d(TAG, "Setting empty tutorial walk through list.");
            return;
        }

        tutorialList = tutorials;

        tutorialIterator = tutorials.iterator();

        // Listening to tutorial closes to we could create the next one when it closed.
        mTutorialView.setTutorialClosedListener(new AbstractTutorialView.TutorialClosedListener() {
            @Override
            public void onClosed() {
                if (DEBUG) Log.v(TAG, "onClosed");

                if (tutorialIterator.hasNext())
                    nextTutorial(tutorialIterator.next());
                else
                    dispatchWalkThroughDone();
            }
        });
    }

    @Override
    public boolean isWalkThrough(){
        return tutorialList != null && tutorialList.size() > 0;
    }

    @Override
    public void startWalkThrough() {
        if (DEBUG) Log.v(TAG, "startWalkThrough");

        if (isWalkThrough())
        {
            nextTutorial(tutorialIterator.next());
        }
    }










    private void dispatchWalkThroughSkipped(){
        if (walkThroughListener != null)
            walkThroughListener.onWalkTroughSkipped();
    }

    private void dispatchNextTutorialShown(){
        if (walkThroughListener != null)
            walkThroughListener.onNextTutorialShown();
    }

    private void dispatchWalkThroughDone(){
        if (walkThroughListener != null)
            walkThroughListener.onWalkThroughDone();
    }



    public interface WalkThroughListener {
        public void onNextTutorialShown();

        public void onWalkTroughSkipped();

        public void onWalkThroughDone();
    }

}
