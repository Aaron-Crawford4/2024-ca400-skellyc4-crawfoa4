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
import { red } from '@mui/material/colors';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import RestoreIcon from '@mui/icons-material/Restore';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import Switch from '@mui/joy/Switch';
import AddBoxIcon from '@mui/icons-material/AddBox';
import SearchIcon from '@mui/icons-material/Search';
import InputAdornment from '@mui/material/InputAdornment';
import Avatar from '@mui/material/Avatar';
import { deepPurple } from '@mui/material/colors';

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
            searchTerm: '',
            HTTPorSSHString: ''
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
          this.setState({ FileDate: data, HTTPorSSHString: "http://gitmd.ie/" + this.state.owner + "/" +  this.state.selectedRepo.name + ".git" });
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
        .then(async (response) => {
            try {
                if (!response.ok) {
                    throw new Error("Failed to remove collaborator: " + response.statusText);
                }
                this.setState(prevState => ({
                    users: prevState.users.filter((user, idx) => idx !== index)
                }));
            } catch (error) {
                console.error("Error removing collaborator:", error);
            }
        })
        .catch((error) => {
            console.error("Error removing collaborator:", error);
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
        .then(() => {
            window.location.reload();
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
          .then(() => {
            window.location.reload();
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
        .then(async (response) => {
            try {
                if (response.ok) {
                    this.setState(prevState => ({
                        users: [...prevState.users, this.state.Collaborator]
                    }));
                } else {
                    console.error("Error adding collaborator:", response.statusText);
                }
            } catch (error) {
                console.error("Error adding collaborator:", error);
            }
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

    handleRepoButtonClick = async (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name, repositoryName: repo.name, RepoOrFile: 1 });
        await this.loadRepoFiles(repo.name);
        await this.loadUsers(repo.name, repo.full_name);
        await this.loadDeletedFiles(repo.name);
    };

    handleRepoDeleteButtonClick = (repo) => {
        this.setState({ selectedRepo: repo, repoUrl: repo.full_name });
        this.DeleteRepo(repo.name);
        window.location.reload();
      };

    handleCollaboratorChange = (event) => {
        this.setState({ Collaborator: event.target.value });
      };

    handleHTTPorSSHChange = () => {
        this.setState(prevState => ({
            HTTPorSSH: prevState.HTTPorSSH === "HTTP" ? "SSH" : "HTTP",
            HTTPorSSHString: prevState.HTTPorSSHString === "http://gitmd.ie/" + this.state.owner + "/" +  this.state.selectedRepo.name + ".git" ? "GitMD@gitea.gitmd.ie:" + this.state.owner + "/" +  this.state.selectedRepo.name + ".git" : "http://gitmd.ie/" + this.state.owner + "/" +  this.state.selectedRepo.name + ".git"
        }));
      };
    
    handleSearchChange = (event) => {
        const newSearch = event.target.value;
        this.setState({ searchTerm: newSearch });
    };

    ShowDeletedFiles = () => {
        console.log(this.state.showDeletedFiles)
        this.setState(prevState => ({
          showDeletedFiles: prevState.showDeletedFiles === 0 ? 1 : 0
        }));
    };

    render() {
        const { repoUrl } = this.state;
        const { view } = this.props.match.params;
        let repoArray;
        let fileArray = this.state.FileDate
        let deletedFileArray = this.state.deletedFiles
        let title;

        if (view === 'my-collection') {
            repoArray = this.state.OwnedRepositories;
            title = 'My Collections';
        } else if (view === 'shared-with-me') {
            repoArray = this.state.SharedRepositories;
            title = 'Shared With Me';
        } else {
            repoArray = this.state.repositories;
            title = 'Collections';
        }
        repoArray = this.sortRepos(repoArray)
        fileArray = (this.sortFiles(fileArray))
        deletedFileArray = (this.sortFiles(deletedFileArray))
        
        return (

                <Box component="main" sx={{ flexGrow: 1, p: 8 }}>
                <Grid container spacing={1}>
                    <Paper elevation={0} className="paper-container-home" >
                    {this.state.RepoOrFile === 0 && (
                    <>
                        <Grid item xs={12} align="center">
                        <Typography style={{ marginRight: '90px', marginBottom: '25px' }} component="h4" variant="h4">
                            {title}
                        </Typography>
                        </Grid>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%', marginBottom: '2px' }}>
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
                        <TextField
                            id="search"
                            value={this.state.searchTerm}
                            onChange={this.handleSearchChange}
                            InputProps={{
                            startAdornment: (
                                <InputAdornment position="start">
                                <SearchIcon />
                                </InputAdornment>
                            ),
                            }}
                            variant="standard"
                        />
                        <>
                        {this.state.sortBy != "dateOldestFirst" && (
                            <Button style={{ marginRight: '47px' }} onClick={() => this.handleSort("dateOldestFirst")}>
                                <div style={{ marginRight: '0px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                <ArrowDropDownIcon />
                            </Button>
                        )}
                        </>
                        <>
                        {this.state.sortBy == "dateOldestFirst" && (
                            <Button style={{ marginRight: '47px' }} onClick={() => this.handleSort("dateNewestFirst")}>
                                <div style={{ marginRight: '0px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                <ArrowDropUpIcon />
                            </Button>
                        )}
                        </>
                        </div>
                        <Divider />
                        {repoArray.map((repo, index) => {
                            if (repo.name.includes(this.state.searchTerm)) {
                                const marginRightValue = `calc(52% - ${repo.owner.login.length * 8}px)`;
                                console.log(marginRightValue)
                                return (
                                    <Grid item xs={12} key={index}>
                                        <div className="repo-container" onClick={() => this.handleRepoButtonClick(repo)}>
                                            <ListItemButton component="div" variant="h6" className="repo-title">
                                                <ListItemIcon>
                                                    <FolderIcon />
                                                </ListItemIcon>
                                                <div style={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                                                    <div style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                                                        {repo.name}
                                                    </div>
                                                    <div style={{ display: 'flex', alignItems: 'center', marginLeft: 'auto', marginRight: marginRightValue  }}>
                                                        <Avatar sx={{ bgcolor: deepPurple[500], width: 24, height: 24, marginRight: '8px' }}></Avatar> {repo.owner.login}
                                                    </div>
                                                    <div style={{ width: '100px', whiteSpace: 'nowrap' }}>{`${repo.created_at.substring(8, 10)}-${repo.created_at.substring(5, 7)}-${repo.created_at.substring(0, 4)}`}</div>
                                                </div>
                                            </ListItemButton>
                                            <ListItemButton style={{ float: 'right' }} onClick={() => this.handleRepoDeleteButtonClick(repo)}>
                                                <DeleteIcon className="repo-delete-button" />
                                            </ListItemButton>
                                        </div>
                                        <Divider />
                                    </Grid>
                                );
                            } else {
                                return null;
                            }
                        })}
                    </>
                    )}
                    {this.state.RepoOrFile === 1 && (
                        <div style={{ textAlign: 'center' }}>
                            <Typography component="h4" variant="h4">Collection: {this.state.selectedRepo && this.state.selectedRepo.name}</Typography>
                            <div style={{ display: "flex", alignItems: "center" }}>
                                <div style={{ display: "flex", alignItems: "center", marginRight: '8px' }}>
                                    <Typography style={{ marginRight: '8px' }}>HTTP</Typography>
                                    <Switch size="lg" color={this.state.showDeletedFiles ? 'primary' : 'primary'} onClick={this.handleHTTPorSSHChange}  checked={this.state.HTTPorSSH === "SSH"} inputProps={{ 'aria-label': 'controlled' }}/>
                                    <Typography style={{ marginLeft: '8px', marginRight: '8px' }}>SSH</Typography>
                                    <TextField
                                        label="Connection Type"
                                        variant="outlined"
                                        style={{ width: '32vw', marginLeft: '8px' }}
                                        margin="normal"
                                        value={this.state.HTTPorSSHString}
                                        readOnly
                                    />
                                </div>
                                <div style={{ display: "flex", alignItems: "center"}}>
                                    <Typography style={{ marginLeft: '8px', marginRight: '8px' }}>Files</Typography>
                                    <Switch size="lg" color={this.state.showDeletedFiles ? 'primary' : 'primary'} onClick={this.ShowDeletedFiles}  checked={this.state.showDeletedFiles === 1} inputProps={{ 'aria-label': 'controlled' }}/>
                                    <Typography style={{ marginLeft: '8px', whiteSpace: 'nowrap' }}>Deleted Files</Typography>
                                    <Button variant="contained" color="primary" onClick={() => { this.props.history.push("/create/" + this.state.selectedRepo.name) }} style={{ marginLeft: '28px' }}>
                                        New File
                                    <AddBoxIcon style={{ marginLeft: '8px' }}></AddBoxIcon>
                                    </Button>
                                </div>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%', marginBottom: '2px', marginTop: '20px' }}>
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
                            <TextField
                                id="search"
                                value={this.state.searchTerm}
                                onChange={this.handleSearchChange}
                                InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                    <SearchIcon />
                                    </InputAdornment>
                                ),
                                }}
                                variant="standard"
                            />
                            <>
                            {this.state.sortBy != "dateOldestFirst" && (
                                <Button style={{ marginRight: '47px' }} onClick={() => this.handleSort("dateOldestFirst")}>
                                    <div style={{ marginRight: '0px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                    <ArrowDropDownIcon />
                                </Button>
                            )}
                            </>
                            <>
                            {this.state.sortBy == "dateOldestFirst" && (
                                <Button style={{ marginRight: '47px' }} onClick={() => this.handleSort("dateNewestFirst")}>
                                    <div style={{ marginRight: '0px', width: '100px', whiteSpace: 'nowrap' }}>Created On</div>
                                    <ArrowDropUpIcon />
                                </Button>
                            )}
                            </>
                            </div>
                            <Divider />
                            {this.state.showDeletedFiles === 0 && (
                                <>
                                {fileArray.map((file, index) => {
                                    if(file[0].includes(this.state.searchTerm)) {
                                        return (
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
                                    )}
                                    else {
                                        return null;
                                    }
                                })}
                                </>
                            )}
                            {this.state.showDeletedFiles === 1 && (
                                <>
                                {deletedFileArray
                                .filter(file => !this.state.FileDate.some(innerList => innerList[0] === file[0]))
                                .map((file, index) => {
                                    if(file[0].includes(this.state.searchTerm)) {
                                        return (
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
                                        )
                                    }
                                    else {
                                        return null;
                                    }
                                })}
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
                                                <PersonRemoveIcon sx={{ color: red[700] }}/>
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