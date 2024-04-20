package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tester.notes.R;
import com.tester.notes.activities.LoginActivity;
import com.tester.notes.activities.RegisterActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTests {
    private LoginActivity activity;
    @Before
    public void setUp(){
        try (ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class)){
            controller.setup();
            activity = controller.get();
        }
    }
    @Test
    public void inputEmailTests(){
        EditText inputEmail = activity.findViewById(R.id.inputEmail);
        String expectedResult = activity.getString(R.string.email_address);
        assertThat(inputEmail.getHint().toString(), equalTo(expectedResult));

        String testerEmail = "tester@mail.com";
        inputEmail.setText(testerEmail);
        assertEquals(testerEmail, inputEmail.getText().toString());

        assertThat(View.VISIBLE, equalTo(inputEmail.getVisibility()));
    }
    @Test
    public void inputPasswordTests(){
        EditText inputPassword = activity.findViewById(R.id.inputPassword);
        String expectedResult = activity.getString(R.string.password);
        assertThat(inputPassword.getHint().toString(), equalTo(expectedResult));

        String testerPass = "testerPass123#";
        inputPassword.setText(testerPass);
        assertEquals(testerPass, inputPassword.getText().toString());

        assertThat(View.VISIBLE, equalTo(inputPassword.getVisibility()));
    }
    @Test
    public void registerButtonTests(){
        try (ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class)){
            controller.setup();
            activity = controller.get();

            Button button = activity.findViewById(R.id.buttonRegister);
            assertTrue(button.isClickable());
            assertEquals(View.VISIBLE, button.getVisibility());

            String buttonExpectedText = activity.getString(R.string.register);
            assertEquals(buttonExpectedText, button.getText().toString());

            button.performClick();
            Intent expectedIntent = new Intent(activity, RegisterActivity.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
    @Test
    public void submitButtonTests(){
        Button button = activity.findViewById(R.id.buttonSubmit);
        assertTrue(button.isClickable());

        String buttonExpectedText = activity.getString(R.string.log_in);
        assertEquals(buttonExpectedText, button.getText().toString());

        assertEquals(View.VISIBLE, button.getVisibility());
    }
}
