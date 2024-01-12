import React, { Component } from "react";
import CreateMarkdownFile from "./CreateMarkdownFile";
import ViewMarkdownFile from "./ViewMarkdownFile";
import IndivMarkdown from "./IndivMarkdown"
import {BrowserRouter as Router, Switch, Route, Link, Redirect} from "react-router-dom"

export default class HomePage extends Component {
    constructor(props) {
        super(props);
    }

    render () {
        return (
        <Router> 
            <Switch>
                <Route exact path="/" component={ViewMarkdownFile}></Route>
                <Route path="/Create" component={CreateMarkdownFile}></Route>
                <Route path="/:uniqueCode" component={IndivMarkdown}></Route>
            </Switch>
        </Router>
        )
    }
}