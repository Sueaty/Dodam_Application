package com.example.sm_pc.myapplication.BabyBot;

import android.support.annotation.NonNull;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.model.ChatModel;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.google.firebase.database.DatabaseReference;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.example.sm_pc.myapplication.model.ChatModel;

import com.example.sm_pc.myapplication.model.UserModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class BabyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    //수정
    private List<ChatModel.Comment> comments = new ArrayList<>();
    public ChatModel.Comment temp = new ChatModel.Comment();

    //private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private Map<String,Object> context = new HashMap<>();
    int dDay = 1;
    int NUM = 0;

    //수정한 부분
    private Map<String,UserModel> users = new HashMap<>();

    private String uid;
    public static String destinationUid;
    private String babyUid;
    String inputmessage;

    public static String chatRoomUid;
    //public static String RoomUid = "";

    //private String babyGrowthMessage;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby);
        //수정한 부분2
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//채팅을 거는 아이디
        babyUid = "Baby";
        destinationUid = getIntent().getStringExtra("destinationUid");
        destinationUid = "P98wBhdBf6dLyXQwUndiNNBVawF2";

        inputMessage = (EditText) findViewById(R.id.message);
        //EditText 에 사용자가 입력한 메시지를 받아와서 inputMessage 에 저장!!!!!!
        btnSend = (ImageButton) findViewById(R.id.btn_send);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //messageArrayList = new ArrayList<>();
        //수정
        comments = new ArrayList<>();
        mAdapter = new ChatAdapter(comments);
        //mAdapter = new ChatAdapter(messageArrayList);

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
                    ChatModel chatBaby = new ChatModel();

                    chatModel.users.put(uid,true);  //true값을 가진 uid
                    chatBaby.users.put(babyUid,true);

                    chatModel.users.put(destinationUid,true);

                    //chatModel.users.put(destinatonUid,true);


                    if(chatRoomUid == null){

                        btnSend.setEnabled(false);
                        //FirebaseDatabase.getInstance().getReference().child("chatrooms1").push().setValue(chatBaby);

                        FirebaseDatabase.getInstance().getReference().child("babyTalk").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                checkChatRoom();
                                temp.message = inputmessage;
                                temp.uid = uid;
                                NUM ++;
                            }
                        });


                    }else {

                        ChatModel.Comment comment = new ChatModel.Comment();
                        comment.uid = uid;
                        comment.message = inputMessage.getText().toString();
                        //FirebaseDatabase.getInstance().getReference().child("chatrooms1").child(chatRoomUid).child("comments").push().setValue(comment);
                        FirebaseDatabase.getInstance().getReference().child("babyTalk").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

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

        FirebaseDatabase.getInstance().getReference().child("babyTalk").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {

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
    public static String getDestinationUid() {
        return destinationUid;
    }

    public int num() {
        int r;
        Random ran = new Random();
        r = ran.nextInt(3);

        return r;
    }


    // Sending a message to Watson Conversation Service
    //왓슨에게 사용자가 입력한 내용 보내주는 부분
    private void sendMessage() {
        inputmessage = this.inputMessage.getText().toString().trim();
        //Message inputMessage = new Message();

        //수정한 부분
        ChatModel.Comment inputComment = new ChatModel.Comment();
        inputComment.message = inputmessage;
        inputComment.uid = uid;

        //inputMessage.setMessage(inputmessage);
        //inputMessage.setMessage("여기다가 쓰는데로 왓슨한데 보내게 됨!!!!!");
        //수정한 부분

        comments.add(inputComment);   //방금 수정
        //messageArrayList.add(inputMessage);
        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();
        final String[][] BabyGrowthList = new String[40][3];
        BabyGrowthList[0][0] = "저는 아직 수정란 상태에요! 지름 0.2cm 정도로 아주 작죠ㅎㅎ";
        BabyGrowthList[0][1] = "난자와 정자가 나팔관 내에서 만나 수정을 하면서 생명이 시작되었어요!!";
        BabyGrowthList[0][2] = "수정이 이루어진 후 12~15시간이 지나면 수정란이 세포분열을 하기 시작한대요!ㅎㅎ";

        BabyGrowthList[1][0] = "아직도 저는 태아가 아니라 배아에요! 하나의 완전한 개체인 태아가 되기 전이고, 임신 8주 이전까지는 배아래요!ㅎㅎ ";
        BabyGrowthList[1][1] = "2주차에는 수정란이 세포분열을 하며 나팔관을 따라 자궁으로 이동해요~";
        BabyGrowthList[1][2] = "수정 후 7~10일이 지나서 수정란은 자궁내막에 착상을 하게 된대요!ㅎㅎ.";

        BabyGrowthList[2][0] = "저는 약 0.2cm 크기이고 무게는 1g 미만이에요!! 아직 엄청 작죠?!ㅎㅎ";
        BabyGrowthList[2][1] = "4개의 아가미에 긴 꼬리가 달려 있는 물고기 모양을 하고있어용><";
        BabyGrowthList[2][2] = "자궁 내막에 성공적으로 착상을 완료한 수정란 2개 중 1개는 태아로 성장하게 되며, 나머지 1개는 저에게 영양을 공급하는 태반이 된대요!!ㅎㅎ";

        BabyGrowthList[3][0] = "저는 사과 씨만한 크기가 되었어요!ㅎvㅎ 탯줄이 저에게 산소와 영양분을 주고 있어요ㅎㅎ 냠냠";
        BabyGrowthList[3][1] = "이제 제가 들어있는 '태낭'이 분명히 비치기 시작할거에요!!";
        BabyGrowthList[3][2] = "많이 컸다 생각했는데 아직 너무 작은가봐요ㅜㅜ 제 심박을 화면으로 보려면 시간이 더 필요해용";

        BabyGrowthList[4][0] = "저에게 심장, 위 등의 장기가 생기기 시작했어요>_<";
        BabyGrowthList[4][1] = "이제 저의 신경계도 발달하고 있어요!!";
        BabyGrowthList[4][2] = "이제 초음파로 태아를 싸고 있는 태낭을 확인할 수 있대요ㅎㅎ";

        BabyGrowthList[5][0] = "저는 아직까지 사람보다 올챙이를 닮았어요ㅎㅎ 몸의 주요 기관이 자라기 시작했어요><";
        BabyGrowthList[5][1] = "제 심장은 지금 일반 사람들보다 느리게 뛰고있고 성장하면서 심장 박동수가 증가해용!!ㅎㅎ";
        BabyGrowthList[5][2] = "뇌와 척수의 신경세포의 80%정도가 만들어지고 있어요!!ㅎㅎㅎ";

        BabyGrowthList[6][0] = "드디어 사람의 형태를 갖추게 되었어요! 키와 체중이 각각 약 2cm, 4g 정도로 지느러미 같은 손가락, 발가락을 지닌 2등신이랍니당^0^";
        BabyGrowthList[6][1] = "귀로 자랄 부분은 작은 구멍처럼, 눈과 콧구멍은 검은 점처럼 보인답니다>_<";
        BabyGrowthList[6][2] = "저의 팔 다리로 자라날 부분은 봉우리처럼 솟아나 있답니다!!ㅎㅎ";

        BabyGrowthList[7][0] = "저에게는 아직 작은 꼬리가 있지만 곧 없어질 것 같아요!!ㅎㅎ헤헤";
        BabyGrowthList[7][1] = "눈과 귀의 시신경, 청각신경과 심장, 뇌, 뼈의 중심, 근육도 더욱 발달했어요! 그리고 아주 조금씩 움직일 수 있답니당 꼬물꼬물~";
        BabyGrowthList[7][2] = "눈꺼풀이 만들어지고 코가 오똑해지고 있어요♡";

        BabyGrowthList[8][0] = "저는 엄지손가락 정도 크기이고 헤엄을 칠 수 있어요!! 어푸어푸~ ";
        BabyGrowthList[8][1] = "내장 기관이 활동하기 시작하고 초음파 검사를 하실 때 저의 심장 박동 소리까지 들을 수 있답니당!";
        BabyGrowthList[8][2] = "저의 팔다리가 발달하여 좀 더 엄마 아빠와 유사하게 변하고 있어요>< 치아 돌기도 생기고 있답니다ㅎㅎ";

        BabyGrowthList[9][0] = "아직 저의 몸무게가 10g도 되지 않지만 저는 거의 제자리를 잡았어요ㅎㅎ";
        BabyGrowthList[9][1] = "꼬리도 없어지고 몸이 곧게 펴지고 길어졌구요!! 5개의 손가락과 발가락이 분명하게 보여요!우왕~";
        BabyGrowthList[9][2] = "신체 거의 모든 부분이 제 모습을 띠기 시작했고 턱, 뺨, 입술, 눈꺼풀이 발달하고 있답니당ㅎㅎ";

        BabyGrowthList[10][0] = "저의 6~8cm 정도로 컸어요! 팔다리 부분과 다리와 허벅지, 종아리, 발이 구분되어 보여요ㅎㅎ";
        BabyGrowthList[10][1] = "대뇌가 발달해서 기억력이 생기고 귀가 만들어지면서 소리를 알아들어요><";
        BabyGrowthList[10][2] = "간, 비장, 맹장 등의 내장이 발달하고 성기가 형성된대요!ㅎㅎ 솜털도 자랐답니당! 보들보들~";

        BabyGrowthList[11][0] = "뇌가 빠르게 발달해서 똑똑해지고 있어요>_<";
        BabyGrowthList[11][1] = "머리 부분이 커져서 몸 전체의 1/3 정도이고 얼굴과 몸에 솜털이 덮였어요!";
        BabyGrowthList[11][2] = "성기가 발달하여 외관상 여자인지 남자인지 구분할 수 있대요 시간이 좀 더 지나면 초음파로 확인도 가능하겠죠?!ㅎㅎ";

        BabyGrowthList[12][0] = "피부에 점차 살이 오르기 시작해서 투명하던 얼굴이 붉어졌어요><";
        BabyGrowthList[12][1] = "눈과 귀가 제자리를 잡았고, 이미 만들어지기 시작한 근육과 기관들이 더욱 성숙해지고 있답니당!";
        BabyGrowthList[12][2] = "탯줄 속에서 돌기처럼 부풀어 만들어진 저의 장이 복강 속으로 들어가기 시작했어요!! 신기하죠?!ㅎㅎ";

        BabyGrowthList[13][0] = "저는 3시간에 1번 오줌을 싸요ㅎㅎ헤헤 오줌은 양수와 섞이지만 양수는 계속 새롭게 분비돼서 깨끗해요!";
        BabyGrowthList[13][1] = "태반이 거의 완성되었고, 관절이 생겼어요!! 우아~~";
        BabyGrowthList[13][2] = "저는 이제 피부가 두꺼워졌는데요!!ㅎㅎ 두꺼워진 피부가 장기를 보호해준대요><";

        BabyGrowthList[14][0] = "저는 자주 손가락을 움직이고 얼굴을 찡그려요>.<";
        BabyGrowthList[14][1] = "초음파로 남아인지 여아인지 구별이 가능하대요!ㅎㅎ 그리고 머리카락, 눈썹, 솜털 등 몸의 털이 자라기 시작했어요!";
        BabyGrowthList[14][2] = "예전에 비해 미세한 부분의 움직임이 엄마한테 느껴질 수 있대요ㅎㅎ꼬물꼬물~";

        BabyGrowthList[15][0] = "저는 지금 어른 손바닥 정도의 크기에요!!";
        BabyGrowthList[15][1] = "저는 지금 활발하게 잘 움직이는 시기여서 머리를 흔들거나 손발을 움직여요~";
        BabyGrowthList[15][2] = "저는 빛에 민감해져서 숨쉬기 전 단계인 딸꾹질을 하고 있어요! 그치만 내 주변에는 공기가 아닌 액체로 가득 차 있어서 엄마 아빠는 딸꾹질 소리를 들을 수 없어요ㅜㅜ";

        BabyGrowthList[16][0] = "엄마 아빠! 나는 요즘 표정이 다양해졌어요~!! 눈동자를 움직일 수도 있고 얼굴을 찡그릴 수도 있어요ㅎㅎ";
        BabyGrowthList[16][1] = "저는 이제 단맛과 쓴맛을 구분할 수 있어요~ 양수를 뱉었다 마셨다 하며 밖으로 나가자마자 숨을 쉴 수 있도록 폐 훈련을 하는 중이에요ㅎㅎ";
        BabyGrowthList[16][2] = "저는 요새 빛의 자극에 반응하기 시작했어요!!";

        BabyGrowthList[17][0] = "저는 아직 3등신이에요ㅜㅜ 하지만 다리의 각 부위가 적절한 비율로 성장하고 있답니다~";
        BabyGrowthList[17][1] = "저는 지금 뼈와 근육이 확실하게 만들어지고 있어용~>< 다리도 팔보다 길어지는 중이랍니다! ";
        BabyGrowthList[17][2] = "촉각, 미각, 청각이 뚜렷해지는 중이에요!ㅎㅎ 눈은 이제 제자리를 찾아 정면볼 수 있어요>< 두개골은 아직 물렁한 연골이지만 곧 단단해지기 시작할 거에요!!ㅎㅎ";

        BabyGrowthList[18][0] = "엄마 아빠, 저는 이제 뇌가 발달해서 엄마와 아빠 목소리를 기억할 수 있어요!!우와>< 특히 엄마의 감정 변화에 따라 반응할 수 있어요!";
        BabyGrowthList[18][1] = "저는 한 달 전과 비교하여 2배의 성장을 했어요~!! 심장박동이 강해져서 청진기로도 제 심장 소리를 들을 수 있을 거예요ㅎㅎ";
        BabyGrowthList[18][2] = "엄마 아빠! 요즘 저는 표정을 지으며 이마를 찡그릴 수도 있고, 눈동자를 조금씩 움직이기도 한답니다~!! 또 엄마의 배에 빛을 비추면 내가 반응할거예요>< 내 망막이 기능을 하기 시작했다는 뜻이죠!!ㅎㅎ";

        BabyGrowthList[19][0] = "저는 이제 키가 약 20~25cm, 몸무게는 약 300g 정도가 되었어요! 그리구 모든 감각기관이 활발하게 발달하고 있죠.ㅎㅎ";
        BabyGrowthList[19][1] = "요즘 후각, 미각, 청각, 시각, 촉각 등 모든 감각기관이 활발하게 발달하고 있는 중이랍니다ㅎㅎ 그래서 엄마의 배 위로 차가운 것이 닿으면 싫은 반응을 나타낼거고, 확실하게 듣지는 못하지만 소리에 대해 반응을 보이기도 할거예용~";
        BabyGrowthList[19][2] = "엄마, 아빠! 내 뇌에 주름이 생기기 시작했어요!!!>< 똑똑해지고 있다는거겠죠?!ㅎㅎ";

        BabyGrowthList[20][0] = "저는 요즘 엄마 아빠보다 많은 미각 봉우리를 갖고 있어요!ㅎㅎ 그래서 맛을 느낄 수 있답니당~ 냠냠";
        BabyGrowthList[20][1] = "엄마 아빠 나는 요즘 딸꾹질을 해요!! 잘못 삼켰을 때 자극을 받은 횡경막으로 인해 딸꾹질을 하는거랍니다~ 그리구 양수 속 단맛을 느끼면 빨리 마시고 쓴맛을 느끼면 마시지 않기도 해용 헤헤";
        BabyGrowthList[20][2] = "저는 계속해서 몸무게가 늘어나고 있어요. 그리고 하얗고 미끈거리는 태지에 싸여 있답니당!! 저는 오랫동안 양수 속에 있어야 하는데 태지는 저의 피부를 보호해준대요~";

        BabyGrowthList[21][0] = "저는 피부 밑 지방이 부족해서 아직 피부가 쭈글쭈글해요ㅜㅜ 힝..";
        BabyGrowthList[21][1] = "엄마 아빠~ 저 요즘 눈썹하고 눈꺼풀도 완성되어가고 손톱도 자라고 있어요!.";
        BabyGrowthList[21][2] = "저는 이제 손발을 자유로이 움직일 수 있고, 몸의 방향도 바꿀 수 있어용! 히히>_< 그래서 가끔 거꾸로 있기도 한답니다~";

        BabyGrowthList[22][0] = "청력이 발달해서 이제 자궁 밖에서 저는 모든 소리를 듣는 것이 가능해요!! 키는 약 28~30cm, 체중은 650g 정도 된답니다~";
        BabyGrowthList[22][1] = "이제 엑스레이 찍으면 저의 두개골, 척추, 갈비뼈 등을 볼 수 있을거예요!! 또 근육이 발달하면서 움직임이 좀더 강해질거에요ㅎㅎ 엄마 너무 놀라지 말아요~";
        BabyGrowthList[22][2] = "엄마 아빠, 저는 이제 입술 모양도 뚜렷해졌고^3^, 눈도 발달했어요☆v☆ 그치만 색소는 띠지 않아용!";

        BabyGrowthList[23][0] = "엄마 아빠! 제 폐 속 혈관은 나중에 폐로 숨쉴 수 있도록 준비운동을 하고 있어요~ 그리고 요즘 뇌에 주름이 잡혔답니다! 뇌가 활동을 시작했어요!!";
        BabyGrowthList[23][1] = "제 피부는 원래 투명했는데 요즘엔 불그스름한 빛을 띠면서 불투명해지고있어요!!";
        BabyGrowthList[23][2] = "부끄럽지만 요즘 내 성기가 많이 발달했어요..ㅎㅎ//";

        BabyGrowthList[24][0] = "아직 피부가 쭈글쭈글하긴한데, 살이 붙으면서 아기의 모습을 갖추어가고 있어용~";
        BabyGrowthList[24][1] = "엄마 아빠 전 아직 몸은 가늘지만 팔 다리의 근육이 발달했어요! 손가락을 빨기도 한답니당~ 쪽쪽><";
        BabyGrowthList[24][2] = "저는 원래 눈꺼풀 위아래가 붙어 있었는데 위아래로 갈라지기 시작했어요!";

        BabyGrowthList[25][0] = "저는 이제 숨쉬기 운동을 시작했어요! 그치만 아직 폐에 공기가 남아 있지는 않아용ㅎㅎ";
        BabyGrowthList[25][1] = "저는 요즘 청력이 더 발달해서 엄마 아빠의 목소리를 분명하게 들을 수 있어요! 그리구 머리카락도 많아져서 너무 기쁘답니당!>_<";
        BabyGrowthList[25][2] = "이제 저의 시신경 기능도 활동하기 시작했어요~ 엄마 배위로 빛을 비추면 제 머리가 빛 쪽으로 돌아가며 반응을 한답니당! ";

        BabyGrowthList[26][0] = "엄마 아빠 저 이제 눈을 떴어요!!우와~";
        BabyGrowthList[26][1] = "제 피부를 덮고 있는 배내털은 모근 방향을 따라 비스듬하게 결을 이루고 있답니당~";
        BabyGrowthList[26][2] = "이제 내 키는 약 35cm, 몸무게는 약 1kg 정도 돼요ㅎㅎ";

        BabyGrowthList[27][0] = "저는 40cm 정도로 자랐구 1.5kg 정도랍니다!! 열심히 성장 중이에요>_<ㅎㅎㅎ";
        BabyGrowthList[27][1] = "이제 시각과 청각이 거의 완성되고, 눈을 떴다 감았다 할 수 있어요!!우앙~ 그리고 일정한 간격을 두고 잠이 들었다가 깨어나기도 한답니다!ㅎㅎ";
        BabyGrowthList[27][2] = "폐는 아직 완전하지는 않지만 많이 커졌답니다!!><";

        BabyGrowthList[28][0] = "저는 이제 눈꺼풀과 눈동자가 완성되어 초점 맞추는 연습을 시작했어용!!ㅎㅎ";
        BabyGrowthList[28][1] = "제 피부는 원래 쭈글쭈글했는데 지방이 형성되면서 포동포동해졌답니다>< 점차 아기 피부를 갖추고 있어용!!";
        BabyGrowthList[28][2] = "이제 엄마의 감정 변화도 알아차릴 수 있답니다!ㅎㅎ 저는 엄마가 행복해 하실 때가 가장 좋아요♥";

        BabyGrowthList[29][0] = "저의 뇌도 급속도로 발달하고 있어요!! 곧 똑똑이가 되겠죠?!ㅎㅎ";
        BabyGrowthList[29][1] = "뇌를 수용할 수 있도록 저의 머리모양이 길어졌어요!><";
        BabyGrowthList[29][2] = "뇌가 주름이 잡히면서 접히는 기간이래요!ㅎVㅎ";

        BabyGrowthList[30][0] = "저의 키는 40cm 정도고, 몸무게는 1.5kg 정도로 양수 안에 거의 꽉 차게 자랐어요!! 움직일 때마다 태동을 심하게 느낄거에요!! 엄마 힘들게해서 미안해요ㅠㅠ♥";
        BabyGrowthList[30][1] = "저의 폐와 소화기관이 거의 발달했나봐요!! 그리구 이제부터 몸무게가 급속도로 늘어날 거에요ㅎㅎ헤헤";
        BabyGrowthList[30][2] = "눈꺼풀이 완전하게 만들어졌어요!ㅎㅎ 우와~ 눈을 떴다 감았다 할 수 있어요!><";

        BabyGrowthList[31][0] = "제가 활발하게 움직여서 엄마가 태동이 크게 느낄 수 있어요ㅠㅠ 엄마 힘들게해서 죄송하고 감사합니다ㅠㅠ♥";
        BabyGrowthList[31][1] = "저는 고개를 양 옆으로 돌릴 수 있어요! 호잇짜>< 그리고 각각의 신체 기관들이 활발하게 발달하고 있답니다!ㅎㅎ";
        BabyGrowthList[31][2] = "밝은 빛을 비추면 저의 홍채가 수축해요! 신기하죠?!ㅎㅎ";

        BabyGrowthList[32][0] = "발톱은 아직 덜 자랐지만 손톱이 손가락 끝에 닿을 만큼 자랐답니당!! 장기도 거의 발달한 상태에요ㅎㅎㅎ";
        BabyGrowthList[32][1] = "저는 출산까지의 기간 7주 동안에 태어날 때의 몸무게 1/3~1/2를 증가 시킬 예정이에요ㅎㅎ히히";
        BabyGrowthList[32][2] = "토실토실 살이 오르고 지방도 축적한 저는 피부도 매끈하고 건강하게 잘 자라고 있어요><";

        BabyGrowthList[33][0] = "저는 아직 머리뼈가 완전히 조합되지는 않은 상태에요ㅎㅎ 세상으로 나갈 때 잘 통과하기 위해 머리뼈가 물렁물렁하답니다!><";
        BabyGrowthList[33][1] = "머리의 방향을 서서히 자궁 아래 쪽을 향하게 하고 있어요!!ㅎㅎ 엄마아빠를 보러갈 준비를 하는 거겠죠?!>_<";
        BabyGrowthList[33][2] = "감각기관이 발달해서 자극에 적극적으로 반응하는 힘이 생겼어요!!우앙~ 가끔 제가 웃거나 화내는 듯한 표정의 변화가 느껴질 수 있어요ㅎㅎ";

        BabyGrowthList[34][0] = "저는 지금 키가 45~46cm 정도이구 몸무게는 2.3~2.6kg 정도로 신생아와 거의 비슷해용!!ㅎㅎ";
        BabyGrowthList[34][1] = "폐를 제외한 내장 기능 대부분이 성숙한 상태여서 지금 세상에 나가도 99%의 아기가 살아갈 수 있대요!ㅎㅎ 하지만 전 출산 예정일에 나갈테니 걱정하지 마세용!><";
        BabyGrowthList[34][2] = "성기도 거의 완성되었고 발톱도 끝까지 다 자랐답니다>_<";

        BabyGrowthList[35][0] = "태반을 통해 엄마로부터 질병에 대한 면역력이 저에게 전달되었어요! 우와~ㅎㅎ";
        BabyGrowthList[35][1] = "내장도 기능도 원활해졌어요>< 저는 여전히 양수를 마시고 오줌을 싼답니당ㅎㅎ";
        BabyGrowthList[35][2] = "저는 편안하게 잘 자라고 있지만 자꾸 커지는 몸 때문에 자궁 속에서 마음대로 움직일 수 없어요ㅠㅠ 얼른 나가고 싶어요>_<";

        BabyGrowthList[36][0] = "저의 키가 50cm 정도 이고, 몸무게도 3kg 정도에요!ㅎㅎ 그리구 거의 모든 장기가 완성되었어요>.<";
        BabyGrowthList[36][1] = "지금은 등을 움츠리고 팔 다리를 앞으로 모은 자세를 하고 있어요!!ㅎㅎ";
        BabyGrowthList[36][2] = "현재는 감염에 대한 저항력이 강해졌어요! 얍얍! 지금 당장 태어나도 건강하게 살 수 있어요><";

        BabyGrowthList[37][0] = "이제 얼굴이 신생아와 거의 비슷하고 머리카락도 2~4cm정도 자랐어요><";
        BabyGrowthList[37][1] = "저는 40분을 주기로 잠을 코오 자요!ㅎㅎ 그리고 깨어 있는 시간 리듬이 생겼어요!!";
        BabyGrowthList[37][2] = "저는 다른 분비물과 함께 벗어버린 솜털과 표피를 삼켰는데 이것은 저의 장을 움직이는 데 도움을 줬답니당!!";

        BabyGrowthList[38][0] = "저는 세상에 나가서 체온을 유지하기 위한 지방층을 계속 쌓고 있답니다!ㅎㅎ";
        BabyGrowthList[38][1] = "저는 반사작용을 할 수 있어서 빛, 촉감에 반응할 있어용!!><";
        BabyGrowthList[38][2] = "저는 소리도 들을 수 있고, 냄새도 맡을 수 있답니다!!";

        BabyGrowthList[39][0] = "전 모든 준비를 마치고 엄마아빠를 보러 곧 밖으로 나갈 준비를 하고있어요^0^";
        BabyGrowthList[39][1] = "저는 태어나면 바로 폐로 호흡하고, 울음소리로 저의 의사를 전달할거에용ㅎㅎㅎ";
        BabyGrowthList[39][2] = "약 5%정도의 아기만이 출산예정일을 지킨다고 하니, 예정일이 지났다고 해도 너무 걱정하시지 마세요!><";

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
                    chatModel.users.put(babyUid, true);

                    ChatModel.Comment comment = new ChatModel.Comment();    //매번 새로운 comment를 만들어서 거기에 message저장
                    comment.uid = babyUid;  //이 메시지의 uid는 babyUid로 지정



                    service.setUsernameAndPassword("bdcc80b8-8377-4204-82a8-e216db9823a9", "ouF38I4DN7cy");
                    MessageRequest newMessage = new MessageRequest.Builder().inputText(inputmessage).context(context).build();
                    response = service.message("9a28c659-3f7a-4ace-862c-294695262fe6", newMessage).execute();

                    if(response.getContext() !=null) {//왓슨에게 답변받은거 GetContext 를 이용하여 context 에 저장
                        context.clear();
                        context = response.getContext();

                        //수정한 부분4
                        comment.message = response.getText().toString().replace("[","").replace("]","");
                        //comment.message = w_answer;
                        if(!comment.message.equals("gotoandroidstudio")) {
                            if(NUM == 1) {
                                FirebaseDatabase.getInstance().getReference().child("babyTalk").child(chatRoomUid).child("comments").push().setValue(temp);
                                NUM = 10;
                            }
                            FirebaseDatabase.getInstance().getReference().child("babyTalk").child(chatRoomUid).child("comments").push().setValue(comment);
                        }//그러나 gotoandroidstudio저장됨Watson말을 저장!?, push는 comment마다 새로운 id를 주기 위해


                    }
                    //Message outMessage=new Message();
                    ChatModel.Comment outComment = new ChatModel.Comment();
                    if(response!=null)
                    {
                        if(response.getOutput()!=null && response.getOutput().containsKey("text"))
                        {

                            final String outputmessage = response.getOutput().get("text").toString().replace("[","").replace("]","");

                            //!!!!!여기가 사용자가 아기상태 물어보면 자바에서 직접 대답하는 부분
                            if(outputmessage.equals("gotoandroidstudio")){
                                if(NUM == 1) {
                                    FirebaseDatabase.getInstance().getReference().child("babyTalk").child(chatRoomUid).child("comments").push().setValue(temp);
                                    NUM = 10;
                                }

                                //수정한 부분5
                                String babyGrowthMessage = BabyGrowthList[dDay-1][num()];
                                comment.message = babyGrowthMessage;
                                FirebaseDatabase.getInstance().getReference().child("babyTalk").child(chatRoomUid).child("comments").push().setValue(comment);    //제대로 된 답변

                                outComment.message = babyGrowthMessage;
                                //w_answer = babyGrowthMessage;

                            }

                            else {
                                outComment.message = outputmessage;
                                //w_answer = outputmessage;
                            }
                            //outMessage.setMessage("여기다가 쓰는거 waston이 출력하게 되어있음!!!!");
                            outComment.uid = babyUid;
                        }
                        comments.add(outComment);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                //recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);
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

    private boolean checkInternetConnection(){
        // get Connectivity Manager object to check connection
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

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