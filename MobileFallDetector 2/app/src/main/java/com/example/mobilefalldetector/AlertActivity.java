package com.example.mobilefalldetector;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlertActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button falseAlarmButton;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        timerTextView = findViewById(R.id.timer);
        falseAlarmButton = findViewById(R.id.false_alarm_button);

        // Start the countdown timer for 30 seconds
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                timerTextView.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                // Timer finished, return to the main activity
                Intent intent = new Intent(AlertActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }.start();

        falseAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop the timer and return to the main activity
                countDownTimer.cancel();
                Intent intent = new Intent(AlertActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the countdown timer if the activity is destroyed
        countDownTimer.cancel();
    }
}