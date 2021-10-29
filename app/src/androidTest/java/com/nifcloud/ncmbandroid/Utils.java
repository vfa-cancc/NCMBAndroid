package com.nifcloud.ncmbandroid;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.LoginCallback;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBUser;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

public class Utils {
    /**
     * Perform action of waiting for request API.
     * @param millis The timeout of until when to wait for.
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    public static void createUser(final String userName, final String pass) {
        NCMBUser user = new NCMBUser();
        user.setUserName(userName);
        user.setPassword(pass);
        user.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {

            }
        });
    }

    public static void deleteUserIfExist(final String userName, final String pass) {
        try {
            NCMBUser.loginInBackground(userName, pass, new LoginCallback() {
                @Override
                public void done(NCMBUser ncmbUser, NCMBException e) {
                    if (e == null) {
                        try {
                            ncmbUser.deleteObject();
                            logout();
                        } catch (NCMBException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        } catch (NCMBException e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        try {
            NCMBUser.logout();
        } catch (NCMBException e) {
            e.printStackTrace();
        }
    }

    public static void login(String username, String password) {
        try {
            NCMBUser.loginInBackground(username, password, new LoginCallback() {
                @Override
                public void done(NCMBUser ncmbUser, NCMBException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                }
            });
        } catch (NCMBException e) {
            e.printStackTrace();
        }
    }

}
