package com.alextinekov.socialcontacts.matches;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;

import com.alextinekov.socialcontacts.data.Contact;
import com.alextinekov.socialcontacts.data.ContactsAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by Alex Tinekov on 17.07.2016.
 */
public class CustomMatches {

    public static Matcher<Object> withNetwork(final String network) {
        return new BoundedMatcher<Object, Contact>(Contact.class) {
            @Override
            protected boolean matchesSafely(Contact book) {
                return network.equals(book.network);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with network: " + network);
            }
        };
    }
    public static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof RecyclerView)) {
                    return false;
                }

                @SuppressWarnings("rawtypes")
                ContactsAdapter adapter = (ContactsAdapter) ((RecyclerView) view).getAdapter();
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (dataMatcher.matches(adapter.getContactOnPosition(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
