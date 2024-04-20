import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import EditMarkdownFile from '../EditMarkdownFile';
import fetchMock from 'jest-fetch-mock';
import mockIndivFileData from '../mockIndivFileData';
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

jest.mock('node-fetch');

global.fetch = jest.fn().mockResolvedValueOnce({
    json: () => Promise.resolve(mockIndivFileData),
  });

describe('editMarkdownFile component', () => {
    it('editing a file', async () => {

    const matchProps = { params: { user: 'user', repo: 'repo', file: 'file' } };
    render(
            <EditMarkdownFile match={matchProps} />
    );
    await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
    });
    
    expect(screen.getByText('Save')).toBeInTheDocument();
    const title = screen.getByLabelText('Title', {exact:false});
    expect(title).toHaveValue('test');
    expect(screen.getByText('test')).toBeInTheDocument();
    const markdownContents = screen.getAllByText('sgsegdsg');
    expect(markdownContents.length).toBe(2);

    const contentInput = screen.getByLabelText('Content', {exact:false});
    fireEvent.change(contentInput, { target: { value: 'secondtest' } });

    await waitFor(() => {
        const markdownContents2 = screen.getAllByText('secondtest');
        expect(markdownContents2.length).toBe(2);
    });

    fireEvent.change(contentInput, { target: { value: '*bold* markdown test' } });

    await waitFor(() => {
        const markdownContents3 = screen.getAllByText('*bold* markdown test');
        expect(markdownContents3.length).toBe(2);
    });

    screen.debug()
      
    });  

  });