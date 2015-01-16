package com.braunster.tutorialview;

import com.braunster.tutorialview.view.AbstractTutorialView;

/**
 * Created by braunster on 04/12/14.
 */
public interface TutorialInterface {


    /**
     * The Layout id of the view that will be used to show the tutorial text or information.
     * */
    public void setTutorialInfoLayoutId(int tutorialInfoLayoutId);

    public int getTutorialInfoLayoutId();
    
    /**
     * The background color for the tutorial.
     * */
    public void setTutorialBackgroundColor(int mTutorialBackgroundColor);

    public int getTutorialBackgroundColor();
    
    /**
     * The text that will be used for the {@link com.braunster.tutorialview.view.TutorialView#mTutorialInfoView mTutorialInfoView}.
     * <b>Only</b> if the layout that is used is the default layout ({@link com.braunster.tutorialview.R.layout#tutorial_text tutorial_text}).
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
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextColor(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    public void setTutorialTextSize(int mTutorialTextSize);

    public int getTutorialTextSize();
    
    /**
     * Set the color that will be used for the default info view text view.
     *
     * This color will also be used for the "Got It" button.
     *
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextSize(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    public void setTutorialTextColor(int mTutorialTextColor);

    public int getTutorialTextColor();
    
    /**
     * Set the typeface that will be used for the default info view text view.
     *
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextSize(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextColor(int)
     *
     * */
    public void setTutorialTextTypeFace(String tutorialTextTypeFaceName);

    public String getTutorialTextTypeFace();
    
    /**
     *  Setting the animation type that will be used to show and hide the tutorial.
     * */
    public void setAnimationType(AbstractTutorialView.AnimationType mAnimationType);

    public AbstractTutorialView.AnimationType getAnimationType();
    
    /**
     * Set the time that will be used for the animation
     * */
    public void setAnimationDuration(long duration);
    
    public long getAnimationDuration();
}
