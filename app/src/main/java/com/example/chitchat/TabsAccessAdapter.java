package com.example.chitchat;

import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Objects;

public class TabsAccessAdapter extends FragmentPagerAdapter {


    public TabsAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
            {
                ChatsFragment CF=new ChatsFragment();
                return CF;
            }
            case 1:
            {
                GroupsFragment GF=new GroupsFragment();
                return GF;
            }
            case 2:
            {
                ContactsFragment CtF=new ContactsFragment();
                return CtF;
            }
            case 3:
            {
                RequestListFragment RLF=new RequestListFragment();
                return RLF;
            }
            default:
            {
                return null;
            }
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
            {

                return "Chats";
            }
            case 1:
            {

                return "Groups";
            }
            case 2:
            {

                return "Contacts";
            }
            case 3:
            {
                return "Requests";
            }
            default:
            {
                return null;
            }
        }
    }
}
