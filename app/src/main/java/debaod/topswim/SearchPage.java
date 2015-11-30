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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPage extends Fragment {


    private long lastClickTime = 0;

    public SearchPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_page, container, false);
        final EditText keywordView = (EditText)rootView.findViewById(R.id.search_keyword);
        final EditText usernameView = (EditText)rootView.findViewById(R.id.search_username);
        final EditText timeRange = (EditText)rootView.findViewById(R.id.search_time_range);

        final Spinner searchSpinner = (Spinner)rootView.findViewById(R.id.search_forum_spinner);
        List<String> forumList = HashAll.getAllForumNameInString();
        forumList.add(0, getString(R.string.forum_allforum));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myApplication.getAppContext(), R.layout.singlespinner, forumList);
        adapter.setDropDownViewResource(R.layout.singlespinner);
        searchSpinner.setAdapter(adapter);

        Button searchBtn = (Button)rootView.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(System.currentTimeMillis() - lastClickTime <3000)
                    return;
                lastClickTime = System.currentTimeMillis();
                if(!MainActivity.isLogin)
                {
                    AlertDialog.Builder netFaildialog = new AlertDialog.Builder(getActivity());
                    netFaildialog.setTitle(R.string.not_login);
                    netFaildialog.setMessage(R.string.jump_to_login);
                    netFaildialog.setCancelable(true);
                    netFaildialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                    return;
                }
                if(keywordView.getText().length()==0 && usernameView.getText().length()==0)
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
                else
                {
                    int spinnerId = searchSpinner.getSelectedItemPosition();
                    final String forumId = HashAll.getForumId(spinnerId);
                    final Myhandler myhandler = new Myhandler();
                    Thread t = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //Boolean done = WebFetch.postSearch(keywordView.getText().toString(), usernameView.getText().toString());
                            int reCode = WebFetch.postSearch(keywordView.getText().toString(),usernameView.getText().toString(),
                                    timeRange.getText().toString(), forumId);
                            Message msg = new Message();
                            msg.what = reCode;
                            myhandler.sendMessage(msg);
                        }
                    });
                    t.start();
                    Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.post_list_toast), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
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
                case WebFetch.SEARCH_SUCCESS:
                    SearchResPage searchResPage = new SearchResPage();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container, searchResPage).commit();
                    break;
                case WebFetch.SEARCH_NO_FOUND:
                    AlertDialog.Builder netFaildialog = new AlertDialog.Builder(getActivity());
                    netFaildialog.setTitle(R.string.search_no_found_title);
                    netFaildialog.setMessage(R.string.search_no_found_text);
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
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume()
    {
        MainPage.fragIndex = 6;
        super.onResume();
    }


}
