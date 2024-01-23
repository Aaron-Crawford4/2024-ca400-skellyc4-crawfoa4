import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import Header from './Header';
import ReactMarkdown from "react-markdown";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";

export default class IndivMarkdown extends Component {
  constructor(props) {
    super(props);
    this.state = {
      markdown: [],
      title: "",
      code: "",
    };
  }

  componentDidMount() {
    const { uniqueCode } = this.props.match.params;

    fetch(`/api/${uniqueCode}`)
      .then((response) => response.json())
      .then((data) => {
        this.setState({ markdown: data });
      })
      .catch((error) => {
        console.error("Error fetching markdown content:", error);
      });
  }

  handleDelete = () => {
    const { title } = this.state.markdown;
    const { code } = this.state.markdown;

    fetch("/api/delete", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        code: code,
        title: title,
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

    return (
      <div className="container">
        <Header />
        <div className="markdown-container">
          <div className="editor-container">
            <Typography component="h4" variant="h4" className="editor-title">
              {markdown.title}
            </Typography>
            <Typography component="div" variant="body1" className="editor-content">
              <ReactMarkdown children={markdown.content} />
            </Typography>
          </div>
        </div>
        <Button
            variant="contained"
            color="secondary"
            component={Link}
            to="/"
            onClick={this.handleDelete}
          >
            Delete File
          </Button>
          <Button
            variant="contained"
            color="secondary"
            component={Link}
            to={`/Edit/${markdown.code}`}
          >
            Edit File
          </Button>
      </div>
    );
  }
}