package cs486.nmnhut.gogo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

public class AlarmActivity extends AppCompatActivity {
    boolean isPlaying;
    MediaPlayer mp;
    EditText txtPlace, txtStartTime, txtEndTime;
    ToggleButton toggleButton;
    ImageButton imageButton;
    Button btnStopAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        txtPlace = findViewById(R.id.txtPlace);
        txtStartTime = findViewById(R.id.txtTimeStart);
        txtEndTime = findViewById(R.id.txtTimeEnd);

        String place;
        String starttime;
        String endtime;

        Intent intent = getIntent();
        place = intent.getStringExtra("Place");
        starttime = intent.getStringExtra("StartTime");
        endtime = intent.getStringExtra("EndTime");

        txtPlace.setText(place);
        txtStartTime.setText(starttime);
        txtEndTime.setText(endtime);

        txtPlace.setInputType(InputType.TYPE_NULL);
        txtStartTime.setInputType(InputType.TYPE_NULL);
        txtEndTime.setInputType(InputType.TYPE_NULL);


        toggleButton = findViewById(R.id.toggleBtnNotification);
        toggleButton.setVisibility(View.INVISIBLE);

        mp = MediaPlayer.create(this, R.raw.notification);


        imageButton = findViewById(R.id.btnDeleteThisActivity);
        imageButton.setVisibility(View.INVISIBLE);

        mp.setLooping(true);
        mp.start();

        btnStopAlarm = findViewById(R.id.btnStopAlarm);
        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                finish();
            }
        });



    }
}
