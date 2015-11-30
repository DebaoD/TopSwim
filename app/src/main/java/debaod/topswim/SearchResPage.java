package debaod.topswim;


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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResPage extends Fragment {

    private long lastClickTime = 0;
    public  static  final int ENTRY_TOPIC = 1;
    public  static  final int UPDATE_DONE = 2;
    SearchResAdapter adapter;
    ListView view;

    public SearchResPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_res_page, container, false);
        view = (ListView)rootView.findViewById(R.id.search_res_list);
        Button prePageBtn = (Button)rootView.findViewById(R.id.search_res_pre);
        Button nextPageBtn = (Button)rootView.findViewById(R.id.search_res_next);
        adapter = new SearchResAdapter(myApplication.getAppContext(), R.layout.single_sch_res, MainPage.schResList);
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (System.currentTimeMillis() - lastClickTime < 3000)
                    return;
                lastClickTime = System.currentTimeMillis();
                SingleSchResult singleSchResult = MainPage.schResList.get(position);
                MainPage.GotoTopicURL = getString(R.string.base_page) + singleSchResult.getLink();

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
                Toast toast = Toast.makeText(myApplication.getAppContext(), getString(R.string.post_list_toast), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });

        prePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Myhandler handler = new Myhandler();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebFetch.updateSearchRes(MainPage.searchResPrePage);
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

        nextPageBtn.setOnClickListener(new View.OnClickListener()
        {
           @Override
           public void onClick(View view) {
               final Myhandler handler = new Myhandler();
               Thread t = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       WebFetch.updateSearchRes(MainPage.searchResNextPage);
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


}
