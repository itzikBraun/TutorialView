package com.braunster.tutorialview;

import android.app.ActionBar;
import android.view.View;

import com.braunster.tutorialview.object.Tutorial;
import com.braunster.tutorialview.view.AbstractTutorialView;

/**
 * Created by braunster on 04/12/14.
 */
public interface TutorialViewInterface extends TutorialInterface {

    /**
     * Surround a given view by a visual tutorial with text.
     * @param mViewToSurround - the view that need to be surrounded with the tutorial.
     * */
    public void setViewToSurround(View mViewToSurround);

    /**
     * Surround a given view by a visual tutorial with text and title.
     *
     * @param title - the text of the title that will be used for the tutorial, If an action bar is assign the title will be in the action bar title.
     * @param mViewToSurround - the view that need to be surrounded with the tutorial.
     * */
    public void setViewToSurround(View mViewToSurround, String title);

    /**
     *  Used mostly for the tutorial activity, Instead of passing a view to surround we pass the view coordination and dimensions.
     *
     *  @param positionToSurroundX - The x position of the area that should be surrounded, This could be obtained by using getX() on the view you want to surround.
     *  @param positionToSurroundY - The y position of the area that should be surrounded, This could be obtained by using getY() on the view you want to surround.
     *  @param positionToSurroundWidth - The width of the area that should be surrounded, This could be obtained by using getMeasuredWidth() on the view you want to surround.
     *  @param positionToSurroundHeight - The height of the area that should be surrounded, This could be obtained by using getMeasuredHeight() on the view you want to surround.
     *
     *  */
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight);

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
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight, String title);

    /**
     * Set the tutorial data for this view and animate it into the screen if wanted.
     *
     * @param tutorial The tutorial object that hold all the tutorial data.
     * @param show pass true if you want the tutorial to be shown.
     * */
    public void setTutorial(Tutorial tutorial, boolean show);

    /**
     * Set the tutorial data for this view and animate it into the screen.
     *
     * @param tutorial The tutorial object that hold all the tutorial data.
     * */
    public void setTutorial(Tutorial tutorial);

    /**
     * Setting the action bar, Used only if you pace the Tutorial view inside your root view.
     *
     * When set the view could change the title and color of the action bar as you pleased.
     *
     * @see #setActionBarRestoreColor(int)
     * @see #changeActionBarColor(boolean)     */
    public void setActionBar(ActionBar actionBar);

    /**
     * Setting a listener that will be called when the tutorial is closed.
     * */
    public void setTutorialClosedListener(AbstractTutorialView.TutorialClosedListener tutorialClosedListener);

    /**
     * If true and an action bar is given to the view the action bar background color will be changed to the tutorial color.
     * You <b>should</b> also set {@link com.braunster.tutorialview.view.TutorialView#mActionBarRestoreColor mActionBarRestoreColor} so when the tutorial is closed the action bar color will ber restored.
     *
     * @see com.braunster.tutorialview.view.TutorialView#setActionBarRestoreColor(int) */
    public void changeActionBarColor(boolean mChangeActionBarColor);

    /**
     * The of the color that will be set as the action bar color when the tutorial view will be closed.
     *
     * @see
     *  com.braunster.tutorialview.view.TutorialView#changeActionBarColor(boolean)
     *  */
    public void setActionBarRestoreColor(int mActionBarColor);

    /**
     *  Set true if the application has status bar.
     * */
    public void setHasStatusBar(boolean hasStatusBar);

    /**
     *  Set true if the application has the activity that hold this view has action bar..
     * */
    public void setHasActionBar(boolean hasActionBar);


    /**
     * @return true if the tutorial is now showing it will remain true until the tutorial is fully animated out.
     * */
    public boolean isShowing();

    /**
     *  @return true if the view is in walk through mode.
     * */
    public boolean isWalkThrough();


    public void show();

    public void hide();

}
