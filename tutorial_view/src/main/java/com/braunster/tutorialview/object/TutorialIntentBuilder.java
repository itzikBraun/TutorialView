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

    public static final String TAG = TutorialIntentBuilder.class.getSimpleName();
    public static final boolean DEBUG = false;

    private static final String TUTORIAL_OBJ = "tutorial_obj";

    private static final String HAS_STATUS_BAR = "has_status_bar";

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
     * Setup tutorial from data saved in the intent.
     * */
    public static void updateTutorialViewFromIntent(final TutorialViewInterface tutorial, Intent intent){
        tutorial.setHasStatusBar(intent.getBooleanExtra(HAS_STATUS_BAR, true));

        /**
         * Update the tutorial with data from the intent.
         * */
        if (intent.getExtras().containsKey(TUTORIAL_OBJ))
        {
            tutorial.setTutorial((Tutorial) intent.getParcelableExtra(TUTORIAL_OBJ), false);
        }
        else throw new IllegalArgumentException("You must pass at least on Tutorial object to the intent builder.");
    }

}
