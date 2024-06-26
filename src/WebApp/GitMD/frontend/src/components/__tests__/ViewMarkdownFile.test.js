import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import ViewMarkdownFile from '../ViewMarkdownFile';
import fetchMock from 'jest-fetch-mock';
import mockRepoData from '../mockRepoData';
fetchMock.enableMocks();

jest.mock('node-fetch');
global.fetch = jest.fn().mockResolvedValueOnce({
  json: () => Promise.resolve(mockRepoData),
}).mockResolvedValueOnce({
  json: () => Promise.resolve(""),
});

describe('ViewMarkdownFile component', () => {
  
    it('renders all collections page', async () => {

      render(<ViewMarkdownFile match={{ params: { view: '' } }} />);
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
      });

      //screen.debug()
      expect(screen.getByText('Collections')).toBeInTheDocument();
      expect(screen.getByText('Created On')).toBeInTheDocument();
      expect(screen.getByText('Name')).toBeInTheDocument();
      expect(screen.getByText('FirstCollection')).toBeInTheDocument();
      expect(screen.getByText('Bobby3')).toBeInTheDocument();
      expect(screen.getByText('04-04-2024')).toBeInTheDocument();
      expect(screen.getByText('Aaron')).toBeInTheDocument();
      expect(screen.getByText('images')).toBeInTheDocument();
      expect(screen.getByText('01-03-2024')).toBeInTheDocument();

      fireEvent.click(screen.getByText('Name'));
      fireEvent.click(screen.getByText('Created On'));

      const deleteButton = screen.getAllByRole('button', { name: '' })[0];
      //fireEvent.click(deleteButton);

      screen.debug(undefined, Infinity)
    });  

  });