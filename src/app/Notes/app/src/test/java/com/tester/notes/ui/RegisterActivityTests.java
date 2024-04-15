package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tester.notes.R;
import com.tester.notes.activities.RegisterActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class RegisterActivityTests {
    private RegisterActivity activity;
    @Before
    public void setUp(){
        try (ActivityController<RegisterActivity> controller = Robolectric.buildActivity(RegisterActivity.class)){
            controller.setup();
            activity = controller.get();
        }
    }
    @Test
    public void inputUsernameTests(){
        EditText inputUsername = activity.findViewById(R.id.inputUsername);
        String expectedResult = activity.getString(R.string.username);
        assertThat(inputUsername.getHint().toString(), equalTo(expectedResult));

        String testerUsername = "testerName";
        inputUsername.setText(testerUsername);
        assertEquals(testerUsername, inputUsername.getText().toString());

        assertThat(View.VISIBLE, equalTo(inputUsername.getVisibility()));
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
    public void submitButtonTests(){
        Button button = activity.findViewById(R.id.buttonSubmit);
        assertTrue(button.isClickable());

        String buttonExpectedText = activity.getString(R.string.submit);
        assertEquals(buttonExpectedText, button.getText().toString());

        assertEquals(View.VISIBLE, button.getVisibility());
    }
}
