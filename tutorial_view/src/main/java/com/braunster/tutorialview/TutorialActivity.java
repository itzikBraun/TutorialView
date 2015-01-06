package com.braunster.tutorialview;

import android.app.Activity;
import android.os.Bundle;

import com.braunster.tutorialview.object.TutorialIntentBuilder;
import com.braunster.tutorialview.view.TutorialLayout;
import com.braunster.tutorialview.view.TutorialView;

/**
 * Created by braunster on 02/12/14.
 */
public class TutorialActivity extends Activity {

    public static final String TAG = TutorialActivity.class.getSimpleName();
    public static final boolean DEBUG = true;

    private TutorialLayout mTutorialLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial_layout);

        mTutorialLayout = (TutorialLayout) findViewById(R.id.tutorial_layout);

        if (getIntent().getExtras() == null)
        {
            throw new NullPointerException("You must provide extras to the activity to set up the tutorial view.");
        }

        // Finish the activity when the tutorial is closed.
        mTutorialLayout.setTutorialClosedListener(new TutorialView.TutorialClosedListener() {
            @Override
            public void onClosed() {
                finish();
            }
        });
    }

    /**
     * Need to wait until the window get focused before doing the animation.
     * Also it is good to post it.
     * */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
            mTutorialLayout.post(new Runnable() {
                @Override
                public void run() {
                    {
                        TutorialIntentBuilder.updateTutorialViewFromIntent(mTutorialLayout, getIntent());

                        TutorialIntentBuilder.showTutorial(mTutorialLayout, getIntent());

                        // If the tutorial is a walk through we would need to set a WalkThroughListener.
                        if (mTutorialLayout.isWalkThrough())
                        {
                            mTutorialLayout.setWalkThroughListener(new TutorialLayout.WalkThroughListener() {
                                @Override
                                public void onNextTutorialShown() {

                                }

                                @Override
                                public void onWalkTroughSkipped() {
//                                    Toast.makeText(TutorialActivity.this, "Walk through was skipped.", Toast.LENGTH_LONG).show();
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

}
