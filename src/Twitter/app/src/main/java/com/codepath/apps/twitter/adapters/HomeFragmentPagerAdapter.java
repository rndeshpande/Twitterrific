package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.twitter.fragments.TimelineFragment;

/**
 * Created by rdeshpan on 10/4/2017.
 */

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Home", "Mentions" };
    private Context context;

    public HomeFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    @Override
    public Fragment getItem(int position) {
        return TimelineFragment.newInstance(position + 1);

        /*switch (position) {
            case 0:
                return TimelineFragment.newInstance(position + 1);
            case 1:
                return MentionsFragment.newInstance(position + 1);
            default:
                return null;
        }
        */
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
