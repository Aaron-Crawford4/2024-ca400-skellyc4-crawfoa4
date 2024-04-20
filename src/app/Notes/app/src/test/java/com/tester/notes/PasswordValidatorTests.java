package com.tester.notes;

import static com.tester.notes.utils.PasswordValidator.isValidPassword;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordValidatorTests {
    @Test
    public void noUppercasePassword() {
        String noUppercasePassword = "password##23";
        assertFalse(isValidPassword(noUppercasePassword));
    }
    @Test
    public void noLowercasePassword() {
        String noLowercasePassword = "PASSWORD##23";
        assertFalse(isValidPassword(noLowercasePassword));
    }
    @Test
    public void noSymbolPassword() {
        String noSymbolPassword = "Password23";
        assertFalse(isValidPassword(noSymbolPassword));
    }
    @Test
    public void noNumberPassword() {
        String noNumberPassword = "Password##";
        assertFalse(isValidPassword(noNumberPassword));
    }
    @Test
    public void tooShortPassword() {
        String tooShortPassword = "Pass2#";
        assertFalse(isValidPassword(tooShortPassword));
    }
    @Test
    public void ValidPassword() {
        String ValidPassword = "Password#23";
        assertTrue(isValidPassword(ValidPassword));
    }
}
