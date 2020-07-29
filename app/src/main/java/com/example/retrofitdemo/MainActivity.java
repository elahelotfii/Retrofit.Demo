package com.example.retrofitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    //Initialize variable
    RecyclerView recyclerView;
    ArrayList<MainData> dataArrayList = new ArrayList<>();
    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        recyclerView = findViewById(R.id.recycler_view);

        //Initialize adapter
        adapter = new MainAdapter(MainActivity.this,dataArrayList);

        //Set layout manager
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //Set adapter
        recyclerView.setAdapter(adapter);

        //Create get data method
        getData();

    }

    private void getData() {

        //Initialize progress dialog
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        //Set message on dialog
        dialog.setMessage("Please Wait...");

        //Set non cancelable
        dialog.setCancelable(false);

        //Show dialog
        dialog.show();

        //Initialize retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://picsum.photos/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        //Create interface
        MainInterface mainInterface = retrofit.create(MainInterface.class);

        //Initialize call
        Call<String> stringCall = mainInterface.STRING_CALL();

        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Check condition
                if (response.isSuccessful() && response.body() != null){
                    //When response is successful and not null
                    //Dismiss dialog
                    dialog.dismiss();

                    try {
                        //Initialize response json array
                        JSONArray jsonArray = new JSONArray(response.body());
                        //Parse json array
                        parseArray(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseArray(JSONArray jsonArray) {
        //Clear array list
        dataArrayList.clear();
        //Use for loop
        for (int i=0; i<jsonArray.length(); i++){

            try {
                //Initialize json object
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //Initialize main data
                MainData data = new MainData();

                //Set image
                data.setImage(jsonObject.getString("download_url"));

                //Set name
                data.setName(jsonObject.getString("author"));

                //Add data in array list
                dataArrayList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Initialize adapter
            adapter = new MainAdapter(MainActivity.this,dataArrayList);

            //Set adapter
            recyclerView.setAdapter(adapter);
        }
    }
}