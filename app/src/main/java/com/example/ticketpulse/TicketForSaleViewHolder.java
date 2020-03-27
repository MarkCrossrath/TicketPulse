package com.example.ticketpulse;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TicketForSaleViewHolder extends RecyclerView.ViewHolder {


    View tView;

    public TicketForSaleViewHolder(View itemView) {
        super(itemView);

        tView = itemView;

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
    public void setDetails(Context ctx, String title, String ticketName, String email, String location, String ticketcode, String description, String date) {
        //Views
        TextView wTitleTv = tView.findViewById(R.id.tfsTitleTv);
        TextView wEmailTv = tView.findViewById(R.id.tfsEmailTv);
        TextView wTicketTv = tView.findViewById(R.id.tfsTicketNameTv);
        TextView wLocationTv = tView.findViewById(R.id.tfsLocationTv);
        TextView wTicketNumberTv = tView.findViewById(R.id.tfsTicketNumberTv);
        TextView wDescriptionTv = tView.findViewById(R.id.tfsDescriptionTv);
        TextView wDateTv = tView.findViewById(R.id.tfsDateTv);






        //set data to views

        wTitleTv.setText(title);
        wEmailTv.setText(email);
        wLocationTv.setText(location);
        wTicketTv.setText(ticketName);
        wTicketNumberTv. setText(ticketcode);
        wDescriptionTv.setText(description);
        wDateTv.setText(date);
    }

    private TicketForSaleViewHolder.ClickListener mClickListener;

    //interface to send callbacks
    public interface ClickListener {
        void onItemClick(View view , int position);

        void onItemLongClick(View view , int position);
    }

    public void setOnClickListener(TicketForSaleViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}






