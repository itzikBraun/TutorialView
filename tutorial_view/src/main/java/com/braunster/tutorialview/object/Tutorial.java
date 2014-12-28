package com.braunster.tutorialview.object;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by braunster on 24/12/14.
 */
public class Tutorial implements Parcelable {

    private String mTitle, mInfoText;

    private float mPositionToSurroundX, mPositionToSurroundY;
    private int mPositionToSurroundWidth, mPositionToSurroundHeight;

    /**
     * Using transparent to indicate that the color was not initialized by the user.
     * */
    private int mBackgroundColor = Color.TRANSPARENT;

    public Tutorial(String title, float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight) {
        this.mTitle = title;
        this.mPositionToSurroundX = positionToSurroundX;
        this.mPositionToSurroundY = positionToSurroundY;
        this.mPositionToSurroundWidth = positionToSurroundWidth;
        this.mPositionToSurroundHeight = positionToSurroundHeight;
    }

    public Tutorial(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight) {
        this.mPositionToSurroundX = positionToSurroundX;
        this.mPositionToSurroundY = positionToSurroundY;
        this.mPositionToSurroundWidth = positionToSurroundWidth;
        this.mPositionToSurroundHeight = positionToSurroundHeight;
    }

    public Tutorial(View view, String title){
        this.mTitle = title;

        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        mPositionToSurroundX = loc[0];
        mPositionToSurroundY = loc[1];

        mPositionToSurroundHeight = view.getMeasuredHeight();
        mPositionToSurroundWidth = view.getMeasuredWidth();
    }

    public Tutorial(View view){
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        mPositionToSurroundX = loc[0];
        mPositionToSurroundY = loc[1];

        mPositionToSurroundHeight = view.getMeasuredHeight();
        mPositionToSurroundWidth = view.getMeasuredWidth();
    }


    public void clear(){
        mPositionToSurroundWidth = -1;
        mPositionToSurroundHeight = -1;
    }


    public String getTitle(){
        return mTitle;
    }

    public float getPositionToSurroundX() {
        return mPositionToSurroundX;
    }

    public float getPositionToSurroundY() {
        return mPositionToSurroundY;
    }

    public int getPositionToSurroundWidth() {
        return mPositionToSurroundWidth;
    }

    public int getPositionToSurroundHeight() {
        return mPositionToSurroundHeight;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public void setInfoText(String mInfoText) {
        this.mInfoText = mInfoText;
    }

    public String getInfoText() {
        return mInfoText;
    }




    /** For passign the object between activities.
     * */
    Tutorial(Parcel in) {
        this.mTitle = in.readString();
        this.mPositionToSurroundWidth = in.readInt();
        this.mPositionToSurroundHeight = in.readInt();
        this.mPositionToSurroundX = in.readFloat();
        this.mPositionToSurroundY = in.readFloat();
        this.mInfoText = in.readString();
        this.mBackgroundColor = in.readInt();
    }

    public static final Parcelable.Creator<Tutorial> CREATOR
            = new Parcelable.Creator<Tutorial>() {

        public Tutorial createFromParcel(Parcel in) {
            return new Tutorial(in);
        }

        public Tutorial[] newArray(int size) {
            return new Tutorial[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeInt(mPositionToSurroundWidth);
        dest.writeInt(mPositionToSurroundHeight);
        dest.writeFloat(mPositionToSurroundX);
        dest.writeFloat(mPositionToSurroundY);
        dest.writeString(mInfoText);
        dest.writeInt(mBackgroundColor);
    }
}
