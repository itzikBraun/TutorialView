package com.braunster.tutorialview;

import com.braunster.tutorialview.object.Tutorial;

import java.util.ArrayList;

/**
 * Created by braunster on 24/12/14.
 *
 * An interface to make a chain of tutorial screen that will be shown to the user by order.
 *
 * The screen could be skipped at any time.
 *
 */
public interface WalkThroughInterface {

    /**
     * Moving to the next tutorial in the walk through.
     * */
    public void nextTutorial(Tutorial tutorial);

    /**
     * Skipping the rest of the walk through.
     * */
    public void skip();

    /**
     * Setting the walk through data.
     * */
    public void setWalkThroughData(ArrayList<Tutorial> tutorials);

    /**
     * Starting the walk through.
     * */
    public void startWalkThrough();
}
