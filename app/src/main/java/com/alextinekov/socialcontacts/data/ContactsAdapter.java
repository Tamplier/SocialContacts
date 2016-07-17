package com.alextinekov.socialcontacts.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alextinekov.socialcontacts.Constants;
import com.alextinekov.socialcontacts.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Tinekov on 16.07.2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    private List<Contact> contacts;
    private Drawable vkIcon;
    private Drawable fbIcon;
    private boolean alphabeticallySorting = true;
    private ArrayList<String> disabledNetworks;
    private Map<String, List<Contact>> separatedContacts;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ImageView socialIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            socialIcon = (ImageView)itemView.findViewById(R.id.social_logo);
        }
    }

    public ContactsAdapter(List<Contact> contacts, Context ctx){
        this.contacts = new ArrayList<>();
        separatedContacts = new HashMap<>();
        disabledNetworks = new ArrayList<>();
        vkIcon = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.vk, null);
        fbIcon = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.fb2, null);

        addContacts(contacts);
        Collections.sort(this.contacts, contactComparator);
    }


    public void addContacts(List<Contact> newContacts){
        if(newContacts != null && newContacts.size() > 0){
            String network = newContacts.get(0).network;
            if(!disabledNetworks.contains(network)){
                List<Contact> oldContacts = separatedContacts.get(network);
                if(oldContacts !=  null) contacts.removeAll(oldContacts);
                contacts.addAll(newContacts);
                Collections.sort(contacts, contactComparator);
                notifyDataSetChanged();
            }
            separatedContacts.remove(network);
            separatedContacts.put(network, newContacts);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.name.setText(contact.name);
        holder.socialIcon.setImageDrawable(contact.network.equals(Constants.VK_NETWORK_NAME)? vkIcon : fbIcon);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void changeSorting(){
        alphabeticallySorting = !alphabeticallySorting;
        Collections.sort(contacts, contactComparator);
        notifyDataSetChanged();
    }

    private Comparator<Contact> contactComparator = new Comparator<Contact>(){
        @Override
        public int compare(Contact lhs, Contact rhs) {
            return lhs.name.compareTo(rhs.name) * (alphabeticallySorting? 1 : -1);
        }
    };

    public void changeVisibilityForNetworkContacts(boolean visible, String network){
        if(visible) {
            disabledNetworks.remove(network);
            List<Contact> contactsToShow = separatedContacts.get(network);
            if(contactsToShow != null)
                contacts.addAll(contactsToShow);
        }
        else {
            disabledNetworks.add(network);
            List<Contact> contactsToHide = separatedContacts.get(network);
            if(contactsToHide != null)
                contacts.removeAll(contactsToHide);
        }
        notifyDataSetChanged();
    }

    public Contact getContactOnPosition(int pos){
        return contacts.get(pos);
    }

}
