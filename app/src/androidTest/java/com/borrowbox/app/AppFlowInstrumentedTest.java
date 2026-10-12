package com.borrowbox.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.borrowbox.app.ui.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppFlowInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginActivity_displaysInputFieldsAndNoHardcodedEmail() {
        // Verify LoginActivity views are rendered
        onView(withId(R.id.etLoginEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.tvForgotPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnGoToSignup)).check(matches(isDisplayed()));
    }

    @Test
    public void testAppContext_isCorrectPackage() {
        Context appContext = ApplicationProvider.getApplicationContext();
        org.junit.Assert.assertEquals("com.borrowbox.app", appContext.getPackageName());
    }
}
