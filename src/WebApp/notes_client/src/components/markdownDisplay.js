import React, { useEffect, useState } from 'react';

const MarkdownDisplay = ({ filename }) => {
    const [markdownContent, setMarkdownContent] = useState('');

    useEffect(() => {
        const apiUrl = `http:/127.0.0.1:8000//display_markdown/${filename}`;

        fetch(apiUrl)
            .then((response) => {
                if (response.ok) {
                    console.log(response)
                } else {
                    throw new Error('Failed to fetch Markdown content');
                }
            })
            .then((data) => {
                console.log(data)
                setMarkdownContent(data);
            })
            .catch((error) => {
                //console.log(response)
                console.error('Error:', error);
            });
    }, [filename]);
    return (
        <div>
            <pre>{markdownContent}</pre>
        </div>
    );
};

export {MarkdownDisplay};