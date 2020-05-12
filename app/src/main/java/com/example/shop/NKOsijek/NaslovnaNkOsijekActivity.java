package com.example.shop.NKOsijek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterViewAnimator;
import android.widget.Button;
import android.widget.StackView;

import com.example.shop.Buyers.MainActivity;
import com.example.shop.R;

public class NaslovnaNkOsijekActivity extends AppCompatActivity {
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_naslovna_nk_osijek );

        login = (Button) findViewById ( R.id.login );

        login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( NaslovnaNkOsijekActivity.this, MainActivity.class );
                startActivity ( intent );
            }
        } );
    }
}
