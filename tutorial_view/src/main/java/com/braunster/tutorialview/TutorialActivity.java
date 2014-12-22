package com.braunster.tutorialview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
            finish();
            return;
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) TutorialIntentBuilder.showTutorial(mTutorialLayout, getIntent());
    }

    /**
     * Back press will close the tutorial which will trigger the
     * {@link com.braunster.tutorialview.TutorialView.TutorialClosedListener#onClosed() onClosed()} method
     * which will finish the activity.
     * */
    @Override
    public void onBackPressed() {
        mTutorialLayout.closeTutorial();
    }

    private static Intent getIntent(Context context, View view, int tutorialInfoLayoutId, boolean hasStatusBar, boolean withColor, int color, String tutorialText, int tutorialTextSize, String typefaceName, AbstractTutorialView.AnimationType animationType){

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

        if (DEBUG) Log.d(TAG, "X: " + x + ", Y: " + y );

        return intentBuilder.getIntent();
    }

    public static Intent getIntent(Context context, View view, int tutorialInfoLayoutId, boolean hasStatusBar){
        return getIntent(context, view, tutorialInfoLayoutId, hasStatusBar, false, -1, null, -1, null, AbstractTutorialView.AnimationType.FROM_TOP);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText){
        return getIntent(context, view, -1, hasStatusBar, false, -1, tutorialText,  -1, null, AbstractTutorialView.AnimationType.FROM_TOP);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText, int color){
        return getIntent(context, view, -1, hasStatusBar, true, color, tutorialText, -1, null, AbstractTutorialView.AnimationType.FROM_TOP);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText, int color, int textSize){
        return getIntent(context, view, -1, hasStatusBar, true, color, tutorialText, textSize, null, AbstractTutorialView.AnimationType.FROM_TOP);
    }

}
