package debaod.topswim;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.http.HttpResponseCache;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Attributes;
import org.xml.sax.XMLReader;


/**
 * Created by debaod on 4/10/2015.
 */
public class WebFetch {
    public static String hashcode;//loginhash
    public static String logoutHash;//hash after login
    public static String uuid;
    public static String strCookies;
    public static CookieStore cookieStore = new BasicCookieStore();
    public static HttpContext localContext = new BasicHttpContext();
    public static final int NETWORK_FAIL = 1;
    public static final int LOGIN_FAIL = 2;
    public static final int LOGIN_SUCCESS = 0;
    public static final int SEARCH_NO_FOUND = 404;
    public static final int SEARCH_SUCCESS = 200;

    public static void getPostList(String URL)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpRequest = new HttpGet(URL);
            try
            {
                HttpResponse httpResponse = httpclient.execute(httpRequest,localContext);
                String htmlString;
                if(httpResponse.getStatusLine().getStatusCode()!=200)
                {
                    return;
                }
                htmlString = EntityUtils.toString(httpResponse.getEntity(),"gbk");
                Document page = Jsoup.parse(htmlString);
                Elements contents = page.getElementsByTag("tbody");
                Element pages = page.getElementsByClass("pages").get(0);
                Element currentPage = pages.getElementsByTag("strong").get(0);
                MainPage.topicscurrentPage = Integer.parseInt(currentPage.text());
                if(MainPage.topicscurrentPage > 1)
                {
                    MainPage.topicsPreviousPageURL = currentPage.previousElementSibling().attr("href");
                }
                else
                {
                    MainPage.topicsPreviousPageURL = URL;
                }
                Element nextPage = currentPage.nextElementSibling();

                if(nextPage != null)
                {
                    MainPage.topicsNextPageURL = nextPage.attr("href");
                }
                else
                {
                    MainPage.topicsNextPageURL =URL;
                }

                MainPage.postInfoList.clear();
                int i = 0;
                for(Element content: contents)
                {
                    if(i > 0)
                    {
                        PostInfo postInfo = new PostInfo();
                        Element topic = content.getElementsByTag("th").get(0);      //only one element
                        String status = topic.attr("class");

                        Elements titleAndPages = topic.getElementsByTag("span");
                        String title = titleAndPages.get(0).getElementsByTag("a").text();
                        String link = titleAndPages.get(0).getElementsByTag("a").attr("href");
                        String pageNums;
                        String lastPageLink;
                        if(titleAndPages.size() > 1)
                        {
                            Element pageNumInfo = titleAndPages.get(1).getElementsByTag("a").last();
                            pageNums = pageNumInfo.text();
                            lastPageLink = pageNumInfo.attr("href");
                        }
                        else
                        {
                            pageNums = "1";
                            lastPageLink = link;
                        }
                        Element authorInfo = content.getElementsByClass("author").get(0);
                        String author = authorInfo.getElementsByTag("a").get(0).text();
                        String date = authorInfo.getElementsByTag("em").get(0).text();

                        Element nums = content.getElementsByClass("nums").get(0);
                        String commentNums = nums.getElementsByTag("strong").get(0).text();
                        String readNums = nums.getElementsByTag("em").get(0).text();

                        Element lastPost = content.getElementsByClass("lastpost").get(0);
                        String lastPostTime = lastPost.getElementsByTag("em").get(0).getElementsByTag("a").get(0).text();
                        String lastPostAuthor = lastPost.getElementsByTag("cite").get(0).getElementsByTag("a").get(0).text();


                        postInfo.setAuthor(author);
                        postInfo.setTitle(title);
                        postInfo.setLink(link);
                        postInfo.setPubDate(date);
                        postInfo.setLastPostAuthor(lastPostAuthor);
                        postInfo.setLastPostTime(lastPostTime);
                        postInfo.setPageNums(pageNums);
                        postInfo.setReadNums(readNums);
                        postInfo.setCommentNums(commentNums);
                        postInfo.setStatus(status);
                        postInfo.setLastPageLink(lastPageLink);
                        MainPage.postInfoList.add(postInfo);
                    }
                    i++;
                }
            }
            catch (IOException e)
            {
                Log.d("PostList", "Can not connect to the server.");
            }

    }

    public static void getCommentList(String URL,int action)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpRequest = new HttpGet(URL);
        try
        {
            HttpResponse httpResponse = httpclient.execute(httpRequest,localContext);
            String htmlString;
            if(httpResponse.getStatusLine().getStatusCode()!=200)
            {
                return;
            }
            htmlString = EntityUtils.toString(httpResponse.getEntity(),"gbk");
            Document page = Jsoup.parse(htmlString);
            Elements contents = page.getElementsByClass("mainbox");
            if(action == 0)
            {
                MainPage.commentList.clear();
            }

            Html.ImageGetter imageGetter = new Html.ImageGetter()
            {
                public Drawable getDrawable(String source)
                {
                    String defualtSmiles = "images/smilies/default/";
                    String specialSmiles = "images/smilies/special/";
                    String gif = ".gif";
                    Drawable drawable = null;
                    URL url;
                    if(source.indexOf(defualtSmiles)==0)
                    {
                        String picFullName = source.substring(defualtSmiles.length(),source.length());
                        String picName = picFullName.substring(0,picFullName.length()-gif.length());
                        int rId = myApplication.getAppContext().getResources().
                                getIdentifier(picName, "drawable", myApplication.getAppContext().getPackageName());

                        drawable = myApplication.getAppContext().getResources().getDrawable(rId);

                    }
                    else if(source.indexOf(specialSmiles)==0)
                    {
                        String picFullName = source.substring(specialSmiles.length(),source.length());
                        String picName = picFullName.substring(0,picFullName.length()-gif.length());
                        int rId = myApplication.getAppContext().getResources().
                                getIdentifier(picName, "drawable", myApplication.getAppContext().getPackageName());

                        drawable = myApplication.getAppContext().getResources().getDrawable(rId);
                    }
                    else
                    {
                        try {
                            url = new URL(myApplication.getAppContext().getResources().getString(R.string.base_page) + source);
                            drawable = Drawable.createFromStream(url.openStream(), "");
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                    return drawable;
                }
            };


            for(Element content: contents)
            {
                SingleComment singleComment = new SingleComment();
                if(MainPage.commentList.size() == 0) {
                    singleComment.setHostTitle(content.getElementsByTag("h1").get(0).text());
                }
                else
                {
                    singleComment.setHostTitle("");
                }
                Element postAuthor = content.getElementsByClass("postauthor").get(0);
                Element posterInfo = postAuthor.getElementsByTag("cite").get(0).getElementsByTag("a").get(0);
                String uidURLHead= "space-uid-";
                String uidURL = posterInfo.attr("href");
                singleComment.setUid(uidURL.substring(uidURLHead.length(), uidURL.length() - 5));
                String poster = posterInfo.text();
                if(MainPage.commentList.size() == 0)
                {
                    MainPage.topicHost = poster;
                }
                Element postContent = content.getElementsByClass("postcontent").get(0);
                singleComment.setPoster(poster);
                singleComment.setPosterTitle(postAuthor.getElementsByTag("em").get(0).text());
                singleComment.setPostTime(postContent.getElementsByClass("postinfo").get(0).text());
                singleComment.setOnlyAuthor(postContent.getElementsByTag("a").get(0).attr("href"));
                Element commentText = postContent.getElementsByClass("t_msgfont").get(0);
                Spanned postText;
                Spanned postWholeText;
                Spanned quoteText;
                Spanned quoteWholeText;
                MyHtmlTagHandle myHtmlTagHandle = new MyHtmlTagHandle();
                if(commentText.getElementsByTag("object").size()!=0)// ½«ÊÓÆµÌæ»»³ÉÍ¼Æ¬
                {
                    Elements videoblocks = commentText.getElementsByTag("object");
                    Tag t = Tag.valueOf("img");
                    Tag subTag = Tag.valueOf("videoimg");
                    for(Element videoblock : videoblocks)
                    {
                        Elements params = videoblock.getElementsByTag("param");
                        String video_url = "";
                        for(Element param : params)
                        {
                            if(param.attr("name").equals("movie"))
                            {
                                video_url = param.attr("value");
                            }
                        }
                        Attributes ab = new Attributes();
                        Attributes subAb = new Attributes();
                        ab.put("src","images/smilies/default/video.png");
                        subAb.put("videourl", video_url);
                        Element subElement = new Element(t,"",ab);
                        subElement.prependChild(new Element(subTag,"",subAb));
                        videoblock.replaceWith(subElement);

                    }
                }
                if(commentText.getElementsByTag("blockquote").size()!=0)
                {
                    Element quote = commentText.getElementsByTag("blockquote").get(0);
                    String quoteString = quote.html();
                    String commentString = commentText.html();
                    String quoteWholeString = commentText.getElementsByClass("quote").get(0).html();
                    postWholeText = Html.fromHtml(commentString, imageGetter, myHtmlTagHandle);
                    quoteText = Html.fromHtml(quoteString, imageGetter, null);
                    quoteWholeText = Html.fromHtml(quoteWholeString, imageGetter, null);
                    postText = new SpannedString(postWholeText.subSequence(0,postWholeText.length()-quoteWholeText.length()));
                }
                else
                {
                    postText = Html.fromHtml(commentText.html(), imageGetter, myHtmlTagHandle);
                    quoteText = null;
                }
                singleComment.setPostText(postText);
                singleComment.setQuoteText(quoteText);
                Log.i("getCommentList", Integer.toString(MainPage.commentList.size()));
                MainPage.commentList.add(singleComment);
            }
            Elements allPagesInfo = page.getElementsByClass("pages_btns").get(0).getElementsByClass("pages");
            if(allPagesInfo.size() != 0)  //More than one page.
            {
                Element allPages = allPagesInfo.get(0);
                Element currentPage = allPages.getElementsByTag("strong").get(0);
                MainPage.currentPage = Integer.parseInt(currentPage.text());
                if(MainPage.currentPage != 1) //There is previous page.
                {
                    MainPage.PreviousPageURL = currentPage.previousElementSibling().attr("href");
                }
                else
                {
                    MainPage.PreviousPageURL = URL;
                }
                Element nextPage = currentPage.nextElementSibling();
                if(nextPage != null) //It's not the last page.
                {
                    MainPage.NextPageURL = nextPage.attr("href");
                    MainPage.isLastPageofTopic = false;
                }
                else
                {
                    MainPage.NextPageURL = URL;
                    MainPage.isLastPageofTopic = true;
                }
            }
            else
            {
                MainPage.PreviousPageURL = URL;
                MainPage.NextPageURL = URL;
                MainPage.currentPage = 1;
                MainPage.isLastPageofTopic = true;
            }

        }
        catch (IOException e)
        {
            Log.d("singleComment", "Can not connect to the server.");
        }
    }

    public static int logIn(String userName, String password)
    {
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        if(!checkLogIn())
        {
           return NETWORK_FAIL;
        }

        HttpPost httpRequest = new HttpPost(myApplication.getAppContext().getResources().getString(R.string.login_page));

        List params = new ArrayList();
        //params.add(new BasicNameValuePair("action","login"));
        params.add(new BasicNameValuePair("formhash",hashcode));
        params.add(new BasicNameValuePair("referer","index.php"));
        params.add(new BasicNameValuePair("loginfield","username"));
        params.add(new BasicNameValuePair("username",userName));
        params.add(new BasicNameValuePair("password",password));
        params.add(new BasicNameValuePair("questionid","0"));
        params.add(new BasicNameValuePair("answer",""));
        params.add(new BasicNameValuePair("cookietime","2592000"));
        params.add(new BasicNameValuePair("loginmode",""));
        params.add(new BasicNameValuePair("styleid",""));
        params.add(new BasicNameValuePair("loginsubmit","ture"));
        try
        {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(httpRequest,localContext);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                String result = EntityUtils.toString(httpResponse.getEntity());
                Header[] cookieHeaders = httpResponse.getHeaders("Set-Cookie");
                if(cookieHeaders.length <2 )
                    return LOGIN_FAIL;
                return LOGIN_SUCCESS;
            }
            else
            {
                return NETWORK_FAIL;
            }
        }
        catch (Exception e)
        {
            Log.i("","");
        }
        return NETWORK_FAIL;
    }

    private static Boolean checkLogIn()
    {
        String loginPage = myApplication.getAppContext().getResources().getString(R.string.login_page);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(loginPage);
        try
        {
            HttpResponse response = httpClient.execute(httpget,localContext);
            if(response.getStatusLine().getStatusCode() == 200)
            {
                String returnHtml = EntityUtils.toString(response.getEntity());
                Document doc = Jsoup.parse(returnHtml);
                hashcode = doc.getElementsByTag("form").get(0).getElementsByTag("input").get(0).attr("value");
                Log.d("","");
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (MalformedURLException e)
        {
            Log.d("Login_Page","URL error!");
        }
        catch (IOException e)
        {
            Log.d("Login_page", "IOExeption.");
        }
        return false;
    }
    public static void getLogoffHashAndUid()
    {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(myApplication.getAppContext().getResources().getString(R.string.home_page));
        try
        {
            HttpResponse response = httpClient.execute(httpget,localContext);
            if(response.getStatusLine().getStatusCode() == 200)
            {
                String returnHtml = EntityUtils.toString(response.getEntity(),"gbk");
                Document doc = Jsoup.parse(returnHtml);
                String hrefString = doc.getElementById("menu").getElementsByClass("notabs").get(0).attr("href");
                String uuidString = doc.getElementById("menu").getElementsByTag("cite").get(0).getElementsByTag("a").get(0).attr("href");
                String uString = "uid";
                String keyString = "formhash";
                logoutHash = hrefString.substring(hrefString.indexOf(keyString)+keyString.length()+1,hrefString.length());
                uuid = uuidString.substring(uuidString.indexOf(uString)+uString.length()+1, uuidString.length());
            }
        }
        catch (Exception e)
        {
            Log.i("", "");
        }
    }

    public static void logoff()
    {
        MainActivity.isLogin = false;
        getLogoffHashAndUid();
        String logoffUrl = "http://www.topswim.net/logging.php?action=logout&formhash="+logoutHash;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(logoffUrl);
        try
        {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            if(response.getStatusLine().getStatusCode() == 200)
            {
                String returnHtml = EntityUtils.toString(response.getEntity(),"gbk");
                Header[] cookieHeaders = response.getHeaders("Set-Cookie");
                Log.i("","");
            }
        }
        catch (Exception e)
        {
            Log.i("","");
        }
    }

    public static void GetSpaceInfo(String uuid)
    {
        String space_base = myApplication.getAppContext().getResources().getString(R.string.space_page);
        String URL = space_base+"&uid="+uuid;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpRequest = new HttpGet(URL);
        try {
            HttpResponse httpResponse = httpclient.execute(httpRequest, localContext);
            String htmlString;
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return;
            }
            htmlString = EntityUtils.toString(httpResponse.getEntity(), "gbk");
            Document page = Jsoup.parse(htmlString);

            Element header = page.getElementById("header");
            String title = header.getElementsByClass("title").get(0).text();
            SpaceInfo.setTitle(title);

            Element moduleInfo = page.getElementById("module_userinfo");
            String status = moduleInfo.getElementsByClass("status").get(0).getElementsByTag("span").get(0).text();
            SpaceInfo.setStatus(status);

            Element detailInfo = page.getElementsByClass("info").get(1);
            SpaceInfo.setRegTime(detailInfo.getElementsByTag("tr").get(1).getElementsByTag("td").get(0).text());
            SpaceInfo.setVisitTime(detailInfo.getElementsByTag("tr").get(2).getElementsByTag("td").get(0).text());
            SpaceInfo.setPostTime(detailInfo.getElementsByTag("tr").get(3).getElementsByTag("td").get(0).text());
            SpaceInfo.setVisitNum(detailInfo.getElementsByTag("tr").get(4).getElementsByTag("td").get(0).text());
            SpaceInfo.setOnlineTime(detailInfo.getElementsByTag("tr").get(5).getElementsByClass("bold").get(0).text());
            SpaceInfo.setAcGroup(detailInfo.getElementsByTag("tr").get(7).getElementsByTag("td").get(0).text());
            SpaceInfo.setPostLevel(detailInfo.getElementsByTag("tr").get(8).getElementsByTag("td").get(0).text());
            SpaceInfo.setAuthority(detailInfo.getElementsByTag("tr").get(9).getElementsByTag("td").get(0).text());
            SpaceInfo.setScore(detailInfo.getElementsByTag("tr").get(10).getElementsByTag("td").get(0).text());
            SpaceInfo.setReputation(detailInfo.getElementsByTag("tr").get(11).getElementsByTag("td").get(0).text());
            SpaceInfo.setGold(detailInfo.getElementsByTag("tr").get(12).getElementsByTag("td").get(0).text());
            SpaceInfo.setContribution(detailInfo.getElementsByTag("tr").get(13).getElementsByTag("td").get(0).text());
            SpaceInfo.setPostNum(detailInfo.getElementsByTag("tr").get(14).getElementsByTag("td").get(0).text());
            SpaceInfo.setSex(detailInfo.getElementsByTag("tr").get(18).getElementsByTag("td").get(0).text());
            SpaceInfo.setBirthday(detailInfo.getElementsByTag("tr").get(19).getElementsByTag("td").get(0).text());
            SpaceInfo.setEmail(detailInfo.getElementsByTag("tr").get(20).getElementsByTag("td").get(0).text());

        }
        catch (Exception e)
        {
            Log.i("","");
        }
    }

    public static boolean postTopic(String urlString, String title, String content, int mode)
    {

        getLogoffHashAndUid();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpRequest = new HttpPost(urlString);
        //httpRequest.setHeader("Content-Type","multipart/form-data; boundary=----WebKitFormBoundaryoZ3DkS4TdYadl8wE");
        httpRequest.setHeader("Accept-Encoding","gzip,deflate");
        httpRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");



        MultipartEntity mutiEntity = new MultipartEntity();
        try
        {
            mutiEntity.addPart("formhash", new StringBody(logoutHash));
            if(mode==0) {
                mutiEntity.addPart("isblog", new StringBody(""));
                mutiEntity.addPart("frombbs", new StringBody("1"));
            }
            mutiEntity.addPart("subject", new StringBody(title, Charset.forName("gbk")));
            mutiEntity.addPart("posteditor_mediatyperadio", new StringBody("on"));
            mutiEntity.addPart("message", new StringBody(content, Charset.forName("gbk")));
            if(mode==0)
            {
                mutiEntity.addPart("iconid", new StringBody("0"));
            }
            mutiEntity.addPart("attachperm[]", new StringBody("0"));

            mutiEntity.addPart("localid[]",new StringBody("1"));
            mutiEntity.addPart("attachperm[]",new StringBody("0"));
            mutiEntity.addPart("wysiwyg", new StringBody("0"));
            if(mode==1)
            {
                mutiEntity.addPart("fid",new StringBody(MainPage.fid));
            }
            httpRequest.setEntity(mutiEntity);
            HttpResponse httpResponse = httpclient.execute(httpRequest, localContext);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                String htmlString = EntityUtils.toString(httpResponse.getEntity(), "gbk");
                Log.i("","");
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (Exception e)
        {
            Log.i("","");
        }

        //String logoffUrl = "http://www.topswim.net/post.php?action=newthread&fid=15&extra=page%3D1&topicsubmit=yes";
        return true;
    }

    public static boolean postSearchOld( String keyword, String username)
    {
        String urlString = "http://www.topswim.net/search.php";
        getLogoffHashAndUid();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.setRedirectHandler(new DefaultRedirectHandler());
        HttpPost httpRequest = new HttpPost(urlString);
        httpRequest.setHeader("Accept-Encoding","gzip,deflate");
        httpRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");

        MultipartEntity mutiEntity = new MultipartEntity();
        try
        {
            mutiEntity.addPart("formhash", new StringBody(logoutHash));

            if(keyword!=null)
            {
                mutiEntity.addPart("srchtxt", new StringBody(keyword, Charset.forName("gbk")));
            }
            else
            {
                mutiEntity.addPart("srchtxt", new StringBody("", Charset.forName("gbk")));
            }

            if(username!=null)
            {
                mutiEntity.addPart("srchuname", new StringBody(username, Charset.forName("gbk")));
            }
            else
            {
                mutiEntity.addPart("srchuname", new StringBody("", Charset.forName("gbk")));
            }

            mutiEntity.addPart("srchtype",new StringBody("title"));
            mutiEntity.addPart("srchfilter", new StringBody("all"));
            mutiEntity.addPart("srchtypeid", new StringBody(""));
            mutiEntity.addPart("srchfrom", new StringBody("0"));
            mutiEntity.addPart("srchfrom", new StringBody("0"));
            mutiEntity.addPart("before", new StringBody(""));
            mutiEntity.addPart("orderby", new StringBody("lastpost"));
            mutiEntity.addPart("ascdesc", new StringBody("desc"));
            mutiEntity.addPart("srchfid%5B%5D", new StringBody("all"));
            mutiEntity.addPart("searchsubmit", new StringBody("true"));


            httpRequest.setEntity(mutiEntity);
            HttpResponse httpResponse = httpclient.execute(httpRequest, localContext);
            if(httpResponse.getStatusLine().getStatusCode() == 320)
            {
                Log.i("","");
                Log.i("","");
            }
            else if(httpResponse.getStatusLine().getStatusCode()!=200)
            {
                return false;
            }

            String htmlString = EntityUtils.toString(httpResponse.getEntity(), "gbk");
            Log.i("", "");
            Document doc = Jsoup.parse(htmlString);
            Elements items = doc.getElementsByClass("threadlist").get(0).getElementsByTag("table").get(0).getElementsByTag("tbody");
            MainPage.schResList.clear();
            for(Element item : items)
            {
                SingleSchResult singleSchRes = new SingleSchResult();
                singleSchRes.setTitle(item.getElementsByTag("th").get(0).getElementsByTag("a").get(0).text());
                singleSchRes.setLink(item.getElementsByTag("th").get(0).getElementsByTag("a").get(0).attr("href"));
                singleSchRes.setForumName(item.getElementsByClass("forum").get(0).getElementsByTag("a").get(0).text());
                singleSchRes.setAuthor(item.getElementsByClass("author").get(0).getElementsByTag("a").get(0).text());
                singleSchRes.setReadNum(item.getElementsByClass("nums").get(0).getElementsByTag("em").get(0).text());
                singleSchRes.setCommentNum(item.getElementsByClass("nums").get(0).getElementsByTag("strong").get(0).text());
                singleSchRes.setLastCommentTime(item.getElementsByClass("lastpost").get(0).getElementsByTag("a").get(0).text());
                MainPage.schResList.add(singleSchRes);
            }
            return true;
        }
        catch (Exception e)
        {
            Log.i("","");
        }
        //String logoffUrl = "http://www.topswim.net/post.php?action=newthread&fid=15&extra=page%3D1&topicsubmit=yes";
        return false;
    }

    public static int postSearch(String keyword, String username, String timeRange, String forumId)
    {
        InputStream inStream = null;
        strCookies = getCookies();
        getLogoffHashAndUid();
        try
        {
            URL myUrL = new URL("http://www.topswim.net/search.php");
            HttpURLConnection conn = (HttpURLConnection)myUrL.openConnection();
            conn.setReadTimeout(100000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cookie", strCookies);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Map<String, String> params = new HashMap<String, String>();

            params.put("formhash", logoutHash);

            if(keyword!=null)
            {
                params.put("srchtxt", keyword);
            }
            else
            {
                params.put("srchtxt", "");
            }

            if(username!=null)
            {
                params.put("srchuname", username);
            }
            else
            {
                params.put("srchuname", "");
            }

            params.put("srchtype", "title");
            params.put("srchfilter", "all");
            params.put("srchtypeid", "");
            params.put("srchfrom", getSearchRange(timeRange));
            params.put("before", "");
            params.put("orderby", "lastpost");
            params.put("ascdesc", "desc");
            params.put("srchfid%5B%5D", forumId);
            params.put("searchsubmit", "true");

            byte[] outputData = getRequestData(params,"gbk").toString().getBytes();

            conn.connect();

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(outputData);

            int response = conn.getResponseCode();
            if(response == 302)
            {
                Log.i("topswim.postSearch","return 302");
                inStream = conn.getInputStream();
                String htmlString = dealResponseResult(inStream);
                return 0;
            }
            if(response == 200)
            {
                Log.i("topswim.postSearch","return OK");
                inStream = conn.getInputStream();

                MainPage.searchResCurrPage = conn.getURL().toString();
                MainPage.searchResNextPage = MainPage.searchResCurrPage;
                MainPage.searchResPrePage = MainPage.searchResCurrPage;

                String htmlString = dealResponseResult(inStream);
                return parseSearchResHtml(htmlString);
            }
            return 0;
        }
        catch (Exception e)
        {
            Log.i("","");
        }

        return 0;
    }

    public static boolean updateSearchRes(String url)
    {
        InputStream inStream = null;
        try {
            URL myUrL = new URL("http://www.topswim.net/search.php");
            HttpURLConnection conn = (HttpURLConnection) myUrL.openConnection();
            conn.setReadTimeout(100000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", strCookies);
            conn.setDoInput(true);

            conn.connect();

            int response = conn.getResponseCode();
            if(response==200)
            {
                inStream = conn.getInputStream();
                MainPage.searchResCurrPage = url;
                MainPage.searchResNextPage = url;
                MainPage.searchResPrePage = url;
                String htmlString = dealResponseResult(inStream);
                parseSearchResHtml(htmlString);
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            Log.i("","");
        }

        return false;
    }

    private static int parseSearchResHtml(String htmlString)
    {
        Document doc = Jsoup.parse(htmlString);
        Elements items = doc.getElementsByClass("threadlist").get(0).getElementsByTag("table").get(0).getElementsByTag("tbody");
        if(items.get(0).getElementsByTag("td").size()==0)
        {
            return SEARCH_NO_FOUND;
        }

        Elements pages = doc.getElementsByClass("pages_btns");
        if(pages.size()>0)
        {
            Elements linksOfPage = pages.get(0).getElementsByClass("pages").get(0).getElementsByTag("a");
            String strCurrentPage = pages.get(0).getElementsByClass("pages").get(0).getElementsByTag("strong").get(0).text();
            int currentPage = Integer.parseInt(strCurrentPage);
            for(Element linkItem : linksOfPage)
            {
                if(linkItem.text().equals(Integer.toString(currentPage - 1)))
                {
                    MainPage.searchResPrePage = myApplication.getAppContext().getString(R.string.base_page)+linkItem.attr("href");
                }
                else if(linkItem.text().equals(Integer.toString(currentPage+1)))
                {
                    MainPage.searchResNextPage = myApplication.getAppContext().getString(R.string.base_page)+linkItem.attr("href");
                }
            }
        }
        MainPage.schResList.clear();
        for(Element item : items)
        {
            SingleSchResult singleSchRes = new SingleSchResult();
            singleSchRes.setTitle(item.getElementsByTag("th").get(0).getElementsByTag("a").get(0).text());
            singleSchRes.setLink(item.getElementsByTag("th").get(0).getElementsByTag("a").get(0).attr("href"));
            singleSchRes.setForumName(item.getElementsByClass("forum").get(0).getElementsByTag("a").get(0).text());
            singleSchRes.setAuthor(item.getElementsByClass("author").get(0).getElementsByTag("a").get(0).text());
            singleSchRes.setReadNum(item.getElementsByClass("nums").get(0).getElementsByTag("em").get(0).text());
            singleSchRes.setCommentNum(item.getElementsByClass("nums").get(0).getElementsByTag("strong").get(0).text());
            singleSchRes.setLastCommentTime(item.getElementsByClass("lastpost").get(0).getElementsByTag("a").get(0).text());
            MainPage.schResList.add(singleSchRes);
        }
        return SEARCH_SUCCESS;
    }

    private static String getCookies()
    {
        List<Cookie> cookies = cookieStore.getCookies();
        String myCookies = "";
        for(int i=0;i<cookies.size();i++)
        {
           myCookies += (cookies.get(i).getName()+"="+cookies.get(i).getValue()+";");
        }
        return myCookies;
    }

    private static Map<String, String> getCookiesInMap()
    {
        List<Cookie> cookies = cookieStore.getCookies();
        Map<String, String> myCookies = new HashMap<String, String>();
        for(int i=0;i<cookies.size();i++)
        {
            myCookies.put(cookies.get(i).getName(),cookies.get(i).getValue());
        }
        return myCookies;
    }

    private static class MyHtmlTagHandle implements  Html.TagHandler
    {
        private boolean isVideoTag = false;
        private int sIndex = 0;
        private int eIndex = 0;
        private String videoUrl ="";
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader)
        {
            if(tag.equalsIgnoreCase("videoimg"))
            {
                isVideoTag = true;
                if(opening)
                {
                    sIndex = output.length();
                }
                else
                {
                    eIndex = output.length();
                    output.setSpan(clickSpan, eIndex-2,eIndex-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        ClickableSpan clickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(myApplication.getAppContext(), "video pic clicked!",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
            }
        };

    }


    private static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //remove the end"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    private static String getSearchRange(String strDays)
    {
        int days;
        if(strDays.equals(""))
        {
            days = 7;
        }
        else if(!isInteger(strDays))
        {
            days = 7;
        }
        else
        {
            days = Integer.parseInt(strDays);
        }
        if((days > 365)||(days==0))
            days = 0;

        return Integer.toString(days * 86400);
    }
    private static boolean isInteger(String s)
    {
        Matcher mer = Pattern.compile("^[0-9]+$").matcher(s);
        return mer.find();
    }


    private static String dealResponseResult(InputStream inputStream)
    {
        String resultData = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try
        {
            while((len = inputStream.read(data)) != -1)
            {
                byteArrayOutputStream.write(data, 0, len);
            }
            resultData = new String(byteArrayOutputStream.toByteArray(),"gbk");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return resultData;
    }

}
