package com.kdotj.colorpickertoolbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;

import com.kdotj.color_picker_toolbar.ColorPickerToolbar;

public class MainActivity extends AppCompatActivity {

    ColorPickerToolbar colorPickerToolbar;
    AppCompatTextView mTvColor;
    AppCompatSpinner mSpIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvColor = (AppCompatTextView) findViewById(R.id.tv_color);
        colorPickerToolbar = (ColorPickerToolbar) findViewById(R.id.color_picker_toolbar);
        colorPickerToolbar.addColorChangeListener(new ColorPickerToolbar.ColorChangeListener() {
            @Override
            public void onColorChanged(int color) {
                mTvColor.setTextColor(color);
            }
        });

        mSpIndicator = (AppCompatSpinner) findViewById(R.id.sp_indicator);
        mSpIndicator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ColorPickerToolbar.IndicatorType indicatorType = ColorPickerToolbar.IndicatorType.NONE;
                switch(position){
                    case 0:
                        indicatorType = ColorPickerToolbar.IndicatorType.NONE;
                        break;
                    case 1:
                        indicatorType = ColorPickerToolbar.IndicatorType.CIRCLE;
                        break;
                    case 2:

                        indicatorType = ColorPickerToolbar.IndicatorType.BAR;
                        break;
                }
                colorPickerToolbar.setIndicatorType(indicatorType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
