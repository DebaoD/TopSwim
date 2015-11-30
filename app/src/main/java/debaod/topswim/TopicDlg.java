package debaod.topswim;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopicDlg extends Fragment{

    PullToRefreshListView listView;
    PopupWindow myPopupWindow;
    public  static  final int REFRESH_DONE = 1;
    public  static  final int LOAD_DONE = 4;
    public  static  final int ONLY_AUTHRO = 3;
    public  static  final int GO_SPACE = 2;
    public  static  final int LOAD_ACTION = 1;
    public  static  final int REFRESH_ACTION = 0;
    public static int position;
    private long lastClickTime = 0;
    public  List<SingleComment> backupCommentList = new ArrayList<>();
    public  Boolean isOnlyAState = false;
    private String[] moreList =
            {
                    myApplication.getAppContext().getString(R.string.popup_title),
                    myApplication.getAppContext().getString(R.string.popup_comment),
                    myApplication.getAppContext().getString(R.string.popup_email),
                    myApplication.getAppContext().getString(R.string.popup_author_detail),
                    myApplication.getAppContext().getString(R.string.popup_author_filter)
            };
    CommentAdapter adapter;

    public TopicDlg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_topic_dlg, container, false);
        listView = (PullToRefreshListView)rootView.findViewById(R.id.topic_dlg);
        //View topLevelWin = getActivity().getWindow().getDecorView();
        Window topWin = getActivity().getWindow();
        iniPopupWindow();
        adapter = new CommentAdapter(myApplication.getAppContext(), R.layout.singlecomment, MainPage.commentList,topWin,myPopupWindow);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Myhandler handler = new Myhandler();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebFetch.getCommentList(getString(R.string.base_page)+MainPage.GotoTopicURL,REFRESH_ACTION);
                        Message msg = new Message();
                        msg.what = REFRESH_DONE;
                        handler.sendMessage(msg);
                    }
                });
                t.start();
                Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.topic_refresh),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
            }
            @Override
            public void onLoad(){
                if(MainPage.isLastPageofTopic)
                {
                    Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.topic_load_last_page),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                    toast.show();
                    return;
                }
                final Myhandler handler = new Myhandler();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebFetch.getCommentList(getString(R.string.base_page)+MainPage.NextPageURL,LOAD_ACTION);
                        Message msg = new Message();
                        msg.what = LOAD_DONE;
                        handler.sendMessage(msg);
                    }
                });
                t.start();
                Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.topic_load),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
            }
        });
        return rootView;
    }

    @Override
    public void onResume()
    {
        MainPage.fragIndex = 3;
        super.onResume();
    }

    private class Myhandler extends Handler
    {
        FragmentManager fragmentManager = getFragmentManager();
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_DONE:
                    adapter.notifyDataSetChanged();
                    listView.onRefreshComplete();
                    break;
                case LOAD_DONE:
                    adapter.notifyDataSetChanged();
                    listView.onLoadComplete();
                    break;
                case GO_SPACE:
                    space authorDetail = new space();
                    myPopupWindow.dismiss();
                    FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                    fragmentTransaction1.addToBackStack(null);
                    fragmentTransaction1.replace(R.id.container, authorDetail).commit();
                    break;
                case ONLY_AUTHRO:
                    myPopupWindow.dismiss();
                    adapter.notifyDataSetChanged();
                    listView.onRefreshComplete();
                    isOnlyAState = true;
                    break;
                default:
                    break;
            }
        }
    }

    private class popWinAdapter extends ArrayAdapter<String>
    {
        private int resourceId;

        public popWinAdapter(Context context, int textViewResourceId, String[] items) {
            super(context, textViewResourceId, items);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            String action = getItem(position);
            View view;
            final int posNum = position;

            if(convertView == null)
            {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            }
            else
            {
                view = convertView;
            }
            Button textView = (Button)view.findViewById(R.id.click_detail_item);
            textView.setText(action);
            if(position==0)
            {
                textView.setTextColor(0xFF2666AC);
                textView.setTextSize(20);
                textView.setBackground(getResources().getDrawable(R.drawable.threebutton1));
                //textView.setBackgroundColor(Color.WHITE);
            }
            if(position==4 && isOnlyAState)
            {
                textView.setText(getString(R.string.back_topic));
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(System.currentTimeMillis() - lastClickTime <3000)
                        return;
                    lastClickTime = System.currentTimeMillis();
                    Toast.makeText(myApplication.getAppContext(),
                            moreList[posNum],
                            Toast.LENGTH_SHORT).show();
                    final Myhandler handler = new Myhandler();
                    switch (posNum) {
                        case 1:
                            if (!MainActivity.isLogin)
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
                                break;
                            }
                            EditDlg editDlg = new EditDlg();
                            Bundle bundle = new Bundle();
                            Pattern p2 = Pattern.compile("thread-(\\w+)-1");
                            Matcher m2 = p2.matcher(MainPage.GotoTopicURL);
                            String tid = "";
                            while (m2.find()) {
                                tid = m2.group(1);
                            }
                            String commStr = getString(R.string.base_page) + "/post.php?action=reply&fid=" + MainPage.fid + "&tid=" + tid + "&extra=page%3D1&replysubmit=yes";
                            Spanned quoteText = MainPage.commentList.get(TopicDlg.position).getPostText();
                            String quoteAuthor = MainPage.commentList.get(TopicDlg.position).getPoster();
                            bundle.putString("author", quoteAuthor);
                            bundle.putString("url", commStr);
                            bundle.putInt("mode", 1);
                            bundle.putCharSequence("quote", quoteText);
                            editDlg.setArguments(bundle);
                            myPopupWindow.dismiss();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                            fragmentTransaction1.addToBackStack(null);
                            fragmentTransaction1.replace(R.id.container, editDlg).commit();
                            break;
                        case 2:
                            break;
                        case 3:
                            Thread space_t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    WebFetch.GetSpaceInfo(MainPage.commentList.get(TopicDlg.position).getUid());
                                    Message msg = new Message();
                                    msg.what = GO_SPACE;
                                    handler.sendMessage(msg);
                                }
                            });
                            space_t.start();
                            break;
                        case 4:
                            if (!isOnlyAState) {
                                backupCommentList.clear();
                                for (int i = 0; i < MainPage.commentList.size(); i++) {
                                    backupCommentList.add(MainPage.commentList.get(i));
                                }
                                Thread onlyA_t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        WebFetch.getCommentList(getString(R.string.base_page) + MainPage.commentList.get(TopicDlg.position).getOnlyAuthor(),REFRESH_ACTION);
                                        Message msg = new Message();
                                        msg.what = ONLY_AUTHRO;
                                        handler.sendMessage(msg);
                                    }
                                });
                                onlyA_t.start();
                                break;
                            } else {
                                MainPage.commentList.clear();
                                for (int i = 0; i < backupCommentList.size(); i++) {
                                    MainPage.commentList.add(backupCommentList.get(i));
                                }
                                myPopupWindow.dismiss();
                                adapter.notifyDataSetChanged();
                                listView.onRefreshComplete();
                                break;
                            }
                        default:
                            break;
                    }
                }
            });
            return view;
        }

    }

    private void iniPopupWindow()
    {
        LayoutInflater inflater = (LayoutInflater)myApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.clickmore_detail_popupwindow,null);
        ListView popUpList = (ListView)layout.findViewById(R.id.clickmore_listview);
        myPopupWindow = new PopupWindow(layout);
        myPopupWindow.setFocusable(true);

        popUpList.setAdapter(new popWinAdapter(myApplication.getAppContext(),R.layout.clickmore_detail_item,moreList));
        /*popUpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position)
                {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
                Toast.makeText(myApplication.getAppContext(),
                        moreList[position],
                        Toast.LENGTH_LONG).show();
            }
        });*/
        popUpList.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        //myPopupWindow.setWidth(getActivity().getWindow().getDecorView().getWidth()*4/5);
        myPopupWindow.setWidth(popUpList.getMeasuredWidth());
        myPopupWindow.setHeight((popUpList.getMeasuredHeight() + 10)
                * (moreList.length));

        //myPopupWindow.setBackgroundDrawable(myApplication.getAppContext().getResources().getDrawable(R.drawable.bg_popup));
        myPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        myPopupWindow.setOutsideTouchable(true);

    }
}
