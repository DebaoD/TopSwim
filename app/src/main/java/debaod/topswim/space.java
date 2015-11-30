package debaod.topswim;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class space extends Fragment {


    public space() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_space, container, false);

        TextView status = (TextView)rootView.findViewById(R.id.space_status);
        if(SpaceInfo.getStatus().equals(getResources().getString(R.string.space_status_online)))
        {
            status.setTextColor(0xFF56994B);
        }
        else if(SpaceInfo.getStatus().equals(getResources().getString(R.string.space_status_offline)))
        {
            status.setTextColor(Color.GRAY);
        }
        else
            status.setTextColor(Color.BLACK);
        status.setText(SpaceInfo.getStatus());
        TextView title = (TextView)rootView.findViewById(R.id.space_title);
        title.setText(SpaceInfo.getTitle());
        TextView regTime = (TextView)rootView.findViewById(R.id.space_regtime);
        regTime.setText(SpaceInfo.getRegTime());
        TextView visitTime = (TextView)rootView.findViewById(R.id.space_visittime);
        visitTime.setText(SpaceInfo.getVisitTime());
        TextView postTime = (TextView)rootView.findViewById(R.id.space_posttime);
        postTime.setText(SpaceInfo.getPostTime());
        TextView visitNum = (TextView)rootView.findViewById(R.id.space_visitnum);
        visitNum.setText(SpaceInfo.getVisitNum());
        TextView onLineTime = (TextView)rootView.findViewById(R.id.space_onlinetime);
        onLineTime.setText(SpaceInfo.getOnlineTime());
        TextView group = (TextView)rootView.findViewById(R.id.space_group);
        group.setText(SpaceInfo.getAcGroup());
        TextView level = (TextView)rootView.findViewById(R.id.space_level);
        level.setText(SpaceInfo.getPostLevel());
        TextView authority = (TextView)rootView.findViewById(R.id.space_authority);
        authority.setText(SpaceInfo.getAuthority());
        TextView score = (TextView)rootView.findViewById(R.id.space_score);
        score.setText(SpaceInfo.getScore());
        TextView reputation = (TextView)rootView.findViewById(R.id.space_reputation);
        reputation.setText(SpaceInfo.getReputation());
        TextView gold = (TextView)rootView.findViewById(R.id.space_gold);
        gold.setText(SpaceInfo.getGold());
        TextView contribution = (TextView)rootView.findViewById(R.id.space_contribution);
        contribution.setText(SpaceInfo.getContribution());
        TextView postNum = (TextView)rootView.findViewById(R.id.space_postnum);
        postNum.setText(SpaceInfo.getPostNum());
        TextView sex = (TextView)rootView.findViewById(R.id.space_sex);
        sex.setText(SpaceInfo.getSex());
        TextView birthday = (TextView)rootView.findViewById(R.id.space_birthday);
        birthday.setText(SpaceInfo.getBirthday());
        TextView Email = (TextView)rootView.findViewById(R.id.space_email);
        Email.setText(SpaceInfo.getEmail());

        return rootView;
    }


}
