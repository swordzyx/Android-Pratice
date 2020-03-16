package com.zero.practiceproject.activity;import android.app.Activity;import android.content.Intent;import android.os.Bundle;import android.os.PersistableBundle;import android.util.Log;import android.view.View;import com.zero.practiceproject.R;public class FirstActivity extends Activity {    private static final String TAG = "FirstActivity";    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        Log.d(TAG, "onCreate()");        setContentView(R.layout.activity_first);    }    @Override    protected void onStart(){        Log.d(TAG, "onStart()");        super.onStart();    }    @Override    protected void onResume() {        Log.d(TAG, "onResume()");        super.onResume();    }    @Override    protected void onPause() {        Log.d(TAG, "onPause()");        super.onPause();    }    @Override    protected void onStop() {        Log.d(TAG, "onStop()");        super.onStop();    }    @Override    protected void onRestart() {        Log.d(TAG, "onRestart()");        super.onRestart();    }    @Override    protected void onDestroy() {        Log.d(TAG, "onDestroy()");        super.onDestroy();    }    @Override    protected void onRestoreInstanceState(Bundle savedInstanceState) {        Log.d(TAG, "onRestoreInstanceState()");        super.onRestoreInstanceState(savedInstanceState);    }    @Override    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {        Log.d(TAG, "onSaveInstanceState()");        super.onSaveInstanceState(outState, outPersistentState);    }    public void onClick(View view){        Intent intent;        switch (view.getId()){            case R.id.first_activity:                intent = new Intent(FirstActivity.this, FirstActivity.class);                startActivity(intent);                break;            case R.id.main_activity:                intent = new Intent(FirstActivity.this, MainActivity.class);                startActivity(intent);                break;            default:                break;        }    }}