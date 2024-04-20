import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import PasswordIcon from '@mui/icons-material/Password';

export default class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      email: "",
      password: "",
      password2: "",
      resetToken: "",
      isAuthenticated: false,
      isRegistering: false,
      error: null,
      forgotPassword: false,
      requestSent: false
    };
  }

  handleInputChange = (e) => {
    this.setState({ [e.target.name]: e.target.value });
  };

  handleLogin = async (e) => {
    e.preventDefault();

    const { email, password } = this.state;

    try {
      const response = await fetch("/api/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        this.setState({ isAuthenticated: true });
      } else {
        const errorData = await response.json();
        this.setState({ error: errorData.detail });
        console.log(this.state.error)
      }
    } catch (error) {
      console.error("Error during login:", error);
    }

  };

  handleRegister = async (e) => {
    e.preventDefault();
  
    const { name, email, password } = this.state;
  
    try {
      const response = await fetch("/api/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name, email, password }),
      });
      if (response.ok) {
        console.log("Registration successful");
  
        const loginResponse = await fetch("/api/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ email, password }),
        });
  
        if (loginResponse.ok) {
          this.setState({ isAuthenticated: true });
        } else {
          const loginErrorData = await loginResponse.json();
          this.setState({ error: loginErrorData.detail });
        }
      } else {
        console.log("error creating user")
        const errorData = await response.json();
        this.setState({ error: errorData.detail });
      }
    } catch (error) {
      console.error("Error during registration:", error);
    }
  };

  toggleRegister = () => {
    this.setState((prevState) => ({
      isRegistering: !prevState.isRegistering,
      error: null,
    }));
  };

  toggleForgotPassword = () => {
    this.setState((prevState) => ({
      forgotPassword: !prevState.forgotPassword,
    }));
  };

  sendEmail = async (e) => {
    e.preventDefault();

    try {
      fetch("/api/passwordReset", {
          method: "PUT",
          credentials: "include",
          headers: { 
              "Content-Type": "application/json" 
          },
          body: JSON.stringify({
              type: "request",
              email: this.state.email
            }),
      })
          .then((response) => response.json())
          .then(() => {
            this.setState({requestSent: true})
          })
    }
    catch(error) {
      console.error("Error sending email:", error);
    };
  }

  resetPassword = async (e) => {
    e.preventDefault();
    try {
      if(this.state.password == this.state.password2) {

      fetch("/api/passwordReset", {
          method: "PUT",
          credentials: "include",
          headers: { 
              "Content-Type": "application/json" 
          },
          body: JSON.stringify({
              type: "reset",
              email: this.state.email,
              resetToken: this.state.resetToken,
              newPassword: this.state.password2
            }),
      })
          .then((response) => response.json())
          .then(() => {
            window.location.reload();
          })
          .catch((error) => {
              console.error("Error resetting email:", error);
          });
      }
      else {
        this.setState({error: "Passwords do not match"})
      }
    }
    catch(error) {
      console.error("Error resetting password:", error);
    };
  }

  render() {
    const { name, email, password, password2, resetToken, isAuthenticated, isRegistering, error } = this.state;

    if (isAuthenticated) {
      window.location.href = '/';
    }

    return (
        <Box component="main" sx={{ flexGrow: 1, p: 8 }}>
          <Container component="main" maxWidth="xs">
            <CssBaseline />
            <Box
              sx={{
                marginTop: 8,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
              }}
            >
              {!this.state.forgotPassword && (
                <>
                <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                  <LockOutlinedIcon />
                </Avatar>
                {!this.state.isRegistering ? (
                  <Typography component="h1" variant="h5">
                    Sign in
                  </Typography>
                ) : (
                  <Typography component="h1" variant="h5">
                    Register
                  </Typography>
                )}
                <Box component="form" onSubmit={isRegistering ? this.handleRegister : this.handleLogin} noValidate sx={{ mt: 1 }}>
                {isRegistering && ( <TextField
                    margin="normal"
                    required
                    fullWidth
                    type="text"
                    label="Username"
                    name="name"
                    value={name}
                    onChange={this.handleInputChange}
                    autoFocus
                  />
                )}
                  <TextField
                    margin="normal"
                    required
                    fullWidth
                    type="text"
                    label="Email Address"
                    name="email"
                    value={email}
                    onChange={this.handleInputChange}
                    autoFocus
                  />
                  <TextField
                    margin="normal"
                    required
                    fullWidth
                    type="password"
                    label="Password"
                    name="password"
                    value={password}
                    onChange={this.handleInputChange}
                  />
                  <p style={{ fontSize: '0.8rem' }}> {isRegistering ? "*Password must be minimum 8 characters" : ""}</p>
                  <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    sx={{ mt: 3, mb: 2 }}
                  >
                    {isRegistering ? "Register" : "Log In"}
                  </Button>
                  <Grid container style={{ display: 'flex', justifyContent: 'space-between'}}>
                    <Grid item>
                      <Link onClick={this.toggleRegister} variant="body2">
                      {isRegistering ? "Have an account? Login" : "Don't have an account? Register"}
                      </Link>
                    </Grid>
                    <Grid item>
                      <Link onClick={this.toggleForgotPassword} variant="body2">
                      Forgot Password?
                      </Link>
                    </Grid>
                  </Grid>
                </Box>
                {error && (
                  <div style={{ color: "red" }}>
                    <p>Error: {error}</p>
                  </div>
                )}
                </>
              )}
              {this.state.forgotPassword && (
                <>
                  <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                    <PasswordIcon />
                  </Avatar>
                  <Typography component="h1" variant="h5">
                    Password Reset
                  </Typography>
                  {!this.state.requestSent && (
                    <>
                      <TextField
                        margin="normal"
                        required
                        fullWidth
                        type="text"
                        label="Email Address"
                        name="email"
                        value={email}
                        onChange={this.handleInputChange}
                        autoFocus
                      />
                      <Button
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        onClick={this.sendEmail}
                      >
                        Request Email
                      </Button>
                    </>
                  )}
                  {this.state.requestSent && (
                    <>
                    <Typography component="h6" variant="body1" textAlign="center">
                      An email will be sent to you including a reset token enter it below to reset your password
                    </Typography>
                    <TextField
                      margin="normal"
                      required
                      fullWidth
                      type="text"
                      label="Reset Token"
                      name="resetToken"
                      value={resetToken}
                      onChange={this.handleInputChange}
                    />
                    <TextField
                      margin="normal"
                      required
                      fullWidth
                      type="password"
                      label="Password"
                      name="password"
                      value={password}
                      onChange={this.handleInputChange}
                    />
                    <TextField
                      margin="normal"
                      required
                      fullWidth
                      type="password"
                      label="Confirm Password"
                      name="password2"
                      value={password2}
                      onChange={this.handleInputChange}
                    />
                    <Button
                      fullWidth
                      variant="contained"
                      sx={{ mt: 3, mb: 2 }}
                      onClick={this.resetPassword}
                    >
                      Reset Password
                    </Button>
                    </>
                  )}
                </>
              )}
            </Box>
          </Container>
        </Box>
      );
  }
}