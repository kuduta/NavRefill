package com.kuduta.navrefill;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReffActivity extends AppCompatActivity {

    ArrayList<String> exData = null;
    ProgressDialog pDialog;
    private List<Data> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reff);

        final String valExpDate = getIntent().getExtras().getString("expValue");  //variable  dialog expiration  date
        final String valMoney = getIntent().getExtras().getString("monValue");  // variable dialog Money
        final Boolean valLic = getIntent().getExtras().getBoolean("licValue");


        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final String strDate = format.format(date);  //date Now

        this.setTitle("ListView");

        final ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> numPhArrayList = new ArrayList<>();

        String url ="http://210.1.51.6/mobile2/json_prepaid.php?U=jaknarong&P=1234554320&qFromDate="+ strDate +"&qToDate="+ valExpDate+"&qMoney="+valMoney;
//        String url = "http://210.1.51.6/mobile2/json_prepaid.php?U=jaknarong&P=1234554320&qFromDate=2017-06-28&qToDate=2017-07-05&qMoney=16";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        final Data item = null;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSONObject jsObj;
//                        tv.setText(response.toString());

                        for (int i = 0; i < response.length(); i++) {

                            try {
                                jsObj = response.getJSONObject(i);

                                String strNumPhone = jsObj.getString("ph_number");
                                String strNetwork = jsObj.getString("network");
                                String strlicense = jsObj.getString("license");
                                String strMoney = jsObj.getString("money");
                                String strExpire = jsObj.getString("expire");

                                strNumPhone = "0" + strNumPhone.substring(2);

//                                arrayList.add(strNumPhone + " , " + strNetwork + " , " + strMoney + " , " + strExpire);

                                //******// Used class Data
                                if(valLic==true) {

                                    if(!strlicense.substring(0, 1).matches("ฯ"))
                                       arrayList.add(strNumPhone + " \t \t" + twoPoint(strMoney) + "\t  \t " + strExpire + "\t \t \t");
//                                        arrayList.add(strNumPhone + "\t" + strlicense +"\t");

                                }else {

                                    arrayList.add(strNumPhone + " \t \t" + twoPoint(strMoney) + "\t  \t " + strExpire + "\t \t \t");
//                                    arrayList.add(strNumPhone + "\t" + strlicense +"\t");
                                }
                                //add new
                                //datas.add(new Data(strNumPhone  ,  strMoney , strExpire));
                                //MyAdapter adapter = new MyAdapter(ReffActivity.this,datas);

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_multiple_choice, arrayList);
                                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                                listView.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            pDialog.hide();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();

                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);

        Button button = (Button) findViewById(R.id.button);
        Button  bthome = (Button) findViewById(R.id.button3);

        bthome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ReffActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                SparseBooleanArray booleanArray = listView.getCheckedItemPositions();

                for (int i = 0; i < listView.getCount(); i++) {
                    if (booleanArray.get(i) == true) {
                        if (!str.equals("")) {
                            str += "\n";

                        }
                        str += listView.getItemAtPosition(i).toString().substring(0,10);
                        numPhArrayList.add(listView.getItemAtPosition(i).toString().substring(0,10));

                    }
                }

                str = "รายการที่เลือก: \n" + str;
                Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();


                Intent intent = new Intent(ReffActivity.this,ShowRefillActivity.class);

//                Intent intent = new Intent(ReffActivity.this,PayDisplayActivity.class);
                intent.putExtra("numPhArrayList", numPhArrayList);
                startActivity(intent);

            }
        });


    }
    private String twoPoint(String mCheck){

        String dot = ".";

        if (mCheck.contains(dot)){
            return mCheck;
        }else{

            return  mCheck+".00";
        }


    }


}
