package com.m2dl.challengemobe;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

public class Score {
    private Date start;
    private DatabaseReference myDb;

    public Score() {
        start = new Date();
        myDb = FirebaseDatabase.getInstance().getReference();

    }

    public Integer saveScore() {
        Date current = new Date();
        Integer score = (int) (current.getTime() - start.getTime()) / 1000;
        String scoreId = UUID.randomUUID().toString();
        myDb.child("scores").child(scoreId).setValue(score);
        return score;
    }

    public DatabaseReference getMyDb() {
        return myDb;
    }
}
