package com.example.sm_pc.myapplication.BabyBot;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sm_pc.myapplication.DodamBot.BotActivity;
import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.model.ChatModel;
import com.example.sm_pc.myapplication.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int SELF = 100;
    //private ArrayList<Message> messageArrayList;    //position
    private List<ChatModel.Comment> comments = new ArrayList<>();

    private String uid;
    private ImageButton btnSend;
    private String chatRoomUid;
    private String destinationUid;
    private UserModel destinationUserModel;
    private String babyUid;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private RecyclerView recyclerView;
    public EditText msg;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;

        //밑에서 ViewHolder을 만들어 줘서 생긴 것, alt+enter를 해줘서 생성됨...?
        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);   //message라는 TextView(사용자가 입렵함)의 내용을 message에 담자
            btnSend = (ImageButton) itemView.findViewById(R.id.btn_send);
        }
        public String getMessage(String m){
            return m;
        }
    }

    public ChatAdapter(List<ChatModel.Comment> messageArrayList) {
        this.comments = messageArrayList;
        //List<ChatModel.Comment> comments = new ArrayList<>();
        //comments = messageArrayList<>();
        chatRoomUid = BabyActivity.getRoomUid();
        destinationUid = BabyActivity.getDestinationUid();
        babyUid = "Baby";

        FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                destinationUserModel = dataSnapshot.getValue(UserModel.class);
                getMessageList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        getMessageList();   //오류가 뜨는 이유: LoginActivity의 chatRoomUid에 내용이 저장되지 않은 상태에서 getMessageList호출
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        //수정한 부분
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else if(viewType == 2){
            // WatBot message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson, parent, false);
        }

        //수정한 부분
        else {
            // Lover's message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_lover, parent, false);

        }

        return new ViewHolder(itemView);
        //XML디자인 한 부분 적용
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel.Comment message = comments.get(position);


        if (message.uid.equals(uid)) {  //message.getId().equals("1") ||
            return SELF;
        }

        else if(message.uid.equals(babyUid))
            return 2;

        else
            return 3;
        //return position;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {    //message를 화면에 보여주는 부분


        ChatModel.Comment message = comments.get(position);
        message.setMessage(message.getMessage());
        ((ViewHolder) holder).message.setText(message.getMessage());

    }

    void getMessageList() {

        if (chatRoomUid != null) {
            FirebaseDatabase.getInstance().getReference().child("babyTalk").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {

                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {

                    comments.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {

                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    //메세지가 갱신
                    notifyDataSetChanged();
                    //recyclerView.scrollToPosition(comments.size() - 1);
                }


                @Override

                public void onCancelled(DatabaseError databaseError) {


                }

            });

        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
        //아이템을 측정하는 카운터,,메시지 배열의 사이즈를 받아서 그만큼 반복
    }


}