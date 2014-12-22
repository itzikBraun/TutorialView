package com.braunster.tutorialview;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by braunster on 04/12/14.
 */
public class TutorialLayout extends RelativeLayout implements TutorialInterface {

    private TutorialView mTutorialView;

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
        mTutorialView = new TutorialView(getContext());

        addView(mTutorialView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public TutorialView getTutorialView() {
        return mTutorialView;
    }

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

    @Override
    public void setTutorialClosedListener(AbstractTutorialView.TutorialClosedListener tutorialClosedListener) {
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
    public boolean isShowing() {
        return mTutorialView.isShowing();
    }

    public void closeTutorial(){
        mTutorialView.closeTutorial();
    }
}
