import React, { Component } from "react";
import CreateMarkdownFile from "./CreateMarkdownFile";
import ViewMarkdownFile from "./ViewMarkdownFile";
import IndivMarkdown from "./IndivMarkdown";
import EditMarkdownFile from "./EditMarkdownFile";
import Login from "./Login";
import PrivateRoute from "./PrivateRoute";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";

export default class HomePage extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <Router>
        <Switch>
          <Route path="/Login" component={Login} />
          <PrivateRoute exact path="/" component={ViewMarkdownFile} />
          <PrivateRoute path="/Create" component={CreateMarkdownFile} />
          <PrivateRoute exact path="/:uniqueCode" component={IndivMarkdown} />
          <PrivateRoute path="/Edit/:uniqueCode" component={EditMarkdownFile} />
        </Switch>
      </Router>
    );
  }
}