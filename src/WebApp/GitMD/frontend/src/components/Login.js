import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import Header from './Header';

export default class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "", // Added name field
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
        // Successful login
        this.setState({ isAuthenticated: true });
      } else {
        // Handle login failure
        const errorData = await response.json();
        this.setState({ error: errorData.detail });
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
        // Registration successful
        console.log("Registration successful");
  
        // Now, perform the login with the same credentials
        const loginResponse = await fetch("/api/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ email, password }),
        });
  
        if (loginResponse.ok) {
          // Successful login
          this.setState({ isAuthenticated: true });
        } else {
          // Handle login failure
          const loginErrorData = await loginResponse.json();
          this.setState({ error: loginErrorData.detail });
        }
      } else {
        // Handle registration failure
        console.log("error creating user")
        const errorData = await response.json();
        this.setState({ error: errorData.detail });
      }
    } catch (error) {
      console.error("Error during registration:", error);
    }
  };

  toggleRegister = () => {
    // Toggle between login and registration forms
    this.setState((prevState) => ({
      isRegistering: !prevState.isRegistering,
      error: null, // Clear any previous errors
    }));
  };

  render() {
    const { name, email, password, isAuthenticated, isRegistering, error } = this.state;

    if (isAuthenticated) {
      return <Redirect to="/" />;
    }

    return (
      <div>
        <Header />
        <h2>{isRegistering ? "Register" : "Login"}</h2>
        <form onSubmit={isRegistering ? this.handleRegister : this.handleLogin}>
          {isRegistering && (
            <label>
              Name:
              <input
                type="text"
                name="name"
                value={name}
                onChange={this.handleInputChange}
              />
            </label>
          )}
          <br />
          <label>
            Email:
            <input
              type="text"
              name="email"
              value={email}
              onChange={this.handleInputChange}
            />
          </label>
          <br />
          <label>
            Password:
            <input
              type="password"
              name="password"
              value={password}
              onChange={this.handleInputChange}
            />
          </label>
          <br />
          <button type="submit">{isRegistering ? "Register" : "Login"}</button>
          <button type="button" onClick={this.toggleRegister}>
            {isRegistering ? "Have an account? Login" : "Don't have an account? Register"}
          </button>
          {error && (
            <div style={{ color: "red" }}>
              <p>Error: {error}</p>
            </div>
          )}
        </form>
      </div>
    );
  }
}