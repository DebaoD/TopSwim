package debaod.topswim;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BulletSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debaod on 4/3/2015.
 */
public class CommentAdapter extends ArrayAdapter<SingleComment> {

    private int resourceId;
    //private View topLevelWin;
    private Window topWin;
    private PopupWindow myPopupWindow;



    public CommentAdapter(Context context, int textViewResourceId, List<SingleComment> objects,Window tWin,PopupWindow popupWin)
    {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        topWin = tWin;
        myPopupWindow = popupWin;
        myPopupWindow.setOnDismissListener(new poponDismissListener());
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        SingleComment singleComment = getItem(position);
        View view;
        ViewHolder viewHolder;
        final int pos = position;

        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.poster = (TextView)view.findViewById(R.id.comment_author);
            viewHolder.hostTitleText = (TextView)view.findViewById(R.id.single_topic_title_text);
            viewHolder.postTime = (TextView)view.findViewById(R.id.topic_create_time);
            viewHolder.postText = (TextView)view.findViewById(R.id.topic_content);
            viewHolder.quoteText = (TextView)view.findViewById(R.id.quote_content);
            viewHolder.posterFloor = (TextView)view.findViewById(R.id.floorNum);
            viewHolder.hostTile = (TextView)view.findViewById(R.id.topic_title);
            viewHolder.clickMoreButton = (ImageButton)view.findViewById(R.id.click_more_button);
            viewHolder.clickMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    TopicDlg.position = pos;
                    if (myPopupWindow.isShowing()) {

                        myPopupWindow.dismiss();// 关闭
                    } else {

                        myPopupWindow.showAtLocation(topWin.getDecorView(), Gravity.CENTER,0,50);// 显示
                        backgroudAlpha(0.3f);
                        MainPage.fragIndex = 4;
                    }
                }
            });
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(singleComment.getPoster().equals(MainPage.topicHost) && position != 0)
            viewHolder.poster.setText(R.string.topic_host);
        else
            viewHolder.poster.setText(singleComment.getPoster()+"("+singleComment.getPosterTitle()+")");

        viewHolder.hostTitleText.setText(singleComment.getHostTitle());

        String rawString = singleComment.getPostTime();
        int index = rawString.indexOf("20");
        String postTime = rawString.substring(index,index+15);
        viewHolder.postTime.setText(postTime);

        viewHolder.postText.setText(singleComment.getPostText());
        viewHolder.postText.setMovementMethod(LinkMovementMethod.getInstance());
        if(singleComment.getQuoteText()==null)
        {
            viewHolder.quoteText.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.quoteText.setText(singleComment.getQuoteText());
        }

        if(position==0) {
            viewHolder.posterFloor.setText(R.string.topic_host);
            viewHolder.hostTile.setVisibility(View.VISIBLE);
            viewHolder.hostTitleText.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.posterFloor.setText(Integer.toString(position+1)+" "+myApplication.getAppContext().getString(R.string.topic_floor_num));
            viewHolder.hostTile.setVisibility(View.GONE);
            viewHolder.hostTitleText.setVisibility(View.GONE);
        }

        return view;
    }

    class ViewHolder
    {
        TextView poster;
        TextView postTime;
        TextView postText;
        TextView hostTitleText;
        TextView hostTile;
        TextView posterFloor;
        TextView quoteText;
        ImageButton clickMoreButton;
    }

    public void backgroudAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = topWin.getAttributes();
        lp.alpha = bgAlpha;
        topWin.setAttributes(lp);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener
    {
        @Override
        public void onDismiss()
        {
            backgroudAlpha(1);
            MainPage.fragIndex = 3;
        }
    }

}
