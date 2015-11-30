package debaod.topswim;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostsList extends Fragment {


    public PostsList() {
        // Required empty public constructor
    }
    public  static  final int ENTRY_TOPIC = 1;
    public  static  final int UPDATE_DONE = 2;
    private long lastClickTime = 0;
    //public  String GotoTopicURL;
    private PostAdapter adapter;
    ListView view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);
        view = (ListView)rootView.findViewById(R.id.Post_List);
        adapter = new PostAdapter(myApplication.getAppContext(),R.layout.singletopic, MainPage.postInfoList);
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(System.currentTimeMillis() - lastClickTime < 3000)
                    return;
                lastClickTime = System.currentTimeMillis();
                PostInfo postInfo = MainPage.postInfoList.get(position);
                MainPage.GotoTopicURL = getString(R.string.base_page)+postInfo.getLink();
                final Myhandler handler = new Myhandler();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebFetch.getCommentList(MainPage.GotoTopicURL, 0);
                        Message msg = new Message();
                        msg.what = ENTRY_TOPIC;
                        handler.sendMessage(msg);
                    }
                });
                t.start();
                Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.post_list_toast),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();

            }
        });
        Button prePage = (Button)rootView.findViewById(R.id.btn_pre);
        Button nextPage = (Button)rootView.findViewById(R.id.btn_next);
        Button newTopic = (Button)rootView.findViewById(R.id.btn_new);
        Pattern p = Pattern.compile("-(\\w+)-");
        Matcher m = p.matcher(MainPage.GotoForumURL);
        while(m.find())
        {
            MainPage.fid = m.group(1);
        }
        prePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Myhandler handler = new Myhandler();
                if(MainPage.topicscurrentPage > 1)
                {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WebFetch.getPostList(getString(R.string.base_page) + MainPage.topicsPreviousPageURL);
                            Message msg = new Message();
                            msg.what = UPDATE_DONE;
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
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WebFetch.getPostList(MainPage.GotoForumURL);
                            Message msg = new Message();
                            msg.what = UPDATE_DONE;
                            handler.sendMessage(msg);
                        }
                    });
                    t.start();
                    Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.no_pre_page),Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                    toast.show();
                }
            }
        });
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Myhandler handler = new Myhandler();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebFetch.getPostList(getString(R.string.base_page) + MainPage.topicsNextPageURL);
                        Message msg = new Message();
                        msg.what = UPDATE_DONE;
                        handler.sendMessage(msg);
                    }
                });
                t.start();
                Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.post_list_toast), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });
        newTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(System.currentTimeMillis() - lastClickTime <5000)
                    return;
                lastClickTime = System.currentTimeMillis();
                if(MainActivity.isLogin)
                {
                    EditDlg editDlg = new EditDlg();
                    String postUrl = "http://www.topswim.net/post.php?action=newthread&fid="+MainPage.fid+"&extra=page%3D1&topicsubmit=yes";
                    Bundle bundle = new Bundle();
                    bundle.putString("url",postUrl);
                    bundle.putInt("mode",0);
                    editDlg.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container, editDlg).commit();
                }
                else
                {
                    AlertDialog.Builder netFaildialog = new AlertDialog.Builder(getActivity());
                    netFaildialog.setTitle(R.string.not_login);
                    netFaildialog.setMessage(R.string.jump_to_login);
                    netFaildialog.setCancelable(true);
                    netFaildialog.setPositiveButton("OK",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent);
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
            }
        });

        return rootView;
    }

    private class Myhandler extends Handler
    {
        FragmentManager fragmentManager = getFragmentManager();
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ENTRY_TOPIC:
                    TopicDlg topicDlg = new TopicDlg();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container, topicDlg).commit();
                    break;
                case UPDATE_DONE:
                    adapter.notifyDataSetChanged();
                    view.setSelection(1);
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume()
    {
        MainPage.fragIndex = 2;
        super.onResume();
    }
}
