import React, { Component } from "react";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import { Link, withRouter } from "react-router-dom";
import Modal from "react-modal";
import Header from './Header';

export default class ViewMarkdownFile extends Component {
    constructor(props) {
        super(props);
        this.state = {
            repositories: [],
            selectedRepo: null,
            repoFiles: [],
            isModalOpen: false,
            repoUrl: '',
            Collaborator: "",
            repositoryName: "",
            HTTPorSSH: "HTTP",
        };
    }

    loadRepoFiles(repoName) {
        fetch("/api/view", {
            method: "POST",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                switch: "files",
                repoName: repoName
            }),
        })
        .then((response) => response.json())
        .then((data) => {
          console.log(data)
          this.setState({ repoFiles: data });
        })
        .catch((error) => {
          console.error("Error fetching repository files:", error);
        });
    }

    DeleteRepo(repoName) {
        fetch("/api/repoDelete", {
            method: "POST",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                repo: repoName
            }),
        })
        .then((response) => response.json())
        .then((data) => {
        })
        .catch((error) => {
          console.error("Error fetching repository files:", error);
        });
    }

    addCollaborator(repoName) {
        console.log("here " + repoName)
        fetch("/api/addUserToRepo", {
            method: "PUT",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                repo: repoName,
                addedUser: this.state.Collaborator
            }),
        })
        .then(() => {
            window.location.reload();
        })
        .catch((error) => {
          console.error("Error adding collaborator:", error);
        });
    }

    loadRepositories() {
        fetch("/api/view", {
            method: "POST",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                switch: "repo",
                repoName: ""
              }),
        })
            .then((response) => response.json())
            .then((data) => {
                //console.log(data)
                console.log(typeof data)
                this.setState({ repositories: data });
            })
            .catch((error) => {
                console.error("Error fetching repositories:", error);
            });
    }

    componentDidMount() {
        this.loadRepositories();
        this.setState({repositoryName : ""})
    }

    openModal = () => {
      this.setState({ isModalOpen: true });
    };
  
    closeModal = () => {
      this.setState({ isModalOpen: false });
    };

    handleRepoButtonClick = (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name, repositoryName: repo.name });
        this.loadRepoFiles(repo.name);
        this.openModal();
      };

    handleRepoDeleteButtonClick = (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name });
        this.DeleteRepo(repo.name);
        this.props.history.push('/');
      };

    handleCollaboratorChange = (event) => {
        this.setState({ Collaborator: event.target.value });
      };

    handleHTTPorSSHChange = (type) => {
        this.setState({ HTTPorSSH: type });
      };

    render() {
        const { repoUrl } = this.state;

        return (
            <div>
                <Header />
                <Grid container spacing={1}>
                    <Grid item xs={12} align="center">
                        <Typography component="h4" variant="h4">
                            Your Repositories
                        </Typography>
                    </Grid>

                    {this.state.repositories.map((repo, index) => (
                        <Grid item xs={12} sm={6} md={4} key={index} align="center">
                            <div className="repo-container">
                                <Typography component="p" variant="h6" className="repo-title">
                                {repo.name}
                                </Typography>
                                <button
                                    onClick={() => this.handleRepoButtonClick(repo)}
                                    className="view-button"
                                >
                                    View Repository    
                                </button>
                                <br></br>
                                <button
                                    onClick={() => this.handleRepoDeleteButtonClick(repo)}
                                    className="view-button"
                                >
                                    Delete Repository
                                </button>
                            </div>
                        </Grid>
                    ))}

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
                        <div style={{ textAlign: 'center' }}>
                            <h2>{this.state.selectedRepo && this.state.selectedRepo.name} Repository</h2>
                            <div style={{ display: "flex", alignItems: "center" }}>
                                <Button
                                variant="contained"
                                style={{ marginRight: "8px" }}
                                onClick={() => this.handleHTTPorSSHChange("HTTP")}
                                >
                                HTTP
                                </Button>
                                <Button
                                variant="contained"
                                style={{ marginRight: "8px" }}
                                onClick={() => this.handleHTTPorSSHChange("SSH")}
                                >
                                SSH
                                </Button>
                                <TextField
                                label="Connection Type"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                value={this.state.HTTPorSSH}
                                readOnly
                                />
                            </div>
                            <h3>File List</h3>
                            <ul style={{ listStyleType: 'none', padding: 0 }}>
                                {this.state.repoFiles.map((file, index) => (
                                    <li key={index}>
                                        <div className="fileList-button">
                                            <Button
                                                onClick={() => window.open(repoUrl + '/' + file.name, '_blank')}
                                            >
                                                {file.name}
                                            </Button>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                                    <TextField label="Add User To Repository" variant="outlined" rows={1} margin="normal" value={this.state.Collaborator} onChange={this.handleCollaboratorChange} style={{ marginRight: '8px' }} />
                                    <div className="addUser-button">
                                        <Button variant="contained" color="primary" onClick={() => this.addCollaborator(this.state.repositoryName)}>
                                            Add User
                                        </Button>
                                    </div>
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    <Button variant="contained" color="primary" onClick={() => { this.props.history.push("/Create/" + this.state.selectedRepo.name) }} style={{ marginRight: '8px' }}>
                                        Create File
                                    </Button>
                                    <Button variant="contained" color="primary" onClick={this.closeModal}>
                                        Close
                                    </Button>
                                </div>
                            </div>
                        </div>
                    </Modal>
                </Grid>
                <br></br><br></br>
            </div>
        );
    }
}