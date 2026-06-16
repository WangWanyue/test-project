package com.example.drawingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawingView drawingView = findViewById(R.id.drawing_view_id);
        Button clearButton = findViewById(R.id.btn_clear);
        clearButton.setOnClickListener(v -> drawingView.clearCanvas());
    }
}
