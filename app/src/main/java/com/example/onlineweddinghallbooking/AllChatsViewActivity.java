package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllChatsViewActivity extends AppCompatActivity implements ChatAdapter.OnNoteListener {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private ImageButton backButton;

    private MessageModel messageModel;

    private ArrayList<String> chatReferences;
    private ArrayList<AllMessageModel> allMessageModelArrayList;
    private ArrayList<MessageModel> messageModelArrayList;

    private int counter;
    private String receiverUid;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats_view);

        initializeFirebase();
        initializeProperties();

        getAllChatReferences();

        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllChatsViewActivity.super.onBackPressed();
                finish();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void initializeProperties() {

        chatReferences = new ArrayList<>();
        allMessageModelArrayList = new ArrayList<>();
        messageModelArrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.chatsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatAdapter = new ChatAdapter(this, allMessageModelArrayList, this);
        recyclerView.setAdapter(chatAdapter);

        backButton = findViewById(R.id.backButton);
    }

    private void getAllChatReferences() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseDatabase.getReference("Users").child(uid).child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatReferences.clear();
                messageModelArrayList.clear();
                allMessageModelArrayList.clear();
                chatAdapter.notifyDataSetChanged();

                if (snapshot.getChildrenCount() > 0)
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String chatRef = dataSnapshot.child("chatRef").getValue(String.class);
                        chatReferences.add(chatRef);
                        getAllMessages(chatRef);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AllChatsViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getAllMessages(String chatRef) {

        counter = 1;

        firebaseDatabase.getReference("Chats").child(chatRef).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    try
                    {
                        if (counter == snapshot.getChildrenCount())
                        {
                            messageModel = dataSnapshot.getValue(MessageModel.class);
                            messageModelArrayList.add(messageModel);
                            counter = 1;

                            getMessageDetails(messageModel);
                        }
                        else
                        {
                            counter++;
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(AllChatsViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AllChatsViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getMessageDetails(MessageModel messageModel) {

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (messageModel.getSenderUid().equals(currentUserUid))
        {
            firebaseDatabase.getReference("Users").child(messageModel.getReceiverUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String name = snapshot.child("name").getValue(String.class);

                    AllMessageModel allMessageModel = new AllMessageModel();
                    allMessageModel.setName(name);
                    allMessageModel.setLastMessage(messageModel.getMsg());
                    allMessageModel.setDate(messageModel.getDateAndTime());

                    allMessageModelArrayList.add(allMessageModel);

                    chatAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(AllChatsViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            firebaseDatabase.getReference("Users").child(messageModel.getSenderUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String name = snapshot.child("name").getValue(String.class);

                    AllMessageModel allMessageModel = new AllMessageModel();
                    allMessageModel.setName(name);
                    allMessageModel.setLastMessage(messageModel.getMsg());
                    allMessageModel.setDate(messageModel.getDateAndTime());

                    allMessageModelArrayList.add(allMessageModel);

                    chatAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(AllChatsViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    @Override
    public void onNoteClick(int position) {

        messageModelArrayList.get(position);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid.equals(messageModelArrayList.get(position).getSenderUid()))
        {
            receiverUid = messageModelArrayList.get(position).getReceiverUid();
        }
        else
        {
            receiverUid = messageModelArrayList.get(position).getSenderUid();
        }

        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("receiverUid", receiverUid);
        i.putExtra("receiverType", "customer");
        i.putExtra("chatReferenceID", chatReferences.get(position));

        startActivity(i);

    }
}