package com.example.sm_pc.myapplication.DodamBot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.model.ChatModel;
import com.example.sm_pc.myapplication.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int SELF = 100;
    //private ArrayList<Message1> messageArrayList;
    private List<ChatModel.Comment> comments = new ArrayList<>();

    private String uid;
    private ImageButton btnSend;
    private String chatRoomUid;
    private String destinatonUid;
    private UserModel destinationUserModel;
    private String dodamUid;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private RecyclerView recyclerView;
    public EditText msg;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            btnSend = (ImageButton) itemView.findViewById(R.id.btn_send);
        }
        public String getMessage(String m){
            return m;
        }

    }

    public ChatAdapter1(List<ChatModel.Comment> messageArrayList) {
        this.comments = messageArrayList;
        //List<ChatModel.Comment> comments = new ArrayList<>();
        //comments = messageArrayList<>();
        chatRoomUid = BotActivity.getRoomUid();
//예진이 외 수정한거
        destinatonUid = BotActivity.getDestinationUid();//여기까지
        dodamUid = "dodam";

        FirebaseDatabase.getInstance().getReference().child("users").child(destinatonUid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    .inflate(R.layout.chat_item_self1, parent, false);
        } else if(viewType == 2){
            // WatBot message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson1, parent, false);
        }
        //수정한 부분
        else {
            // Lover's message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_lover1, parent, false);

        }
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel.Comment message = comments.get(position);
        if (message.uid.equals(uid)) {  //message.getId().equals("1") ||
            return SELF;
        }
        else if(message.uid.equals(dodamUid))
            return 2;
        else
            return 3;
        //return position;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatModel.Comment message = comments.get(position);
        message.setMessage(message.getMessage());
        ((ViewHolder) holder).message.setText(message.getMessage());

    }

    void getMessageList() {

        if (chatRoomUid != null) {
            FirebaseDatabase.getInstance().getReference().child("dodamTalk").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {

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
    }
}
