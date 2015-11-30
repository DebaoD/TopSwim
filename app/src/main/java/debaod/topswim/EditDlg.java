package debaod.topswim;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditDlg extends Fragment {

    public  static  final int Topic_SUBMIT_DONE = 1;
    public  static  final int Comment_SUBMIT_DONE = 2;
    private long lastClickTime = 0;


    public EditDlg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_dlg, container, false);
        final EditText titleView = (EditText)rootView.findViewById(R.id.edt_title);
        final EditText contentView = (EditText)rootView.findViewById(R.id.edt_cont);
        final String urlString = (String)getArguments().get("url");
        final int mode = (int)getArguments().get("mode");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(myApplication.getAppContext());
        String enString = sp.getString("backWords", getString(R.string.endstr_default) + android.os.Build.MODEL).replaceAll("\\$\\{model\\}",android.os.Build.MODEL);

        String quoteStr = "";
        if(mode==1)
        {
            titleView.setFocusable(false);
            quoteStr = "\n"+"[quote]"+getString(R.string.edit_comment_from)+"[i]"+getArguments().get("author")+"[/i]"+": "+"\n"+getArguments().get("quote")+"[/quote]";
            contentView.setText(quoteStr);
        }
        final int quoteLen = quoteStr.length();
        final String endingStr = "\n\n--\n" + enString;
        final Button submit = (Button)rootView.findViewById(R.id.edt_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(System.currentTimeMillis() - lastClickTime <3000)
                    return;
                lastClickTime = System.currentTimeMillis();
                if(mode==0&&(titleView.getText().length()==0 ||contentView.getText().length() < 5))
                {
                    AlertDialog.Builder netFaildialog = new AlertDialog.Builder(getActivity());
                    netFaildialog.setTitle(R.string.search_error_title);
                    netFaildialog.setMessage(R.string.search_error_text);
                    netFaildialog.setCancelable(true);
                    netFaildialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    netFaildialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    netFaildialog.show();
                    return;
                }
                else if(mode==1 && (contentView.getText().length() - quoteLen) < 5)
                {
                    AlertDialog.Builder netFaildialog = new AlertDialog.Builder(getActivity());
                    netFaildialog.setTitle(R.string.edit_check_title);
                    netFaildialog.setMessage(R.string.edit_text_check2);
                    netFaildialog.setCancelable(true);
                    netFaildialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    netFaildialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    netFaildialog.show();
                    return;
                }
                final Myhandler handle = new Myhandler();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        String titleStr;
                        if(mode==0)
                        {
                            titleStr = titleView.getText().toString();
                        }
                        else if(mode==1)
                        {
                            titleStr = "";
                        }
                        else
                        {
                            titleStr ="";
                        }
                        Boolean done = WebFetch.postTopic(urlString,titleStr,contentView.getText().toString()+endingStr,mode);
                        if(done)
                        {
                            Message msg = new Message();
                            if(mode==0) {
                                WebFetch.getPostList(MainPage.GotoForumURL);
                                msg.what = Topic_SUBMIT_DONE;
                            }
                            else if(mode==1)
                            {
                                WebFetch.getCommentList(MainPage.GotoTopicURL,0);
                                msg.what = Comment_SUBMIT_DONE;
                            }

                            handle.sendMessage(msg);
                        }
                    }
                });
                t.start();
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
                case Topic_SUBMIT_DONE:
                    //PostsList postsList = new PostsList();
                    //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.replace(R.id.container, postsList).commit();
                    fragmentManager.popBackStackImmediate();
                    fragmentManager.beginTransaction().commit();
                    break;
                case Comment_SUBMIT_DONE:
                    //TopicDlg topicDlg = new TopicDlg();
                    //FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                    //fragmentTransaction1.replace(R.id.container, topicDlg).commit();
                    fragmentManager.popBackStackImmediate();
                    fragmentManager.beginTransaction().commit();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume()
    {
        MainPage.fragIndex = 5;
        super.onResume();
    }


}
