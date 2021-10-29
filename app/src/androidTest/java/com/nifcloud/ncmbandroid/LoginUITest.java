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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.nifcloud.ncmbandroid.Utils.logout;
import static com.nifcloud.ncmbandroid.Utils.waitFor;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginUITest {

    ViewInteraction edtName;
    ViewInteraction edtPass;
    ViewInteraction btnLogin;
    ViewInteraction btnSignup;
    ViewInteraction btnAutoLogin;
    ViewInteraction btnSocialLogin;
    ViewInteraction loadingView;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        edtName = onView(withId(R.id.username));
        edtPass = onView(withId(R.id.password));
        btnLogin = onView(withId(R.id.login));
        btnSignup = onView(withId(R.id.signup));
        btnAutoLogin = onView(withId(R.id.autoLogin));
        btnSocialLogin = onView(withId(R.id.socialLogin));
        loadingView = onView(withId(R.id.loading));
    }

    @After
    public void afterTest() {
        logout();
    }

    @Test
    public void initialScreen() {
        edtName.check(matches(isDisplayed()));
        edtPass.check(matches(isDisplayed()));
        btnLogin.check(matches(withText("Sign in")));
        btnSignup.check(matches(withText("Sign up")));
        btnAutoLogin.check(matches(withText("Use auto login")));
        btnSocialLogin.check(matches(withText("Social Login")));
    }

    @Test
    public void validateUsername() {
        edtName.perform(typeText(""));
        edtPass.perform(typeText("123456"), pressImeActionButton());
        btnLogin.check(matches(not(isEnabled())));
        btnSignup.check(matches(not(isEnabled())));
    }

    @Test
    public void validateEmptyPassword() {
        edtName.perform(typeText("Hoge"));
        edtPass.perform(typeText(""), pressImeActionButton());
        btnLogin.check(matches(not(isEnabled())));
        btnSignup.check(matches(not(isEnabled())));
    }

    @Test
    public void signinWithWrongPasswd() {
        edtName.perform(typeText("testuser"));
        edtPass.perform(typeText("12345"), pressImeActionButton());
        btnLogin.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Authentication error with ID/PASS incorrect.")).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin() {
        Utils.createUser("hoge", "123456");
        onView(isRoot()).perform(waitFor(3000));
        edtName.perform(typeText("hoge"));
        edtPass.perform(typeText("123456"), pressImeActionButton());
        btnLogin.perform(click());
        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.textView)).check(matches(withText("ANDROID SDK TEST")));
    }

    @Test
    public void testAutoLogin() {
        btnAutoLogin.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withId(R.id.textView)).check(matches(withText("ANDROID SDK TEST")));
    }

    @Test
    public void testSignupError() {
        Utils.createUser("hoge", "123456");
        onView(isRoot()).perform(waitFor(3000));
        edtName.perform(typeText("hoge"));
        edtPass.perform(typeText("123456"), pressImeActionButton());
        btnSignup.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("userName is duplication.")).check(matches(isDisplayed()));
    }

    @Test
    public void testSignupSuccess() {
        Utils.deleteUserIfExist("hoge", "123456");
        onView(isRoot()).perform(waitFor(3000));
        edtName.perform(typeText("hoge"));
        edtPass.perform(typeText("123456"), pressImeActionButton());
        btnSignup.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withText("Signup success! Please Signin to continue!")).check(matches(isDisplayed()));
    }


}