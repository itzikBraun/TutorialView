package com.braunster.tutorialviewapp;

import android.content.Intent;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;

import com.braunster.tutorialview.TutorialActivity;
import com.braunster.tutorialview.object.Tutorial;
import com.braunster.tutorialview.object.TutorialBuilder;
import com.braunster.tutorialview.object.TutorialIntentBuilder;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by braunster on 08.05.15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;

    TutorialActivity tutorialActivity;

    private boolean startTutorialForResult = false;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
    }

    @SmallTest
    public void testWalkThrough(){

        assertNotNull(activity);

        onView(withText("T")).check(matches(isDisplayed()));

        startWalkThrough();

        checkWalkThroughOpen();
    }

    @SmallTest
    public void testClick(){
        TouchUtils.clickView(this, activity.findViewById(R.id.view_almost_top_right));


        onView(withId(R.id.view_almost_top_right)).check(doesNotExist());
    }




    public void checkWalkThroughOpen(){

        // Tutorial should be open thus the button should not exist.
        onView(withText("T")).check(doesNotExist());

        assertNotNull(tutorialActivity);

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getInstrumentation().callActivityOnDestroy(tutorialActivity);

                    getInstrumentation().callActivityOnResume(activity);

                    onView(withText("T")).check(matches(isDisplayed()));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    public void startWalkThrough(){


        Tutorial t1 = getBasicBuilderForTest(activity.findViewById(R.id.view_bottom_left))
                .setTutorialInfoTextPosition(Tutorial.InfoPosition.ABOVE)
                .setTutorialGotItPosition(Tutorial.GotItPosition.BOTTOM)
                .build();

        Tutorial t2 = getBasicBuilderForTest(activity.findViewById(R.id.view_top_right))
                .setTutorialInfoTextPosition(Tutorial.InfoPosition.LEFT_OF)
                .build();

        Tutorial t3 = getBasicBuilderForTest(activity.findViewById(R.id.view_almost_top_right))
                .setTutorialInfoTextPosition(Tutorial.InfoPosition.RIGHT_OF)
                .build();

        Tutorial t4 = getBasicBuilderForTest(activity.findViewById(R.id.view_top_left))
                .setTutorialInfoTextPosition(Tutorial.InfoPosition.BELOW)
                .build();

        Tutorial t5 = getBasicBuilderForTest(activity.findViewById(R.id.view_center))
                .setTutorialInfoTextPosition(Tutorial.InfoPosition.BELOW)
                .setTutorialGotItPosition(Tutorial.GotItPosition.BOTTOM)
                .build();


        final TutorialIntentBuilder builder = new TutorialIntentBuilder(activity);

        // if true the status bar and navigation bar will be colored on Lollipop devices.
        builder.changeSystemUiColor(true);

        builder.setWalkThroughList(t1, t2, t3, t4, t5);

        final Intent intent = builder.getIntent();
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);

        tutorialActivity = (TutorialActivity) getInstrumentation().startActivitySync(intent);

        assertNotNull(tutorialActivity);

        assertEquals(tutorialActivity.getClass(), TutorialActivity.class);
    }

    private TutorialBuilder getBasicBuilderForTest(View v){
        TutorialBuilder tBuilder = new TutorialBuilder();

        tBuilder.setTitle("The Title")
                .setViewToSurround(v)
                .setInfoText("This is the explanation about the view.")
                .setBackgroundColor(randomColor())
                .setTutorialTextColor(Color.WHITE)
                .setTutorialTextTypeFaceName("fonts/olivier.ttf")
                .setTutorialTextSize(30)
                .setAnimationDuration(500);

        return tBuilder;
    }

    private int randomColor(){
        Random rnd = new Random();
        return  Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
