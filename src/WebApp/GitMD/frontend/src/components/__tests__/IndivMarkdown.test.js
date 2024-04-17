import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import IndivMarkdown from '../IndivMarkdown';
import mockIndivFileData from '../mockIndivFileData';
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

global.fetch.mockResolvedValueOnce({
    json: () => Promise.resolve(mockIndivFileData),
  }).mockResolvedValueOnce({
    json: () => Promise.resolve([['NDQ0NDQ=', 'web', '17/04/2024', '14:25:30'], ['ZGRk', 'web', '17/04/2024', '14:23:35']]),
  });

describe('IndivMarkdown component', () => {
    it('renders individual markdown page', async () => {
  
        const matchProps = { params: { user: 'user', repo: 'repo', file: 'file' } };
        
        render(
                <IndivMarkdown match={matchProps} />
        );
        await act(async () => {
            await new Promise(resolve => setTimeout(resolve, 0));
        });

        expect(screen.getByText('file'.slice(0, -3))).toBeInTheDocument();
        expect(screen.getByText('Previous Versions of file')).toBeInTheDocument();
        expect(screen.getByText('sgsegdsg')).toBeInTheDocument();
        expect(screen.getByText('Edit File')).toBeInTheDocument();

        const showOldContent = screen.getByText("User web on the 17/04/2024 at 14:23:35");
        fireEvent.click(showOldContent);
        expect(screen.getByText('ddd')).toBeInTheDocument();

        //screen.debug()
        
    }); 

});