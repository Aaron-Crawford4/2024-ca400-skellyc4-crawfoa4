import React from 'react';
import Typography from '@mui/material/Typography';
import { Link } from "react-router-dom";

const Header = () => {
  return (
    <header className="header">
      <Typography component="h4" variant="h4" color="inherit">
        <Link to="/" className="home-button-link">
          GitMD
        </Link>
        <div className='create-button'>
          <Link to="/create" className="create-button-link">
            Create Markdown File
          </Link>
        </div>
      </Typography>
    </header>
  );
};

export default Header;