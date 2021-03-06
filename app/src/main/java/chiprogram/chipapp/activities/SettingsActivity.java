package chiprogram.chipapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import chiprogram.chipapp.R;
import chiprogram.chipapp.classes.CHIPUser;
import chiprogram.chipapp.classes.CommonFunctions;

public class SettingsActivity extends BaseActivity {

    private CHIPUser m_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Show the Up button in the action bar.
        setupActionBar();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        m_user = extras.getParcelable(ProfileActivity.ARGUMENT_USER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, HomeActivity.class);

            // add in user to bundle
            Bundle extras = new Bundle();
            extras.putParcelable(ProfileActivity.ARGUMENT_USER, m_user);

            intent.putExtras(extras);

            NavUtils.navigateUpTo(this, intent);
            finish();
            return true;
        } else if (id == R.id.action_settings) {
            CommonFunctions.navigateToSettings(this, m_user);
            return true;
        } else if (id == R.id.action_training) {
            CommonFunctions.navigateToTraining(this, m_user);
            return true;
        } else if (id == R.id.action_profile) {
            CommonFunctions.navigateToProfile(this, m_user);
            return true;
        } else if (id == R.id.action_logout) {
            CommonFunctions.handleLogout(this);
            return true;
        } else if (id == R.id.action_about_chip) {
            CommonFunctions.navigateToAboutCHIP(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
