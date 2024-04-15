import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import IndivMarkdown from '../IndivMarkdown';
import fetchMock from 'jest-fetch-mock';
fetchMock.enableMocks();

jest.mock("react-markdown", () => (props) => {
    return <>{props.children}</>
})

jest.mock("remark-gfm", () => () => {
})

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    Link: ({ to, children }) => <a href={to}>{children}</a>,
}));

describe('IndivMarkdown component', () => {
    it('renders individual markdown page', async () => {
  
        const matchProps = { params: { user: 'user', repo: 'repo', file: 'file' } };
        
        render(
            <BrowserRouter>
                <IndivMarkdown match={matchProps} />
            </BrowserRouter>
        );

        expect(screen.getByText('file'.slice(0, -3))).toBeInTheDocument();
        expect(screen.getByText('Previous Versions of file')).toBeInTheDocument();
        expect(screen.getByText('Edit File')).toBeInTheDocument();
        
    }); 

});