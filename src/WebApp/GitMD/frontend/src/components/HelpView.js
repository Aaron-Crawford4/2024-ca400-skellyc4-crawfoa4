import React, { Component } from "react";
import Box from '@mui/material/Box';
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import TextField from '@mui/material/TextField';
import Button from "@mui/material/Button";
import Paper from '@mui/material/Paper';
import ReactMarkdown from "react-markdown";
import remarkGfm from 'remark-gfm';

export default class HelpView extends Component {
  constructor(props) {
    super(props);
    this.state = {
        email: "",
        password: "",
        password2: "",
        content: "",
        markdownContent: ""
      };
  }

  componentDidMount() {
    
    const markdown = `
# School of Computing &mdash; Year 4 Project Proposal Form

## SECTION A

|                     |                     |
|---------------------|---------------------|
|Project Title:       | Universal Notes App |
|Student 1 Name:      | Ciaran Skelly       |
|Student 1 ID:        | 20324213            |
|Student 2 Name:      | Aaron Crawford      |
|Student 2 ID:        | 20336753            |
|Project Supervisor:  | Stephen Blott       |


## SECTION B

### Introduction

Many note taking applications are needlessly expensive, cannot be accessed on all platforms or have limited functionality. Our project seeks to provide an easy to use note-taking application for web and mobile platforms. The application will enable users to create, modify, organise, and access their notes effortlessly regardless of the platform they are on.
The notes wil be stored in an easy to use text format that allows us to cater for a number of use cases.

### Outline

The proposed project involves developing an online note taking application capable of storing and managing notes. The system will comprise of a backend, git based server and clients for the web and Android platforms. Notes will be written in markdown as we believe it has an easy to learn/use syntax and its features are useful and allow it to fit many use cases.

User will be able to create, edit, view and delete notes through an intuitive ui on both android and web platforms. 
Using git for our backend will make it possible to synchronise the note files across platforms.

### Background

This idea was suggested by our supervisor and when we read the brief we immediately saw the advantages it could bring. The idea of allowing users to use our project through a web application, android app but also pull their notes to any device using git made this idea very attractive to us. We had also dealt with the annoyance of the limited functionality of free note taking apps and saw an opportunity to work on a project that would improve this.

### Achievements

This project will allow users to take and store notes across a range of platforms which are a web client, an android application and use git to pull down their notes onto a linux machine and edit them. Using git will allow us to effectively synchronise a user’s notes across all their devices. We would also like to have the functionality to share your notes with other users, doing this will allow a team lead to share notes on a project or allow users to send a shopping list to each other
. This will allow us to have a wide variety of users ranging from tech savvy users who would want to pull their notes to different devices using git to the average tech user who just wants an app or site to store their notes. Storing notes is also a functionality that encompasses a wide user base, from someone writing a shopping list to employees taking notes on the work they need to achieve or their projects. 

### Justification

One use case for this project that we personally would use it for is in the workplace. During our internships we both realised that it was hard to keep track of all our tasks or details about each individual task. We saw the benefit this project would have to allow users to take notes on their work laptops using the web application but also on their personal phones or computers using a combination of the android app and web app. This would allow users to take notes while on calls, in meetings or on the move. The ability to share your notes with other users will allow users to write notes for each other so that a manager can write a list of tasks for an employee with notes on each task or a parent to write a shopping list and send it to their kids to buy the items.

### Programming language(s)
Python for backend, Java for android application, Javascript for front-end

### Programming tools / Tech stack
Django for our web-app framework, RESTful APIs, python library GitPython to work with git programmatically, Android Studio or React Native, React, Webhooks, MySQL.

### Hardware
N/A

### Learning Challenges

We will have to learn React Native if we decide to use that for creating our mobile app. This will be our first time working with the GitPython library and even though we have used git before we have not worked with it. While we have worked with javascript before our knowledge of the language will have to be improved for this project. We will have to learn how to use webhooks to ensure that all data between users and devices is synchronised.

### Breakdown of work

#### Student 1
I will work on developing the Android app and creating the APIs for requests to the backend. I will also work on creating tests for our CI pipeline. My partner and I will share an equal responsibility for writing the required project documentation.

#### Student 2

I will work on the development of the web application both backend and frontend. I will also work on the backend development with git. Finally I will be working on creating tests for our CI pipeline and writing up documentation.
    
`

    this.setState({content : markdown, markdownContent : markdown })
}

  resetPassword() {
    if(this.state.password != this.state.password2) {
        console.log(this.state.password, this.state.password2)
    }

    else {
        console.log(this.state.email, this.state.password)
        fetch("/api/passwordReset", {
            method: "PUT",
            headers: { 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({
                email: this.state.email,
                newPassword: this.state.password,
            }),
        })
        .then(() => {
            //window.location.reload();
        })
        .catch((error) => {
        console.error("Error adding collaborator: ", error);
        });
    }
}

  handleInputChange = (e) => {
    this.setState({ [e.target.name]: e.target.value });
  };

  handleContentChange = (event) => {
    this.setState({
      content: event.target.value,
      markdownContent: event.target.value,
    });
  };

  render() {
    const { email, password, password2 } = this.state;
    
    

    return (
        <Box component="main" sx={{ flexGrow: 1, p: 10 }}>
            {/* <Grid item xs={12} align="left">
                <Typography textAlign={"center"} component="h4" variant="h4">
                    Reset Password
                </Typography>
            </Grid>
            <TextField
                  margin="normal"
                  required
                  fullWidth
                  type="text"
                  label="Email Address"
                  name="email"
                  value={email}
                  onChange={this.handleInputChange}
                  autoFocus
            />
            <TextField
                  margin="normal"
                  required
                  fullWidth
                  type="password"
                  label="Password"
                  name="password"
                  value={password}
                  onChange={this.handleInputChange}
            />
            <TextField
                  margin="normal"
                  required
                  fullWidth
                  type="password"
                  label="Confirm Password"
                  name="password2"
                  value={password2}
                  onChange={this.handleInputChange}
            />
            <Button variant="contained" color="primary" onClick={() => this.resetPassword()}>
                Submit
            </Button> */}

            <Typography textAlign={"center"} style={{ marginTop: '10px' }} component="h4" variant="h4">
                Markdown Help
            </Typography>
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
                <ReactMarkdown remarkPlugins={[remarkGfm]}  children={this.state.markdownContent}></ReactMarkdown>
                </Typography>
            </Paper>
            </div>
        </Box>
    );
  }
}