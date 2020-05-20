package com.example.android.finalhikaway;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BluetoothPageAdapter extends RecyclerView.Adapter<BluetoothPageAdapter.ViewHolder>{

    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> addressList = new ArrayList<>();
    private Context context;
    private ConnectCallback connectCallback;

    public BluetoothPageAdapter(ArrayList<String> nameList, ArrayList<String> addressList, Context context) {
        this.nameList = nameList;
        this.addressList = addressList;
        this.context = context;
        connectCallback = ((ConnectCallback)context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_list_layout,parent,false);
        BluetoothPageAdapter.ViewHolder viewHolder = new BluetoothPageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(nameList.get(position));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectCallback.notifyBluetoothConnect(addressList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ConstraintLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.textViewBluetoothListName);
            layout = (ConstraintLayout)itemView.findViewById(R.id.constraintLayoutBluetoothList);


        }
    }

    public static interface ConnectCallback{
        void notifyBluetoothConnect(String address);
    }

}
