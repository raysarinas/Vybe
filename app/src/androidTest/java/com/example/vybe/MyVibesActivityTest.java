package com.example.vybe;

import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.vybe.AddEdit.AddEditVibeEventActivity;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
@RunWith(AndroidJUnit4.class)
public class MyVibesActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MyVibesActivity> rule =
            new ActivityTestRule<>(MyVibesActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void NewVibeButton_CreateVibe_Pass() {
        solo.assertCurrentActivity("Wrong Activity", MyVibesActivity.class);

        // Go to AddEditVibeEventActivity and confirm the current activity changes
        solo.clickOnButton("ADD");

        solo.assertCurrentActivity("Wrong Activity", AddEditVibeEventActivity.class);

    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}