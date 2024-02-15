package com.examaple.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn1;
    private FrameLayout widgetContainer;
    private AppWidgetHost appWidgetHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.myButton);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        widgetContainer = findViewById(R.id.widgetContainer);
        initializeWidgetHost();
    }

    private void initializeWidgetHost() {
        appWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
        appWidgetHost.startListening();
        addWidget();
    }

    private void addWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        AppWidgetHost appWidgetHost = new AppWidgetHost(this, 1);

        int appWidgetId = appWidgetHost.allocateAppWidgetId();
        String appWidgetProvider = "com.examaple.myapplication.widget.WidgetProvider";
        ComponentName componentName = new ComponentName(this, appWidgetProvider);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        AppWidgetProviderInfo appWidgetInfo = null;

        for (int id : appWidgetIds) {
            if (appWidgetId == id) {
                appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
                break;
            }
        }

        if (appWidgetInfo != null) {
            AppWidgetHostView hostView = appWidgetHost.createView(this, appWidgetId, appWidgetInfo);
            hostView.setAppWidget(appWidgetId, appWidgetInfo);

            // Assuming widgetContainer is a ViewGroup in your layout
            widgetContainer.addView(hostView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appWidgetHost != null) {
            appWidgetHost.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (appWidgetHost != null) {
            appWidgetHost.startListening();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (appWidgetHost != null) {
            appWidgetHost.startListening();
        }
    }
}
