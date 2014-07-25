package com.jway.apostolic;

//import com.chauhai.android.batsg.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioGroup;

public class SegmentedControlView extends RadioGroup {

	  private Drawable backgroundFirstButton;
	  private Drawable backgroundLastButton;
	  private Drawable backgroundMiddleButton;
	  private Drawable backgroundUniqueButton;

	  /**
	   * Constructor.
	   * @param context
	   */
	  public SegmentedControlView(Context context) {
	    super(context);

	    // Set orientation to horizontal.
	    this.setOrientation(HORIZONTAL);
	  }

	  /**
	   * Constructor.
	   * @param context
	   * @param attrs
	   */
	  public SegmentedControlView(Context context, AttributeSet attrs) {
	    super(context, attrs);

	    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SegmentedControlView);

	    // Retrieve button style specified in the XML layout file
	    backgroundFirstButton = a.getDrawable(R.styleable.SegmentedControlView_firstButtonBackground);
	    backgroundLastButton = a.getDrawable(R.styleable.SegmentedControlView_lastButtonBackground);
	    backgroundMiddleButton = a.getDrawable(R.styleable.SegmentedControlView_middleButtonBackground);
	    backgroundUniqueButton = a.getDrawable(R.styleable.SegmentedControlView_uniqueButtonBackground);

	    // Set the orientation to horizontal by default.
	    int orientation = a.getInt(R.styleable.SegmentedControlView_android_orientation, HORIZONTAL);
	    setOrientation(orientation);

	    a.recycle();
	  }

	  /**
	   * Change the button images when inflate the layout.
	   */
	  @Override
	  protected void onFinishInflate() {
	    super.onFinishInflate();
	    changeButtonsImages();
	  }

	  /**
	   * Set background for buttons.
	   */
	  private void changeButtonsImages(){
	    int count = getChildCount();

	    if (count > 1) {
	      // Set background for the first button.
	      if (backgroundFirstButton != null) {
	        getChildAt(0).setBackgroundDrawable(backgroundFirstButton);
	      }
	      // Set background for the middle buttons.
	      if (backgroundMiddleButton != null) {
	        for (int i = count - 2; i > 0; i--) {
	          getChildAt(i).setBackgroundDrawable(backgroundMiddleButton);
	        }
	      }
	      // Set background for the last button.
	      if (backgroundLastButton != null) {
	        getChildAt(count-1).setBackgroundDrawable(backgroundLastButton);
	      }
	    } else if (count == 1) {
	      // Set background when there is only one button.
	      if (backgroundUniqueButton != null) {
	        getChildAt(0).setBackgroundDrawable(backgroundUniqueButton);
	      }
	    }
	  }

	  /**
	   * Setter for backgroundFirstButton.
	   * @param backgroundFirstButton
	   */
	  public void setBackgroundFirstButton(Drawable backgroundFirstButton) {
	    this.backgroundFirstButton = backgroundFirstButton;
	  }

	  /**
	   * Setter for backgroundLastButton.
	   * @param backgroundLastButton
	   */
	  public void setBackgroundLastButton(Drawable backgroundLastButton) {
	    this.backgroundLastButton = backgroundLastButton;
	  }

	  /**
	   * Setter for backgroundMiddleButton.
	   * @param backgroundMiddleButton
	   */
	  public void setBackgroundMiddleButton(Drawable backgroundMiddleButton) {
	    this.backgroundMiddleButton = backgroundMiddleButton;
	  }

	  /**
	   * Setter for backgroundUniqueButton.
	   * @param backgroundUniqueButton
	   */
	  public void setBackgroundUniqueButton(Drawable backgroundUniqueButton) {
	    this.backgroundUniqueButton = backgroundUniqueButton;
	  }
	}