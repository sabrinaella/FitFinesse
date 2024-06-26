package com.example.fitfinessee.JadwalWoPage.activity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitfinessee.R;
import com.example.fitfinessee.JadwalWoPage.adapter.TaskAdapter;
import com.example.fitfinessee.JadwalWoPage.bottomSheetFragment.CreateTaskBottomSheetFragment;
import com.example.fitfinessee.JadwalWoPage.bottomSheetFragment.ShowCalendarViewBottomSheet;
import com.example.fitfinessee.JadwalWoPage.broadcastReceiver.AlarmBroadcastReceiver;
import com.example.fitfinessee.JadwalWoPage.database.DatabaseClient;
import com.example.fitfinessee.JadwalWoPage.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivityJadwal extends BaseActivity implements CreateTaskBottomSheetFragment.setRefreshListener {
    RecyclerView taskRecycler;
    TextView addTask;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();
    ImageView noDataImage;
    ImageView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jadwal_activity_main);

        taskRecycler = findViewById(R.id.taskRecycler);
        addTask = findViewById(R.id.addTask);
        noDataImage = findViewById(R.id.noDataImage);
        calendar = findViewById(R.id.calendar);

        setUpAdapter();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(this, AlarmBroadcastReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Glide.with(getApplicationContext()).load(R.drawable.no_task1).into(noDataImage);

        addTask.setOnClickListener(view -> {
            CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
            createTaskBottomSheetFragment.setTaskId(0, false, this, MainActivityJadwal.this);
            createTaskBottomSheetFragment.show(getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });

        getSavedTasks();

        calendar.setOnClickListener(view -> {
            ShowCalendarViewBottomSheet showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
            showCalendarViewBottomSheet.show(getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });
    }

    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(this, tasks, this);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        taskRecycler.setAdapter(taskAdapter);
    }

    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                noDataImage.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    @Override
    public void refresh() {
        getSavedTasks();
    }
}
