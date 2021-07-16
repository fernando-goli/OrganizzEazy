package com.example.organizzeazy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.organizzeazy.R;
import com.example.organizzeazy.activity.CadastroActivity;
import com.example.organizzeazy.activity.LoginActivity;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

     setButtonBackVisible(false);
     setButtonNextVisible(false);

    addSlide(new FragmentSlide.Builder()
        .background(android.R.color.holo_blue_light)
        .fragment(R.layout.intro_1)
        .build()
        );
        addSlide(new FragmentSlide.Builder()
            .background(android.R.color.holo_blue_light)
            .fragment(R.layout.intro_2)
            .build()
        );
        addSlide(new FragmentSlide.Builder()
            .background(android.R.color.holo_blue_light)
            .fragment(R.layout.intro_3)
            .build()
        );
        addSlide(new FragmentSlide.Builder()
            .background(android.R.color.holo_blue_light)
            .fragment(R.layout.intro_4)
            .build()
        );
        addSlide(new FragmentSlide.Builder()
            .background(android.R.color.holo_blue_light)
            .fragment(R.layout.intro_cadastro)
            .canGoForward(false)
            .canGoForward(false)
            .build()
        );


    } //onCreate

    public void btnEntrar(View view){
        startActivity(new Intent(this, LoginActivity.class));

    }

    public void btnCadastrar(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }

}
