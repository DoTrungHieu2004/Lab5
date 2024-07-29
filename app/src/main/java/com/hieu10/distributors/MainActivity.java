package com.hieu10.distributors;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hieu10.distributors.adapter.DistributorAdapter;
import com.hieu10.distributors.model.Distributor;
import com.hieu10.distributors.model.Response;
import com.hieu10.distributors.service.HTTPRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private EditText ed_search;
    private RecyclerView rv_distributor;
    private FloatingActionButton fab_add;
    private HTTPRequest httpRequest;
    private ArrayList<Distributor> list = new ArrayList<>();
    private DistributorAdapter adapter;
    private static final String TAG = "MainActivity";
    private ProgressDialog progDialog;
    DistributorAdapter.DistributorClick click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_search = findViewById(R.id.ed_search);
        rv_distributor = findViewById(R.id.rv_distributor);
        fab_add = findViewById(R.id.fab_add);

        fetchAPI();
        userListener();
    }

    private void fetchAPI() {
        progDialog = new ProgressDialog(this);
        progDialog.setTitle("Loading...");
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.show();

        httpRequest = new HTTPRequest();
        httpRequest.callAPI()
                .getListDistributor()
                .enqueue(getDistributorAPI);
    }

    private void userListener() {
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = ed_search.getText().toString().trim();
                    httpRequest.callAPI()
                            .searchDistributor(key)
                            .enqueue(getDistributorAPI);
                    Log.d(TAG, "onEditorAction: " + key);
                    return true;
                }
                return false;
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdd();
            }
        });
    }

    private void showDialogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add distributor");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        EditText ed_name = view.findViewById(R.id.ed_name);
        Button btn_submit = view.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ed_name.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Distributor name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Distributor distributor = new Distributor();
                    distributor.setName(name);
                    httpRequest.callAPI()
                            .addDistributor(distributor)
                            .enqueue(responseDistributorAPI);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void getData() {
        adapter = new DistributorAdapter(list, this, click);
        rv_distributor.setAdapter(adapter);
        progDialog.dismiss();
    }

    Callback<Response<ArrayList<Distributor>>> getDistributorAPI = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    list = response.body().getData();
                    getData();
                    Log.d(TAG, "onResponse: " + list.size());
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable throwable) {
            Log.e(TAG, "onFailure: " + throwable.getMessage());
        }
    };

    Callback<Response<ArrayList<Distributor>>> responseDistributorAPI = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {

                    list = response.body().getData();
                    getData();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable throwable) {

        }
    };;

    private void showDialogEdit(Distributor distributor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add distributor");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        EditText ed_name = view.findViewById(R.id.ed_name);
        Button btn_submit = view.findViewById(R.id.btn_submit);

        ed_name.setText(distributor.getName());

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = distributor.getName();

                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Distributor name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Distributor distributor1 = new Distributor();
                    distributor1.setName(ed_name.getText().toString().trim());
                    httpRequest.callAPI()
                            .updateDistributor(distributor.getId(), distributor1)
                            .enqueue(responseDistributorAPI);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void delete(Distributor distributor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", (dialog, which) -> {
            httpRequest.callAPI()
                    .deleteDistributor(distributor.getId())
                    .enqueue(responseDistributorAPI);
        });
        builder.setNegativeButton("no", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    public void edit(Distributor distributor) {
        showDialogEdit(distributor);
    }
}