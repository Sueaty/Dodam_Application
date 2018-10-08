package com.example.sm_pc.myapplication.DodamBot;

import android.support.annotation.NonNull;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.model.ChatModel;
import com.example.sm_pc.myapplication.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BotActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter1 mAdapter;
    //수정
    private List<ChatModel.Comment> comments = new ArrayList<>();

    //private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private Map<String,Object> context = new HashMap<>();

    //수정한 부분
    private Map<String,UserModel> users = new HashMap<>();

    private String uid;

//예진이 외
    //private String destinatonUid;
    public static String destinationUid;//여기까지,, 이거 하울코딩 오타! destination인뎅!!
    private String dodamUid;

    public static String chatRoomUid;
    //public static String RoomUid = "";

    //private String babyGrowthMessage;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//채팅을 거는 아이디
        dodamUid = "dodam";
//예진말고 수정한 부분
        //destinationUid가 없는 경우는 어떡하지??
        destinationUid = getIntent().getStringExtra("destinationUid");
        //destinatonUid = "Jcm43qnZ0gfiha4NJjBx9401STE3";//상대방이 whgusdk98
        //destinatonUid = "JRPBQZMjewhWp66q5lWq8MxCQlF2";//상대방이 whgusdk
        destinationUid = "8HIp1rEZLOagLQXNynoE8PDLzBq2";//상대방이 dad
        //여기까지

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //messageArrayList = new ArrayList<>();
        comments = new ArrayList<>();
        mAdapter = new ChatAdapter1(comments);
        //mAdapter = new ChatAdapter1(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()) {
                    //ChatModel.Comment comment1 = new ChatModel.Comment();
                    ChatModel chatModel = new ChatModel();
//예진이 밑에 있는 게 왜 필요하지??
                    chatModel.users.put(uid,true);  //true값을 가진 uid
//예진이가 한거 외
                    chatModel.users.put(destinationUid,true);
                    checkChatRoom();
                    //여기까지
                    if(chatRoomUid == null){

                        btnSend.setEnabled(false);
                        //FirebaseDatabase.getInstance().getReference().child("chatrooms1").push().setValue(chatBaby);

                        FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                checkChatRoom();
                            }
                        });

                    }
                    else {

                        ChatModel.Comment comment = new ChatModel.Comment();
                        comment.uid = uid;
//예진 외 수정함,,이코드를 sendMessage로 보냄
                        //comment.message = inputMessage.getText().toString();
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override

                            public void onComplete(@NonNull Task<Void> task) {
                                inputMessage.setText("");
                            }
                        });

                    }
                    sendMessage();
                }
            }

        }); //setOnClickListener
        checkChatRoom();

    }



    void  checkChatRoom(){

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid)) {
                        chatRoomUid = item.getKey();
                        btnSend.setEnabled(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static String getRoomUid() {
        return chatRoomUid;
    }
//예진이 수정외
    public static String getDestinationUid() {
        return destinationUid;
    }


    // Sending a message to Watson Conversation Service
    private void sendMessage() {
//예진 외 수정한거
        ChatModel.Comment comment = new ChatModel.Comment();
        comment.uid = uid;
        comment.message = this.inputMessage.getText().toString().replace("[","").replace("]","");
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment);//여기까지
        final String inputmessage = this.inputMessage.getText().toString().trim();
        //Message1 inputMessage = new Message1();

        //수정한 부분
        ChatModel.Comment inputComment = new ChatModel.Comment();
        inputComment.message = inputmessage;
        inputComment.uid = uid;

        comments.add(inputComment);
        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        //수정한 부분3
        ChatModel chatModel = new ChatModel();
        chatModel.users.put(uid, true);


        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {

                    ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_09_20);
                    MessageResponse response;

                    //수정한 부분6
                    ChatModel chatModel = new ChatModel();
                    chatModel.users.put(dodamUid, true);
//예진 외 수정,,,이걸 밖에(sendMessage 바로 아래)에도 써줌...or 아예 이 밑의 코드를 밖으로 빼는 방법은...??
                    ChatModel.Comment comment = new ChatModel.Comment();    //매번 새로운 comment를 만들어서 거기에 message저장
                    comment.uid = dodamUid;  //이 메시지의 uid는 babyUid로 지정

                    service.setUsernameAndPassword("bdcc80b8-8377-4204-82a8-e216db9823a9", "ouF38I4DN7cy");
                    MessageRequest newMessage = new MessageRequest.Builder().inputText(inputmessage).context(context).build();
                    response = service.message("7e44c660-c381-4b1d-b041-e0ae20f0b666", newMessage).execute();
                    if(response.getContext() !=null)
                    {
                        context.clear();
                        context = response.getContext();

                        //수정한 부분4
                        comment.message = response.getText().toString().replace("[","").replace("]","");
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment);

                    }
                    //Message1 outMessage=new Message1();
                    ChatModel.Comment outComment = new ChatModel.Comment();
                    if(response!=null)
                    {
                        if(response.getOutput()!=null && response.getOutput().containsKey("text"))
                        {

                            final String outputmessage = response.getOutput().get("text").toString().replace("[","").replace("]","");
                            outComment.message = outputmessage;
                            //outMessage.setId("2");
                            outComment.uid = dodamUid;
                            comments.add(outComment);

                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);

                                }

                            }
                        });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected){
            return true;
        }
        else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }


}

