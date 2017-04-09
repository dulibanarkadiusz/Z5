package com.example.arek.zadanie5;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by Arek on 2017-04-05.
 */

public class UpDownView extends LinearLayout {
    ImageButton mUpButton, mDownButton;
    float maxValue, minValue, defaultValue, stepValue;
    ButtonEvents buttonEventsMinus = new ButtonEvents(0.25f);
    EditText mEditText;
    int mButtonOrientation;
    LinearLayout mLinearLayout;

    public UpDownView(Context context){
        super(context);
        init(context,null,0);
    }

    public UpDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public UpDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.up_down_view, this, true);
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.UpDownView, defStyle, 0);
        try {
            mLinearLayout = (LinearLayout) getChildAt(0);

            mUpButton = (ImageButton) mLinearLayout.getChildAt(0);
            mEditText = (EditText) mLinearLayout.getChildAt(1);
            mDownButton = (ImageButton) mLinearLayout.getChildAt(2);
            mButtonOrientation = a.getInteger(
                    R.styleable.UpDownView_btnOrientation, 0);
            minValue = a.getFloat(R.styleable.UpDownView_min, 0);
            maxValue = a.getFloat(R.styleable.UpDownView_max, 0);
            defaultValue = a.getFloat(R.styleable.UpDownView_value, 0);
            stepValue = a.getFloat(R.styleable.UpDownView_step, 1);

            mEditText.setText(minValue+"");

            if (a.hasValue(R.styleable.UpDownView_btnDown)) {
                mDownButton.setImageDrawable(
                        a.getDrawable(R.styleable.UpDownView_btnDown));
            }

            mUpButton.setOnClickListener(buttonEventsMinus);
            mUpButton.setOnLongClickListener(buttonEventsMinus);
            mUpButton.setOnTouchListener(buttonEventsMinus);
            mDownButton.setOnLongClickListener(buttonEventsMinus);
            mDownButton.setOnClickListener(buttonEventsMinus);
            mDownButton.setOnTouchListener(buttonEventsMinus);

        }
        finally {
            a.recycle();
        }

    }

    private class ButtonEvents implements OnClickListener, OnLongClickListener, OnTouchListener {
        private float mCurrentStep, mValue, mButtonStep;
        private boolean mPressed = false;
        private Handler mAutoUpdateHandler = new Handler();

        public ButtonEvents(float step) {
            mButtonStep = step;
        }

        @Override
        public void onClick(View v) {
            mCurrentStep = mButtonStep;
            if (v.getX()>mUpButton.getX()) mCurrentStep *= -1;

            try {
                mValue = Float.parseFloat(mEditText.getText().toString());
            } catch (NumberFormatException nfe) {
            }
            update();
        }

        @Override
        public boolean onLongClick(View v) {
            mPressed = true;
            mCurrentStep = mButtonStep;
            if (v.getX()>mUpButton.getX()) mCurrentStep *= -1;

            try {
                mValue = Float.parseFloat(mEditText.getText().toString());
            }
            catch (NumberFormatException nfe) { }
            mAutoUpdateHandler.post( new AutoUpdater() );
            return false;
        }

        private void update(){
            mValue+=(mCurrentStep);
            if (mValue > maxValue || mValue < minValue)
                mValue-=(mCurrentStep);

            mEditText.setText(String.valueOf(mValue));
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if( (motionEvent.getAction()==MotionEvent.ACTION_UP || motionEvent.getAction()==MotionEvent.ACTION_CANCEL)) {
                mPressed = false;
            }
            return false;

        }


        private class AutoUpdater implements Runnable {

            public void run()
            {
                if (mPressed)
                {
                    update();
                    mAutoUpdateHandler.postDelayed(new AutoUpdater(), 100);
                }
            }
        }
    }




}
