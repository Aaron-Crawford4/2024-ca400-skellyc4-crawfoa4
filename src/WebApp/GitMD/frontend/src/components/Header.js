import React, { useState, useEffect } from 'react';
import Typography from '@mui/material/Typography';
import { Link, useHistory } from 'react-router-dom';
import Avatar from '@mui/material/Avatar';
import { deepPurple } from '@mui/material/colors';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import ListItem from '@mui/material/ListItem';
import Drawer from '@mui/material/Drawer';
import Button from '@mui/material/Button';

const Header = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [name, setName] = useState('');
    const [anchorEl, setAnchorEl] = useState(null);
    const history = useHistory();
    const [open, setOpen] = useState(false);
  
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
  
    const handleAvatarClick = (event) => {
      setAnchorEl(event.currentTarget);
    };
  
    const handleAvatarClose = () => {
      setAnchorEl(null);
    };
  
    useEffect(() => {
      const checkLoginStatus = async () => {
        try {
          const response = await fetch('/api/user', {
            method: 'GET',
            credentials: 'include',
          });
  
          if (response.ok) {
            const data = await response.json();
            const { name } = data;
            setName(name);
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

    const handleHomeClick = () => {
      history.push('/');
    };

    const toggleDrawer = (newOpen) => () => {
      console.log("here")
      setOpen(newOpen);
    };

    const getRouteForText = (text) => {
      switch (text) {
        case 'Create a Repository':
          return '/create';
        case 'Images':
          return '/images';
        case 'Sign Out':
          return '/';
        case 'Help':
          return '/';
      }
    };

    const DrawerList = (
      <Box sx={{ width: 250 }} role="presentation" onClick={toggleDrawer(false)}>
        <List>
          <ListItem disablePadding>
            <ListItemButton onClick={handleHomeClick}>
                <ListItemText primary={"GITMD"} />
            </ListItemButton>
          </ListItem>
          <ListItem disablePadding>
            <ListItemButton sx={{ cursor: 'default', '&:hover': { backgroundColor: 'inherit' } }}>
                <ListItemText primary={"Welcome " + name} />
            </ListItemButton>
          </ListItem>
        </List>
        <Divider />
        <List>
          {['Create a Repository', 'Images'].map((text, index) => (
            <ListItem key={text} disablePadding>
              <ListItemButton component={Link} to={getRouteForText(text)}>
                <ListItemText primary={text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider />
        <List>
          {['Sign Out', 'Help'].map((text, index) => (
            <ListItem key={text} disablePadding>
              <ListItemButton component={Link} to={getRouteForText(text)} onClick={text === 'Sign Out' ? handleLogout : undefined}>
                <ListItemText primary={text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Box>
    );

  return (
    <div className='header'>
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <div>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            role="button"
            onClick={toggleDrawer(true)}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Drawer open={open} onClose={toggleDrawer(false)}>
            {DrawerList}
          </Drawer>
          </div>
          <Typography onClick={handleHomeClick} variant="h6" component="div" sx={{ flexGrow: 1 }} style={{cursor: 'pointer', transition: 'color 0.3s',}}>
            GITMD
          </Typography>
          {isLoggedIn && (
          <div className='avatar-button'>
            <Typography onClick={handleAvatarClick} className='username-heading' style={{fontSize:'25px', marginRight: '10px'}}>
              {name}
            </Typography>
            <Avatar
              sx={{ bgcolor: deepPurple[500], cursor: 'pointer' }}
              onClick={handleAvatarClick}
            >
              {name.charAt(0).toUpperCase()}
            </Avatar>
            {/* <Popover
              open={Boolean(anchorEl)}
              anchorEl={anchorEl}
              onClose={handleAvatarClose}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
              }}
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
            >
              <MenuItem onClick={handleLogout}>Logout</MenuItem>
              <MenuItem >Help</MenuItem>
            </Popover> */}
          </div>
        )}
        </Toolbar>
      </AppBar>
    </Box>
    </div>
  );
}
export default Header;