package com.alextinekov.socialcontacts.data;

import android.content.Context;

/**
 * Created by Alex Tinekov on 15.07.2016.
 */
public class Contact {
    public Contact(String network, String name){
        this.network = network;
        this.name = name;
    }
    public Contact(String name, String bday, String email, String phone, String photoUrl, String network){
        this.name = name;
        this.bday = bday;
        this.email = email;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.network = network;
    }
    public String name;
    public String bday;
    //It looks like email is unavailable in vk and fb. Contacts permission gives you access only to mobile and home phones.
    public String email;
    public String phone;
    public String photoUrl;
    public String network;
}
