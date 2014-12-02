package com.braunster.tutorialview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by braunster on 02/12/14.
 */
public class TutorialActivity extends Activity {

    public static final String TAG = TutorialActivity.class.getSimpleName();
    public static final boolean DEBUG = true;

    // View location, width and height.
    private static final String
            VIEW_TO_SURROUND_X = "view_to_surround_x",
            VIEW_TO_SURROUND_Y = "view_to_surround_y",
            VIEW_TO_SURROUND_WIDTH = "view_to_surround_width",
            VIEW_TO_SURROUND_HEIGHT = "view_to_surround_height";

    private static final String TUTORIAL_INFO_LAYOUT_ID = "tut_info_layout_id";

    private static final String HAS_STATUS_BAR = "has_status_bar";

    private static final String TUTORIAL_BACKGROUND_COLOR = "tut_background_color";

    private static final String TUTORIAL_TEXT = "tutorial_default_text";
    private static final String TUTORIAL_TEXT_TYPEFACE = "tutorial_default_text_typeface";
    private static final String TUTORIAL_TEXT_COLOR = "tutorial_default_text_color";
    private static final String TUTORIAL_TEXT_SIZE = "tutorial_default_text_size";

    private TutorialView tutorialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);

        tutorialView = (TutorialView) findViewById(R.id.tutorial_view);

        if (getIntent().getExtras() == null)
        {
            finish();
            return;
        }

        final int x = getIntent().getIntExtra(VIEW_TO_SURROUND_X, -1);
        final int y = getIntent().getIntExtra(VIEW_TO_SURROUND_Y, -1);
        final int width = getIntent().getIntExtra(VIEW_TO_SURROUND_WIDTH, -1);
        final int height = getIntent().getIntExtra(VIEW_TO_SURROUND_HEIGHT, -1);

        tutorialView.setHasStatusBar(getIntent().getBooleanExtra(HAS_STATUS_BAR, true));

        // Setting the tutorial background color if was given.
        if (getIntent().getExtras().containsKey(TUTORIAL_BACKGROUND_COLOR))
            tutorialView.setTutorialBackgroundColor(getIntent().getIntExtra(TUTORIAL_BACKGROUND_COLOR, -1));

        final int tutorialLayoutId = getIntent().getIntExtra(TUTORIAL_INFO_LAYOUT_ID, -1);

        // Setting the custom layout id if isn't -1.
        if (tutorialLayoutId == -1)
            tutorialView.setTutorialText(getIntent().getStringExtra(TUTORIAL_TEXT));

        // Getting the text size, -1 will be ignored by the tutorial view.
        tutorialView.setTutorialTextSize(getIntent().getExtras().getInt(TUTORIAL_TEXT_SIZE, -1));

        tutorialView.post(new Runnable() {
            @Override
            public void run() {
                tutorialView.setPositionToSurround(x, y, width, height);
                tutorialView.setTutorialInfoLayoutId(tutorialLayoutId);
            }
        });

        // Text color
        if (getIntent().getExtras().containsKey(TUTORIAL_TEXT_COLOR))
            tutorialView.setTutorialTextColor(getIntent().getExtras().getInt(TUTORIAL_TEXT_COLOR));

        // Text Typeface
        if (getIntent().getExtras().containsKey(TUTORIAL_TEXT_TYPEFACE))
            tutorialView.setTutorialTextTypeFace(getIntent().getExtras().getString(TUTORIAL_TEXT_TYPEFACE));

        // Finish the activity when the tutorial is closed.
        tutorialView.setTutorialClosedListener(new TutorialView.TutorialClosedListener() {
            @Override
            public void onClosed() {
                finish();
            }
        });

    }

    /**
     * Back press will close the tutorial which will trigger the
     * {@link com.braunster.tutorialview.TutorialView.TutorialClosedListener#onClosed() onClosed()} method
     * which will finish the activity.
     * */
    @Override
    public void onBackPressed() {
        tutorialView.closeTutorial();
    }

    private static Intent getIntent(Context context, View view, int tutorialInfoLayoutId, boolean hasStatusBar, boolean withColor, int color, String tutorialText){

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
                .setTutorialLayoutId(tutorialInfoLayoutId)

                // FIXME For testing.
                .setTutorialTextSize(35)
                .setTutorialTextTypefaceName("fonts/roboto_light.ttf");

        if (withColor)
            intentBuilder.setBackgroundColor(color);

        if (tutorialText != null && !tutorialText.isEmpty())
            intentBuilder.setTutorialText(tutorialText);

        if (DEBUG) Log.d(TAG, "X: " + x + ", Y: " + y );

        return intentBuilder.getIntent();
    }

    public static Intent getIntent(Context context, View view, int tutorialInfoLayoutId, boolean hasStatusBar){
        return getIntent(context, view, tutorialInfoLayoutId, hasStatusBar, false, -1, null);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText){
        return getIntent(context, view, -1, hasStatusBar, false, -1, tutorialText);
    }

    public static Intent getIntent(Context context, View view, boolean hasStatusBar, String tutorialText, int color){
        return getIntent(context, view, -1, hasStatusBar, true, color, tutorialText);
    }

    public static class TutorialIntentBuilder{
        private Intent intent;

        public TutorialIntentBuilder(Context context){
            intent = new Intent(context, TutorialActivity.class );
        }

        public TutorialIntentBuilder setX(int x){
            intent.putExtra(VIEW_TO_SURROUND_X, x);
            return this;
        }

        public TutorialIntentBuilder setY(int y){
            intent.putExtra(VIEW_TO_SURROUND_Y, y );
            return this;
        }

        public TutorialIntentBuilder setWidth(int width){
            intent.putExtra(VIEW_TO_SURROUND_WIDTH, width);
            return this;
        }

        public TutorialIntentBuilder setHeight(int height){
            intent.putExtra(VIEW_TO_SURROUND_HEIGHT, height);
            return this;
        }

        public TutorialIntentBuilder hasStatusBar(boolean hasStatusBar){
            intent.putExtra(HAS_STATUS_BAR, hasStatusBar);
            return this;
        }

        public TutorialIntentBuilder setTutorialLayoutId(int tutorialLayoutId){
            intent.putExtra(TUTORIAL_INFO_LAYOUT_ID, tutorialLayoutId);
            return this;
        }

        public TutorialIntentBuilder setBackgroundColor(int color){
            intent.putExtra(TUTORIAL_BACKGROUND_COLOR, color);
            return this;
        }

        public TutorialIntentBuilder setTutorialText(String tutorialText){
            intent.putExtra(TUTORIAL_TEXT, tutorialText);
            return this;
        }

        public TutorialIntentBuilder setTutorialTextTypefaceName(String typefaceName){
            intent.putExtra(TUTORIAL_TEXT_TYPEFACE, typefaceName);
            return this;
        }

        public TutorialIntentBuilder setTutorialTextSize(int textSize){
            intent.putExtra(TUTORIAL_TEXT_SIZE, textSize);
            return this;
        }

        public TutorialIntentBuilder setTutorialTextColor(int color){
            intent.putExtra(TUTORIAL_TEXT_COLOR, color);
            return this;
        }

        public Intent getIntent(){
            return intent;
        }
    }

    private static int getActionBarHeight(Context context){
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        return actionBarHeight;
    }

}
