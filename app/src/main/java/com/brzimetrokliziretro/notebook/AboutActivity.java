package com.brzimetrokliziretro.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvVersionName = (TextView)findViewById(R.id.tv_version_value);
        String version = BuildConfig.VERSION_NAME;
        tvVersionName.setText(version);

        final TextView tvSendMail = (TextView)findViewById(R.id.tv_email);
        tvSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                String emailAdress = tvSendMail.getText().toString();
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdress});
                emailIntent.setType("plain/text");
                startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
