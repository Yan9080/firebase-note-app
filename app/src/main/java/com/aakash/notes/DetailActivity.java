package com.aakash.notes;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    private Note mNote;

    private EditText mTitle;
    private EditText mContent;
    private FloatingActionButton mFabDetail;

    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_detail);

        mNote = (Note) getIntent().getSerializableExtra("note");

        mDatabase = FirebaseDatabase.getInstance();

        mTitle = findViewById(R.id.editTitle);
        mContent = findViewById(R.id.editContent);
        animateViewUp(mTitle);
        animateViewUp(mContent);

        mTitle.setText(mNote.getTitle());
        mContent.setText(mNote.getContent());

        mFabDetail = findViewById(R.id.fabDetail);
        animateFab();
        mFabDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.getReference("Users").child(mNote.getUsername()).orderByChild("title").equalTo(mNote.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            setData(data.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        animateViewDown(mTitle);
        animateViewDown(mContent);
        super.onBackPressed();
    }

    private void setData(String key){
        mDatabase.getReference("Users").child(mNote.getUsername()).child(key).setValue(new Note(
                mTitle.getText().toString(),
                mContent.getText().toString(),
                mNote.getUsername()
        ));
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void animateFab() {
        mFabDetail.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_anim);
        mFabDetail.startAnimation(animation);
    }

    private void animateViewUp(View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_anim_up);
        view.startAnimation(animation);
    }

    private void animateViewDown(View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_anim_down);
        view.startAnimation(animation);
    }


}
