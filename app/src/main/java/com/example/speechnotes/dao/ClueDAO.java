package com.example.speechnotes.dao;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.example.speechnotes.models.Clue;

import java.util.ArrayList;
import java.util.List;

public class ClueDAO extends AppCompatActivity {

    private static ClueDAO instance; // Singleton instance
    private ArrayList<Clue> clues;
    private int mapSize=0;

    private ClueDAO(){
        clues = new ArrayList<>();
    }

    public static ClueDAO getInstance(){
        Log.e("get instance", "get instance");
        if(instance == null){
            instance = new ClueDAO();
        }
        return instance;
    }


    public ArrayList<Clue> getClues(){
        return clues;
    }

    public void addAllClues(List<Clue> clues){
        mapSize = clues.size();
        this.clues.addAll(clues);
    }

    public void addClue(Clue clue){
        mapSize++;
        Log.e("ADD", clue.getTitle()+ " "+ clue.getPosition());
        clues.add(clue);
    }

    public Clue findClue(String title) throws NullPointerException{
        for(Clue clue: clues) {
            if (clue.getTitle().toLowerCase().equals(title.toLowerCase())) return clue;
        }
        throw new NullPointerException();
    }

    public void updateClue(Clue clue){
        Log.e("UPDATE", clue.getTitle()+ " "+ clue.getPosition());
        clues.set(clue.getPosition(), clue);
    }

    public Clue getClue(int id) {
        if (clues.size() == id) {
            return clues.get(id - 1);
        }
        return clues.get(id);
    }

    public int getMapSize() {
        return mapSize;
    }

    public void deleteClue(Clue clue) {
        mapSize--;
        clues.remove(clue.getPosition());
        Log.e("Delete", clue.getTitle()+ " "+ clue.getPosition());
    }

    public List<Clue> getAllClues(){
        return clues;
    }

    public void clear(){
        clues.clear();
    }

}
