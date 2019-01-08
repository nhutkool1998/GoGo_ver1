package cs486.nmnhut.gogo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

class ChatMessage {
    String content;
    String username;

    public ChatMessage() {

    }
}

class chatAdapter extends ArrayAdapter<ChatMessage> {
    List<ChatMessage> messageList;
    Context context;

    public chatAdapter(Context context, List<ChatMessage> messageList) {
        super(context, R.layout.chat_item, messageList);
        this.context = context;
        this.messageList = messageList;
    }

    class ChatViewHolder {
        public TextView txtChatMessage, txtChatPerson;
    }

    public void add(ChatMessage c) {
        messageList.add(c);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ChatViewHolder c;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.chat_item, parent, false);
            c = new ChatViewHolder();
            c.txtChatMessage = convertView.findViewById(R.id.txtChatMessage);
            c.txtChatPerson = convertView.findViewById(R.id.txtChatPerson);
            convertView.setTag(c);
        } else
            c = (ChatViewHolder) convertView.getTag();
        ChatMessage m = messageList.get(position);
        c.txtChatPerson.setText(m.username);
        c.txtChatMessage.setText(m.content);
        return convertView;
    }
}

public class ChatActivity extends AppCompatActivity {
    ListView listView;
    Button btnSend;
    EditText txtChatContent;
    List<ChatMessage> list;
    String tripID, UID, UName;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    chatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = findViewById(R.id.listViewChat);
        btnSend = findViewById(R.id.btnSendMessage);
        txtChatContent = findViewById(R.id.txtChatContent);

        UID = DatabaseHelper.currentUserID();
        UName = DatabaseHelper.getUserEmail();

        list = new ArrayList<>();
        adapter = new chatAdapter(this, list);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        tripID = intent.getStringExtra("tripID");

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Chat").child(tripID);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.content = txtChatContent.getText().toString();
                chatMessage.username = UName;

                ref.push().setValue(chatMessage);
            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage c = dataSnapshot.getValue(ChatMessage.class);
                adapter.add(c);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

