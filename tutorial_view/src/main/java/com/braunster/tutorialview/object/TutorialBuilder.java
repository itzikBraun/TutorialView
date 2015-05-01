package com.braunster.tutorialview.object;

import android.graphics.Color;
import android.view.View;

import com.braunster.tutorialview.view.AbstractTutorialView;

/**
 * Created by braunster on 24/12/14.
 */
public class TutorialBuilder  {

    private String mTitle;

    /**
     * Holds the text that will be shown in the default info view
     * */
    private String mInfoText;

    private float mPositionToSurroundX = -1, mPositionToSurroundY = -1;
    private int mPositionToSurroundWidth = -1, mPositionToSurroundHeight = -1;

    /**
     * Holds the value that will be used for placing the info text in the tutorial view.
     *
     * @see com.braunster.tutorialview.object.Tutorial.InfoPosition
     */
    private int mTutorialInfoTextPosition = Tutorial.InfoPosition.AUTO;

    /**
     * Holds the value that will be used for placing the "GotIt" button in the tutorial view.
     *
     * @see com.braunster.tutorialview.object.Tutorial.GotItPosition
     */
    private int mTutorialGotItPosition = Tutorial.GotItPosition.AUTO;
    
    /**
     * Holds the animation duration that will be used to animate the tutorial in and out.
     *
     * Id does not assigned it will be ignored and {@link com.braunster.tutorialview.view.AbstractTutorialView#DEFAULT_ANIM_DURATION } will be used.
     * */
    protected long mAnimationDuration =-1;

    /**
     * Holds the animation type value that will be preformed on opening and closing the tutorial.
     * */
    protected AbstractTutorialView.AnimationType mAnimationType = AbstractTutorialView.AnimationType.RANDOM;

    /**
     * The layout id that will be used to inflate the tutorial info view.
     * */
    protected int mTutorialInfoLayoutId = -1;

    /**
     * Holds the color for the default info view text view.
     * */
    protected int mTutorialTextColor = Color.WHITE;

    /**
     * Holds the name of the typeface that will be used for the default info view text view
     * */
    protected String mTutorialTextTypeFaceName = null;

    /**
     * Holds the size of the default info view text view.
     * */
    protected int mTutorialTextSize = -1;

    /**
     * Using transparent to indicate that the color was not initialized by the user.
     * */
    private int mBackgroundColor = Color.TRANSPARENT;


    private View view;
    
    public TutorialBuilder(){

    }

    public TutorialBuilder setViewToSurround(View view){
        this.view = view;
        
        return this;
    }

    public TutorialBuilder setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        
        return this;
    }

    public TutorialBuilder setInfoText(String mInfoText) {
        this.mInfoText = mInfoText;

        return this;
    }

    public TutorialBuilder setTutorialTextSize(int mTutorialTextSize) {
        this.mTutorialTextSize = mTutorialTextSize;

        return this;
    }

    public TutorialBuilder setTitle(String mTitle) {
        this.mTitle = mTitle;

        return this;
    }

    public TutorialBuilder setPositionToSurroundX(float mPositionToSurroundX) {
        this.mPositionToSurroundX = mPositionToSurroundX;

        return this;
    }

    public TutorialBuilder setmPositionToSurroundY(float mPositionToSurroundY) {
        this.mPositionToSurroundY = mPositionToSurroundY;

        return this;
    }

    public TutorialBuilder setPositionToSurroundWidth(int mPositionToSurroundWidth) {
        this.mPositionToSurroundWidth = mPositionToSurroundWidth;

        return this;
    }

    public TutorialBuilder setPositionToSurroundHeight(int mPositionToSurroundHeight) {
        this.mPositionToSurroundHeight = mPositionToSurroundHeight;

        return this;
    }
    
    public TutorialBuilder setTutorialTextColor(int mTutorialTextColor) {
        this.mTutorialTextColor = mTutorialTextColor;

        return this;
    }

    public TutorialBuilder setTutorialTextTypeFaceName(String mTutorialTextTypeFaceName) {
        this.mTutorialTextTypeFaceName = mTutorialTextTypeFaceName;

        return this;
    }

    public TutorialBuilder setTutorialInfoLayoutId(int mTutorialInfoLayoutId) {
        this.mTutorialInfoLayoutId = mTutorialInfoLayoutId;

        return this;
    }

    public TutorialBuilder setAnimationType(AbstractTutorialView.AnimationType mAnimationType) {
        this.mAnimationType = mAnimationType;

        return this;
    }

    public TutorialBuilder setAnimationDuration(long mAnimationDuration) {
        this.mAnimationDuration = mAnimationDuration;

        return this;
    }

    public TutorialBuilder setTutorialInfoTextPosition(int infoTextPosition) {
        this.mTutorialInfoTextPosition = infoTextPosition;
        
        return this;
    }

    public TutorialBuilder setTutorialGotItPosition(int gotItPosition) {
        this.mTutorialGotItPosition = gotItPosition;
        return this;
    }

    public Tutorial build(){
        Tutorial tutorial = new Tutorial();

        if (view != null)
            tutorial.setViewToSurround(view);
        else
        {
            if (mPositionToSurroundHeight == -1 || mPositionToSurroundWidth == -1 || mPositionToSurroundX == -1 || mPositionToSurroundY == -1)
                throw new IllegalArgumentException("You need to init all view position and dimensions or just use the setViewToSurround");
            
            tutorial.setPositionToSurroundY(mPositionToSurroundY);
            tutorial.setPositionToSurroundX(mPositionToSurroundX);
            tutorial.setPositionToSurroundHeight(mPositionToSurroundHeight);
            tutorial.setPositionToSurroundWidth(mPositionToSurroundWidth);
        }
        
        tutorial.setTitle(mTitle);
        tutorial.setTutorialText(mInfoText);
        tutorial.setTutorialBackgroundColor(mBackgroundColor);
        tutorial.setTutorialTextColor(mTutorialTextColor);
        tutorial.setTutorialTextSize(mTutorialTextSize);
        tutorial.setTutorialInfoLayoutId(mTutorialInfoLayoutId);
        tutorial.setAnimationDuration(mAnimationDuration);
        tutorial.setAnimationType(mAnimationType);
        tutorial.setTutorialTextTypeFace(mTutorialTextTypeFaceName);
        tutorial.setTutorialInfoTextPosition(mTutorialInfoTextPosition);
        tutorial.setTutorialGotItPosition(mTutorialGotItPosition);
        
        return tutorial;
    }
}
