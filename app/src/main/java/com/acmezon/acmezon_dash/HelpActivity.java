package com.acmezon.acmezon_dash;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView email = (TextView) findViewById(R.id.corporate_mail);
        TextView siteLink = (TextView) findViewById(R.id.site_link);

        if (Build.VERSION.SDK_INT > 23) {
            email.setText(Html.fromHtml(String.format("<a href=\"mailto:%s\">%s</a>",
                    getString(R.string.corporate_mail),
                    getString(R.string.corporate_mail)), Html.FROM_HTML_MODE_LEGACY));

            siteLink.setText(Html.fromHtml(String.format("<a href=\"%s\">%s</a>",
                    getString(R.string.site_link),
                    getString(R.string.site_link)), Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            email.setText(Html.fromHtml(String.format("<a href=\"mailto:%s\">%s</a>",
                    getString(R.string.corporate_mail),
                    getString(R.string.corporate_mail))));

            //noinspection deprecation
            siteLink.setText(Html.fromHtml(String.format("<a href=\"%s\">%s</a>",
                    getString(R.string.site_link),
                    getString(R.string.site_link))));
        }

        email.setMovementMethod(LinkMovementMethod.getInstance());
        siteLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
