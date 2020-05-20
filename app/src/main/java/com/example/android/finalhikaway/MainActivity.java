package com.example.android.finalhikaway;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
    }

    private void initializeViews() {

        fragmentMain = (FrameLayout)findViewById(R.id.mainFragment);

        //add first fragment to FrameLayout
        FirstIntroFragment fragment1 = new FirstIntroFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        //instantiate fragments on start
        FragmentTransaction addTransaction = fragmentManager.beginTransaction();
        addTransaction.add(R.id.mainFragment,new FourthIntroFragment(),"fourth_intro");
        addTransaction.add(R.id.mainFragment,new ThirdIntroFragment(),"third_intro");
        addTransaction.add(R.id.mainFragment,new SecondIntroFragment(),"second_intro");
        addTransaction.add(R.id.mainFragment,new FirstIntroFragment(),"first_intro");
        addTransaction.commit();

    }
}
