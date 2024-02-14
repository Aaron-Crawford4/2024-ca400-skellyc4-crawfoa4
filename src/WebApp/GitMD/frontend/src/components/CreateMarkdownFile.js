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
      title: "",
      content: "",
    };
  }

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
        title: this.state.title,
        content: this.state.content,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("File created successfully:", data);
      })
      .catch((error) => {
        console.error("Error creating file:", error);
      });
  };

  render() {
    return (
      <div>
        <Header />
        <div className="editor-container">
          <Typography component="h4" variant="h4" align="center">
            Create Markdown File
          </Typography>
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