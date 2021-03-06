package chiprogram.chipapp.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import java.util.Locale;

import chiprogram.chipapp.classes.Assessment;
import chiprogram.chipapp.dialogs.ConfirmationDialog;
import chiprogram.chipapp.fragments.AssessmentFragmentTab;
import chiprogram.chipapp.fragments.ContentListFragmentTab;
import chiprogram.chipapp.fragments.NavItemListFragmentTab;
import chiprogram.chipapp.R;
import chiprogram.chipapp.database.CHIPLoaderSQL;
import chiprogram.chipapp.classes.CHIPUser;
import chiprogram.chipapp.classes.CommonFunctions;
import chiprogram.chipapp.classes.Content;
import chiprogram.chipapp.classes.NavItem;
import chiprogram.chipapp.webview.WebViewActivity;

public class NavItemTabsActivity extends BaseActivity implements ActionBar.TabListener,
        NavItemListFragmentTab.Callbacks,
        ContentListFragmentTab.Callbacks,
        ConfirmationDialog.ConfirmationDialogListener,
        View.OnClickListener {

    public enum TabType {
        CONTENT,
        CHILDREN,
        ASSESSMENTS
    }

    public static final String CURRENT_ID = "chiprogram.chipapp.CURRENT_ID";

    private static final String CONFIRM_RETAKE_ASSESSMENT = "fragment_confirm_retake_assessment";

    public final static int REQUEST_ASSESSMENT = 1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private FragmentManager m_fragmentManager;

    private CHIPUser m_user;
    private NavItem m_navItem;
    String m_currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_item_tabs);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        m_fragmentManager = getFragmentManager();

        m_user = extras.getParcelable(ProfileActivity.ARGUMENT_USER);
        m_currentId = extras.getString(CURRENT_ID);
        m_navItem = CHIPLoaderSQL.getInstance().getNavItem(m_currentId, this);

        CHIPLoaderSQL.getInstance().setMostRecentNavItem(this, m_user.get_id(), m_currentId);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // TODO: make sure this is the right way to handle a null nav item
        if (m_navItem != null) {
            this.setTitle(m_navItem.toString());

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            mSectionsPagerAdapter.hasContent = m_navItem.hasContent();
            mSectionsPagerAdapter.hasChildren = m_navItem.hasChildren();
            mSectionsPagerAdapter.hasAssessments = m_navItem.hasAssessments();

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            // When swiping between different sections, select the corresponding
            // tab. We can also use ActionBar.Tab#select() to do this if we have
            // a reference to the Tab.
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            });

            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent;
            // add in user to bundle
            Bundle extras = new Bundle();
            extras.putParcelable(ProfileActivity.ARGUMENT_USER, m_user);
            if (m_navItem.getParentId() == null) {
                intent = new Intent(this, ModuleListActivity.class);
            } else {
                intent = new Intent(this, NavItemTabsActivity.class);
                NavItem parent = CHIPLoaderSQL.getInstance().getNavItem(m_navItem.getParentId(), this);
                extras.putString(NavItemTabsActivity.CURRENT_ID, parent.getId());
            }

            intent.putExtras(extras);

            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_settings) {
            CommonFunctions.navigateToSettings(this, m_user);
            return true;
        } else if (id == R.id.action_profile) {
            CommonFunctions.navigateToProfile(this, m_user);
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onItemSelected(boolean isNavItem, int index) {
        if (isNavItem) {
            NavItem chosenNavItem = m_navItem.getChild(index);

            Intent intent = new Intent(this, NavItemTabsActivity.class);

            // add in user to bundle
            Bundle extras = new Bundle();
            extras.putParcelable(ProfileActivity.ARGUMENT_USER, m_user);
            extras.putString(NavItemTabsActivity.CURRENT_ID, chosenNavItem.getId());

            intent.putExtras(extras);

            startActivity(intent);
        } else {
            // branch based on type of content
            Content chosenContent = m_navItem.getContent(index);

            switch (chosenContent.getType()) {
                case YOUTUBE_VIDEO:
                    Intent intent1 = YouTubeStandalonePlayer.createVideoIntent(this, "AIzaSyDnglrdVhIpcrMuQ6Kjw8E2nniSUyfs44Y",
                            CommonFunctions.getYouTubeVideoID(chosenContent.getLink()), 0, true, false);
                    startActivity(intent1);
                    break;
                case PDF_LINK:
                    runWebViewActivity(chosenContent);
                    break;
                case PPT_LINK:
                    runWebViewActivity(chosenContent);
                    break;
            }
        }
    }

    private void runWebViewActivity(Content chosenContent) {
        Intent intent2 = new Intent(this, WebViewActivity.class);

        // add in user to bundle
        Bundle extras = new Bundle();
        extras.putParcelable(ProfileActivity.ARGUMENT_USER, m_user);
        extras.putString(WebViewActivity.CONTENT_ID, chosenContent.getId());
        intent2.putExtras(extras);
        startActivity(intent2);
    }

    @Override
    public void onClick(View view) {
        if (view.getClass() == TableRow.class) {
            // check if they have a passing grade for this assessment,
            // if so, confirm they actually want to retake the assessment
            String assessmentId = (String) view.getTag();
            int userScore = CHIPLoaderSQL.getInstance().getAssessmentScore(this, assessmentId, m_user.get_email());
            Assessment assessmentClicked = CHIPLoaderSQL.getInstance().getAssessment(assessmentId);
            double passingPercent = assessmentClicked.getPassingPercent();
            int numQuestions = assessmentClicked.getNumQuestions();

            if (userScore != -1 && ((double)userScore) * 100 / numQuestions >= passingPercent) {
                FragmentTransaction fragmentTransaction = m_fragmentManager.beginTransaction();

                ConfirmationDialog confirmDeleteCompletedTasksDialog =
                        new ConfirmationDialog();

                Bundle args = new Bundle();
                args.putString(ConfirmationDialog.ARG_MESSAGE_TEXT, getString(R.string.confirm_retake_assessment));
                args.putString(ConfirmationDialog.ARG_TAG, CONFIRM_RETAKE_ASSESSMENT + assessmentId);

                confirmDeleteCompletedTasksDialog.setArguments(args);

                fragmentTransaction.add(confirmDeleteCompletedTasksDialog, CONFIRM_RETAKE_ASSESSMENT);
                fragmentTransaction.commit();
            } else {
                onFinishConfirmationDialog(CONFIRM_RETAKE_ASSESSMENT + assessmentId);
            }
        }
    }

    public void onFinishConfirmationDialog(String tag) {
        if (tag.startsWith(CONFIRM_RETAKE_ASSESSMENT)) {
            String assessmentId = tag.substring(CONFIRM_RETAKE_ASSESSMENT.length());

            Intent intent = new Intent(this, AssessmentActivity.class);

            // add in user to bundle
            Bundle extras = new Bundle();
            extras.putParcelable(ProfileActivity.ARGUMENT_USER, m_user);
            extras.putString(AssessmentActivity.ASSESSMENT_ID, assessmentId);

            intent.putExtras(extras);

            startActivityForResult(intent, REQUEST_ASSESSMENT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();

            if (requestCode == REQUEST_ASSESSMENT) {
                // score is held in resultCode (-1 == cancel)
                if (resultCode == -1) {

                } else {
                    // TODO: update score in database

                    // TODO: update assessments fragment
                    this.recreate();
                }
            }
        }
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public boolean hasContent;
        public boolean hasChildren;
        public boolean hasAssessments;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            hasContent = false;
            hasChildren = false;
            hasAssessments = false;
        }

        private TabType getTabType(int position) {
            switch(position) {
                case 0:
                    if (hasContent) {
                        return TabType.CONTENT;
                    } else if (hasChildren) {
                        return TabType.CHILDREN;
                    } else if (hasAssessments) {
                        return TabType.ASSESSMENTS;
                    } else {
                        return null;
                    }
                case 1:
                    if (hasContent) {
                        if (hasChildren) {
                            return TabType.CHILDREN;
                        } else if (hasAssessments) {
                            return TabType.ASSESSMENTS;
                        } else {
                            return null;
                        }
                    } else if (hasChildren) {
                        if (hasAssessments) {
                            return TabType.ASSESSMENTS;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                case 2:
                    if (hasContent && hasChildren && hasAssessments) {
                        return TabType.ASSESSMENTS;
                    } else {
                        return null;
                    }
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch(getTabType(position)) {
                case CONTENT:
                    return ContentListFragmentTab.newInstance(m_user, m_currentId);
                case CHILDREN:
                    return NavItemListFragmentTab.newInstance(m_user, m_currentId);
                case ASSESSMENTS:
                    return AssessmentFragmentTab.newInstance(m_user, m_currentId);
            }
            return null;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (hasContent) {
                ++count;
            }
            if (hasChildren) {
                ++count;
            }
            if (hasAssessments) {
                ++count;
            }
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch(getTabType(position)) {
                case CONTENT:
                    return getString(R.string.title_section1).toUpperCase(l);
                case CHILDREN:
                    if (m_navItem.getChildrenName() == null) {
                        return getString(R.string.title_section2).toUpperCase(l);
                        /*
                    } else if (m_navItem.getChildrenName().equals("Modules")) { // TODO: figure out how to make this more general
                        return getString(R.string.title_section2_modules).toUpperCase(l);
                    } else if (m_navItem.getChildrenName().equals("Chapters")) {
                        return getString(R.string.title_section2_chapters).toUpperCase(l);
                    } else if (m_navItem.getChildrenName().equals("Sessions")) {
                        return getString(R.string.title_section2_sessions).toUpperCase(l);
                    } else {
                        return m_navItem.getChildrenName().toUpperCase(l);
                        */
                    } else {
                        return (m_navItem.getChildrenName().toUpperCase(l));
                    }
                case ASSESSMENTS:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
}
