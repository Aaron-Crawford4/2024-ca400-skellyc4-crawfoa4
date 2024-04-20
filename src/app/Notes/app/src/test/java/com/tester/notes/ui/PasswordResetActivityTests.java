package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tester.notes.R;
import com.tester.notes.activities.PasswordResetActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class PasswordResetActivityTests {
    private PasswordResetActivity activity;
    @Before
    public void setUp(){
        try (ActivityController<PasswordResetActivity> controller = Robolectric.buildActivity(PasswordResetActivity.class)){
            controller.setup();
            activity = controller.get();
        }
    }
    @Test
    public void inputResetTokenTests(){
        EditText inputResetToken = activity.findViewById(R.id.inputResetToken);

        String expectedResult = activity.getString(R.string.reset_token);
        assertThat(inputResetToken.getHint().toString(), equalTo(expectedResult));

        String testerToken = "sdfkldfklskd";
        inputResetToken.setText(testerToken);
        assertEquals(testerToken, inputResetToken.getText().toString());

        assertThat(View.VISIBLE, equalTo(inputResetToken.getVisibility()));
    }
    @Test
    public void inputPasswordTests(){
        EditText inputPassword = activity.findViewById(R.id.inputPassword);

        String expectedResult = activity.getString(R.string.new_password);
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

        String buttonExpectedText = activity.getString(R.string.set_new_password);
        assertEquals(buttonExpectedText, button.getText().toString());

        assertEquals(View.VISIBLE, button.getVisibility());
    }
}
