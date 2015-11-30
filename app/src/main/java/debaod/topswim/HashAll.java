package debaod.topswim;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by debaod on 3/27/2015.
 */
public class HashAll {

    public static String getForumURL(int groupPosition, int childPosition)
    {
        String GotoForumURL = "http://www.topswim.net/";
        String EndURL = "-1.html";
        switch (groupPosition)
        {
            case 0:
                if(childPosition <= 2)
                    GotoForumURL += "forum-"+Integer.toString(4+childPosition)+EndURL;
                else if(childPosition == 3)
                    GotoForumURL += "forum-"+Integer.toString(15)+EndURL;
                else if(childPosition ==4)
                    GotoForumURL += "forum-"+Integer.toString(122)+EndURL;
                else if(childPosition ==5)
                    GotoForumURL += "forum-"+Integer.toString(16)+EndURL;
                else if(childPosition ==6)
                    GotoForumURL += "forum-"+Integer.toString(17)+EndURL;
                else if(childPosition ==7)
                    GotoForumURL += "forum-"+Integer.toString(55)+EndURL;
                else if(childPosition ==8)
                    GotoForumURL += "forum-"+Integer.toString(56)+EndURL;
                break;
            case 1:
                if(childPosition ==0)
                    GotoForumURL += "forum-"+Integer.toString(46)+EndURL;
                else if(childPosition ==1)
                    GotoForumURL += "forum-"+Integer.toString(48)+EndURL;
                else if(childPosition ==2)
                    GotoForumURL += "forum-"+Integer.toString(23)+EndURL;
                else if(childPosition ==3)
                    GotoForumURL += "forum-"+Integer.toString(43)+EndURL;
                else if(childPosition ==4)
                    GotoForumURL += "forum-"+Integer.toString(117)+EndURL;
                else if(childPosition ==5)
                    GotoForumURL += "forum-"+Integer.toString(35)+EndURL;
                else if(childPosition ==6)
                    GotoForumURL += "forum-"+Integer.toString(38)+EndURL;
                else if(childPosition ==7)
                    GotoForumURL += "forum-"+Integer.toString(109)+EndURL;
                else if(childPosition ==8)
                    GotoForumURL += "forum-"+Integer.toString(49)+EndURL;
                else if(childPosition ==9)
                    GotoForumURL += "forum-"+Integer.toString(52)+EndURL;
                else if(childPosition ==10)
                    GotoForumURL += "forum-"+Integer.toString(44)+EndURL;
                else if(childPosition ==11)
                    GotoForumURL += "forum-"+Integer.toString(123)+EndURL;
                break;
            case 2:
                if(childPosition ==0)
                    GotoForumURL += "forum-"+Integer.toString(110)+EndURL;
                else if(childPosition ==1)
                    GotoForumURL += "forum-"+Integer.toString(101)+EndURL;
                else if(childPosition ==2)
                    GotoForumURL += "forum-"+Integer.toString(125)+EndURL;
                else if(childPosition ==3)
                    GotoForumURL += "forum-"+Integer.toString(104)+EndURL;
                else if(childPosition ==4)
                    GotoForumURL += "forum-"+Integer.toString(113)+EndURL;
                else if(childPosition ==5)
                    GotoForumURL += "forum-"+Integer.toString(111)+EndURL;
                else if(childPosition ==6)
                    GotoForumURL += "forum-"+Integer.toString(108)+EndURL;
                else if(childPosition ==7)
                    GotoForumURL += "forum-"+Integer.toString(106)+EndURL;
                else if(childPosition ==8)
                    GotoForumURL += "forum-"+Integer.toString(103)+EndURL;
                else if(childPosition ==9)
                    GotoForumURL += "forum-"+Integer.toString(102)+EndURL;
                else if(childPosition ==10)
                    GotoForumURL += "forum-"+Integer.toString(126)+EndURL;
                else if(childPosition ==11)
                    GotoForumURL += "forum-"+Integer.toString(112)+EndURL;
                else if(childPosition ==12)
                    GotoForumURL += "forum-"+Integer.toString(128)+EndURL;
                else if(childPosition ==13)
                    GotoForumURL += "forum-"+Integer.toString(129)+EndURL;
                else if(childPosition ==14)
                    GotoForumURL += "forum-"+Integer.toString(127)+EndURL;
                else if(childPosition ==15)
                    GotoForumURL += "forum-"+Integer.toString(120)+EndURL;
                else if(childPosition ==16)
                    GotoForumURL += "forum-"+Integer.toString(119)+EndURL;
                else if(childPosition ==17)
                    GotoForumURL += "forum-"+Integer.toString(118)+EndURL;
                else if(childPosition ==18)
                    GotoForumURL += "forum-"+Integer.toString(115)+EndURL;
                else if(childPosition ==19)
                    GotoForumURL += "forum-"+Integer.toString(114)+EndURL;
                else if(childPosition ==20)
                    GotoForumURL += "forum-"+Integer.toString(100)+EndURL;
                else if(childPosition ==21)
                    GotoForumURL += "forum-"+Integer.toString(124)+EndURL;
                else if(childPosition ==22)
                    GotoForumURL += "forum-"+Integer.toString(112)+EndURL;
                else if(childPosition ==23)
                    GotoForumURL += "forum-"+Integer.toString(107)+EndURL;
                else if(childPosition ==24)
                    GotoForumURL += "forum-"+Integer.toString(105)+EndURL;
                break;
            case 3:
                GotoForumURL += "forum-"+Integer.toString(59-childPosition)+EndURL;
                break;
            default:
                break;

        }
        return GotoForumURL;
    }

    public static String getForumId(int pos)
    {
        String forumId;
        int childPosition;
        if(pos==0)
            return "all";
        childPosition = pos - 1;
        if(childPosition <= 2)
            forumId = Integer.toString(4+childPosition);
        else if(childPosition == 3)
            forumId = Integer.toString(15);
        else if(childPosition ==4)
            forumId = Integer.toString(122);
        else if(childPosition ==5)
            forumId = Integer.toString(16);
        else if(childPosition ==6)
            forumId = Integer.toString(17);
        else if(childPosition ==7)
            forumId = Integer.toString(55);
        else if(childPosition ==8)
            forumId = Integer.toString(56);
        else if(childPosition ==9)
            forumId = "forum-"+Integer.toString(46);
        else if(childPosition ==10)
            forumId = "forum-"+Integer.toString(48);
        else if(childPosition ==11)
            forumId = "forum-"+Integer.toString(23);
        else if(childPosition ==12)
            forumId = "forum-"+Integer.toString(43);
        else if(childPosition ==13)
            forumId = "forum-"+Integer.toString(117);
        else if(childPosition ==14)
            forumId = "forum-"+Integer.toString(35);
        else if(childPosition ==15)
            forumId = "forum-"+Integer.toString(38);
        else if(childPosition ==16)
            forumId = "forum-"+Integer.toString(109);
        else if(childPosition ==17)
            forumId = "forum-"+Integer.toString(49);
        else if(childPosition ==18)
            forumId = "forum-"+Integer.toString(52);
        else if(childPosition ==19)
            forumId = "forum-"+Integer.toString(44);
        else if(childPosition ==20)
            forumId = "forum-"+Integer.toString(123);
        else if(childPosition ==21)
            forumId = Integer.toString(110);
        else if(childPosition ==22)
            forumId = Integer.toString(101);
        else if(childPosition ==23)
            forumId = Integer.toString(125);
        else if(childPosition ==24)
            forumId = Integer.toString(104);
        else if(childPosition ==25)
            forumId = Integer.toString(113);
        else if(childPosition ==26)
            forumId = Integer.toString(111);
        else if(childPosition ==27)
            forumId = Integer.toString(108);
        else if(childPosition ==28)
            forumId = Integer.toString(106);
        else if(childPosition ==29)
            forumId = Integer.toString(103);
        else if(childPosition ==30)
            forumId = Integer.toString(102);
        else if(childPosition ==31)
            forumId = Integer.toString(126);
        else if(childPosition ==32)
            forumId = Integer.toString(112);
        else if(childPosition ==33)
            forumId = Integer.toString(128);
        else if(childPosition ==34)
            forumId = Integer.toString(129);
        else if(childPosition ==35)
            forumId = Integer.toString(127);
        else if(childPosition ==36)
            forumId = Integer.toString(120);
        else if(childPosition ==37)
            forumId = Integer.toString(119);
        else if(childPosition ==38)
            forumId = Integer.toString(118);
        else if(childPosition ==39)
            forumId = Integer.toString(115);
        else if(childPosition ==40)
            forumId = Integer.toString(114);
        else if(childPosition ==41)
            forumId = Integer.toString(100);
        else if(childPosition ==42)
            forumId = Integer.toString(124);
        else if(childPosition ==43)
            forumId = Integer.toString(112);
        else if(childPosition ==44)
            forumId = Integer.toString(107);
        else if(childPosition ==45)
            forumId = Integer.toString(105);
        else if(childPosition==46)
            forumId = Integer.toString(59);
        else if(childPosition==47)
            forumId = Integer.toString(58);
        else
            forumId = "all";
        return forumId;
    }


    public static int[][] getAllForumName()
    {
             return new int[][]
                {
                        {
                                R.string.forum_freestyle,
                                R.string.forum_breaststroke,
                                R.string.forum_butterfly,
                                R.string.forum_backstroke,
                                R.string.forum_childswim,
                                R.string.forum_foreignresource,
                                R.string.forum_videoresource,
                                R.string.forum_videoresource2
                        },
                        {
                                R.string.forum_interestingnews,
                                R.string.forum_race,
                                R.string.forum_diary,
                                R.string.forum_open_water_swimming,
                                R.string.forum_winter_swimming,
                                R.string.forum_total_immersion,
                                R.string.forum_go_swim,
                                R.string.forum_life_saving,
                                R.string.forum_dive,
                                R.string.forum_fashion,
                                R.string.forum_health_care,
                                R.string.forum_tea
                        },
                        {
                                R.string.forum_guangdong,
                                R.string.forum_shanghai,
                                R.string.forum_hainan,
                                R.string.forum_xianggang,
                                R.string.forum_jiangsu,
                                R.string.forum_hubei,
                                R.string.forum_beijing,
                                R.string.forum_anhui,
                                R.string.forum_guangxi,
                                R.string.forum_jilin,
                                R.string.forum_hebei,
                                R.string.forum_helongjiang,
                                R.string.forum_jiangxi,
                                R.string.forum_hunan,
                                R.string.forum_shanxi,
                                R.string.forum_niaoning,
                                R.string.forum_tianjing,
                                R.string.forum_sicuan,
                                R.string.forum_chongqin,
                                R.string.forum_zejiang,
                                R.string.forum_fujian,
                                R.string.forum_yunnan,
                                R.string.forum_shandong,
                                R.string.forum_other_region
                        },
                        {
                                R.string.forum_sysop,
                                R.string.forum_boardop
                        }

                };// The end of array.
    }

    public static List<String> getAllForumNameInString()
    {
        int[][] AllForumName = getAllForumName();
        List<String> strAllForum = new ArrayList<>();
        for(int k=0;k<4;k++) {
            for (int i : AllForumName[k]) {
                strAllForum.add(myApplication.getAppContext().getString(i));
            }
        }

        return strAllForum;
    }


    public static int[] getAllForumType()
    {
        return new int[]
                {
                        R.string.forum_hotforum, R.string.forum_diversity,
                        R.string.forum_regionforum,R.string.forum_bbsop
                };
    }

}
