import React, { Component } from "react";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { Link } from "react-router-dom";
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
    }

    openModal = () => {
      this.setState({ isModalOpen: true });
    };
  
    closeModal = () => {
      this.setState({ isModalOpen: false });
    };

    handleRepoButtonClick = (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name });
        this.loadRepoFiles(repo.name);
        this.openModal();
      };

      handleRepoDeleteButtonClick = (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name });
        this.DeleteRepo(repo.name);
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
                    >
                        <div>
                            <h2>{this.state.selectedRepo && this.state.selectedRepo.name}</h2>
                            <ul>
                                {this.state.repoFiles.map((file, index) => (
                                    <li key={index}>
                                        <a
                                            href={repoUrl + '/' + file.name}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            onClick={this.closeModal}
                                        >
                                            {file.name}
                                        </a>
                                    </li>
                                ))}
                            </ul>
                            <button onClick={() => { this.props.history.push("/Create/" + this.state.selectedRepo.name)}}>Create File</button>
                            <button onClick={this.closeModal}>Close</button>
                        </div>
                    </Modal>
                </Grid>
            </div>
        );
    }
}