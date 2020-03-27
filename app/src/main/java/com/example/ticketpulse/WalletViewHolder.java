package com.example.ticketpulse;

import android.content.Context;
import android.view.View;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;



public class WalletViewHolder extends RecyclerView.ViewHolder {

    View wView;

    public WalletViewHolder(View itemView) {
        super(itemView);

        wView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
        //item long click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });

    }

    //set details to recycler view row
    public void setDetails(Context ctx, String title,String ticketName, String email,  String location, String ticketcode, String description, String date) {
        //Views
        TextView wTitleTv = wView.findViewById(R.id.walletTitleTv);
        TextView wEmailTv = wView.findViewById(R.id.walletEmailTv);
        TextView wTicketTv = wView.findViewById(R.id.walletTicketNameTv);
        TextView wLocationTv = wView.findViewById(R.id.walletLocationTv);
        TextView wTicketNumberTv = wView.findViewById(R.id.walletTicketNumberTv);
        TextView wDescriptionTv = wView.findViewById(R.id.walletDescriptionTv);
        TextView wDateTv = wView.findViewById(R.id.walletDateTv);





        //set data to views

        wTitleTv.setText(title);
        wEmailTv.setText(email);
        wLocationTv.setText(location);
        wTicketTv.setText(ticketName);
        wTicketNumberTv. setText(ticketcode);
        wDescriptionTv.setText(description);
        wDateTv.setText(date);
    }

    private WalletViewHolder.ClickListener mClickListener;

    //interface to send callbacks
    public interface ClickListener {
        void onItemClick(View view , int position);

        void onItemLongClick(View view , int position);
    }

    public void setOnClickListener(WalletViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
