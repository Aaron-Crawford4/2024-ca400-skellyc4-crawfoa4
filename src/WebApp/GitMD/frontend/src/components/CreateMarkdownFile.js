import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";
import Header from './Header';

export default class CreateMarkdownFile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      repoTitle: "",
      title: "",
      content: "",
    };
  }

  handlerepoTitleChange = (event) => {
    this.setState({ repoTitle: event.target.value });
  };

  handleTitleChange = (event) => {
    this.setState({ title: event.target.value });
  };

  handleContentChange = (event) => {
    this.setState({ content: event.target.value });
  };

  handleSubmit = () => {
    const { title, content } = this.state;

    if (!title || !content) {
      console.error("Title and content are required.");
      return;
    }

    fetch("/api/create", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        repoTitle: this.state.repoTitle,
        title: this.state.title,
        content: this.state.content,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("repo created successfully:", data);
      })
      .catch((error) => {
        console.error("Error creating repo:", error);
      });
  };

  render() {
    return (
      <div>
        <Header />
        <div className="editor-container">
          <Typography component="h4" variant="h4" align="center">
            Create Repository
          </Typography>
          <TextField
            label="Repository Title"
            variant="outlined"
            fullWidth
            margin="normal"
            value={this.state.repoTitle}
            onChange={this.handlerepoTitleChange}
          />
          <TextField
            label="Title"
            variant="outlined"
            fullWidth
            margin="normal"
            value={this.state.title}
            onChange={this.handleTitleChange}
          />
          <TextField
            label="Content"
            variant="outlined"
            fullWidth
            multiline
            rows={10}
            margin="normal"
            value={this.state.content}
            onChange={this.handleContentChange}
          />
          <Button
            variant="contained"
            color="primary"
            component={Link}
            to="/"
            onClick={this.handleSubmit}
          >
            Create
          </Button>
        </div>
      </div>
    );
  }
}