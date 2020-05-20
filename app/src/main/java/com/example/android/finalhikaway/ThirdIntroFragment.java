package com.example.android.finalhikaway;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ThirdIntroFragment extends Fragment {

    private View view;
    private TextView skip,next;
    private ImageView back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third_intro, container, false);
        this.view = view;

        initializeViews();
        activateListeners();

        return view;
    }

    private void initializeViews() {

        skip = (TextView)view.findViewById(R.id.textViewThirdIntroSkip);
        next = (TextView)view.findViewById(R.id.textViewThirdIntroNext);
        back = (ImageView)view.findViewById(R.id.imageViewThirdIntroBack);

    }


    private void activateListeners() {

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity().getApplicationContext(),BluetoothActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainFragment,new FourthIntroFragment());
                fragmentTransaction.commit();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainFragment,new SecondIntroFragment());
                fragmentTransaction.commit();


            }
        });

    }


}
