package com.braunster.tutorialview;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.braunster.tutorialview.object.Tutorial;
import com.braunster.tutorialview.object.TutorialIntentBuilder;
import com.braunster.tutorialview.view.TutorialLayout;
import com.braunster.tutorialview.view.TutorialView;

import java.util.ArrayList;

/**
 * Created by braunster on 02/12/14.
 */
public class TutorialActivity extends Activity {

    public static final String TAG = TutorialActivity.class.getSimpleName();
    public static final boolean DEBUG = true;

    private TutorialLayout mTutorialLayout;

    private Tutorial mTutorial;

    private int mCurrentTutorialPos = 0;

    private static final String CURRENT_TUTORIAL = "current_tutorial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial_layout);

        mTutorialLayout = (TutorialLayout) findViewById(R.id.tutorial_layout);

        if (getIntent().getExtras() == null)
        {
            throw new NullPointerException("You must provide extras to the activity to set up the tutorial view.");
        }

        mTutorialLayout.setHasStatusBar(TutorialIntentBuilder.hasStatusBar(getIntent()));

        mTutorial = TutorialIntentBuilder.getTutorial(getIntent());

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


        // Finish the activity when the tutorial is closed.
        mTutorialLayout.setTutorialClosedListener(new TutorialView.TutorialClosedListener() {
            @Override
            public void onClosed() {
                finish();
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
                                    finish();
                                }

                                @Override
                                public void onWalkThroughDone() {
                                    finish();
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
        if (TutorialIntentBuilder.skipOnBackPressed(getIntent()))
        {
            mTutorialLayout.skip();
        }
        else mTutorialLayout.closeTutorial();
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
}
