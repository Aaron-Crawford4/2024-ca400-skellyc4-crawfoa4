import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import ViewMarkdownFile from '../ViewMarkdownFile';
import fetchMock from 'jest-fetch-mock';
import mockRepoData from '../mockRepoData';
import mockFileData from '../mockFileData';
import mockDeletedFileData from '../mockDeletedFileData';
fetchMock.enableMocks();

jest.mock('node-fetch');
global.fetch = jest.fn().mockResolvedValueOnce({
  json: () => Promise.resolve(mockRepoData),
});

describe('ViewMarkdownFile component', () => {
    it('renders files in collection page', async () => {

      render(<ViewMarkdownFile match={{ params: { view: '' } }} />);
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
      });

      //screen.debug()
      expect(screen.getByText('Collections')).toBeInTheDocument();

      global.fetch.mockResolvedValueOnce({
        json: () => Promise.resolve(mockFileData),
      }).mockResolvedValueOnce({
        json: () => Promise.resolve(["user1", "user2"]),
      }).mockResolvedValueOnce({
        json: () => Promise.resolve(mockDeletedFileData),
      }).mockResolvedValueOnce({
        json: () => Promise.resolve(""),
      }).mockResolvedValueOnce({
        json: () => Promise.resolve(""),
      });
      fireEvent.click(screen.getByText('FirstCollection'));
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
      });


      expect(screen.getByText('New File')).toBeInTheDocument();
      expect(screen.getByText('HTTP')).toBeInTheDocument();
      expect(screen.getByText('SSH')).toBeInTheDocument();
      fireEvent.click(screen.getByText('Name'));
      fireEvent.click(screen.getByText('Created On'));

      expect(screen.getByText('Collection: FirstCollection')).toBeInTheDocument();
      expect(screen.getByText('test1.md')).toBeInTheDocument();
      expect(screen.getByText('test2.md')).toBeInTheDocument();
      expect(screen.getByText('11-04-2024')).toBeInTheDocument();
      expect(screen.getByText('10-04-2024')).toBeInTheDocument();
      
      expect(screen.getByText('Users With Access')).toBeInTheDocument();
      expect(screen.getByText('user1')).toBeInTheDocument();
      expect(screen.getByText('(owner)')).toBeInTheDocument();
      expect(screen.getByText('user2')).toBeInTheDocument();
      //screen.debug(undefined, Infinity)
      const addUserInput = screen.getByLabelText('Add User To Repository', {exact:false});
      fireEvent.change(addUserInput, { target: { value: 'user3' } });
      expect(addUserInput).toHaveValue('user3');

      fireEvent.click(screen.getByText("Add User"));
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
     });
      
      const removeUserButton = screen.getAllByRole('button', { name: '' })[2];
      fireEvent.click(removeUserButton);
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
      });
      screen.debug(undefined, Infinity)
    });  

  });