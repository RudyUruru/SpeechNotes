package com.example.speechnotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speechnotes.dao.ClueDAO;
import com.example.speechnotes.models.Clue;

public class AddingActivity extends AppCompatActivity {

    public ClueDAO clueDao = ClueDAO.getInstance();
    public Clue clue;
    public EditText editTitle;
    public EditText editTime;
    public EditText editHintText;
    public ImageButton addButton;
    public ImageButton backToHints;
    public boolean newHint=false;
    public String savingListHint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_activity);


        editTitle = findViewById(R.id.editTitle);
        editTime = findViewById(R.id.editTime);
        editHintText = findViewById(R.id.editClueText);

        final Intent intent = this.getIntent();
        int position = intent.getIntExtra("hint",Integer.MAX_VALUE);
        savingListHint = intent.getStringExtra("savingListHint");
        if(clueDao.getMapSize()>0 && position< clueDao.getMapSize()){
            clue = clueDao.getClue(position);
        }
        if(clue !=null) {
            editTitle.setText(clue.getTitle());
            editTime.setText(String.valueOf(Double.valueOf(clue.getMillisInFuture())/60000));
            editHintText.setText(clue.getText());
            newHint=false;
        }
        else{
            clue =new Clue();
            clue.setPosition(clueDao.getMapSize());
            newHint=true;
        }
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editTitle.getText().toString()) && !TextUtils.isEmpty(editTime.getText().toString()) && !TextUtils.isEmpty(editHintText.getText().toString())) {

                    clue.setText(editHintText.getText().toString());
                    clue.setTitle(editTitle.getText().toString());
                    clue.setMillisInFuture(Long.valueOf((long) (Double.valueOf(editTime.getText().toString())*60000)));
                    if (newHint) {
                        clueDao.addClue(clue);
                        Log.e("Add", clue.getTitle()+ " "+ clue.getPosition());
                    }else{
                        Log.e("Update", clue.getTitle()+ " "+ clue.getPosition());
                        clueDao.updateClue(clue);
                    }

                    Intent intent1 = new Intent(getApplicationContext(), ClueListActivity.class);
                    intent1.putExtra("savingListHint",savingListHint);
                    startActivity(intent1);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите данные во все поля!",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
            }
        });

        backToHints=findViewById(R.id.backToHints);
        backToHints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), ClueListActivity.class);
                intent1.putExtra("savingListHint",savingListHint);
                startActivity(intent1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplicationContext(), ClueListActivity.class);
        intent1.putExtra("savingListHint",savingListHint);
        startActivity(intent1);
    }

}
