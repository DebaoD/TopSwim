package debaod.topswim;

import android.text.Spannable;
import android.text.Spanned;

/**
 * Created by debaod on 4/3/2015.
 */
public class SingleComment {
    private String poster;
    private String posterTitle;
    private String postTime;
    private Spanned postText;
    private String hostTitle;
    private Spanned quoteText;
    private String uid;
    private String onlyAuthor;


    public String getPoster()
    {
        return poster;
    }
    public void setPoster(String s)
    {
        poster = s;
    }
    public String getPosterTitle()
    {
        return posterTitle;
    }
    public void setPosterTitle(String s)
    {
        posterTitle = s;
    }
    public String getPostTime()
    {
        return postTime;
    }
    public void setPostTime(String s)
    {
        postTime = s;
    }
    public Spanned getPostText()
    {
        return postText;
    }
    public void setPostText(Spanned s)
    {
        postText = s;
    }
    public String getHostTitle()
    {
        return hostTitle;
    }
    public void setHostTitle(String s)
    {
        hostTitle = s;
    }
    public Spanned getQuoteText()
    {
        return quoteText;
    }
    public void setQuoteText(Spanned s)
    {
        quoteText = s;
    }
    public String getUid()
    {
        return uid;
    }
    public void setUid(String s)
    {
        uid = s;
    }
    public String getOnlyAuthor()
    {
        return onlyAuthor;
    }
    public void setOnlyAuthor(String s)
    {
        onlyAuthor = s;
    }
}
