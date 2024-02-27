import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import Header from './Header';
import ReactMarkdown from "react-markdown";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';

export default class IndivMarkdown extends Component {
  constructor(props) {
    super(props);
    this.state = {
      markdown: [],
      title: "",
      sha: "",
    };
  }

  componentDidMount() {
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;

    fetch(`/api/${user}/${repo}/${file}`)
      .then((response) => response.json())
      .then((data) => {
        this.setState({ 
          markdown: data,
          sha: data.sha
        });
      })
      .catch((error) => {
        console.error("Error fetching markdown content:", error);
      });
  }

  handleDelete = () => {
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;

    fetch("/api/delete", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        file: file,
        repo: repo,
        user: user,
        sha: this.state.sha,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("File deleted successfully:", data);
      })
      .catch((error) => {
        console.error("Error deleting file:", error);
      });
  };

  render() {
    const { markdown } = this.state;
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;
    if(markdown.content != undefined)
      var content = atob(markdown.content);
      console.log(content)
    return (
      <div>
      <Header />
      <div className="container">
        <div className="markdown-container">
          <div className="editor-container">
            <Typography component="h4" variant="h4" className="editor-title">
              {file.slice(0, -3)}
            </Typography>
            <Typography component="div" variant="body1" className="editor-content">
              <ReactMarkdown>{content}</ReactMarkdown>
            </Typography>
          </div>
        </div>
        <div>
        <Button
            variant="contained"
            color="error"
            component={Link}
            to="/"
            onClick={this.handleDelete}
            style={{margin: '10px', marginBottom: '40px'}}
          >
            Delete File
          </Button>
          <Button
            variant="contained"
            color="primary"
            component={Link}
            to={`/Edit/${user}/${repo}/${file}`}
            style={{margin: '10px', marginBottom: '40px'}}
          >
            Edit File
          </Button>
        </div>
      </div>
      </div>
    );
  }
}
