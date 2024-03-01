import React, { useState, useEffect } from 'react';
import Typography from '@mui/material/Typography';
import { Link, useHistory } from 'react-router-dom';

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const history = useHistory();

  const handleLogout = async () => {
    try {
      const response = await fetch('/api/logout', {
        method: 'POST',
      });

      if (response.ok) {
        setIsLoggedIn(false);
        history.push('/login');
        console.log('Logout successful');
      } else {
        console.error('Logout failed');
      }
    } catch (error) {
      console.error('Error during logout:', error);
    }
  };

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const response = await fetch('/api/user', {
          method: 'GET',
          credentials: 'include',
        });

        if (response.ok) {
          setIsLoggedIn(true);
        } else {
          setIsLoggedIn(false);
        }
      } catch (error) {
        console.error('Error checking login status:', error);
      }
    };

    checkLoginStatus();
  }, []);
  return (
    <header className="header">
      <Typography component="h4" variant="h4" color="inherit">
      <div className='create-button'>
          <Link to="/create" className="create-button-link">
            Create Markdown File
          </Link>
        </div>
        <Link to="/" className="home-button-link" style={{ marginRight: "110px" }}>
          GitMD
        </Link>
        {isLoggedIn && (
            <Link onClick={handleLogout} className="logout-button">
              Logout
            </Link>
        )}
      </Typography>
    </header>
  );
};

export default Header;