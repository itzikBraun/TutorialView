package com.braunster.tutorialview.object;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.braunster.tutorialview.view.AbstractTutorialView;

/**
 * Created by braunster on 24/12/14.
 */
public class Tutorial implements Parcelable {

    private String mTitle;

    /**
     * Holds the text that will be shown in the default info view
     * */
    private String mInfoText;

    private float mPositionToSurroundX, mPositionToSurroundY;
    private int mPositionToSurroundWidth, mPositionToSurroundHeight;

    /**
     * Holds the animation duration that will be used to animate the tutorial in and out.
     *
     * Id does not assigned it will be ignored and {@link com.braunster.tutorialview.view.AbstractTutorialView#DEFAULT_ANIM_DURATION } will be used.
     * */
    protected long mAnimationDuration =-1;

    /**
     * Holds the animation type value that will be preformed on opening and closing the tutorial.
     * */
    protected AbstractTutorialView.AnimationType mAnimationType = AbstractTutorialView.AnimationType.RANDOM;

    /**
     * The layout id that will be used to inflate the tutorial info view.
     * */
    protected int mTutorialInfoLayoutId = -1;

    /**
     * Holds the color for the default info view text view.
     * */
    protected int mTutorialTextColor = Color.WHITE;

    /**
     * Holds the name of the typeface that will be used for the default info view text view
     * */
    protected String mTutorialTextTypeFaceName = null;

    /**
     * Holds the typeface that will be used for the default info view text view
     * */
    protected Typeface mTutorialTextTypeFace = null;

    /**
     * Holds the size of the default info view text view.
     * */
    protected int mTutorialTextSize = -1;

    /**
     * Using transparent to indicate that the color was not initialized by the user.
     * */
    private int mBackgroundColor = Color.TRANSPARENT;

    public Tutorial(){

    }

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

    public int getTutorialTextSize() {
        return mTutorialTextSize;
    }

    public void setTutorialTextSize(int mTutorialTextSize) {
        this.mTutorialTextSize = mTutorialTextSize;
    }


    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }


    public void setPositionToSurroundX(float mPositionToSurroundX) {
        this.mPositionToSurroundX = mPositionToSurroundX;
    }

    public void setmPositionToSurroundY(float mPositionToSurroundY) {
        this.mPositionToSurroundY = mPositionToSurroundY;
    }

    public void setPositionToSurroundWidth(int mPositionToSurroundWidth) {
        this.mPositionToSurroundWidth = mPositionToSurroundWidth;
    }

    public void setPositionToSurroundHeight(int mPositionToSurroundHeight) {
        this.mPositionToSurroundHeight = mPositionToSurroundHeight;
    }

    public int getTutorialTextColor() {
        return mTutorialTextColor;
    }

    public void setTutorialTextColor(int mTutorialTextColor) {
        this.mTutorialTextColor = mTutorialTextColor;
    }

    public Typeface getTutorialTextTypeFace() {
        return mTutorialTextTypeFace;
    }

    public void setTutorialTextTypeFace(Typeface mTutorialTextTypeFace) {
        this.mTutorialTextTypeFace = mTutorialTextTypeFace;
    }

    public int getTutorialInfoLayoutId() {
        return mTutorialInfoLayoutId;
    }

    public void setTutorialInfoLayoutId(int mTutorialInfoLayoutId) {
        this.mTutorialInfoLayoutId = mTutorialInfoLayoutId;
    }

    public AbstractTutorialView.AnimationType getAnimationType() {
        return mAnimationType;
    }

    public void setAnimationType(AbstractTutorialView.AnimationType mAnimationType) {
        this.mAnimationType = mAnimationType;
    }

    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(long mAnimationDuration) {
        this.mAnimationDuration = mAnimationDuration;
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
        this.mAnimationDuration = in.readLong();
        this.mTutorialInfoLayoutId = in.readInt();
        this.mTutorialTextTypeFaceName = in.readString();
        this.mTutorialTextColor = in.readInt();
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
        dest.writeLong(mAnimationDuration);
        dest.writeInt(mTutorialInfoLayoutId);
        dest.writeString(mTutorialTextTypeFaceName);
        dest.writeInt(mTutorialTextColor);
    }
}
