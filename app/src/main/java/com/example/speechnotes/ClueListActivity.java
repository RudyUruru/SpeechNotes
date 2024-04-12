package com.example.speechnotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speechnotes.dao.ClueDAO;
import com.example.speechnotes.models.Clue;
import com.google.gson.GsonBuilder;


import java.util.Arrays;
import java.util.List;

public class ClueListActivity extends AppCompatActivity {

    private ListView clueList;
    public ClueDAO clueDAO = ClueDAO.getInstance();
    public ClueAdapter adapter;
    public boolean isBackButtonPressed = false;
    public SharedPreferences sharedPreferences;
    public String hints;
    public String savingListHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clue_list);
        Intent intent = getIntent();
        savingListHint = intent.getStringExtra("savingListHint");
        Log.i("Get lecture", savingListHint);
        //конструкторы сохранения заметок
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ClueListActivity.this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        isBackButtonPressed=false;
        //создание листа из заметок
        clueList =findViewById(R.id.clueList);
        adapter = new ClueAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, clueDAO.getClues());
        clueList.setAdapter(adapter);

        //проверка на наличие сохраненных заметок
        if(!sharedPreferences.getString(savingListHint,"[]").equals("[]") && clueDAO.getAllClues().isEmpty()){
            Log.i("Get hints", sharedPreferences.getString(savingListHint,"[]"));
            clueDAO.addAllClues(Arrays.asList(gsonBuilder.create().fromJson(sharedPreferences.getString(savingListHint,"[]"), Clue[].class)));
            String hints = gsonBuilder.create().toJson(clueDAO.getAllClues());
            Log.i("Add hints to DAO", hints);
        }
        else Log.i("Get hints", "нет заметок или уже есть в базе данных");

        TextView nameLecture = findViewById(R.id.nameLecture);
        nameLecture.setText(savingListHint);

        //кнопка добавления заметки
        ImageButton addHint= findViewById(R.id.addClue);
        addHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddingActivity.class);
                intent.putExtra("hint", clueDAO.getMapSize());
                intent.putExtra("savingListHint",savingListHint);
                startActivity(intent);
            }
        });


        ImageButton backToLectures= findViewById(R.id.backToLectures);
        backToLectures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),LecturesListActivity.class);
                isBackButtonPressed=true;
                Log.i("Clear hints", "true");
                startActivity(intent1);
            }
        });


        //кнопка включения таймера
        ImageButton play= findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clueDAO.getMapSize()>0){
                    GsonBuilder gsonBuilder1 = new GsonBuilder();
                    String hints = gsonBuilder1.create().toJson(clueDAO.getAllClues());

                    Log.i("Add hints to DAO", hints);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("savingListHint",savingListHint);
                    startActivity(intent);

                }
                else Toast.makeText(getApplicationContext(),"Нет заметок!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //сохранение заметок при остановке активити
    @Override
    protected void onStop() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        hints = gsonBuilder.create().toJson(clueDAO.getAllClues());
        if(isBackButtonPressed==true) {
            Log.i("Clear hintDao onStop",savingListHint+ hints);
            clueDAO.clear();
            isBackButtonPressed=false;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(savingListHint, hints);
        Log.i("Save hints onStop", savingListHint+hints);
        editor.apply();
        super.onStop();
    }

    //сохранение заметок при уничтожении активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //внутренний класс адаптера для просмотра всех заметок
    private class ClueAdapter extends ArrayAdapter<Clue>{

        public ClueAdapter(@NonNull Context context, int resource, @NonNull List<Clue> objects) {
            super(context, resource, objects);
        }

        //создание адаптера
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent){
            final Clue clue = getItem(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.clue, null);

            ClueHolder holder = new ClueHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.settings = convertView.findViewById(R.id.settings);
            holder.delete=convertView.findViewById(R.id.delete);



            holder.title.setText(clue.getTitle());
            convertView.setTag(holder);

            //установка слушателя на кнопку редактирования заметки
            holder.settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), AddingActivity.class);
                    intent.putExtra("savingListHint",savingListHint);
                    intent.putExtra("hint", clue.getPosition());
                    startActivity(intent);
                }
            });

            //установка слушателя на кнопку удаления заметки
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.remove(clue);
                    clueDAO.deleteClue(clue);
                }
            });



            return convertView;
        }
    }
    //внутренний класс фрагмента заметки для инициализации его объектов (кнопок и полей)
    private static  class ClueHolder {
        public TextView title;
        public ImageButton settings;
        public ImageButton delete;
    }
    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplicationContext(),LecturesListActivity.class);
        isBackButtonPressed=true;
        Log.i("Clear hints", "true");
        startActivity(intent1);
    }
}
