import React, { Component } from "react";
import Typography from "@mui/material/Typography";
import ReactMarkdown from "react-markdown";
import Button from "@mui/material/Button";
import { Link } from "react-router-dom";
import Paper from '@mui/material/Paper';
import Modal from "react-modal";
import Box from '@mui/material/Box';
import RestoreIcon from '@mui/icons-material/Restore';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import Grid from "@mui/material/Grid";
import { Divider } from "@mui/material";


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
        this.showPreviousVersions()
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

  showPreviousVersions = () => {
    this.loadFileCommits();
  };

  loadPreviousCommit = (content) => {
    this.state.atobContent = atob(content);
    console.log(this.state.atobContent)
    this.forceUpdate();
  };

  render() {
    const { user } = this.props.match.params;
    const { repo } = this.props.match.params;
    const { file } = this.props.match.params;

    return (
      <Box component="main" sx={{ flexGrow: 1, p: 8 }}>
        <div className="container">
            <div style={{ display: 'flex', flexDirection: 'row' }}>
            <Paper elevation={3} className="paper-container">
              <Typography component="h3" variant="h3" className="editor-title">
                {file.slice(0, -3)}
              </Typography>
              <Typography component="div" variant="body1" className="editor-content">
                <ReactMarkdown>{this.state.atobContent}</ReactMarkdown>
              </Typography>
            </Paper>
            <ul style={{ listStyleType: 'none', padding: 0, marginLeft: '20px' }}>
              <h1 style={{ textAlign: 'center' }}>Previous Versions of {file}</h1>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
              <div>File Version</div>
              <div >Restore</div>
              </div>
              <Divider />
              {this.state.commits.map((file, index) => (
                <Grid item xs={12} key={index}>
                    <div className="repo-container">
                    <ListItemButton component="p" variant="h6" className="repo-title" onClick={ () => this.loadPreviousCommit(file[0])}>
                        <ListItemIcon>
                        <InsertDriveFileIcon />
                        </ListItemIcon>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                        <div>User {file[1]} on the {file[2]} at {file[3]}</div>
                        </div>
                    </ListItemButton>
                    <ListItemButton style={{ float: 'right' }} onClick={ () => this.handleSubmit(file[0])}>
                        <RestoreIcon className="repo-delete-button" />
                    </ListItemButton>
                    </div>
                    <Divider />
                </Grid>
              ))}
              <Button
              variant="contained"
              color="primary"
              component={Link}
              to={`/edit/${user}/${repo}/${file}`}
              style={{margin: '10px', marginBottom: '40px'}}
            >
              Edit File
            </Button>
            </ul>
          </div>
        </div>
      </Box>
    );
  }
}
