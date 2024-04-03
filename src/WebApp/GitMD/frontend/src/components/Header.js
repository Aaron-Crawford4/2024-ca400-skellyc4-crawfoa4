import React, { useState, useEffect } from 'react';
import Typography from '@mui/material/Typography';
import { Link, useHistory } from 'react-router-dom';
import Avatar from '@mui/material/Avatar';
import { deepPurple } from '@mui/material/colors';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import AppBar from '@mui/material/AppBar';
import CssBaseline from '@mui/material/CssBaseline';
import Toolbar from '@mui/material/Toolbar';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import HomeIcon from '@mui/icons-material/Home';
import FolderIcon from '@mui/icons-material/Folder';
import FolderSharedIcon from '@mui/icons-material/FolderShared';
import DeleteIcon from '@mui/icons-material/Delete';
import LogoutIcon from '@mui/icons-material/Logout';
import HelpIcon from '@mui/icons-material/Help';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import AddBoxIcon from '@mui/icons-material/AddBox';

const drawerWidth = 240;

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [name, setName] = useState('');
  const [anchorEl, setAnchorEl] = useState(null);
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

  const getLinkForIndex = (index) => {
    console.log(index)
    switch (index) {
      case 0:
        window.location.href = '/';
        break;
      case 1:
        window.location.href = '/my-collection';
        break;
      case 2:
        window.location.href = '/shared-with-me';
        break;
      case 3:
        handleLogout()
        break;
      case 4:
        window.location.href = '/help';
        break;
      default:
        window.location.href = '/';
        break;
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

  return (
    <div className='header'>
    <Box sx={{ display: 'flex' }}>
    <CssBaseline />
    <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
      <Toolbar>
        <Typography variant="h6" noWrap component="div">
          GITMD
        </Typography>
      </Toolbar>
    </AppBar>
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box', bgcolor: '#f2f2f2', },
      }}
    >
      <Toolbar />
      <Box sx={{ overflow: 'auto' }}>
        <List>
          <ListItem disablePadding>
            <ListItemButton sx={{ cursor: 'default', '&:hover': { backgroundColor: 'inherit' } }}>
                <ListItemText primary={"Welcome " + name} />
            </ListItemButton>
          </ListItem>
          <ListItem disablePadding>
              <ListItemButton to='/create'>
                <ListItemIcon>
                  <AddBoxIcon />
                </ListItemIcon>
                <ListItemText> New Collection </ListItemText>
              </ListItemButton>
            </ListItem>
        </List>
        <Divider />
        <List>
          {['Home', 'My Collection', 'Shared With Me'].map((text, index) => (
            <ListItem key={text} disablePadding>
              <ListItemButton onClick={() => to = getLinkForIndex(index)}>
                <ListItemIcon>
                  {index === 0 && <HomeIcon />}
                  {index === 1 && <FolderIcon />}
                  {index === 2 && <FolderSharedIcon />}
                </ListItemIcon>
                <ListItemText primary={text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider />
        <List>
          {['Sign Out', 'Help'].map((text, index) => (
            <ListItem key={text} disablePadding>
              <ListItemButton onClick={() => to = getLinkForIndex(index + 3)}>
                <ListItemIcon>
                  {index % 2 === 0 ? <LogoutIcon /> : <HelpIcon />}
                </ListItemIcon>
                <ListItemText primary={index === 1 ? 'Help' : isLoggedIn ? 'Sign Out' : 'Sign In'} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Box>
    </Drawer>
  </Box>
  <Toolbar />
  </div>
  );
};

export default Header;