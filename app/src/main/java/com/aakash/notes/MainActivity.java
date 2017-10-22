package com.aakash.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener {

    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private CircleImageView mUserImage;
    private TextView mUsername;
    private FloatingActionButton mFab;
    private List<Note> mNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initiateViews();

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title("Information")
                        .content("Do you want to sign out?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                signOut();
                            }
                        })
                        .show();
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNoteActivity(view);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initiateViews() {
        mNotes = new ArrayList<>();
        mUserImage = findViewById(R.id.user_image);
        mUsername = findViewById(R.id.usernameWelcome);
        mViewPager = findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter(this);
        mFab = findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
    }

    private void animateFab() {
        mFab.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_anim);
        mFab.startAnimation(animation);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mUser = firebaseAuth.getCurrentUser();

        if (mUser != null) {
            Glide.with(this).load(mUser.getPhotoUrl()).into(mUserImage);
            mUsername.setText("Welcome " + mUser.getDisplayName());
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference("Users").child(mUser.getDisplayName());
            loadNotes();
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }

    private void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();
                    }
                }
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }

    private void startNoteActivity(View view) {
        if (mUser != null) {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("username", mUser.getDisplayName());
            startActivity(intent);
        }
    }

    private void loadNotes() {
        mDatabase.getReference("Users").child(mUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    mCardAdapter.clearList();

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Note note = snap.getValue(Note.class);
                        mCardAdapter.addCardItem(note);
                        mNotes.add(note);
                    }

                    setAdapter();
                    animateFab();
                } else {
                    Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                    animateFab();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter() {
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mCardAdapter);
    }
}
