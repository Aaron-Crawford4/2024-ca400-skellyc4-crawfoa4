import React, { Component } from "react";
import CreateMarkdownFile from "./CreateMarkdownFile";
import ViewMarkdownFile from "./ViewMarkdownFile";
import IndivMarkdown from "./IndivMarkdown";
import EditMarkdownFile from "./EditMarkdownFile";
import Login from "./Login";
import ImageView from "./ImageView";
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
          <Route path="/login" component={Login} />
          <PrivateRoute exact path="/" component={ViewMarkdownFile} />
          <PrivateRoute path="/images" component={ImageView} />
          <PrivateRoute exact path="/create" component={CreateMarkdownFile} />
          <PrivateRoute path="/create/:repo" component={CreateMarkdownFile} />
          <PrivateRoute exact path="/:user/:repo/:file" component={IndivMarkdown} />
          <PrivateRoute path="/edit/:user/:repo/:file" component={EditMarkdownFile} />
        </Switch>
      </Router>
    );
  }
}