package com.example.memorylosscompanion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public  static final int MSG_TYPE_LEFT = 0;
    public  static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;

    FirebaseUser fUser;

    public MessageAdapter(Context mContext, List<Chat> mChat){
        this.mContext = mContext;
        this.mChat = mChat;

    }

    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
       if (viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(mContext).inflate(R.layout.right_chat, parent, false);
           return new MessageAdapter.ViewHolder(view);
       }else{
           View view = LayoutInflater.from(mContext).inflate(R.layout.left_chat, parent, false);
           return new MessageAdapter.ViewHolder(view);
       }

    }
    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView view_message;
        public  TextView view_username;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view_message = itemView.findViewById(R.id.message_chat);
            view_username = itemView.findViewById(R.id.username_chat);



        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder ViewHolder, int position) {
        Chat chat = mChat.get(position);
        ViewHolder.view_message.setText(chat.getMessage());
        ViewHolder.view_username.setText(chat.getuName());

    }





    public int getItemViewType(int pos){

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(pos).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

}
