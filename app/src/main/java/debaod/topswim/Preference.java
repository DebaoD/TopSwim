package debaod.topswim;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import github.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Preference extends PreferenceFragment {


    public Preference() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onResume()
    {
        MainPage.fragIndex = 6;
        super.onResume();
    }

}
