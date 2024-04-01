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
import { createTheme, ThemeProvider } from '@mui/material/styles';

export default class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      email: "",
      password: "",
      isAuthenticated: false,
      isRegistering: false,
      error: null,
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

  render() {
    const { name, email, password, isAuthenticated, isRegistering, error } = this.state;

    if (isAuthenticated) {
      return <Redirect to="/" />;
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
              <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                <LockOutlinedIcon />
              </Avatar>
              <Typography component="h1" variant="h5">
                Sign in
              </Typography>
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
                <Grid container>
                  <Grid item>
                    <Link onClick={this.toggleRegister} variant="body2">
                    {isRegistering ? "Have an account? Login" : "Don't have an account? Register"}
                    </Link>
                  </Grid>
                </Grid>
              </Box>
              {error && (
                <div style={{ color: "red" }}>
                  <p>Error: {error}</p>
                </div>
              )}
            </Box>
          </Container>
        </Box>
      );
  }
}