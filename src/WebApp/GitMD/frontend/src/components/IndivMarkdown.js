import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import Header from './Header';
import ReactMarkdown from "react-markdown";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";
import Paper from '@mui/material/Paper';
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';
import Modal from "react-modal";

export default class IndivMarkdown extends Component {
  constructor(props) {
    super(props);
    this.state = {
      markdown: [],
      title: "",
      sha: "",
      owner: "",
      commits: [],
      atobContent: "",
    };
  }

  componentDidMount() {
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;
    const regex = /\/repos\/([^/]+)\//;

    fetch(`/api/${user}/${repo}/${file}`)
      .then((response) => response.json())
      .then((data) => {
        this.setState({ 
          markdown: data,
          atobContent: atob(data.content),
          sha: data.sha,
          owner: (data.url).match(regex)[1]
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

  handleSubmit = (content) => {
    
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;
    this.state.atobContent = atob(content);
    console.log(this.state.atobContent)

    fetch(`/api/edit`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        user: user,
        content: this.state.atobContent,
        repo: repo,
        file: file,
        sha: this.state.sha,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("File updated successfully:", data);
        window.location.reload();
      })
      .catch((error) => {
        console.error("Error updating file:", error);
      });
  };

  loadFileCommits() {
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;
    fetch("/api/previousVersions", {
        method: "POST",
        credentials: "include",
        headers: { 
            "Content-Type": "application/json" 
        },
        body: JSON.stringify({
          file: file,
          repo: repo,
          owner: this.state.owner,
        }),
    })
    .then((response) => response.json())
    .then((data) => {
      this.setState({ commits: data });
    })
    .catch((error) => {
      console.error("Error fetching repository commits:", error);
    });
}

  openModal = () => {
    this.setState({ isModalOpen: true });
  };

  closeModal = () => {
    this.setState({ isModalOpen: false });
  };

  showPreviousVersions = () => {
    this.loadFileCommits();
    this.openModal();
  };

  loadPreviousCommit = (content) => {
    this.state.atobContent = atob(content);
    console.log(this.state.atobContent)
    this.closeModal();
  };

  render() {
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;

    return (
      <div>
        <Header />
        <div className="container">
          <div style={{ display: 'flex', flexDirection: 'row' }}>
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
              to={`/edit/${user}/${repo}/${file}`}
              style={{margin: '10px', marginBottom: '40px'}}
            >
              Edit File
            </Button>
            <Button
              variant="contained"
              color="warning"
              component={Link}
              onClick={this.showPreviousVersions}
              style={{margin: '10px', marginBottom: '40px'}}
            >
              View Previous Versions
            </Button>
            </div>
            <Paper elevation={3} className="paper-container">
              <Typography component="h3" variant="h3" className="editor-title">
                {file.slice(0, -3)}
              </Typography>
              <Typography component="div" variant="body1" className="editor-content">
                <ReactMarkdown>{this.state.atobContent}</ReactMarkdown>
              </Typography>
            </Paper>
        </div>
        <Modal
          isOpen={this.state.isModalOpen}
          onRequestClose={this.closeModal}
          contentLabel="Repository Files Modal"
          style={{
            content: {
              width: '50%',
              margin: 'auto',
            },
          }}
        >
        <ul style={{ listStyleType: 'none', padding: 0 }}>
          <h1 style={{textAlign: "center"}}>Previous Versions of {file}</h1>
          {this.state.commits.map((file, index) => (
            <li key={index}>
              <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'center', alignItems: 'center',  margin: '10px 0px 10px 0px', padding: '10px 0px 10px 0px', width: '100%'}}>
              <div className="fileList-button">
              <Button onClick={ () => this.loadPreviousCommit(file[0])}>
                Preview Previous Edit By User {file[1]} on the {file[2]} at {file[3]}
              </Button>
              </div>
              <Button variant="contained" color="warning" onClick={ () => this.handleSubmit(file[0])}>
                Restore To This Version
              </Button>
              </div>
              </li>
            ))}
        </ul>
        <Button variant="contained" color="primary" onClick={this.closeModal}>
            Close
        </Button>
        </Modal>
      </div>
    );
  }
}
