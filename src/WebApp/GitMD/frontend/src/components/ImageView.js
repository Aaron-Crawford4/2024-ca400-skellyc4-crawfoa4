import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import Header from './Header';

export default class ImageView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      file: null,
      redirect: false,
    };
  }

  handleChange = (event) => {
    console.log(event.target.files[0])
    this.setState({ file: event.target.files[0] });
  };

  handleUpload = () => {
    if (this.state.file) {
        const formData = new FormData();
        formData.append('image', this.state.file);
    
        fetch("/api/image", {
          method: "POST",
          body: formData, 
        })
          .then((response) => response.json())
          .then((data) => {
            console.log("File uploaded successfully:", data);
            this.setState({ redirect: true });
          })
          .catch((error) => {
            console.error("Error uploading file:", error);
          });
      }
  };

  render() {
    const { redirect } = this.state;

    if (redirect) {
      return <Redirect to="/" />;
    }

    return (
      <div>
        <Header />
        <div className="App">
          <h2>Add Image:</h2>
          <input type="file" onChange={this.handleChange} />
          <button onClick={this.handleUpload}>Upload</button>
        </div>
      </div>
    );
  }
}