import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";
import ReactMarkdown from "react-markdown";
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import remarkGfm from 'remark-gfm';
import rehypeRaw from 'rehype-raw'

export default class EditMarkdownFile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: "",
      content: "",
      markdownContent: "",
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
          title: data.name.slice(0, -3),
          content: atob(data.content),
          markdownContent: atob(data.content),
          sha: data.sha
        });
      })
      .catch((error) => {
        console.error("Error fetching markdown content:", error);
      });
  }

  handleTitleChange = (event) => {
    console.log(event.target.value)
    this.setState({ title: event.target.value });
  };

  handleContentChange = (event) => {
    this.setState({ content: event.target.value, markdownContent: event.target.value, });
  };

  handleSubmit = () => {
    const { title, content } = this.state;
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;

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
      <Box component="main" sx={{ flexGrow: 1, p: 8 }}>
        <div className="editor-container">
        <div style={{ display: 'flex', flexDirection: 'row' }}>
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
          <TextField
            label="Title"
            variant="outlined"
            fullWidth
            multiline
            rows={1}
            margin="normal"
            value={this.state.title}
            onChange={this.handleTitleChange}
          />
          <div style={{ display: 'flex', gap: '0x', flexWrap: 'wrap' }}>
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
                  width: '100%',
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
                <ReactMarkdown remarkPlugins={[remarkGfm]}  children={this.state.markdownContent}></ReactMarkdown>
            </Typography>
            </Paper>
          </div>
        </div>
      </Box>
    );
  }
}