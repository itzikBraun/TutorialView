package com.braunster.tutorialview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.braunster.tutorialview.object.Debug;
import com.braunster.tutorialview.object.Tutorial;
import com.braunster.tutorialview.object.TutorialIntentBuilder;
import com.braunster.tutorialview.view.AbstractTutorialView;
import com.braunster.tutorialview.view.TutorialLayout;
import com.braunster.tutorialview.view.TutorialView;

import java.util.ArrayList;

/**
 * Created by braunster on 02/12/14.
 */
public class TutorialActivity extends Activity {

    public static final String TAG = TutorialActivity.class.getSimpleName();
    public static final boolean DEBUG = Debug.TutorialActivity;

    private TutorialLayout mTutorialLayout;

    private Tutorial mTutorial;

    private int mCurrentTutorialPos = 0;

    private static final String CURRENT_TUTORIAL = "current_tutorial";

    private Bundle savedInstanceState;

    public static final String VIEWED_TUTORIALS= "viewed_tutorials", IS_WALKTHROUGH = "is_tutorial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        lockOrientation();

        setContentView(R.layout.activity_tutorial_layout);

        mTutorialLayout = (TutorialLayout) findViewById(R.id.tutorial_layout);

        if (getIntent().getExtras() == null)
        {
            throw new NullPointerException("You must provide extras to the activity to set up the tutorial view.");
        }

        mTutorialLayout.setHasStatusBar(TutorialIntentBuilder.hasStatusBar(getIntent()));

        mTutorial = TutorialIntentBuilder.getTutorial(getIntent());

        // Finish the activity when the tutorial is closed.
        // In case the tutorial view is showing a walkthrough this would be ignored.
        mTutorialLayout.setTutorialClosedListener(new TutorialView.TutorialClosedListener() {
            @Override
            public void onClosed() {
                resultDone();
            }
        });

        updateSystemUIColors();
    }

    /**
     * Need to wait until the window get focused before doing the animation.
     * Also it is good to post it.
     * */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        
        if (TutorialIntentBuilder.isWalkThrough(getIntent()))
        {
            ArrayList<Tutorial> tutorials = TutorialIntentBuilder.getWalkThroughData(getIntent());

            // If the activity is re created we should start
            // from the last tutorial that was visible
            if (savedInstanceState != null)
            {
                mCurrentTutorialPos = savedInstanceState.getInt(CURRENT_TUTORIAL);

                if (DEBUG) Log.d(TAG, String.format("mCurrentTutorialPos: %s, Tutorial list size: %s",
                        mCurrentTutorialPos, tutorials.size()));

                // Removing tutorials that was already shown.
                int i = 0;
                while (i < mCurrentTutorialPos)
                {
                    i++;
                    tutorials.remove(0);
                }

                // Setting the current tutorial to be the new first tutorial
                mTutorial = tutorials.get(0);
            }

            mTutorialLayout.setWalkThroughData(tutorials);
        }
        else
        {
            mTutorialLayout.setTutorial(mTutorial, false);
        }

        if (hasFocus && !mTutorialLayout.isShowing())
            mTutorialLayout.post(new Runnable() {
                @Override
                public void run() {
                    {
                        TutorialIntentBuilder.showTutorial(mTutorialLayout, getIntent());

                        // If the tutorial is a walk through we would need to set a WalkThroughListener.
                        if (mTutorialLayout.isWalkThrough())
                        {
                            mTutorialLayout.setWalkThroughListener(new TutorialLayout.WalkThroughListener() {
                                @Override
                                public void onNextTutorialShown(Tutorial tutorial) {

                                    // Keeping track of the position in the tutorial list.
                                    // Used for a situation that the activity was killed and we need
                                    // to re-create it from the last visible tutorial.
                                    mCurrentTutorialPos++;

                                    mTutorial = tutorial;
                                    updateSystemUIColors();
                                }

                                @Override
                                public void onWalkTroughSkipped() {
                                    resultSkipped();
                                }

                                @Override
                                public void onWalkThroughDone() {
                                    resultDone();
                                }
                            });
                        }
                    }
                }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CURRENT_TUTORIAL, mCurrentTutorialPos);
    }

    /**
     * Back press will close the tutorial which will trigger the
     * {@link com.braunster.tutorialview.view.TutorialView.TutorialClosedListener#onClosed() onClosed()} method
     * which will finish the activity.
     *
     * If the tutorial is in walk through mode then the current tutorial will closed and the next one will be shown,
     * Unless the activity was set to skip when back pressed.
     *
     * @see com.braunster.tutorialview.object.TutorialIntentBuilder#skipOnBackPressed(android.content.Intent)
     *
     * */
    @Override
    public void onBackPressed() {
        // Skipping the walkthrough if requested.
        if (mTutorialLayout.isWalkThrough() && TutorialIntentBuilder.skipOnBackPressed(getIntent()))
        {
            mTutorialLayout.skip();
        }
        else {
            // Setting a new TutorialCloseListener so we could return
            // a result to the calling activity that the tutorial was closed using the back press.
            if (!mTutorialLayout.isWalkThrough())
            {
                mTutorialLayout.setTutorialClosedListener(new AbstractTutorialView.TutorialClosedListener() {
                    @Override
                    public void onClosed() {
                        resultSkipped();
                    }
                });
            }

            mTutorialLayout.closeTutorial();
        }
    }

    private void updateSystemUIColors(){

        if (isFinishing())
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            if (isDestroyed())
                return;

        // coloring the status bar and navigation bar from lollipop.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (TutorialIntentBuilder.changesSystemUiColors(getIntent()) && mTutorial != null)
            {
                getWindow().setStatusBarColor(mTutorial.getTutorialBackgroundColor());
                getWindow().setNavigationBarColor(mTutorial.getTutorialBackgroundColor());
            }
        }
    }

    /**
     * Locking the orientation so the activity will stay in the same orientation as it was entered.
     * The position of the place that need to stay visible is pre-calculated so currently there
     * is no other way to handle orientation changes.
     *
     * Code was taken from this stackoverflow answer:
     * http://stackoverflow.com/a/8450316/2568492
     * */
    private void lockOrientation(){
        switch (getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    if(rotation == android.view.Surface.ROTATION_90|| rotation == android.view.Surface.ROTATION_180){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    if(rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                }
                break;
        }
    }

    private void resultSkipped(){

        if (getCallingActivity() != null)
        {
            Intent intent = getResultIntent();

            // Adding the amount of tutorials that was shown before skipping.
            if (mTutorialLayout.isWalkThrough()){
                intent.putExtra(VIEWED_TUTORIALS, mCurrentTutorialPos);
            }

            setResult(RESULT_CANCELED, intent);
        }

        finish();
    }

    private void resultDone(){

        if (getCallingActivity() != null)
        {
            Intent intent = getResultIntent();
            setResult(RESULT_OK, intent);
        }


        finish();
    }

    private Intent getResultIntent(){


        Intent intent = new Intent();

        intent.putExtra(IS_WALKTHROUGH, mTutorialLayout.isWalkThrough());

        return intent;
    }
}
