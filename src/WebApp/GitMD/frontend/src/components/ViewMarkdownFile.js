import React, { Component } from "react";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import DeleteIcon from '@mui/icons-material/Delete';
import Box from '@mui/material/Box';
import { Divider } from "@mui/material";
import Paper from '@mui/material/Paper';
import ListItemButton from '@mui/material/ListItemButton';
import FolderIcon from '@mui/icons-material/Folder';
import ListItemIcon from '@mui/material/ListItemIcon';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import ListItemText from '@mui/material/ListItemText';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import RestoreIcon from '@mui/icons-material/Restore';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';


export default class ViewMarkdownFile extends Component {
    constructor(props) {
        super(props);
        this.state = {
            repositories: [],
            OwnedRepositories: [],
            SharedRepositories: [],
            selectedRepo: null,
            repoFiles: [],
            users: [],
            repoUrl: '',
            Collaborator: "",
            repositoryName: "",
            owner: "",
            HTTPorSSH: "HTTP",
            RepoOrFile: 0,
            FileDate: [],
            showDeletedFiles: 0,
            deletedFiles: [],
            sortBy: 'alphabetically',
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
          this.setState({ repoFiles: data[0], FileDate: data[1] });
        })
        .catch((error) => {
          console.error("Error fetching repository files:", error);
        });
    }

    loadDeletedFiles(repoName) {
        fetch("/api/view", {
            method: "POST",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                switch: "deletedFiles",
                repoName: repoName
            }),
        })
        .then((response) => response.json())
        .then((data) => {
          this.setState({ deletedFiles: data});
        })
        .catch((error) => {
          console.error("Error fetching repository files:", error);
        });
    }

    loadUsers(repoName, repoFullName) {
        fetch("/api/collaborators", {
            method: "POST",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                repoFullName: repoFullName,
                repoName: repoName
            }),
        })
        .then((response) => response.json())
        .then((data) => {
          this.setState({ users: data, owner: data[0] });
        })
        .catch((error) => {
          console.error("Error loading collaborators:", error);
        });
    }

    restoreDeletedFile(file) {
        fetch("/api/restoreFile", {
            method: "POST",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                repoName: this.state.repositoryName,
                file: file,
            }),
        })
        .then((response) => {
            window.location.reload();
          })
        .catch((error) => {
          console.error("Error removing collaborator", error);
        });
    }

    removeUser(index, repoName, repoFullName) {
        fetch("/api/removeCollaborator", {
            method: "POST",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                repoFullName: repoFullName,
                repoName: repoName,
                collaborator: this.state.users[index],
            }),
        })
        .then((response) => {
            window.location.reload();
          })
        .catch((error) => {
          console.error("Error removing collaborator", error);
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

    handleDeleteFile(file) {
    
        fetch("/api/delete", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            file: file,
            repo: this.state.repositoryName,
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

    addCollaborator(repoName, repoFullName) {
        fetch("/api/addUserToRepo", {
            method: "PUT",
            credentials: "include",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                repoFullName: repoFullName,
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
                this.setState({ repositories: data[0], OwnedRepositories: data[1], SharedRepositories: data[2]});
            })
            .catch((error) => {
                console.error("Error fetching repositories:", error);
            });
    }

    sortFiles = (fileArray) => {
        //console.log("fileArray type:", typeof fileArray);
        if(fileArray.length != 0){
            switch (this.state.sortBy) {
                case 'alphabetically':
                    return fileArray.sort((a, b) => a[0].localeCompare(b[0]));
                case 'alphabeticallyReverse':
                    return fileArray.sort((a, b) => b[0].localeCompare(a[0]));
                case 'dateNewestFirst':
                    return fileArray.sort((a, b) => new Date(b[1]) - new Date(a[1]));
                case 'dateOldestFirst':
                    return fileArray.sort((a, b) => new Date(a[1]) - new Date(b[1]));
                default:
                    return fileArray;
            }
        }
        return fileArray;
    };

    sortRepos = (repoArray) => {
        switch (this.state.sortBy) {
            case 'alphabetically':
                return repoArray.slice().sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()));
            case 'alphabeticallyReverse':
                return repoArray.slice().sort((a, b) => b.name.toLowerCase().localeCompare(a.name.toLowerCase()));
            case 'dateNewestFirst':
                return repoArray.slice().sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
            case 'dateOldestFirst':
                return repoArray.slice().sort((a, b) => new Date(a.created_at) - new Date(b.created_at));
            default:
                return repoArray.slice();
        }
    };

    handleSort(sortBy) {
        this.setState({sortBy : sortBy})
    }

    componentDidMount() {
        this.loadRepositories();
        this.setState({repositoryName : ""})
    }

    handleRepoButtonClick = (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name, repositoryName: repo.name, RepoOrFile: 1 });
        this.loadRepoFiles(repo.name);
        this.loadUsers(repo.name, repo.full_name);
        this.loadDeletedFiles(repo.name);
      };

    handleRepoDeleteButtonClick = (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name });
        this.DeleteRepo(repo.name);
        window.location.reload();
      };

    handleCollaboratorChange = (event) => {
        this.setState({ Collaborator: event.target.value });
      };

    handleHTTPorSSHChange = (type) => {
        this.setState({ HTTPorSSH: type });
      };

    ShowDeletedFiles = (num) => {
        this.setState({ showDeletedFiles: num });
      };

    render() {
        const { repoUrl } = this.state;
        const { view } = this.props.match.params;
        let repoArray;
        let fileArray = this.state.FileDate
        let deletedFileArray = this.state.deletedFiles

        if (view === 'my-collection') {
            repoArray = this.state.OwnedRepositories;
        } else if (view === 'shared-with-me') {
            repoArray = this.state.SharedRepositories;
        } else {
            repoArray = this.state.repositories;
        }
        // console.log(repoArray)
        // console.log(fileArray)
        // console.log(deletedFileArray)
        repoArray = this.sortRepos(repoArray)
        fileArray = (this.sortFiles(fileArray))
        deletedFileArray = (this.sortFiles(deletedFileArray))
        console.log("repoArray type:", typeof repoArray);
        console.log("fileArray type:", typeof fileArray);
        
        
        return (

                <Box component="main" sx={{ flexGrow: 1, p: 8 }}>
                <Grid container spacing={1}>
                    <Paper elevation={0} className="paper-container-home">
                    {this.state.RepoOrFile === 0 && (
                    <>
                        <Grid item xs={12} align="center">
                        <Typography component="h4" variant="h4">
                            Collections
                        </Typography>
                        </Grid>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                        <>
                        {this.state.sortBy != "alphabeticallyReverse" && (
                            <Button onClick={() => this.handleSort("alphabeticallyReverse")}>
                                Name
                                <ArrowDropDownIcon />
                            </Button>
                        )}
                        </>
                        <>
                        {this.state.sortBy == "alphabeticallyReverse" && (
                            <Button onClick={() => this.handleSort("alphabetically")}>
                                Name
                                <ArrowDropUpIcon />
                            </Button>
                        )}
                        </>
                        <>
                        {this.state.sortBy != "dateOldestFirst" && (
                            <Button onClick={() => this.handleSort("dateOldestFirst")}>
                                <div style={{ marginRight: '70px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                <ArrowDropDownIcon />
                            </Button>
                        )}
                        </>
                        <>
                        {this.state.sortBy == "dateOldestFirst" && (
                            <Button onClick={() => this.handleSort("dateNewestFirst")}>
                                <div style={{ marginRight: '70px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                <ArrowDropUpIcon />
                            </Button>
                        )}
                        </>
                        </div>
                        <Divider />
                        {repoArray.map((repo, index) => (
                        <Grid item xs={12} key={index}>
                            <div className="repo-container" onClick={() => this.handleRepoButtonClick(repo)}>
                            <ListItemButton component="p" variant="h6" className="repo-title">
                                <ListItemIcon>
                                <FolderIcon />
                                </ListItemIcon>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                                    <div>{repo.name}</div>
                                    <div style={{ width: '100px', whiteSpace: 'nowrap' }}>{`${repo.created_at.substring(8, 10)}-${repo.created_at.substring(5, 7)}-${repo.created_at.substring(0, 4)}`}</div>
                                    </div>
                            </ListItemButton>
                            <ListItemButton style={{ float: 'right' }} onClick={() => this.handleRepoDeleteButtonClick(repo)}>
                                <DeleteIcon className="repo-delete-button" />
                            </ListItemButton>
                            </div>
                            <Divider />
                        </Grid>
                        ))}
                    </>
                    )}
                    {this.state.RepoOrFile === 1 && (
                        <div style={{ textAlign: 'center' }}>
                            <h1>{this.state.selectedRepo && this.state.selectedRepo.name}</h1>
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
                                <Button variant="contained" color="primary" onClick={() => { this.props.history.push("/Create/" + this.state.selectedRepo.name) }} style={{ marginLeft: '8px' }}>
                                    New File
                                </Button>
                                {this.state.showDeletedFiles === 0 && (
                                <Button variant="contained" color="primary" onClick={() => { this.ShowDeletedFiles(1) }} style={{ marginLeft: '8px' }}>
                                    Deleted Files
                                </Button>
                                )}
                                {this.state.showDeletedFiles === 1 && (
                                <Button variant="contained" color="primary" onClick={() => { this.ShowDeletedFiles(0) }} style={{ marginLeft: '8px' }}>
                                    Files
                                </Button>
                                )}
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                            <>
                            {this.state.sortBy != "alphabeticallyReverse" && (
                                <Button onClick={() => this.handleSort("alphabeticallyReverse")}>
                                    Name
                                    <ArrowDropDownIcon />
                                </Button>
                            )}
                            </>
                            <>
                            {this.state.sortBy == "alphabeticallyReverse" && (
                                <Button onClick={() => this.handleSort("alphabetically")}>
                                    Name
                                    <ArrowDropUpIcon />
                                </Button>
                            )}
                            </>
                            <>
                            {this.state.sortBy != "dateOldestFirst" && (
                                <Button onClick={() => this.handleSort("dateOldestFirst")}>
                                    <div style={{ marginRight: '70px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                    <ArrowDropDownIcon />
                                </Button>
                            )}
                            </>
                            <>
                            {this.state.sortBy == "dateOldestFirst" && (
                                <Button onClick={() => this.handleSort("dateNewestFirst")}>
                                    <div style={{ marginRight: '70px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                    <ArrowDropUpIcon />
                                </Button>
                            )}
                            </>
                            </div>
                            <Divider />
                            {this.state.showDeletedFiles === 0 && (
                                <>
                                {fileArray.map((file, index) => (
                                    <Grid item xs={12} key={index}>
                                        <div className="repo-container">
                                        <ListItemButton component="p" variant="h6" className="repo-title" onClick={() => window.open(repoUrl + '/' + file[0], '_blank')}>
                                            <ListItemIcon>
                                            <InsertDriveFileIcon />
                                            </ListItemIcon>
                                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                                            <div>{file[0]}</div>
                                            <div style={{ width: '100px', whiteSpace: 'nowrap' }}>{`${file[1].substring(8, 10)}-${file[1].substring(5, 7)}-${file[1].substring(0, 4)}`}</div>
                                            </div>
                                        </ListItemButton>
                                        <ListItemButton style={{ float: 'right' }} onClick={() => this.handleDeleteFile(file[0])}>
                                            <DeleteIcon className="repo-delete-button" />
                                        </ListItemButton>
                                        </div>
                                        <Divider />
                                    </Grid>
                                ))}
                                </>
                            )}
                            {this.state.showDeletedFiles === 1 && (
                                <>
                                {deletedFileArray
                                .filter(file => !this.state.FileDate.some(innerList => innerList[0] === file[0]))
                                .map((file, index) => (
                                    <Grid item xs={12} key={index}>
                                        <div className="repo-container">
                                        <ListItemButton component="p" variant="h6" className="repo-title" onClick={() => window.open(repoUrl + '/' + file[0], '_blank')}>
                                            <ListItemIcon>
                                            <InsertDriveFileIcon />
                                            </ListItemIcon>
                                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                                            <div>{file[0]}</div>
                                            <div style={{ width: '100px', whiteSpace: 'nowrap' }}>{`${file[1].substring(8, 10)}-${file[1].substring(5, 7)}-${file[1].substring(0, 4)}`}</div>
                                            </div>
                                        </ListItemButton>
                                        <ListItemButton style={{ float: 'right' }} onClick={() => this.restoreDeletedFile(file[0])}>
                                            <RestoreIcon className="repo-delete-button" />
                                        </ListItemButton>
                                        </div>
                                        <Divider />
                                    </Grid>
                                ))}
                                </>
                            )}

                            <div style={{ display: 'flex', flexDirection: 'row', marginTop: '10px' }}>
                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginRight: '10px', padding: '10px', borderRadius: '8px', width: '100%', border: '1px solid #ccc'  }}>
                                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                                    <TextField label="Add User To Repository" variant="outlined" rows={1} margin="normal" value={this.state.Collaborator} onChange={this.handleCollaboratorChange} style={{ marginRight: '8px' }} />
                                    <div className="addUser-button">
                                        <Button variant="contained" color="primary" onClick={() => this.addCollaborator(this.state.repositoryName, this.state.repoUrl)}>
                                        Add User
                                        </Button>
                                    </div>
                                    </div>
                                </div>
                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', padding: '10px', borderRadius: '8px', width: '80%', border: '1px solid #ccc'  }}>
                                    <Typography variant="h6" gutterBottom>
                                    Users With Access
                                    </Typography>
                                    <ul>
                                        {this.state.users.slice().map((user, index) => (
                                            <li key={index} style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                                            <span>{user}</span>
                                            {user === this.state.owner && <span style={{ marginLeft: '8px', color: 'blue' }}>(owner)</span>}
                                            {user !== this.state.owner && (
                                                <ListItemButton variant="contained" color="error" onClick={() => this.removeUser(index, this.state.repositoryName, this.state.repoUrl)} style={{ marginLeft: '8px' }}>
                                                <PersonRemoveIcon />
                                                </ListItemButton>
                                            )}
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>
                        )}
                    </Paper>
                </Grid>
                <br></br><br></br>
                </Box>
        );
    }
}