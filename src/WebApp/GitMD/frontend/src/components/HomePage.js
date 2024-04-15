import React, { Component } from "react";
import CreateMarkdownFile from "./CreateMarkdownFile";
import ViewMarkdownFile from "./ViewMarkdownFile";
import IndivMarkdown from "./IndivMarkdown";
import EditMarkdownFile from "./EditMarkdownFile";
import Login from "./Login";
import HelpView from "./HelpView";
import PrivateRoute from "./PrivateRoute";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import Header from './Header';
import Box from '@mui/material/Box';

export default class HomePage extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <Router>
        <Box sx={{ display: 'flex' }}>
        <Header />
        <Switch>
          <Route path="/login" component={Login} />
          <Route exact path="/help" component={HelpView} />
          <PrivateRoute exact path="/create" component={CreateMarkdownFile} />
          <PrivateRoute exact path="/" component={ViewMarkdownFile} />
          <PrivateRoute exact path="/:view" component={ViewMarkdownFile} />
          <PrivateRoute path="/create/:repo" component={CreateMarkdownFile} />
          <PrivateRoute exact path="/:user/:repo/:file" component={IndivMarkdown} />
          <PrivateRoute path="/edit/:user/:repo/:file" component={EditMarkdownFile} />
        </Switch>
        </Box>
      </Router>
    );
  }
}