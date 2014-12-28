package com.braunster.tutorialview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

        TutorialIntentBuilder.updateTutorialViewFromIntent(mTutorialLayout, getIntent());

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
                                    Toast.makeText(TutorialActivity.this, "Walk through was skipped.", Toast.LENGTH_LONG).show();
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

    private static Intent getIntent(Context context, View view,
                                    int tutorialInfoLayoutId,
                                    boolean hasStatusBar,
                                    boolean withColor,
                                    int color,
                                    String tutorialText,
                                    int tutorialTextSize,
                                    String typefaceName,
                                    AbstractTutorialView.AnimationType animationType,
                                    ArrayList<Tutorial> tutorials){

        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        int x = loc[0];
        int y = loc[1];

        TutorialIntentBuilder intentBuilder = new TutorialIntentBuilder(context)
                .setX(x)
                .setY(y)
                .setWidth(view.getMeasuredWidth())
                .setHeight(view.getMeasuredHeight())
                .hasStatusBar(hasStatusBar)
                .setTutorialLayoutId(tutorialInfoLayoutId);

        if (tutorialTextSize != -1)
            intentBuilder.setTutorialTextSize(tutorialTextSize);

        if (typefaceName != null && !typefaceName.isEmpty())
            intentBuilder.setTutorialTextTypefaceName(typefaceName);

        if (animationType != null)
            intentBuilder.setAnimationType(animationType.ordinal());

        if (withColor)
            intentBuilder.setBackgroundColor(color);

        if (tutorialText != null && !tutorialText.isEmpty())
            intentBuilder.setTutorialText(tutorialText);

        // Setting the walk through data, Null or empty list will be ignored.
        intentBuilder.setWalkThroughList(tutorials);

        if (DEBUG) Log.d(TAG, "X: " + x + ", Y: " + y );

        return intentBuilder.getIntent();
    }

    public static Intent getIntent(Context context, View view, int tutorialInfoLayoutId, boolean hasStatusBar){
        return getIntent(context, view, tutorialInfoLayoutId, hasStatusBar, false, -1, null, -1, null, AbstractTutorialView.AnimationType.FROM_TOP, null);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText){
        return getIntent(context, view, -1, hasStatusBar, false, -1, tutorialText,  -1, null, AbstractTutorialView.AnimationType.FROM_TOP, null);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText, int color){
        return getIntent(context, view, -1, hasStatusBar, true, color, tutorialText, -1, null, AbstractTutorialView.AnimationType.FROM_TOP, null);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText, int color, int textSize){
        return getIntent(context, view, -1, hasStatusBar, true, color, tutorialText, textSize, null, AbstractTutorialView.AnimationType.FROM_TOP, null);
    }

}
