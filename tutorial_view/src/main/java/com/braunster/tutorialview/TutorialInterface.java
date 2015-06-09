package com.braunster.tutorialview;

import com.braunster.tutorialview.view.AbstractTutorialView;

/**
 * Created by braunster on 04/12/14.
 */
public interface TutorialInterface {


    /**
     * The Layout id of the view that will be used to show the tutorial text or information.
     *
     * @param tutorialInfoLayoutId  the id of the layout that should be used for the info text.
     * */
    public void setTutorialInfoLayoutId(int tutorialInfoLayoutId);

    public int getTutorialInfoLayoutId();
    
    /**
     * The background color for the tutorial.
     *
     * @param mTutorialBackgroundColor  the backgrond color to use for the background
     * */
    public void setTutorialBackgroundColor(int mTutorialBackgroundColor);

    public int getTutorialBackgroundColor();
    
    /**
     * The text that will be used for the {@link com.braunster.tutorialview.view.TutorialView#mTutorialInfoView mTutorialInfoView}.
     * <b>Only</b> if the layout that is used is the default layout com.braunster.tutorialview.R.layout#tutorial_text.
     *
     * @param tutorialText The explenation text about the view.
     *
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextTypeFace(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextColor(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextSize(int)
     *
     * */
    public void setTutorialText(String tutorialText);

    public String getTutorialText();
    
    /**
     * Set the size that will be used for the default info view text view.
     * The size of the title and other button won't be changed. 
     *
     * @param mTutorialTextSize the size that would be used for the info text.
     *
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextColor(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    public void setTutorialTextSize(int mTutorialTextSize);

    public int getTutorialTextSize();
    
    /**
     * @param mTutorialTextColor the color that will be used for the default info view text view.
     *
     * This color will also be used for the "Got It" button.
     *
     *
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextSize(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    public void setTutorialTextColor(int mTutorialTextColor);

    public int getTutorialTextColor();
    
    /**
     * @param tutorialTextTypeFaceName  the typeface name that will be used for the default info view text view.
     *
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextSize(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextColor(int)
     *
     * */
    public void setTutorialTextTypeFace(String tutorialTextTypeFaceName);

    public String getTutorialTextTypeFace();
    
    /**
     *  @param animationType the animation type that will be used to show and hide the tutorial.
     * */
    public void setAnimationType(AbstractTutorialView.AnimationType animationType);

    public AbstractTutorialView.AnimationType getAnimationType();
    
    /**
     * @param duration the time that will be used for the animation
     * */
    public void setAnimationDuration(long duration);
    
    public long getAnimationDuration();

    public void setTutorialInfoTextPosition(int infoTextPosition);

    public int getTutorialInfoTextPosition();

    public void setTutorialGotItPosition(int gotItPosition);

    public int getTutorialGotItPosition();
}
