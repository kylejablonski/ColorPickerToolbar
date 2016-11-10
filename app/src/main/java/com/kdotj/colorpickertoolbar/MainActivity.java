package com.kdotj.colorpickertoolbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;

import com.kdotj.color_picker_toolbar.ColorPickerToolbar;

public class MainActivity extends AppCompatActivity {

    ColorPickerToolbar colorPickerToolbar;
    AppCompatTextView mTvColor;
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
    }

}
