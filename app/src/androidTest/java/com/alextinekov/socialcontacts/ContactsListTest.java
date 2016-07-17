package com.alextinekov.socialcontacts;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.alextinekov.socialcontacts.data.Contact;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.is;
import static com.alextinekov.socialcontacts.matches.CustomMatches.withNetwork;
import static com.alextinekov.socialcontacts.matches.CustomMatches.withAdaptedData;
import static org.hamcrest.CoreMatchers.not;


/**
 * Created by Alex Tinekov on 17.07.2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContactsListTest {
    @Rule
    public ActivityTestRule<ContactsActivity> activityRule = new ActivityTestRule<>(ContactsActivity.class);

    @Test
    public void switchVKTest(){
        onView(withId(R.id.vk_contacts)).perform(click());

        onView(withId(R.id.contacts_list)).check(matches(not(withAdaptedData(is(withNetwork(Constants.VK_NETWORK_NAME))))));
    }

    @Test
    public void switchFBTest(){
        onView(withId(R.id.fb_contacts)).perform(click());

        onView(withId(R.id.contacts_list)).check(matches(not(withAdaptedData(is(withNetwork(Constants.FB_NETWORK_NAME))))));
    }
}
