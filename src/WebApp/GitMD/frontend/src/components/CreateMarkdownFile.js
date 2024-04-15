import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";
import ReactMarkdown from "react-markdown";
import Paper from '@mui/material/Paper';
import remarkGfm from 'remark-gfm';
import Box from '@mui/material/Box';

export default class CreateMarkdownFile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      repoTitle: "",
      repoTitleError: "",
      title: "",
      content: "",
      markdownContent: "",
      repoNameFromParams: "",
    };
  }

  componentDidMount() {
    try {
      const { repo } = this.props.match.params;
      this.setState({ repoNameFromParams: repo, repoTitle: repo }, () => {
        console.log(this.state.repoNameFromParams);
      });}
    catch{
      console.log("here2")
    }
  }

  handlerepoTitleChange = (event) => {
    const newRepoTitle = event.target.value;

    if (newRepoTitle.includes(' ')) {
      this.setState({ repoTitleError: "Repository title cannot contain space characters." });
    } else {
      this.setState({ repoTitle: newRepoTitle, repoTitleError: "" });
    }
  };

  handleTitleChange = (event) => {
    this.setState({ title: event.target.value });
  };

  handleContentChange = (event) => {
    this.setState({
      content: event.target.value,
      markdownContent: event.target.value,
    });
  };

  handleSubmit = () => {
    const { title, content } = this.state;

    if (!title || !content) {
      console.error("Title and content are required.");
      return;
    }
    if (this.state.repoTitleError != "") {
      console.error("Repository title cannot contain space character");
      return;
    }
    console.log("here")
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
        this.props.history.push('/');
      })
      .catch((error) => {
        console.error("Error creating repo:", error);
      });
  };

  render() {
    return (
      <Box component="main" sx={{ flexGrow: 1, p: 8 }}>
        <div className="editor-container">
          <div style={{ display: 'flex', flexDirection: 'row' }}>
            <Button
              variant="contained"
              color="primary"
              component={Link}
              onClick={this.handleSubmit}
            >
              Create
            </Button>
            <Typography component="h4" variant="h4" textAlign={"center"} style={{ marginLeft: '38%' }}>
              Create A File
            </Typography>
          </div>
          {this.state.repoNameFromParams === undefined && (
          <TextField
            label="Repository Title"
            variant="outlined"
            fullWidth
            margin="normal"
            value={this.state.repoTitle}
            onChange={this.handlerepoTitleChange}
            error={Boolean(this.state.repoTitleError)}
            helperText={this.state.repoTitleError}
          />
        )}
          <TextField
            label="First File Title"
            variant="outlined"
            fullWidth
            margin="normal"
            value={this.state.title}
            onChange={this.handleTitleChange}
          />
          <div style={{ display: 'flex', flexWrap: 'wrap', flexDirection: 'row' }}>
          <Paper elevation={3} className="paper-container">
            <TextField
              label="Content"
              margin="none"
              multiline
              fullWidth
              inputProps={{
                style: {
                  fontSize: 16,
                  fontFamily: 'Arial',
                  lineHeight: '1.5',
                },
              }}
              value={this.state.content}
              onChange={this.handleContentChange}
            />
            </Paper>
            <Paper elevation={3} className="paper-container" >
            <Typography 
              label="Markdown Content"
              multiline
              fullWidth
              style={{
                overflowWrap: 'break-word',
                maxWidth: '100%',
                paddingLeft: "10px",
                paddingRight: "10px",
              }}
              margin="normal">
              <ReactMarkdown remarkPlugins={[remarkGfm]}  children={this.state.markdownContent} ></ReactMarkdown>
            </Typography>
            </Paper>
          </div>
        </div>
      </Box>
    );
  }
}