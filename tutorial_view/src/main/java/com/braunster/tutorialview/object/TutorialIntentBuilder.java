package com.braunster.tutorialview.object;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import com.braunster.tutorialview.TutorialActivity;
import com.braunster.tutorialview.TutorialViewInterface;
import com.braunster.tutorialview.WalkThroughInterface;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by braunster on 04/12/14.
 */
public class TutorialIntentBuilder {

    private static final String TAG = TutorialIntentBuilder.class.getSimpleName();
    private static final boolean DEBUG = Debug.TutorialIntentBuilder;

    private static final String TUTORIAL_OBJ = "tutorial_obj";

    private static final String HAS_STATUS_BAR = "has_status_bar";

    private static final String WALK_THROUGH_DATA = "walk_through_data";

    private static final String SKIP_TUTORIAL_ON_BACK_PRESSED = "skip_tutorial_on_back_pressed";

    private static final String CHANGE_SYSTEM_UI_COLOR = "change_system_ui_colors";

    private final Intent intent;

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

    /**
     * @param change if true changes the color of the status and navigation
     *                bars on devices running API 21 (Lollipop) and up.
     * * * */
    public TutorialIntentBuilder changeSystemUiColor(boolean change){
        intent.putExtra(CHANGE_SYSTEM_UI_COLOR, change);

        return this;
    }

    public TutorialIntentBuilder hasStatusBar(boolean hasStatusBar){
        intent.putExtra(HAS_STATUS_BAR, hasStatusBar);
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








    public static boolean changesSystemUiColors(Intent intent){
        return intent.getBooleanExtra(CHANGE_SYSTEM_UI_COLOR, true);
    }

    /**
     * @return the value passed in the intent, Default value is true as most apps has a status bar.
     * */
    public static boolean hasStatusBar(Intent intent){
        return intent.getBooleanExtra(HAS_STATUS_BAR, true);

    }

    /**
     * @return true if should skip the walk through when user press on the back button.
     * */
    public static boolean skipOnBackPressed(Intent intent){
        return intent.getBooleanExtra(SKIP_TUTORIAL_ON_BACK_PRESSED, false);
    }

    /**
     *  Used in the tutorial activity to show the tutorial,
     *
     *  The data from the intent would be recovered and would be set on the TutorialInterface.
     *
     * */
    public static void showTutorial(TutorialViewInterface tutorial, Intent intent){

        // Showing the first tutorial in the list.
        if (tutorial instanceof WalkThroughInterface)
        {
            if (DEBUG) Log.d(TAG, "WalkThroughInterface");

            if (intent.getExtras().containsKey(WALK_THROUGH_DATA))
            {
                ArrayList<Tutorial> tutorials = intent.getExtras().getParcelableArrayList(WALK_THROUGH_DATA);

                ((WalkThroughInterface) tutorial).setWalkThroughData(tutorials);
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
        else throw new IllegalArgumentException("You must pass at least on Tutorial object to the intent builder.");

    }

    /**
     * @return true if the intent contains a walk through data (List of tutorials)
     * */
    public static boolean isWalkThrough(Intent intent){
        if (intent.getExtras().containsKey(WALK_THROUGH_DATA))
            return true;


        return false;
    }

    /**
     * Get the tutorial passed in the intent, If a list of tutorial had been passed as
     * a walk through the first in the list will be returned.
     * */
    public static Tutorial getTutorial(Intent intent){

        // Showing the first tutorial in the list.
        if (intent.getExtras().containsKey(WALK_THROUGH_DATA))
        {
            ArrayList<Tutorial> tutorials = intent.getExtras().getParcelableArrayList(WALK_THROUGH_DATA);

            return tutorials.get(0);
        }
        else return (Tutorial) intent.getParcelableExtra(TUTORIAL_OBJ);

    }

    /**
     * Getting the list of tutorials that will be used for the walk through
     * */
    public static ArrayList<Tutorial> getWalkThroughData(Intent intent){
        if (intent.getExtras().containsKey(WALK_THROUGH_DATA))
        {
            return intent.getExtras().getParcelableArrayList(WALK_THROUGH_DATA);
        }

        return new ArrayList<>();
    }
}
