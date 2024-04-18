package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tester.notes.R;
import com.tester.notes.activities.RequestResetTokenActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class RequestResetTokenActivityTests {
    private RequestResetTokenActivity activity;
    @Before
    public void setUp(){
        try (ActivityController<RequestResetTokenActivity> controller = Robolectric.buildActivity(RequestResetTokenActivity.class)){
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
    public void submitButtonTests(){
        Button button = activity.findViewById(R.id.buttonSubmit);
        assertTrue(button.isClickable());

        String buttonExpectedText = activity.getString(R.string.get_reset_token);
        assertEquals(buttonExpectedText, button.getText().toString());

        assertEquals(View.VISIBLE, button.getVisibility());
    }
}
