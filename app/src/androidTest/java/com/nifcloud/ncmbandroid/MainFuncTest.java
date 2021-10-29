package com.nifcloud.ncmbandroid;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.nifcloud.ncmbandroid.Utils.logout;
import static com.nifcloud.ncmbandroid.Utils.waitFor;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainFuncTest {
    static ViewInteraction edtName;
    static ViewInteraction edtPass;
    static ViewInteraction btnLogin;
    ViewInteraction btnSave;
    ViewInteraction btnGet;
    ViewInteraction btnSaveFile;
    ViewInteraction btnGetFile;
    ViewInteraction btnScript;
    ViewInteraction btnGetInstallation;
    ViewInteraction btnPush;
    ViewInteraction btnRichPush;
    ViewInteraction btnDialogPush;
    ViewInteraction btnSilentPush;
    ViewInteraction btnLogout;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        edtName = onView(withId(R.id.username));
        edtPass = onView(withId(R.id.password));
        btnLogin = onView(withId(R.id.login));

        Utils.createUser("hoge", "123456");
        onView(isRoot()).perform(waitFor(3000));
        edtName.perform(typeText("hoge"));
        edtPass.perform(typeText("123456"), pressImeActionButton());
        btnLogin.perform(click());
        onView(isRoot()).perform(waitFor(3000));

        btnSave = onView(withId(R.id.saveBtn));
        btnGet = onView(withId(R.id.getBtn));
        btnSaveFile = onView(withId(R.id.saveFileBtn));
        btnGetFile = onView(withId(R.id.getFileBtn));
        btnScript = onView(withId(R.id.scriptBtn));
        btnGetInstallation = onView(withId(R.id.installationBtn));
        btnPush =  onView(withId(R.id.pushBtn));
        btnRichPush =  onView(withId(R.id.richPushBtn));
        btnDialogPush =  onView(withId(R.id.pushDialogBtn));
        btnSilentPush =  onView(withId(R.id.slientPushBtn));
        btnLogout = onView(withId(R.id.logOut));
    }

    @After
    public void afterTest() {
        logout();
    }

    @Test
    public void initialScreen() {
        onView(withId(R.id.textView)).check(matches(withText("ANDROID SDK TEST")));
        btnSave.check(matches(isDisplayed()));
        btnGet.check(matches(isDisplayed()));
        btnSaveFile.check(matches(isDisplayed()));
        btnGetFile.check(matches(isDisplayed()));
        btnScript.check(matches(isDisplayed()));
        btnGetInstallation.check(matches(isDisplayed()));
        btnGetInstallation.check(matches(isDisplayed()));
        btnPush.check(matches(isDisplayed()));
        btnRichPush.check(matches(isDisplayed()));
        btnDialogPush.check(matches(isDisplayed()));
        btnSilentPush.check(matches(isDisplayed()));
        btnLogout.check(matches(isDisplayed()));
    }

    @Test
    public void testSaveData() {
        btnSave.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Save object successfully")).check(matches(isDisplayed()));
        onView(withText("OK")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void testGetData() {
        btnGet.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Get objects")).check(matches(isDisplayed()));
        onView(withText("OK")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void testSaveFile() {
        btnSaveFile.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Save file successfully")).check(matches(isDisplayed()));
        onView(withText("OK")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void testGetFile() {
        btnSaveFile.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("OK")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        btnGetFile.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Info")).check(matches(isDisplayed()));
    }

    @Test
    public void testExecuteGetScript() {
        btnScript.perform(scrollTo()).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Info")).check(matches(isDisplayed()));
    }

    @Test
    public void testGetInstallation() {
        btnGetInstallation.perform(scrollTo()).perform(scrollTo()).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Info")).check(matches(isDisplayed()));
    }

    @Test
    public void testSendPush() {
        btnPush.perform(scrollTo()).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Send push Successful")).check(matches(isDisplayed()));
    }

    @Test
    public void testSendRichPush() {
        btnRichPush.perform(scrollTo()).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Send push Successful")).check(matches(isDisplayed()));
    }

    @Test
    public void testSendDialogPush() {
        btnDialogPush.perform(scrollTo()).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Send push Successful")).check(matches(isDisplayed()));
    }

    @Test
    public void testLogoutBtn() {
        btnLogout.perform(scrollTo()).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        btnLogin.check(matches(withText("Sign in")));
    }

}