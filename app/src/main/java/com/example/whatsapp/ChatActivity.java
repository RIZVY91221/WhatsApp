package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{
    private ListView chatList;
    private ArrayList<String> chatArray;
    private ArrayAdapter adapter;
    private ImageButton sendButton;
    private String selectUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton=findViewById(R.id.enter_chat);
        chatList=findViewById(R.id.chat_list_view);

        sendButton.setOnClickListener(this);
        chatArray=new ArrayList<>();
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,chatArray);
        chatList.setAdapter(adapter);

        Intent intent= getIntent();
        selectUserName= intent.getStringExtra("selectUserName");

        FancyToast.makeText(ChatActivity.this, "Chat with "+selectUserName, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();

        try {
            ParseQuery<ParseObject> fristUserChatQuary = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuary = ParseQuery.getQuery("Chat");

            fristUserChatQuary.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
            fristUserChatQuary.whereEqualTo("targetRecipient", selectUserName);

            secondUserChatQuary.whereEqualTo("sender", selectUserName);
            secondUserChatQuary.whereEqualTo("targetRecipient", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> allQuerys = new ArrayList<>();
            allQuerys.add(fristUserChatQuary);
            allQuerys.add(secondUserChatQuary);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQuerys);
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e)
                {
                    if (objects.size()>0 && e==null){
                        for (ParseObject sms:objects){
                            String chatSMS=sms.get("message")+"";

                            if (sms.get("message").equals(ParseUser.getCurrentUser().getUsername())){
                                chatSMS=ParseUser.getCurrentUser().getUsername()+" : "+chatSMS;
                            }
                            if (sms.get("message").equals(selectUserName)){
                                chatSMS=selectUserName +" : "+chatSMS;
                            }
                            chatArray.add(chatSMS);
                        }
                        adapter.notifyDataSetChanged();
                    }

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        final EditText edtMessege=findViewById(R.id.chat_edit_text);
        ParseObject chat=new ParseObject("Chat");
        chat.put("sender",ParseUser.getCurrentUser().getUsername());
        chat.put("targetRecipient",selectUserName);
        chat.put("message",edtMessege.getText().toString());
        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    FancyToast.makeText(ChatActivity.this, "Message send to "+selectUserName, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                    chatArray.add(ParseUser.getCurrentUser().getUsername()+" : "+edtMessege.getText().toString());
                    adapter.notifyDataSetChanged();
                    edtMessege.setText(" ");
                }
            }
        });
    }
}
