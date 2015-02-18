package yellr.net.yellr_android.activities;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.AssignmentsFragment;
import yellr.net.yellr_android.fragments.PostFragment;
import yellr.net.yellr_android.fragments.StoriesFragment;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.notifications.NotificationsIntentService;
import yellr.net.yellr_android.intent_services.profile.ProfileIntentService;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;
import yellr.net.yellr_android.utils.YellrUtils;

public class HomeActivity extends ActionBarActivity implements ActionBar.TabListener, AssignmentsFragment.OnFragmentInteractionListener, StoriesFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    static int CHECK_FOR_NEW_DATA_INTERVAL = 15 * 60 * 1000; // 15 minutes
    final Handler checkNewDataHandler = new Handler();
    Runnable checkNewDatarunable = new Runnable() {

        @Override
        public void run() {
            try{

                Log.d("checkNewDatarunable.run()","Calling all intent services to update HTTP data ...");

                //do your code here

                SharedPreferences sharedPref = getSharedPreferences("clientId", Context.MODE_PRIVATE);
                String clientId = sharedPref.getString("clientId", "");

                Context context = getApplicationContext();

                // Assignments Intent Service
                Intent assignmentsWebIntent = new Intent(context, AssignmentsIntentService.class);
                assignmentsWebIntent.putExtra(AssignmentsIntentService.PARAM_CUID, clientId);
                assignmentsWebIntent.setAction(AssignmentsIntentService.ACTION_GET_ASSIGNMENTS);
                context.startService(assignmentsWebIntent);

                // Notifications Intent Service
                Intent notificationsWebIntent = new Intent(context, NotificationsIntentService.class);
                notificationsWebIntent.putExtra(NotificationsIntentService.PARAM_CUID, clientId);
                notificationsWebIntent.setAction(NotificationsIntentService.ACTION_GET_NOTIFICATIONS);
                context.startService(notificationsWebIntent);

                // Stories Intent Service
                Intent storiesWebIntent = new Intent(context, StoriesIntentService.class);
                storiesWebIntent.putExtra(StoriesIntentService.PARAM_CUID, clientId);
                storiesWebIntent.setAction(StoriesIntentService.ACTION_GET_STORIES);
                context.startService(storiesWebIntent);

                // Profile Intent Service
                Intent profileWebIntent = new Intent(context, ProfileIntentService.class);
                profileWebIntent.putExtra(ProfileIntentService.PARAM_CUID, clientId);
                profileWebIntent.setAction(ProfileIntentService.ACTION_GET_PROFILE);
                context.startService(profileWebIntent);

                //also call the same runnable
                //checkNewDataHandler.postDelayed(this, CHECK_FOR_NEW_DATA_INTERVAL);
            }
            catch (Exception e) {

                Log.d("checkNewDatarunable.run()","ERROR: " + e.toString());

                // TODO: handle exception
            }
            finally{

                Log.d("checkNewDatarunable.run()","re-launching postDelayed");

                //also call the same runnable
                checkNewDataHandler.postDelayed(this, CHECK_FOR_NEW_DATA_INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //
        // See if we have a clientId in shared preferences, and if we
        // don't then create one
        // TODO this actually set's a UUID of there isn't one
        // This should be a seperate check
        String clientId = YellrUtils.getUUID(this);

        //IntentServicesHelper.getAssignments(this,clientId);
        //IntentServicesHelper.getStories(this,clientId);
        //IntentServicesHelper.getNotifications(this,clientId);
        //IntentServicesHelper.getMessages(this,clientId);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        Log.d("HomeActivity.onCreate()","Calling postDelayed on new data handler, delay: " + String.valueOf(CHECK_FOR_NEW_DATA_INTERVAL));

        // fire handler to check for new data
        checkNewDataHandler.postDelayed(checkNewDatarunable, CHECK_FOR_NEW_DATA_INTERVAL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        /*New Story*/
        menu.findItem(R.id.action_new_post).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_pencil_square_o)
                        .colorRes(R.color.black)
                        .actionBarSize()
        );

        /*Notification Icon*/
        menu.findItem(R.id.action_notification).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_bell_o)
                    .colorRes(R.color.black)
                    .actionBarSize()
        );

        /*Messages Icon*/
        menu.findItem(R.id.action_messages).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_envelope_o)
                    .colorRes(R.color.black)
                    .actionBarSize()
        );

        /*Profile Icon*/
        menu.findItem(R.id.action_profile).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_user)
                        .colorRes(R.color.black)
                        .actionBarSize()
        );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_post:
                Intent postIntent = new Intent(this, PostActivity.class);
                postIntent.putExtra(PostFragment.ARG_ASSIGNMENT_ID, 0);
                startActivity(postIntent);
                break;

            case R.id.action_notification:
                Intent notificationsIntent = new Intent(this, NotificationsActivity.class);
                startActivity(notificationsIntent);
                break;

            case R.id.action_profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                break;

            default:
                break;
        }
        return true;
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
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            switch(position){
                case 0:
                    return new AssignmentsFragment();
                case 1:
                    return new StoriesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_fragment_assignments).toUpperCase(l);
                case 1:
                    return getString(R.string.title_fragment_stories).toUpperCase(l);
            }
            return null;
        }
    }


}
