import React, { Component } from "react";
import Box from '@mui/material/Box';
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import TextField from '@mui/material/TextField';
import Button from "@mui/material/Button";
import Paper from '@mui/material/Paper';
import ReactMarkdown from "react-markdown";
import remarkGfm from 'remark-gfm';

export default class HelpView extends Component {
  constructor(props) {
    super(props);
    this.state = {
        email: "",
        password: "",
        password2: "",
        content: "",
        markdownContent: ""
      };
  }

  componentDidMount() {
    
    const markdown = `# GitMD


Below is examples of what you can do in markdown.  
You can edit this file as you wish to get a better understanding of the capabilities  
of markdown  

# Heading 1

Different Headings can be achieved by using the hash symbol before a sentence  
and these can be scaled from 1 has symbol to 6 to change the size

###### Heading 6

Text can be written normally like so in bold **like so** or italic *like so*  
you can also combine **bold *italic* text**  
We can strike through words like so ~~The world is flat.~~  
or put a divider through a page like so.  
___

## Lists

Lists in markdown can be numbered:  
1. item 
2. next item
    1. nested item
3. following item  

Or bullet points:  
- First item
- Second item
- Third item
    - Indented item
    - Indented item
- Fourth item

Unordered list can use  
*  asterisks

- Or minuses

+ Or pluses

We can also make task lists like so:  

- [x] Write the press release
- [ ] Update the website
- [ ] Contact the media 

## Tables  

Tables can be made as seen below

| Kilometres| Miles |
| - | - |
| 5| 9|
| 17| 23| 

## Footnote  

Footnotes can also be written like so, clicking the link will bring you to the bottom of the page where to footnote is stored.  
A note[^1]

[^1]: Big note.

## Links  

- Links can be written like so https://Google.com  
- or you can hide the link behind a word like so  
My favourite search engine is [Google](https://Google.com).  
- We can also make text appear when a link is hovered  
My favourite search engine is [Google](https://Google.com "A Search Engine").  
- Email addresses can also be links fake@example.com  
- We can also store all our links elsewhere by doing the following  
My favourite search engine is [Google][1].  

[1]: https://Google.com

### Code

we can write code functions like so to differentiate it from normal text  
\`function example(){const x = 10;}\`  

### Images  

Images and gifs can be placed inside markdown files like so  


#### Inline-style: 
![alt text](https://content.codecademy.com/courses/learn-cpp/community-challenge/highfive.gif "Gif Title")


#### Reference-style:   
Or you Can Reference an image to have less content on your page like so
![Image of a Cat][logo]

[logo]: http://www.online-image-editor.com//styles/2014/images/example_image.png "Cat"
`;
    

    this.setState({content : markdown, markdownContent : markdown })
}

  handleInputChange = (e) => {
    this.setState({ [e.target.name]: e.target.value });
  };

  handleContentChange = (event) => {
    this.setState({
      content: event.target.value,
      markdownContent: event.target.value,
    });
  };

  render() {

    return (
        <Box component="main" sx={{ flexGrow: 1, p: 10 }}>

            <Typography textAlign={"center"} style={{ marginTop: '10px' }} component="h4" variant="h4">
                Markdown Help
            </Typography>
            <div style={{ display: 'flex', flexWrap: 'wrap', flexDirection: 'row' }}>
            <Paper elevation={3} className="paper-container">
                <TextField
                label="Content"
                margin="none"
                multiline
                fullWidth
                inputProps={{
                    style: {
                    fontSize: 16,
                    fontFamily: 'Arial',
                    lineHeight: '1.5',
                    },
                }}
                value={this.state.content}
                onChange={this.handleContentChange}
                />
            </Paper>
            <Paper elevation={3} className="paper-container" >
                <Typography 
                label="Markdown Content"
                multiline
                fullWidth
                style={{
                    overflowWrap: 'break-word',
                    maxWidth: '100%',
                    paddingLeft: "10px",
                    paddingRight: "10px",
                }}
                margin="normal">
                <ReactMarkdown remarkPlugins={[remarkGfm]}  children={this.state.markdownContent}></ReactMarkdown>
                </Typography>
            </Paper>
            </div>
        </Box>
    );
  }
}