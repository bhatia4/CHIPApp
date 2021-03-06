package chiprogram.chipapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import chiprogram.chipapp.R;
import chiprogram.chipapp.database.CHIPLoaderSQL;
import chiprogram.chipapp.classes.CHIPUser;
import chiprogram.chipapp.classes.CommonFunctions;

public class ProfileActivity extends Activity {

    public static final String ARGUMENT_USER = "chiprogram.chipapp.ARGUMENT_USER";

    public static final int PROFILE_EDIT = 1;
    public static final int EDIT_SUCCESSFUL = 1;
    public static final int EDIT_FAILED = 1;

    private CHIPUser m_user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Show the Up button in the action bar.
        setupActionBar();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        m_user = extras.getParcelable(ARGUMENT_USER);

        // Update profile for current user
        this.setTitle(getString(R.string.title_activity_profile) + " " + m_user.get_firstName());

        TextView emailView = (TextView) findViewById(R.id.prof_email);
        TextView firstNameView = (TextView) findViewById(R.id.prof_firstName);
        TextView lastNameView = (TextView) findViewById(R.id.prof_lastName);
        TextView roleView = (TextView) findViewById(R.id.prof_role);

        emailView.setText(m_user.get_email());
        firstNameView.setText(m_user.get_firstName());
        lastNameView.setText(m_user.get_lastName());
        roleView.setText(m_user.get_role());
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return super.onCreateOptionsMenu(menu);
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

            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_settings) {
            CommonFunctions.navigateToSettings(this, m_user);
            return true;
        } else if (id == R.id.action_training) {
            CommonFunctions.navigateToTraining(this, m_user);
            return true;
        } else if (id == R.id.action_discussion) {
            CommonFunctions.navigateToDiscussion(this, m_user);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == PROFILE_EDIT) {
            Bundle extras = data.getExtras();

            if (resultCode == EDIT_SUCCESSFUL) {
                Intent intent = getIntent();
                Bundle rootExtras = intent.getExtras();
                rootExtras.putParcelable(ARGUMENT_USER, extras.getParcelable(ARGUMENT_USER));
                intent.putExtras(rootExtras);
                setIntent(intent);
                recreate();
            }
        }
    }
}
