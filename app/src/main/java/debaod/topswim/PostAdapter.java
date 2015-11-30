package debaod.topswim;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by debaod on 4/1/2015.
 */
public class PostAdapter extends ArrayAdapter<PostInfo>
{
    private int resourceId;

    public PostAdapter(Context context, int textViewResourceId, List<PostInfo> objects)
    {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PostInfo postInfo = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView)view.findViewById(R.id.topic_title_text);
            viewHolder.author = (TextView)view.findViewById(R.id.topic_author_text);
            viewHolder.date = (TextView)view.findViewById(R.id.topic_date_text);
            viewHolder.lastPostTime = (TextView)view.findViewById(R.id.lastpost_time_text);
            viewHolder.lastPostAuthror = (TextView)view.findViewById(R.id.lastpost_author_text);
            viewHolder.nums = (TextView)view.findViewById(R.id.num_text);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        String titleString = postInfo.getTitle()+" ";
        String pageNumString = "("+postInfo.getPageNums()+")"+" ";
        String statusString = postInfo.getStatus();
        Spannable titleStringExpand;
        if(statusString.equals("common"))
        {
            titleStringExpand = new SpannableString(titleString + pageNumString);
            titleStringExpand.setSpan(new BulletSpan(16, Color.GREEN), 0, titleString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleStringExpand.setSpan(new ForegroundColorSpan(Color.BLUE), titleString.length(), titleString.length() + pageNumString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else
        {
            titleStringExpand = new SpannableString(titleString + pageNumString + statusString);
            titleStringExpand.setSpan(new BulletSpan(16, Color.GREEN), 0, titleString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleStringExpand.setSpan(new ForegroundColorSpan(Color.BLUE), titleString.length(), titleString.length() + pageNumString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleStringExpand.setSpan(new ForegroundColorSpan(Color.GREEN), titleString.length() + pageNumString.length(), titleString.length() + pageNumString.length() + statusString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        viewHolder.title.setText(titleStringExpand);

        viewHolder.author.setText(postInfo.getAuthor());

        viewHolder.date.setText(postInfo.getPubDate());

        String bracket = "[";
        String comNums = postInfo.getCommentNums();
        String readNums = "/"+postInfo.getReadNums()+"]";
        Spannable numsExpand = new SpannableString(bracket+comNums+readNums);
        numsExpand.setSpan(new ForegroundColorSpan(Color.BLACK),0,bracket.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numsExpand.setSpan(new ForegroundColorSpan(Color.BLUE),bracket.length(),bracket.length()+comNums.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numsExpand.setSpan(new ForegroundColorSpan(Color.BLACK),bracket.length()+comNums.length(),bracket.length()+comNums.length()+readNums.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.nums.setText(numsExpand);

        viewHolder.lastPostTime.setText(postInfo.getLastPostTime());

        viewHolder.lastPostAuthror.setText(postInfo.getLastPostAuthor());

        LinearLayout singleTopicLayout = (LinearLayout)view.findViewById(R.id.single_Topic_layout);
        LinearLayout lastPostLayout = (LinearLayout)view.findViewById(R.id.last_post_layout);

        return view;
    }

    class ViewHolder
    {
        TextView title;
        TextView author;
        TextView date;
        TextView nums;
        TextView lastPostTime;
        TextView lastPostAuthror;
    }
}
