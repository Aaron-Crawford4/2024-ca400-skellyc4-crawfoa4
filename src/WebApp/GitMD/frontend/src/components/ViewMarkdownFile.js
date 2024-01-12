import React, { Component } from "react";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { Link } from "react-router-dom";
import Header from './Header';
import ReactMarkdown from "react-markdown";

export default class ViewMarkdownFile extends Component {
    constructor(props) {
        super(props);
        this.state = {
            markdownFiles: [],
          };
    }
   
    loadMarkdownFiles() {
        fetch("/api/view", {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        })
            .then((response) => response.json())
            .then((data) => {
            this.setState({ markdownFiles: data });
        })
            .catch((error) => {
            console.error("Error fetching markdown files:", error);
        });
    }

    componentDidMount() {
        this.loadMarkdownFiles();
    }

    render () {
        return (
        <div>
        <Header/>
        <Grid container spacing={1}>

            <Grid item xs={12} align="center">
                <Typography component="h4" variant="h4">
                    Your Markdown Files
                </Typography>
            </Grid>

            {this.state.markdownFiles.map((file, index) => (
                <Grid item xs={12} sm={6} md={4} key={index} align="center">
                <div className="file-container">
                    <Typography component="p" variant="h6" className="file-title">
                        {file.title}
                    </Typography>
                    <Typography component="p" variant="body1" className="file-content">
                        {file.content.length > 50 ? file.content.slice(0, 100) + '...' : <ReactMarkdown children={file.content} />}
                    </Typography>
                    <Link to={`/${file.code}`} className="view-button">
                        View Full Content
                    </Link>
                </div>
                </Grid>
            ))}
        </Grid>
        </div>
        );
    }
}