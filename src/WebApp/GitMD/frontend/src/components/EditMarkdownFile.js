import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";
import Header from './Header';

export default class EditMarkdownFile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: "",
      content: "",
      sha: "",
    };
  }

  componentDidMount() {
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;
    console.log(user, repo, file)

    fetch(`/api/${user}/${repo}/${file}`)
      .then((response) => response.json())
      .then((data) => {
        this.setState({
          title: data.name,
          content: atob(data.content),
          sha: data.sha
        });
      })
      .catch((error) => {
        console.error("Error fetching markdown content:", error);
      });
  }

  handleTitleChange = (event) => {
    this.setState({ title: event.target.value });
  };

  handleContentChange = (event) => {
    this.setState({ content: event.target.value });
  };

  handleSubmit = () => {
    const { title, content } = this.state;
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;
    console.log("Submitting with:", { user, repo, file });

    if (!title || !content) {
      console.error("Title and content are required.");
      return;
    }

    fetch(`/api/edit`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        user: user,
        content: this.state.content,
        repo: repo,
        file: file,
        sha: this.state.sha,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("File updated successfully:", data);
      })
      .catch((error) => {
        console.error("Error updating file:", error);
      });
  };

  render() {
    return (
      <div>
        <Header />
        <div className="editor-container">
          <Typography component="h4" variant="h4" align="center">
            Edit Markdown File
          </Typography>
          <TextField
            label="Title"
            variant="outlined"
            fullWidth
            margin="normal"
            value={this.state.title.slice(0, -3)}
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
            Save
          </Button>
        </div>
      </div>
    );
  }
}