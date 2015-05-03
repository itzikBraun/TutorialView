package com.braunster.tutorialview.view;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.braunster.tutorialview.R;
import com.braunster.tutorialview.TutorialInterface;
import com.braunster.tutorialview.TutorialViewInterface;
import com.braunster.tutorialview.WalkThroughInterface;
import com.braunster.tutorialview.object.Tutorial;

import java.util.Random;

/**
 * Created by braunster on 03/12/14.
 */
public abstract class AbstractTutorialView extends RelativeLayout implements TutorialViewInterface {

    protected static final boolean DEBUG_DRAW = false;

    public static final String TAG = AbstractTutorialView.class.getSimpleName();
    public static final boolean DEBUG = false;

    protected static final int DEFAULT_BACKGROUND_COLOR = Color.BLUE;

    protected static final int DEFAULT_PRECAUTION_ABOVE_DISTANCE = 5, DEFAULT_CIRCLE_PADDING = 40, INNER_CIRCLE_DEFAULT_ALPHA = 140, DEFAULT_STROKE_WIDTH = 13;

    /**
     * Default animation duration that is used to animate the tutorial in and out.
     * */
    private static final int DEFAULT_ANIM_DURATION  = 600;

    protected int mViewToSurroundCenterX = 0, mViewToSurroundCenterY = 0, mViewToSurroundRadius = 0;

    protected int statusBarHeight = 0, actionBarHeight = 0;

    protected View mTutorialInfoView, mGotItButton, mSkipButton, mTitleView;

    protected Tutorial mTutorial;

    /**
     * The paint that will be used to draw the tutorial background color.
     * */
    protected Paint mBackgroundPaint;

    /**
     * Used for debug drawing to find problems with calculations.
     * */
    protected Paint debugPaint;

    /**
     * The paint that will be used to draw the inner circles.
     * */
    protected Paint mInnerCirclePaint;

    /**
     * true if the tutorial is visible.
     * */
    protected boolean showing = false;

    /** true if the tutorial is animating closing of the tutorial.
     *
     * Good for checking checking if we should do close operation or we are in the middle of was.
     **/
    protected boolean closing = false;

    protected ActionBar mActionBar;

    /**
     * If true when the tutorial view will be visible the action bar color will be change to the tutorial background color
     * */
    protected boolean mChangeActionBarColor = false;

    /**
     * The default color of the action bar,
     * This is used combine with mChangeActionBarColor
     * to set the background of the action bar to the tutorial color.
     * */
    protected int mActionBarRestoreColor = -1;

    /**
     * The title of the action bar before we changed it to the tutorial title,
     * Used for restoring the action bar state after tutorial is closed.
     * */
    protected String mActionBarOldTitle = "";

    /**
     * Listener for the tutorial closing.
     * */
    protected TutorialClosedListener tutorialClosedListener;

    /**
     * Holds the view bounds on screen,
     * This is used for DEBUG_DRAW and for creating the arcs surrounding the view.
     * */
    protected RectF mViewBoundsInParent;

    /**
     * Holds the typeface that will be used for the default info view text view
     * */
    protected Typeface mTutorialTextTypeFace = null;

    /**
     * Animation type that are supported by the tutorial view.
     * */
    public enum AnimationType{
        RANDOM,
        FROM_TOP,
        FROM_BOTTOM,
        FROM_RIGHT,
        FROM_LEFT,
        FROM_TOP_RIGHT,
        FROM_TOP_LEFT,
        FROM_BOTTOM_RIGHT,
        FROM_BOTTOM_LEFT,
        FROM_VIEW_TO_SURROUND;

        private static final Random random = new Random();

        public static AnimationType getRandom(){
            // Skipping random
            return values()[random.nextInt(values().length -1) + 1];
        }
    }

    public AbstractTutorialView(Context context) {
        super(context);
        init();
    }

    public AbstractTutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractTutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface TutorialClosedListener{
        public void onClosed();
    }

    public abstract void closeTutorial();

    /**
     * This function will be called before the view needs to be draw.
     *
     * This is the place to place the entering animation code.
     * */
    public abstract void beforeFirstDraw();

    public abstract Animation show(Runnable onShown);

    public abstract Animation hide(Runnable onHidden);



    private void init(){

        mTutorial = new Tutorial();

        setWillNotDraw(false);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(DEFAULT_BACKGROUND_COLOR);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(DEFAULT_BACKGROUND_COLOR);
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA);
        mInnerCirclePaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);


        if (DEBUG_DRAW){
            debugPaint = new Paint();
            debugPaint.setColor(Color.BLACK);
        }
    }

    /**
     * Surround a given view by a visual tutorial with text.
     * @param mViewToSurround the view that need to be surrounded with the tutorial.
     * */
    @Override
    public void setViewToSurround(View mViewToSurround) {
        setViewToSurround(mViewToSurround, null);
    }

    /**
     * Surround a given view by a visual tutorial with text and title.
     *
     * @param title the text of the title that will be used for the tutorial, If an action bar is assign the title will be in the action bar title.
     * @param mViewToSurround the view that need to be surrounded with the tutorial.
     * */
    @Override
    public void setViewToSurround(View mViewToSurround, String title) {
        
        mTutorial.setTitle(title);
        mTutorial.setViewToSurround(mViewToSurround);

        setTutorial(mTutorial);

        // Moving the tutorial to the front so no other view will be visible
        bringToFront();
    }

    /**
     *  Used mostly for the tutorial activity, Instead of passing a view to surround we pass the view coordination and dimensions.
     *
     *  @param positionToSurroundX The x position of the area that should be surrounded, This could be obtained by using getX() on the view you want to surround.
     *  @param positionToSurroundY The y position of the area that should be surrounded, This could be obtained by using getY() on the view you want to surround.
     *  @param positionToSurroundWidth The width of the area that should be surrounded, This could be obtained by using getMeasuredWidth() on the view you want to surround.
     *  @param positionToSurroundHeight The height of the area that should be surrounded, This could be obtained by using getMeasuredHeight() on the view you want to surround.
     *
     *  */
    @Override
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight){
        setPositionToSurround(positionToSurroundX, positionToSurroundY, positionToSurroundWidth, positionToSurroundHeight, null);
    }

    /**
     *  Used mostly for the tutorial activity, Instead of passing a view to surround we pass the view coordination and dimensions.
     *
     *  @param title The title text that will be used for this tutorial.
     *  @param positionToSurroundX The x position of the area that should be surrounded, This could be obtained by using getX() on the view you want to surround.
     *  @param positionToSurroundY The y position of the area that should be surrounded, This could be obtained by using getY() on the view you want to surround.
     *  @param positionToSurroundWidth The width of the area that should be surrounded, This could be obtained by using getMeasuredWidth() on the view you want to surround.
     *  @param positionToSurroundHeight The height of the area that should be surrounded, This could be obtained by using getMeasuredHeight() on the view you want to surround.
     *
     *  */
    @Override
    public void setPositionToSurround(float positionToSurroundX, float positionToSurroundY, int positionToSurroundWidth, int positionToSurroundHeight, String title){
        mTutorial.setTitle(title);
        mTutorial.setPositionToSurroundX(positionToSurroundX);
        mTutorial.setPositionToSurroundY(positionToSurroundY);
        mTutorial.setPositionToSurroundWidth(positionToSurroundWidth);
        mTutorial.setPositionToSurroundHeight(positionToSurroundHeight);

        setTutorial(mTutorial);
    }

    @Override
    public void setTutorial(Tutorial tutorial){
        setTutorial(tutorial, true);
    }

    @Override
    public void setTutorial(Tutorial tutorial, boolean show){
        if (showing)
            return;

        this.mTutorial = tutorial;

        // Using the given title if not empty else we use the one in the tutorial object.
        initActionBar(mTutorial.getTitle() == null || mTutorial.getTitle().isEmpty() ? tutorial.getTitle() : mTutorial.getTitle());

        if (tutorial.getTutorialBackgroundColor() != Color.TRANSPARENT)
            setTutorialBackgroundColor(tutorial.getTutorialBackgroundColor());

        calcViewBoundsRadiusAndCenter();

        beforeFirstDraw();

        // If we setting a tutorial we should make sure the tutorial type face will be loaded.
        // The other way to show the tutorial like setPositionToSurround or setViewToSurround does not need that cause they
        // are build for interacting with the view itself and not through the tutorial object.
        initTypeFace();
        
        if (show)
            show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode())
            return;
/*
        // No need to draw if does not have any view to surround,
        // Or the view size is null (in case we do not use a view but only its coordinates).
        if ( !showing || (mTutorial != null && mTutorial.getPositionToSurroundHeight() == -1 && mTutorial.getPositionToSurroundWidth() == -1) )
            return;*/
    }

    /**
     * Calculating the view position variables and the circle radius.
     * */
    protected void calcViewBoundsRadiusAndCenter(){

        // Get the diagonal length of the square that need to be surrounded, And add padding to it.
        mViewToSurroundRadius = (int) Math.sqrt(Math.pow(mTutorial.getPositionToSurroundWidth(), 2) + Math.pow(mTutorial.getPositionToSurroundHeight(), 2)) / 2 + DEFAULT_CIRCLE_PADDING;

        /* The center of the view that need to be surrounded*/
        mViewToSurroundCenterX = (int) (mTutorial.getPositionToSurroundX() + mTutorial.getPositionToSurroundWidth()/ 2);
        mViewToSurroundCenterY = (int) (mTutorial.getPositionToSurroundY() + mTutorial.getPositionToSurroundHeight() / 2);

        /* THis rect will be used to draw the arcs.*/
        mViewBoundsInParent = new RectF(mViewToSurroundCenterX - mViewToSurroundRadius,
                mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight,
                mViewToSurroundCenterX + mViewToSurroundRadius,
                mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);

        if (DEBUG) Log.i(TAG, "X: " + mViewToSurroundCenterX + ", Y: " + mViewToSurroundCenterY + ", R: " + mViewToSurroundRadius);
    }

    /**
     * Inflating the layout by given id that will explain about the view that has been surrounded.
     *
     * If no id was assigned the default layout will be used, <b>see</b> {@link TutorialView#setTutorialText(String) setTutorialText(String)}
     * for setting the text for the explanation.
     *
     * */
    protected void inflateTutorialInfo(){
        checkTutorialDefault();

        mTutorialInfoView.setVisibility(INVISIBLE);

        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Adding the tutorial view.
        addView(mTutorialInfoView, params);

        // Inflating the "Got It" button.
        mGotItButton = inflate(getContext(), R.layout.got_it_button_view, null);
        ((TextView) mGotItButton).setTextColor(mTutorial.getTutorialTextColor());
        if (mTutorialTextTypeFace != null)
        {
            ((TextView) mGotItButton).setTypeface(mTutorialTextTypeFace);
        }
        
        // Adding skip button if the tutorial is a part of a Walkthrough.
        if (isWalkThrough())
        {
            // Inflating the "Skip" button.
            mSkipButton = inflate(getContext(), R.layout.skip_button_view, null);
            ((TextView) mSkipButton).setTextColor(mTutorial.getTutorialTextColor());
            if (mTutorialTextTypeFace != null)
            {
                ((TextView) mSkipButton).setTypeface(mTutorialTextTypeFace);
            }
        }

        // Inflating the Title for the tutorial if has any. 
        if (mTutorial.getTitle() != null && !mTutorial.getTitle().isEmpty())
        {
            mTitleView = inflate(getContext(), R.layout.title_view, null);
            ((TextView) mTitleView).setTextColor(mTutorial.getTutorialTextColor());
            ((TextView) mTitleView).setText(mTutorial.getTitle());
            
            if (mTutorialTextTypeFace != null)
            {
                ((TextView) mTitleView).setTypeface(mTutorialTextTypeFace);
            }
        }

        mTutorialInfoView.post(tutorialInfoViewPost);
    }

    /**
     * If there is now layout id we will inflate the default tutorial info layout.
     * This layout will be populated with the tutorial text passed to the view.
     * */
    private void checkTutorialDefault(){
        // If no info was assigned we use the default
        if (mTutorial.getTutorialInfoLayoutId() == -1)
        {
            mTutorial.setTutorialInfoLayoutId(R.layout.tutorial_text);
        }

        mTutorialInfoView = inflate(getContext(), mTutorial.getTutorialInfoLayoutId(), null);

        // When the default view is used it should be combined with the tutorial text variable.
        if (mTutorial.getTutorialInfoLayoutId() == R.layout.tutorial_text)
        {
            TextView txtTutorial = ((TextView) mTutorialInfoView.findViewById(R.id.tutorial_info_text));
            txtTutorial.setText(mTutorial.getTutorialText());

            if (mTutorial.getTutorialTextSize() != -1)
            {
                txtTutorial.setTextSize(mTutorial.getTutorialTextSize());
            }

            txtTutorial.setTextColor(mTutorial.getTutorialTextColor());

            if (mTutorialTextTypeFace != null)
            {
                txtTutorial.setTypeface(mTutorialTextTypeFace);
            }
        }
    }

    /**
     * Animate the tutorial info view out and remove it from the view.
     * */
    protected void removeTutorialInfo(){
        if (mTutorialInfoView != null && mGotItButton != null)
        {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.tutorial_info_view_fade_out);
            mTutorialInfoView.setAnimation(animation);
            mGotItButton.setAnimation(animation);
            if (mTitleView != null) mTitleView.setAnimation(animation);

            if (mSkipButton != null) mSkipButton.setAnimation(animation);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Removing the info
                    removeView(mTutorialInfoView);
                    mTutorialInfoView = null;

                    // Removing the "got it" button
                    removeView(mGotItButton);

                    if (mSkipButton != null) removeView(mSkipButton);

                    if (mTitleView != null) removeView(mTitleView);

                    // Clearing the tutorial title is needed so it wont be used if the next tutorial does not have a title.
                    mTitleView = null;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            animation.start();
        }
    }

    protected void dispatchTutorialClosed(){
        if (tutorialClosedListener!=null)
            tutorialClosedListener.onClosed();
    }

    /**
     * Initialize the type face object from the mTutorial object type face name.
     ***/
    protected void initTypeFace (){
        mTutorialTextTypeFace = Typeface.createFromAsset(getResources().getAssets(), mTutorial.getTutorialTextTypeFace());
    }
    
    protected void initActionBar(String title){
        // Hide the action bar if given
        if (mActionBar != null)
        {
            if (mChangeActionBarColor)
                mActionBar.setBackgroundDrawable(new ColorDrawable(mTutorial.getTutorialBackgroundColor()));

            if (title != null && !title.isEmpty())
            {
                mActionBarOldTitle = mActionBar.getTitle().toString();
                mActionBar.setTitle(title);
            }
        }
    }

    /**
     * Restore the action bar if has a reference to it.
     *
     * The color will be restored if we mChangeActionBarColor is populated
     *
     * The name will be restored if mActionBarOldTitle is populated.
     *
     * */
    protected void restoreActionBar(){
        if (mActionBar != null) {

            if (mTutorial != null)
                mTutorial.clear();

            if (mChangeActionBarColor)
                mActionBar.setBackgroundDrawable(new ColorDrawable(mActionBarRestoreColor));

            if (mActionBarOldTitle != null && !mActionBarOldTitle.isEmpty())
                mActionBar.setTitle(mActionBarOldTitle);
        }
    }

    /**
     *  Drawing two semi transparent circles so the view would look better.
     * */
    protected void drawInnerCircles(Canvas canvas){
        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA);
        canvas.drawCircle(mViewToSurroundCenterX,
                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() / 2,
                mInnerCirclePaint);

        mInnerCirclePaint.setAlpha(INNER_CIRCLE_DEFAULT_ALPHA/2);
        canvas.drawCircle(mViewToSurroundCenterX,
                mViewToSurroundCenterY - statusBarHeight - actionBarHeight,
                mViewToSurroundRadius - mInnerCirclePaint.getStrokeWidth() - mInnerCirclePaint.getStrokeWidth() / 2,
                mInnerCirclePaint);
    }

    /**
     * OnClickListener for hiding the tutorial when clicked.
     * */
    private final OnClickListener closeTutorialClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            closeTutorial();
        }
    };

    /**
     * OnClickListener for skipping the walk through tutorial.
     * */
    private final OnClickListener skipWalkThroughClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ((WalkThroughInterface) getParent()).skip();
        }
    };

    /**
     * No need to draw if the view to surround or his dimensions is null.
     * */
    protected  boolean shouldDraw(){
        return mTutorial != null && mTutorial.getPositionToSurroundHeight() != -1 && mTutorial.getPositionToSurroundWidth() != -1;
    }

    /**
     * The Layout id of the view that will be used to show the tutorial text or information.
     * */
    @Override
    public void setTutorialInfoLayoutId(int tutorialInfoLayoutId) {
        this.mTutorial.setTutorialInfoLayoutId(tutorialInfoLayoutId);
    }

    @Override
    public void setActionBar(ActionBar actionBar) {
        this.mActionBar = actionBar;
    }

    @Override
    public void setTutorialClosedListener(TutorialClosedListener tutorialClosedListener) {
        this.tutorialClosedListener = tutorialClosedListener;
    }

    /**
     * The background color for the tutorial.
     * */
    @Override
    public void setTutorialBackgroundColor(int mTutorialBackgroundColor) {
        mTutorial.setTutorialBackgroundColor(mTutorialBackgroundColor);
        mBackgroundPaint.setColor(mTutorialBackgroundColor);
        mInnerCirclePaint.setColor(mTutorialBackgroundColor);
    }

    /**
     * If true and an action bar is given to the view the action bar background color will be changed to the tutorial color.
     * You <b>should</b> also set {@link TutorialView#mActionBarRestoreColor mActionBarRestoreColor} so when the tutorial is closed the action bar color will ber restored.
     *
     * @see TutorialView#setActionBarRestoreColor(int) */
    @Override
    public void changeActionBarColor(boolean mChangeActionBarColor) {
        this.mChangeActionBarColor = mChangeActionBarColor;
    }

    /**
     * The of the color that will be set as the action bar color when the tutorial view will be closed.
     *
     * @see
     *  TutorialView#changeActionBarColor(boolean)
     *  */
    @Override
    public void setActionBarRestoreColor(int mActionBarColor) {
        this.mActionBarRestoreColor = mActionBarColor;
    }

    /**
     *  Get the height of the status bar.
     * */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     *  Set true if the application has status bar.
     * */
    @Override
    public void setHasStatusBar(boolean hasStatusBar){
        statusBarHeight = hasStatusBar ? getStatusBarHeight() : 0;
    }

    /**
     *  Set true if the application has the activity that hold this view has action bar..
     * */
    @Override
    public void setHasActionBar(boolean hasActionBar){
        actionBarHeight = hasActionBar ? getActionBarHeight() : 0;
    }

    /**
     * The text that will be used for the {@link TutorialView#mTutorialInfoView mTutorialInfoView}.
     * <b>Only</b> if the layout that is used is the default layout ({@link com.braunster.tutorialview.R.layout#tutorial_text tutorial_text}).
     *
     * @see TutorialView#setTutorialTextTypeFace(String)
     * @see TutorialView#setTutorialTextColor(int)
     * @see TutorialView#setTutorialTextSize(int)
     *
     * */
    @Override
    public void setTutorialText(String tutorialText) {
        this.mTutorial.setTutorialText(tutorialText);
    }

    private int getActionBarHeight(){
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        return actionBarHeight;
    }

    /**
     * Set the size that will be used for the default info view text view.
     * The size of the title and other button won't be changed. 
     *
     * @see TutorialView#setTutorialText(String)
     * @see TutorialView#setTutorialTextColor(int)
     * @see TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    @Override
    public void setTutorialTextSize(int mTutorialTextSize) {
        this.mTutorial.setTutorialTextSize(mTutorialTextSize);
    }

    /**
     * Set the color that will be used for the default info view text view.
     *
     * This color will also be used for the "Got It" button.
     *
     * @see TutorialView#setTutorialTextSize(int)
     * @see TutorialView#setTutorialText(String)
     * @see TutorialView#setTutorialTextTypeFace(String)
     *
     * */
    @Override
    public void setTutorialTextColor(int mTutorialTextColor) {
        this.mTutorial.setTutorialTextColor(mTutorialTextColor);
    }

    /**
     * Set the name of the wanted typeface for this tutorial.
     * 
     * The Typeface will be used for the "GotIt" and "Skip" buttons, 
     * The Typeface will be also used in the mTutorialInfoView if the default one is used.
     *
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextSize(int)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialText(String)
     * @see com.braunster.tutorialview.view.TutorialView#setTutorialTextColor(int) 
     **/
    @Override
    public void setTutorialTextTypeFace(String tutorialTextTypeFaceName) {

        if (tutorialTextTypeFaceName.isEmpty())
            return;

        this.mTutorial.setTutorialTextTypeFace(tutorialTextTypeFaceName);
        
        initTypeFace();
    }
    
    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void setAnimationType(AnimationType mAnimationType) {
        this.mTutorial.setAnimationType(mAnimationType);
    }
    
    @Override
    public void setAnimationDuration(long animationDuration) {
        this.mTutorial.setAnimationDuration(animationDuration);
    }

    public long getAnimationDuration() {
        return mTutorial.getAnimationDuration() == -1 ? DEFAULT_ANIM_DURATION : mTutorial.getAnimationDuration();
    }

    @Override
    public AnimationType getAnimationType() {
        return mTutorial.getAnimationType();
    }

    @Override
    public int getTutorialInfoLayoutId() {
        return mTutorial.getTutorialInfoLayoutId();
    }

    @Override
    public int getTutorialBackgroundColor() {
        return mTutorial.getTutorialBackgroundColor();
    }

    @Override
    public String getTutorialText() {
        return mTutorial.getTutorialText();
    }

    @Override
    public int getTutorialTextSize() {
        return mTutorial.getTutorialTextSize();
    }

    @Override
    public int getTutorialTextColor() {
        return mTutorial.getTutorialTextColor();
    }

    @Override
    public String getTutorialTextTypeFace() {
        return mTutorial.getTutorialTextTypeFace();
    }

    @Override
    public void setTutorialInfoTextPosition(int infoTextPosition) {
        mTutorial.setTutorialInfoTextPosition(infoTextPosition);
    }

    @Override
    public int getTutorialInfoTextPosition() {
        return mTutorial.getTutorialInfoTextPosition();
    }

    @Override
    public int getTutorialGotItPosition() {
        return mTutorial.getTutorialGotItPosition();
    }

    @Override
    public void setTutorialGotItPosition(int gotItPosition) {
        mTutorial.setTutorialGotItPosition(gotItPosition);
    }

    @Override
    public boolean isWalkThrough(){
        return (getParent() instanceof TutorialInterface)
                && getParent() instanceof WalkThroughInterface
                && ((TutorialViewInterface) getParent()).isWalkThrough();
    }

    /**
     * Posted on the tutorial info view to do adjustment and animation to the view.
     * */
    private final Runnable tutorialInfoViewPost = new Runnable() {
        
        RelativeLayout.LayoutParams skipButtonParams = null;
        RelativeLayout.LayoutParams titleParams = null;
        RelativeLayout.LayoutParams gotItButtonParams;
        
        boolean isTopHalf;
        
        @Override
        public void run() {
            
            // for deciding where to place views.
            isTopHalf = mViewToSurroundCenterY < getMeasuredHeight() / 2;
            
            // Got it button basic params
            gotItButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            gotItButtonParams.addRule(CENTER_HORIZONTAL);
            
            // Init the skip button params if has skip button.
            if (mSkipButton != null)
                skipButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
     
            // Setting the layout params of the title view if needed.
            initTitle();
 
            setInfoPosition();

            setGotItButtonPosition();
            
            if (mTitleView != null)
            {
                addView(mTitleView, titleParams);
            }

            addView(mGotItButton, gotItButtonParams);
            mGotItButton.setOnClickListener(closeTutorialClickListener);

            // Add the skip button to the view if needed.
            placeAndAddSkipButton();
           
            // Start the entrance animation.
            animate();
        }

        /**
         * Placing the TutorialInfoView according to the position set in the Tutorial object,
         **/
        private void setInfoPosition(){
            LayoutParams p;
            switch(mTutorial.getTutorialInfoTextPosition())
            {
                case Tutorial.InfoPosition.AUTO:
                    autoInfoPosition();
                    break;

                case Tutorial.InfoPosition.ABOVE:
                    placeInfoAbove();
                    break;

                case Tutorial.InfoPosition.BELOW:
                    placeInfoBelow();
                    break;

                case Tutorial.InfoPosition.RIGHT_OF:
                    alignInfoTopToViewTop();

                    // Setting the X value to the right of the view to surround
                    mTutorialInfoView.setX(mViewToSurroundCenterX + mViewToSurroundRadius + DEFAULT_PRECAUTION_ABOVE_DISTANCE * getResources().getDisplayMetrics().density);

                    // Setting the width of the view to be as the remainder from the right of the view to surround.
                    // This adjustment make sure that all text is visible to the user.
                    p = (LayoutParams) mTutorialInfoView.getLayoutParams();
                    p.width = getMeasuredWidth() - mViewToSurroundCenterX - mViewToSurroundRadius;
                    mTutorialInfoView.setLayoutParams(p);

                    break;

                case Tutorial.InfoPosition.LEFT_OF:
                    alignInfoTopToViewTop();

                    // Setting the width of the view to be as the remainder from the left of the view to surround.
                    // This adjustment make sure that all text is visible to the user.
                    p = (LayoutParams) mTutorialInfoView.getLayoutParams();
                    p.width = (int) (mViewToSurroundCenterX - mViewToSurroundRadius - DEFAULT_PRECAUTION_ABOVE_DISTANCE * getResources().getDisplayMetrics().density);
                    mTutorialInfoView.setLayoutParams(p);

                    // Setting the info text X value to the most left.
                    mTutorialInfoView.setX(0);
                    break;
            }
            
        }

        /**
         * Placing the "GotIt" Button according to the position set in the Tutorial object,
         **/
        private void setGotItButtonPosition(){
            switch (mTutorial.getTutorialGotItPosition())
            {
                case Tutorial.GotItPosition.AUTO:
                    autoOtherViewsPosition();
                    break;
                
                case Tutorial.GotItPosition.BOTTOM:
                    gotItButtonParams.addRule(ALIGN_PARENT_BOTTOM);

                    if (skipButtonParams != null)
                        skipButtonParams.addRule(ALIGN_PARENT_TOP);
                    break;
                
                case Tutorial.GotItPosition.TOP:
                    // Placing the got it button below the title.
                    if (mTitleView != null)
                        gotItButtonParams.addRule(BELOW, mTitleView.getId());
                    else
                        gotItButtonParams.addRule(ALIGN_PARENT_TOP);

                    if (skipButtonParams != null) skipButtonParams.addRule(ALIGN_PARENT_BOTTOM);
                    break;
            }
        }
        
        /**
         * Auto positioning of the info view - Default behavior.
         **/
        private void autoInfoPosition(){
            // The view to surround is in the top half of the screen so the info will be below it.
            if (isTopHalf)
            {
                placeInfoAbove();
            }
            // The view to surround is in the bottom half of the screen.
            else {
                placeInfoAbove();
            }
        }

        /**
         * Auto positioning of the other views ("GotIt", "Skip" and the "Title") - Default behavior.
         **/
        private void autoOtherViewsPosition(){
            // The view to surround is in the top half of the screen so the info will be below it.
            if (isTopHalf)
            {
                gotItButtonParams.addRule(ALIGN_PARENT_BOTTOM);

                if (skipButtonParams != null)
                    skipButtonParams.addRule(ALIGN_PARENT_TOP);
            }
            // The view to surround is in the bottom half of the screen.
            else {
                // Placing the got it button below the title.
                if (mTitleView != null)
                    gotItButtonParams.addRule(BELOW, mTitleView.getId());
                else
                    gotItButtonParams.addRule(ALIGN_PARENT_TOP);

                if (skipButtonParams != null) skipButtonParams.addRule(ALIGN_PARENT_BOTTOM);
            }
        }

        /**
         * Align the info text to the top of the surrounding view.
         **/
        private void alignInfoTopToViewTop(){
            mTutorialInfoView.setY(mViewToSurroundCenterY - mViewToSurroundRadius - statusBarHeight - actionBarHeight);
        }
        
        /**
         * Placing the info view above the view to surround.
         **/
        private void placeInfoAbove(){
            mTutorialInfoView.setY(mViewToSurroundCenterY - mViewToSurroundRadius -
                    mTutorialInfoView.getMeasuredHeight() - mTutorialInfoView.getPaddingTop()
                    - mTutorialInfoView.getPaddingBottom() - statusBarHeight - actionBarHeight
                    - DEFAULT_PRECAUTION_ABOVE_DISTANCE * getResources().getDisplayMetrics().density);
        }
        
        /**
         * Placing the info view below the view to surround.
         **/
        private void placeInfoBelow(){
            mTutorialInfoView.setY(mViewToSurroundCenterY + mViewToSurroundRadius - statusBarHeight - actionBarHeight);            
        }
        
        private void initTitle(){
            if (mTitleView != null)
            {
                titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                titleParams.addRule(ALIGN_PARENT_TOP);
                titleParams.addRule(CENTER_HORIZONTAL);
            }            
        }
        
        private void placeAndAddSkipButton(){
            if (skipButtonParams != null)
            {
                // Checking if the view to surround is in the left or right part of the screen.
                if (isTopHalf)
                {
                    skipButtonParams.addRule(ALIGN_PARENT_RIGHT);
//                    if (titleParams != null) titleParams.addRule(ALIGN_PARENT_RIGHT);
                }
                // The view to surround is in the bottom half of the screen.
                else {
                    skipButtonParams.addRule(ALIGN_PARENT_LEFT);
//                    if (titleParams != null) titleParams.addRule(ALIGN_PARENT_LEFT);
                }

                addView(mSkipButton, skipButtonParams);
                mSkipButton.setOnClickListener(skipWalkThroughClickListener);
            }     
        }

        /** 
         * Staring the animation for the views.
         **/
        private void animate(){
            // Animating the views in
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.tutorial_info_view_fade_in);

            mTutorialInfoView.setAnimation(animation);
            mGotItButton.setAnimation(animation);

            if (mSkipButton != null) mSkipButton.setAnimation(animation);

            if (mTitleView != null) mTitleView.setAnimation(animation);

            animation.start();            
        }
    };
}
