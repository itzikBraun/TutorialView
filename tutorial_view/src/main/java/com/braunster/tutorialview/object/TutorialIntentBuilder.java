package com.braunster.tutorialview.object;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import com.braunster.tutorialview.TutorialActivity;
import com.braunster.tutorialview.TutorialInterface;
import com.braunster.tutorialview.WalkThroughInterface;
import com.braunster.tutorialview.view.AbstractTutorialView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by braunster on 04/12/14.
 */
public class TutorialIntentBuilder {

    public static final String TAG = TutorialIntentBuilder.class.getSimpleName();
    public static final boolean DEBUG = false;

    // View location, width and height.
    private static final String
            VIEW_TO_SURROUND_X = "view_to_surround_x",
            VIEW_TO_SURROUND_Y = "view_to_surround_y",
            VIEW_TO_SURROUND_WIDTH = "view_to_surround_width",
            VIEW_TO_SURROUND_HEIGHT = "view_to_surround_height";

    private static final String TUTORIAL_OBJ = "tutorial_obj";

    private static final String TUTORIAL_INFO_LAYOUT_ID = "tut_info_layout_id";

    private static final String HAS_STATUS_BAR = "has_status_bar";

    private static final String TUTORIAL_BACKGROUND_COLOR = "tut_background_color";

    private static final String TUTORIAL_TEXT = "tutorial_default_text";
    private static final String TUTORIAL_TEXT_TYPEFACE = "tutorial_default_text_typeface";
    private static final String TUTORIAL_TEXT_COLOR = "tutorial_default_text_color";
    private static final String TUTORIAL_TEXT_SIZE = "tutorial_default_text_size";

    private static String TUTORIAL_ANIMATION_DURATION = "tutorial_animation_duration";
    private static final String TUTORIAL_ANIMATION = "tutorial_animation";

    private static final String WALK_THROUGH_DATA = "walk_through_data";

    private static final String SKIP_TUTORIAL_ON_BACK_PRESSED = "skip_tutorial_on_back_pressed";

    private Intent intent;

    public TutorialIntentBuilder(Context context){
        intent = new Intent(context, TutorialActivity.class );
    }

    public TutorialIntentBuilder setTutorial(Tutorial tutorial){
        intent.putExtra(TUTORIAL_OBJ, tutorial);
        return this;
    }

    public TutorialIntentBuilder(Intent intent){
        this.intent = intent;
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

    public TutorialIntentBuilder setAnimationDuration(long duration){
        intent.putExtra(TUTORIAL_ANIMATION_DURATION, duration);
        return this;
    }

    public TutorialIntentBuilder setWalkThroughList(ArrayList<Tutorial> tutorials){
        if (tutorials != null && tutorials.size() > 0)
        {
            intent.putParcelableArrayListExtra(WALK_THROUGH_DATA, tutorials);
        }

        return this;
    }

    public TutorialIntentBuilder setWalkThroughList(Tutorial... tutorials){
        if (tutorials != null && tutorials.length > 0)
        {
            intent.putParcelableArrayListExtra(WALK_THROUGH_DATA, new ArrayList<Parcelable>(Arrays.asList(tutorials)));
        }

        return this;
    }

    /**
     * @param skip if true the {@link com.braunster.tutorialview.TutorialActivity TutorialActivity} will skip the tutorial
     * on back pressed.
     *
     * @see com.braunster.tutorialview.view.TutorialLayout#skip()
     * */
    public TutorialIntentBuilder skipTutorialOnBackPressed(boolean skip){
        intent.putExtra(SKIP_TUTORIAL_ON_BACK_PRESSED, skip);
        return this;
    }

    /**
     *  @return the intent that was build and populated with all attributes.
     * */
    public Intent getIntent(){
        return intent;
    }

    /**
     * @return true if should skip the walk through when user press on the back button.
     * */
    public static boolean skipOnBackPressed(Intent intent){
        return intent.getBooleanExtra(SKIP_TUTORIAL_ON_BACK_PRESSED, false);
    }

    public TutorialIntentBuilder setAnimationType(int animationType){
        intent.putExtra(TUTORIAL_ANIMATION, animationType);
        return this;
    }

    /**
     *  Used in the tutorial activity to show the tutorial,
     *
     *  The data from the intent would be recovered and would be set on the TutorialInterface.
     *
     * */
    public static void showTutorial(TutorialInterface tutorial, Intent intent){

        // Showing the first tutorial in the list.
        if (tutorial instanceof WalkThroughInterface)
        {
            if (DEBUG) Log.d(TAG, "WalkThroughInterface");

            if (intent.getExtras().containsKey(WALK_THROUGH_DATA))
            {
                ((WalkThroughInterface) tutorial).startWalkThrough();
                return;
            }
            else if (DEBUG) Log.d(TAG, "ShowTutorial, WalkThrough DoesNot have walk through data.");
        }

        /**
         * If we have set the tutorial data from a tutorial obj we can just call show.
         *
         * Else we would have to use the setPositionToSurround.
         * */
        if (intent.getExtras().containsKey(TUTORIAL_OBJ))
        {
            tutorial.show();
        }
        else
        {
            final int x = intent.getIntExtra(VIEW_TO_SURROUND_X, -1);
            final int y = intent.getIntExtra(VIEW_TO_SURROUND_Y, -1);
            final int width = intent.getIntExtra(VIEW_TO_SURROUND_WIDTH, -1);
            final int height = intent.getIntExtra(VIEW_TO_SURROUND_HEIGHT, -1);

            tutorial.setPositionToSurround(x, y, width, height);
        }

    }

    /**
     * Setup tutorial from data saved in the intent.
     * */
    public static void updateTutorialViewFromIntent(final TutorialInterface tutorial, Intent intent){
        tutorial.setHasStatusBar(intent.getBooleanExtra(HAS_STATUS_BAR, true));

        /**
         * Update the tutorial with data from the intent.
         * */
        if (!intent.getExtras().containsKey(TUTORIAL_OBJ))
        {
            // Setting the tutorial background color if was given.
            if (intent.getExtras().containsKey(TUTORIAL_BACKGROUND_COLOR))
                tutorial.setTutorialBackgroundColor(intent.getIntExtra(TUTORIAL_BACKGROUND_COLOR, -1));

            final int tutorialLayoutId = intent.getIntExtra(TUTORIAL_INFO_LAYOUT_ID, -1);

            // Setting the custom layout id if isn't -1.
            if (tutorialLayoutId == -1)
                tutorial.setTutorialText(intent.getStringExtra(TUTORIAL_TEXT));

            // Getting the text size, -1 will be ignored by the tutorial view.
            tutorial.setTutorialTextSize(intent.getExtras().getInt(TUTORIAL_TEXT_SIZE, -1));

            // Text color
            if (intent.getExtras().containsKey(TUTORIAL_TEXT_COLOR))
                tutorial.setTutorialTextColor(intent.getExtras().getInt(TUTORIAL_TEXT_COLOR));

            // Text Typeface
            if (intent.getExtras().containsKey(TUTORIAL_TEXT_TYPEFACE))
                tutorial.setTutorialTextTypeFace(intent.getExtras().getString(TUTORIAL_TEXT_TYPEFACE));

            if (intent.getExtras().containsKey(TUTORIAL_ANIMATION))
                tutorial.setAnimationType(AbstractTutorialView.AnimationType.values()[intent.getExtras().getInt(TUTORIAL_ANIMATION)]);

            tutorial.setTutorialInfoLayoutId(tutorialLayoutId);

            tutorial.setAnimationDuration(intent.getLongExtra(TUTORIAL_ANIMATION_DURATION, -1));

            if (tutorial instanceof WalkThroughInterface)
                ((WalkThroughInterface) tutorial).setWalkThroughData(intent.<Tutorial>getParcelableArrayListExtra(WALK_THROUGH_DATA));
        }
        else
        {
            tutorial.setTutorial((Tutorial) intent.getParcelableExtra(TUTORIAL_OBJ), false);
        }
    }

}
