package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    static String userUid;
    String receiverUid;
    String chatReferenceID;
    String receiverType;
    String venueUid;

    ImageView receiverImg;
    TextView receiverName;

    EditText messageET;
    ImageButton sendMessageBtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    ArrayList<MessageModel> messageArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        initializeWidgets();
        initializeFirebase();

        getAllMessages();

        sendMsgBtnOnCLickListener();

        displayReceiverInformation();
    }

    private void displayReceiverInformation() {

        if (receiverType.equalsIgnoreCase("customer"))
        {
            firebaseDatabase.getReference("Users").child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);
                    receiverName.setText(user.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            addChatReferenceForCustomer();
            addChatReferenceForServiceProvider();

            firebaseDatabase.getReference("Venues").child(venueUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Venue venue = snapshot.getValue(Venue.class);
                    receiverName.setText(venue.getName());
                    Picasso.get().load(venue.getThumbnailImage()).into(receiverImg);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void addChatReferenceForCustomer() {

        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Object> map = new HashMap<>();
                map.put("chatRef", chatReferenceID);

                firebaseDatabase.getReference("Users").child(userUid).child("Chats").child(chatReferenceID).updateChildren(map);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addChatReferenceForServiceProvider() {

        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Object> map = new HashMap<>();
                map.put("chatRef", chatReferenceID);

                firebaseDatabase.getReference("Users").child(receiverUid).child("Chats").child(chatReferenceID).updateChildren(map);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendMsgBtnOnCLickListener() {

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!messageET.getText().toString().equals(""))
                {
                    firebaseDatabase.getReference("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String key = databaseReference.child("Chats").child(chatReferenceID).push().getKey();

                            Date c =  Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                            String formattedDate = df.format(c);

                            Map<String, Object> map = new HashMap<>();
                            map.put("msg", messageET.getText().toString());
                            map.put("senderUid", userUid);
                            map.put("receiverUid", receiverUid);
                            map.put("dateAndTime", formattedDate);
                            databaseReference.child("Chats").child(chatReferenceID).child(key).updateChildren(map);

                            messageET.setText("");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void getAllMessages() {

        firebaseDatabase.getReference("Chats").child(chatReferenceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messageArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);

                    addMessageToArrayList(messageModel);
                    messageAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addMessageToArrayList(MessageModel messageModel) {

        messageArrayList.add(messageModel);
    }

    private void initializeWidgets() {

        recyclerView = findViewById(R.id.messageRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageArrayList);
        recyclerView.setAdapter(messageAdapter);

        receiverUid = getIntent().getStringExtra("receiverUid");
        receiverType = getIntent().getStringExtra("receiverType");

        receiverImg = findViewById(R.id.receiverImage);
        receiverName = findViewById(R.id.receiverName);

        messageET = findViewById(R.id.messageET);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);

        chatReferenceID = getIntent().getStringExtra("chatReferenceID");

        venueUid = getIntent().getStringExtra("venueUid");
    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }
}