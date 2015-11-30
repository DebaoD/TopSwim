package debaod.topswim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends ActionBarActivity {

    public static String loginId;
    private static String loginPwd;
    public  static Boolean isLogin = false;
    private  final int NETWORK_CHECKED = 1;
    private  final int NETWORK_FAIL = 4;
    private  final int LOGIN_SUCCESS = 2;
    private  final int LOGIN_FAILED = 3;
    private long lastClickTime = 0;
    private SharedPreferences mysharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = (Button)findViewById(R.id.btn_login);
        Button loginAsGuest = (Button)findViewById(R.id.btn_nolog);
        mysharedpreferences = getSharedPreferences("loginPre",MODE_PRIVATE);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((System.currentTimeMillis()-lastClickTime)<3000)
                    return;
                lastClickTime = System.currentTimeMillis();
                final Handler handler = new MyHandler();
                EditText textName = (EditText)findViewById(R.id.textName);
                EditText textPwd = (EditText)findViewById(R.id.textPwd);
                CheckBox cb = (CheckBox)findViewById(R.id.cb_rem);

                loginId = textName.getText().toString();
                loginPwd = textPwd.getText().toString();
                if(cb.isChecked())
                {
                    SharedPreferences.Editor editor = mysharedpreferences.edit();
                    editor.putString("username",loginId);
                    editor.putString("password",loginPwd);
                    editor.putBoolean("rem",true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mysharedpreferences.edit();
                    editor.putString("username","");
                    editor.putString("password","");
                    editor.putBoolean("rem",false);
                    editor.apply();
                }
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result = WebFetch.logIn(loginId,loginPwd);
                        if(result == WebFetch.LOGIN_SUCCESS)
                        {
                            Message message = new Message();
                            message.what = LOGIN_SUCCESS;
                            handler.sendMessage(message);
                        }
                        else if(result == WebFetch.LOGIN_FAIL)
                        {
                            Message message = new Message();
                            message.what = LOGIN_FAILED;
                            handler.sendMessage(message);
                        }
                        else
                        {
                            Message message = new Message();
                            message.what = NETWORK_FAIL;
                            handler.sendMessage(message);
                        }
                    }
                });
                t.start();
            }
        });
        loginAsGuest.setOnClickListener(new loginAsGuestListener());

        EditText text = (EditText)findViewById(R.id.textName);
        text.setText(mysharedpreferences.getString("username",""));
        text = (EditText)findViewById(R.id.textPwd);
        text.setText(mysharedpreferences.getString("password",""));
        CheckBox cb = (CheckBox)findViewById(R.id.cb_rem);
        cb.setChecked(mysharedpreferences.getBoolean("rem",false));

    }

    class loginAsGuestListener implements View.OnClickListener{

        public  Handler handler;

        @Override
        public void onClick(View v)
        {
            if((System.currentTimeMillis()-lastClickTime)<3000)
                return;
            lastClickTime = System.currentTimeMillis();
            /*
            InputMethodManager  imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
            String local = ims.getLocale();
            Toast toast = Toast.makeText(myApplication.getAppContext(), local,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,0);
            toast.show();
            */
            handler = new MyHandler();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if(checkHomePageURL())
                    {
                        Message message = new Message();
                        message.what = NETWORK_CHECKED;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = NETWORK_FAIL;
                        handler.sendMessage(message);
                    }
                }
            });
            t.start();
        }
    }
    private class MyHandler extends Handler
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case NETWORK_CHECKED:
                    Intent intentGuset = new Intent();
                    intentGuset.setClass(MainActivity.this, MainPage.class);
                    MainActivity.this.startActivity(intentGuset);
                    break;
                case NETWORK_FAIL:
                    AlertDialog.Builder netFaildialog = new AlertDialog.Builder(MainActivity.this);
                    netFaildialog.setTitle(R.string.network_not_work);
                    netFaildialog.setMessage(R.string.please_check_network);
                    netFaildialog.setCancelable(true);
                    netFaildialog.setPositiveButton("OK",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    netFaildialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    netFaildialog.show();
                    break;
                case LOGIN_SUCCESS:
                    isLogin = true;
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MainPage.class);
                    MainActivity.this.startActivity(intent);
                    break;
                case LOGIN_FAILED:
                    AlertDialog.Builder logFailDialog = new AlertDialog.Builder(MainActivity.this);
                    logFailDialog.setTitle(R.string.login_fail);
                    logFailDialog.setMessage(R.string.login_fail_message);
                    logFailDialog.setCancelable(true);
                    logFailDialog.setPositiveButton("OK",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    logFailDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    logFailDialog.show();
                    break;
                default:
                    break;
            }
        }
    }

    public Boolean checkHomePageURL()
    {
        String homePage = getString(R.string.home_page);
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(homePage);
            CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
            {
                return true;
            }
        }
        catch (MalformedURLException e)
        {
            Log.d("Home_Page","URL error!");
        }
        catch (IOException e)
        {
            Log.d("Home_page","IOExeption.");
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
