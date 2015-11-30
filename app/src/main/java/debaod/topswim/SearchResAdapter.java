package debaod.topswim;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by debaod on 11/4/2015.
 */
public class SearchResAdapter extends ArrayAdapter<SingleSchResult>
{
    private int resourceId;

    public SearchResAdapter(Context context, int textViewResourceId, List<SingleSchResult> objects)
    {
        super(context,  textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        SingleSchResult singleSchResult = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView)view.findViewById(R.id.search_res_title);
            viewHolder.author = (TextView)view.findViewById(R.id.search_res_author);
            viewHolder.forum = (TextView)view.findViewById(R.id.search_res_forum);
            viewHolder.lastPostTime = (TextView)view.findViewById(R.id.search_res_time);
            viewHolder.nums = (TextView)view.findViewById(R.id.search_res_num);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        String strTitle = singleSchResult.getTitle();
        String strAuthor = singleSchResult.getAuthor();
        String strForum = singleSchResult.getForumName();
        String strReadNum = "/"+singleSchResult.getReadNum()+"]";
        String strCommentNum = singleSchResult.getCommentNum();
        String strTime = singleSchResult.getLastCommentTime();

        viewHolder.title.setText(strTitle);
        viewHolder.author.setText(strAuthor);
        viewHolder.forum.setText(strForum);
        viewHolder.lastPostTime.setText(strTime);


        String bracket = "[";
        Spannable numsExpand = new SpannableString(bracket+strCommentNum+strReadNum);
        numsExpand.setSpan(new ForegroundColorSpan(Color.BLACK), 0, bracket.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numsExpand.setSpan(new ForegroundColorSpan(Color.BLUE), bracket.length(), bracket.length() + strCommentNum.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numsExpand.setSpan(new ForegroundColorSpan(Color.BLACK), bracket.length() + strCommentNum.length(), bracket.length() + strCommentNum.length() + strReadNum.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.nums.setText(numsExpand);

        return view;
    }



    class ViewHolder
    {
        TextView title;
        TextView author;
        TextView forum;
        TextView nums;
        TextView lastPostTime;
    }
}
