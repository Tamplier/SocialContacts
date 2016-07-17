package com.alextinekov.socialcontacts;

import com.vk.sdk.VKScope;

/**
 * Created by Alex Tinekov on 14.07.2016.
 */
public class Constants {
    public static final String[] VK_SCOPES = new String[]{VKScope.FRIENDS};
    public static final String[] FB_SCOPES = new String[]{"user_friends", "user_birthday"};
    public static final String VK_FIELDS = "id,first_name,last_name,photo_max_orig,contacts,bdate";
    public static final String FB_FIELDS = "id, name, birthday, picture";
    public static final String FB_NETWORK_NAME = "Facebook";
    public static final String VK_NETWORK_NAME = "ВКонтакте";
}
