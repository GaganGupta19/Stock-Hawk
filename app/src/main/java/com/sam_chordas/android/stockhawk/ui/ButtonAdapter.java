package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sam_chordas.android.stockhawk.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Gagan on 8/2/2016.
 */
public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonList> {
    private Activity mAct;
    private ArrayList<String> buttonNames;
    ButtonAdapter(Activity mAct, ArrayList<String> buttonnames){
        this.buttonNames = buttonnames;
        this.mAct = mAct;
    }
    @Override
    public ButtonAdapter.ButtonList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_button_list,parent,false);
        ButtonList buttonList = new ButtonList(v);
        return buttonList;
    }

    @Override
    public void onBindViewHolder(ButtonAdapter.ButtonList holder, int position) {
        holder.getButton().setText(buttonNames.get(position));
        holder.getButton().setBackgroundColor(ContextCompat.getColor(mAct,R.color.material_blue_500));
        holder.getButton().setBorderColor(ContextCompat.getColor(mAct,R.color.white));
    }

    @Override
    public int getItemCount() {
        return buttonNames.size();
    }

    public static class ButtonList extends RecyclerView.ViewHolder{

        @BindView(R.id.button) FancyButton button;
        public ButtonList(View itemView) {
            super(itemView);
            ButterKnife.bind(this ,itemView );
            button.setRadius(30);
        }
        public FancyButton getButton() {
            return button;
        }


    }
}
