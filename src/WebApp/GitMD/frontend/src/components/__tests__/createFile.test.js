import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import CreateMarkdownFile from '../CreateMarkdownFile';
import fetchMock from 'jest-fetch-mock';
import mockCreateFileData from '../mockCreateFileData';
import { ConstructionOutlined } from '@mui/icons-material';
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

global.fetch.mockResolvedValueOnce({
    json: () => Promise.resolve(mockCreateFileData),
  });

describe('CreateMarkdownFile component', () => {
    it('creating a file', async () => {

    render(<CreateMarkdownFile match={{ params: { view: '' } }} />);
    await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
    });
    await waitFor(() => {
        expect(screen.queryByText('contentcontent')).not.toBeInTheDocument();
      });

    const collectionInput = screen.getByLabelText('Repository Title', {exact:false});
    const titleInput = screen.getByLabelText('File Title', {exact:false});
    const contentInput = screen.getByLabelText('Content', {exact:false});
    const submitButton = screen.getByText('Create');

    fireEvent.change(collectionInput, { target: { value: 'collection' } });
    fireEvent.change(titleInput, { target: { value: 'title' } });
    fireEvent.change(contentInput, { target: { value: 'contentcontent' } });
    expect(collectionInput).toHaveValue('collection');
    expect(titleInput).toHaveValue('title');
    expect(contentInput).toHaveValue('contentcontent');

    await waitFor(() => {
        expect(screen.queryByText('contentcontent', { selector: 'p' })).toBeInTheDocument();
        expect(screen.queryByText('contentcontent', { selector: 'textarea' })).toBeInTheDocument();
    });

    fireEvent.click(submitButton);
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
      });

    //screen.debug()
      
    });  

  });