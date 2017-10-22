package com.aakash.notes;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNoteActivity extends AppCompatActivity {

    private EditText mTitle;
    private EditText mContent;
    private FloatingActionButton mFab;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_add_note);

        mTitle = findViewById(R.id.titleText);
        mContent = findViewById(R.id.contentText);

        mUsername = getIntent().getStringExtra("username");

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("Users").child(mUsername);

        mFab = findViewById(R.id.fab);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_anim);
        mFab.startAnimation(animation);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty()){
                    Note note = new Note(mTitle.getText().toString(), mContent.getText().toString(), mUsername);
                    mReference.push().setValue(note);
                    finish();
                } else {
                    Snackbar.make(mFab, "Empty Data", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isEmpty(){
        return mTitle.getText().toString().equals("");
    }
}
