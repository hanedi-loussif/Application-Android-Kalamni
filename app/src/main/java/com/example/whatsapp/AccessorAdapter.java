package com.example.whatsapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AccessorAdapter extends FragmentPagerAdapter {
    public AccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {

        switch (i) {
            case 0:
                chatsFragment chatsFragment= new chatsFragment();
                return chatsFragment ;
            case 1 :
                groupsFragment groupsFragment=new groupsFragment() ;
                return groupsFragment;
            case 2 :
                contactsFragment contactsFragment=new contactsFragment();
                return contactsFragment ;
            case 3:
                requestFragment requestFragment=new requestFragment();
                return requestFragment ;
            default:
            return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "chats" ;

            case 1 :
                return "groups" ;
            case 2 :
                return "contact";
            case 3 :
                return "Requests";

            default:
                return null;
        }

    }
}
