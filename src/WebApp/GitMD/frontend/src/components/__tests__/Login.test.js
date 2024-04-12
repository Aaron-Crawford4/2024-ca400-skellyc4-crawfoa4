import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import Login from '../Login';
import fetchMock from 'jest-fetch-mock';
fetchMock.enableMocks();

describe('Login component', () => {
  it('renders sign-in form by default', () => {
    render(<Login />);
    expect(screen.getByText('Sign in')).toBeInTheDocument();
    expect(screen.queryByText('Register')).not.toBeInTheDocument();
    expect(screen.queryByText('Password Reset')).not.toBeInTheDocument();
  });

  it('toggles to registration form when "Register" link is clicked', () => {
    render(<Login />);
    fireEvent.click(screen.getByText('Don\'t have an account? Register'));
    expect(screen.getAllByText('Register')).toHaveLength(2);
    expect(screen.queryByText('Sign in')).not.toBeInTheDocument();
    expect(screen.queryByText('Password Reset')).not.toBeInTheDocument();
  });

  it('toggles to password reset form when "Forgot Password?" link is clicked', () => {
    render(<Login />);
    fireEvent.click(screen.getByText('Forgot Password?'));
    expect(screen.getByText('Password Reset')).toBeInTheDocument();
    expect(screen.queryByText('Sign in')).not.toBeInTheDocument();
    expect(screen.queryByText('Register')).not.toBeInTheDocument();
  });

  it('fills in the sign-in form', () => {
    render(<Login />);
    expect(screen.getByText('Sign in')).toBeInTheDocument();
    // Find input fields and submit button
    const emailInput = screen.getByLabelText('Email Address', {exact:false});
    const passwordInput = screen.getByLabelText('Password', {exact:false});
    const submitButton = screen.getByRole('button', { name: 'Log In' });

    // Fill in the input fields
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    // Submit the form
    fireEvent.click(submitButton);

    // Assert that the form is submitted correctly
    expect(emailInput).toHaveValue('test@example.com');
    expect(passwordInput).toHaveValue('password123');
  });
  it('fills in the register form', () => {
    render(<Login />);
    fireEvent.click(screen.getByText('Don\'t have an account? Register'));
    expect(screen.getAllByText('Register')).toHaveLength(2);
    expect(screen.queryByText('Sign in')).not.toBeInTheDocument();
    expect(screen.queryByText('Password Reset')).not.toBeInTheDocument();

    const emailInput = screen.getByLabelText('Email Address', {exact:false});
    const usernameInput = screen.getByLabelText('Username', {exact:false});
    const passwordInput = screen.getByLabelText('Password', {exact:false});
    const submitButton = screen.getByRole('button', { name: 'Register' });

    // Fill in the input fields
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(usernameInput, { target: { value: 'test' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    // Submit the form
    fireEvent.click(submitButton);

    // Assert that the form is submitted correctly
    expect(emailInput).toHaveValue('test@example.com');
    expect(usernameInput).toHaveValue('test');
    expect(passwordInput).toHaveValue('password123');
  });

  it('complete the forget password process', async () => {
    render(<Login />);
    fireEvent.click(screen.getByText('Forgot Password?'));
    expect(screen.getByText('Password Reset')).toBeInTheDocument();

    // Mock API request
    const fetchMockPromise = Promise.resolve({
      json: () => Promise.resolve({ message: 'Email sent successfully' })
    });
    jest.spyOn(global, 'fetch').mockImplementation(() => fetchMockPromise);

    const emailInput = screen.getByLabelText('Email Address', {exact:false});
    const requestButton = screen.getByRole('button', { name: 'Request Email' });

    // Fill in the input fields for password reset
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });

    // Submit the form for password reset
    fireEvent.click(requestButton);

    // Wait for the asynchronous operation to complete
    await act(async () => {
      await fetchMockPromise;
    });

    // Assert that the form is submitted correctly
    expect(emailInput).toHaveValue('test@example.com');
    
    expect(screen.getByText('An email will be sent to you including a reset token enter it below to reset your password')).toBeInTheDocument();
    const tokenInput = screen.getByLabelText('Reset Token', {exact:false});
    const passwordInputs = screen.getAllByLabelText('Password', { exact: false });
    const submitButton = screen.getByRole('button', { name: 'Reset Password' });

    fireEvent.change(tokenInput, { target: { value: 'exampleToken' } });
    passwordInputs.forEach(input => {
      fireEvent.change(input, { target: { value: 'newpassword123' } });
    });

    fireEvent.click(submitButton);

    expect(tokenInput).toHaveValue('exampleToken');
    passwordInputs.forEach(input => {
      expect(input).toHaveValue('newpassword123');
    });
  });
});

