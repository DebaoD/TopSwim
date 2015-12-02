package debaod.topswim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainPage extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static String GotoForumURL;
    public static String GotoTopicURL;
    public static String fid = "4";
    public static String topicsNextPageURL;
    public static String NextPageURL;
    public static boolean isLastPageofTopic = false;
    public static String topicsPreviousPageURL;
    public static String PreviousPageURL;
    public static int currentPage;  // the page of a topic
    public static int topicscurrentPage;  //the page of a forum.
    public static String searchResNextPage;
    public static String searchResPrePage;
    public static String searchResCurrPage;
    private static final int UPDATE_SPACE = 1;
    public static String topicHost;
    public static List<PostInfo> postInfoList = new ArrayList<PostInfo>();
    public static List<SingleComment> commentList = new ArrayList<>();
    public static List<SingleSchResult> schResList = new ArrayList<>();
    private long exitTime = 0;
    public static int fragIndex = 0;//0:mainpage.1:Placeholder.2:postlist.3:topicdlg.4:popuplist.5:editdlg.6:search
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    //private ExpandableListView expandableListView_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.title_section_home);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(MainPage.fragIndex==1)
            {
                if ((System.currentTimeMillis() - exitTime) > 2000)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.back_logoff_check), Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                }
                else
                {
                    if (MainActivity.isLogin)
                    {
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                WebFetch.logoff();
                            }
                        });
                        t.start();
                    }
                    System.exit(0);
                }
                return true;
            }
            else if(MainPage.fragIndex==4)
            {
                MotionEvent me = MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN,20,20,0);
                onTouchEvent(me);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.addToBackStack(null);
        mTitle = getString(R.string.title_section_home);
        final Handler handler = new myNaviHandler();
        switch (position)
        {
            case 0:
                mTitle = getString(R.string.title_section_home);
                ActionBar actionBar = getSupportActionBar();
                fragmentTransaction
                        .replace(R.id.container, PlaceholderFragment.newInstance(position, fragmentManager,actionBar))
                        .commit();
                break;
            case 1:
                mTitle = getString(R.string.title_section_mailbox);
                MailBox mailBox = new MailBox();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container, mailBox).commit();
                break;
            case 2:
                mTitle = getString(R.string.title_section_search);
                SearchPage searchPage = new SearchPage();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container, searchPage).commit();
                break;
            case 3:
                mTitle = getString(R.string.title_section_controlpanel);
                Preference preference = new Preference();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container, preference).commit();
                break;
            case 4:
                mTitle = getString(R.string.title_section_personalcenter);
                fragmentTransaction.addToBackStack(null);
                if(MainActivity.isLogin)
                {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WebFetch.getLogoffHashAndUid();
                            WebFetch.GetSpaceInfo(WebFetch.uuid);
                            Message msg = new Message();
                            msg.what = UPDATE_SPACE;
                            handler.sendMessage(msg);
                        }
                    });
                    t.start();
                    Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.post_list_toast),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                    toast.show();
                }
                else
                {
                    AlertDialog.Builder netFaildialog = new AlertDialog.Builder(MainPage.this);
                    netFaildialog.setTitle(R.string.not_login);
                    netFaildialog.setMessage(R.string.jump_to_login);
                    netFaildialog.setCancelable(true);
                    netFaildialog.setPositiveButton("OK",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent = new Intent();
                            intent.setClass(MainPage.this, MainActivity.class);
                            MainPage.this.startActivity(intent);
                        }
                    });
                    netFaildialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    netFaildialog.show();
                }
                break;
            case 5:
                if(MainActivity.isLogin)
                {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WebFetch.logoff();
                        }
                    });
                    t.start();
                }
                finish();
            default:
                break;
        }
    }

    private  class myNaviHandler extends Handler
    {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UPDATE_SPACE:
                    space sp = new space();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container, sp).commit();
                    break;
                default:
                    break;
            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section_home);
                break;
            case 2:
                mTitle = getString(R.string.title_section_mailbox);
                break;
            case 3:
                mTitle = getString(R.string.title_section_search);
                break;
            case 4:
                mTitle = getString(R.string.title_section_controlpanel);
                break;
            case 5:
                mTitle = getString(R.string.title_section_personalcenter);
                break;
            case 6:
                mTitle = getString(R.string.title_section_logoff);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.main_page, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(getFragmentManager().getBackStackEntryCount()==0)
        {
            finish();
        }

    }*/
/*
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static FragmentManager fragmentManager;
        public int[] forumTypes = HashAll.getAllForumType();
        public int[][] nameofForums = HashAll.getAllForumName();
        public final static int UPDATE_DONE = 1;
        private long lastClickTime = 0;
        private static ActionBar actionBar;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, FragmentManager fManager, ActionBar acBar) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            fragmentManager = fManager;
            actionBar = acBar;
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           View rootView = inflater.inflate(R.layout.fragment_main_page, container, false);

            final BaseExpandableListAdapter adapter = new BaseExpandableListAdapter()
            {

                @Override
                public int getGroupCount() {
                    // TODO Auto-generated method stub
                    return forumTypes.length;
                }

                @Override
                public Object getGroup(int groupPosition) {
                    // TODO Auto-generated method stub
                    return forumTypes[groupPosition];
                }

                @Override
                public long getGroupId(int groupPosition) {
                    // TODO Auto-generated method stub
                    return groupPosition;
                }

                @Override
                public int getChildrenCount(int groupPosition) {
                    // TODO Auto-generated method stub
                    return nameofForums[groupPosition].length;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    // TODO Auto-generated method stub
                    return nameofForums[groupPosition][childPosition];
                }

                @Override
                public long getChildId(int groupPosition, int childPosition) {
                    // TODO Auto-generated method stub
                    return childPosition;
                }

                @Override
                public boolean hasStableIds() {
                    // TODO Auto-generated method stub
                    return true;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
                {
                    return getGenericViewGroup(forumTypes[groupPosition]);
                }

                @Override
                public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
                {
                    return getGenericViewChild(nameofForums[groupPosition][childPosition]);
                }

                @Override
                public boolean isChildSelectable(int groupPosition, int childPosition) {
                    // TODO Auto-generated method stub
                    return true;
                }

                public TextView getGenericViewGroup(int s)
                {
                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, DensityUtil.dip2px(myApplication.getAppContext(),40));
                    TextView text = new TextView(myApplication.getAppContext());
                    text.setLayoutParams(lp);
                    text.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    text.setPadding(90, 0, 0, 0);
                    text.setText(s);
                    text.setTextColor(Color.rgb(0,0,0));
                    return text;
                }

                public TextView getGenericViewChild(int s)
                {
                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, DensityUtil.dip2px(myApplication.getAppContext(),50));
                    TextView text = new TextView(myApplication.getAppContext());
                    text.setLayoutParams(lp);
                    text.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
                    text.setPadding(36,0,0,0);
                    text.setText(s);
                    text.setTextSize(DensityUtil.dip2px(myApplication.getAppContext(),5));
                    text.setTextColor(Color.rgb(58,75,231));
                    return text;
                }

            };


            final ExpandableListView expandableListView_home = (ExpandableListView)rootView.findViewById(R.id.area_view);
            expandableListView_home.setAdapter(adapter);
            expandableListView_home.setChildDivider(this.getResources().getDrawable(R.color.divider));
            expandableListView_home.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
            {
                @Override
                public void onGroupExpand(int groupPosition)
                {
                    for(int i=0;i< forumTypes.length;i++)
                    {
                        if (groupPosition != i)
                        {
                            expandableListView_home.collapseGroup(i);
                        }
                    }
                }
            });
            expandableListView_home.expandGroup(0);

            expandableListView_home.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
            {

                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
                {
                    if((System.currentTimeMillis()-lastClickTime)<5000)
                        return true;
                    lastClickTime = System.currentTimeMillis();
                    GotoForumURL = HashAll.getForumURL(groupPosition, childPosition);
                    actionBar.setTitle(nameofForums[groupPosition][childPosition]);
                    final Myhandler handler = new Myhandler();
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WebFetch.getPostList(GotoForumURL);
                            Message message = new Message();
                            message.what = UPDATE_DONE;
                            handler.sendMessage(message);
                        }
                    });
                    t.start();
                    Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.post_list_toast),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                    toast.show();
                    return true;
                }
            }
            );
            return rootView;
        }

        private static class Myhandler extends Handler
        {
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case UPDATE_DONE:
                        PostsList postsList = new PostsList();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.container, postsList).commit();
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainPage) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));

        }

        @Override
        public void onResume()
        {
            MainPage.fragIndex = 1;
            super.onResume();
        }
    }


}
