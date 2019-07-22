package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> arrayList;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView=findViewById(R.id.listView);
        mSwipeRefreshLayout=findViewById(R.id.refressLayout);

        listView.setOnItemClickListener(this);

        arrayList=new ArrayList<>();
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);

        try{
            ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size()>0 && e==null){
                        for (ParseUser appUser:objects){
                            arrayList.add(appUser.getUsername());
                        }
                        listView.setAdapter(adapter);
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();
                    parseQuery.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereNotContainedIn("username",arrayList);
                    parseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size()>0){
                                if (e==null){

                                    for (ParseUser appuser:objects){
                                        arrayList.add(appuser.getUsername());
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (mSwipeRefreshLayout.isRefreshing()){
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                }

                            }
                            else {
                                if (mSwipeRefreshLayout.isRefreshing()){
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_logout:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null) {

                            FancyToast.makeText(Home.this, ParseUser.getCurrentUser().get("username") + " is Logout Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                            Intent intent = new Intent(Home.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent =new Intent(Home.this,ChatActivity.class);
        intent.putExtra("selectUserName",arrayList.get(position));
        startActivity(intent);
    }
}
