import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import ViewMarkdownFile from '../ViewMarkdownFile';
import fetchMock from 'jest-fetch-mock';
import mockRepoData from '../mockRepoData';
fetchMock.enableMocks();

jest.mock('node-fetch');
global.fetch = jest.fn().mockResolvedValueOnce({
  json: () => Promise.resolve(mockRepoData),
});

describe('ViewMarkdownFile component', () => {
    it('renders my collections page', async () => {
  
      render(<ViewMarkdownFile match={{ params: { view: 'my-collection' } }} />);
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
      });
      
      //screen.debug()
      expect(screen.getByText('My Collections')).toBeInTheDocument();
      expect(screen.getByText('Created On')).toBeInTheDocument();
      expect(screen.getByText('Name')).toBeInTheDocument();
      expect(screen.getByText('FirstCollection')).toBeInTheDocument();
      expect(screen.getByText('Bobby3')).toBeInTheDocument();
      expect(screen.getByText('04-04-2024')).toBeInTheDocument();
      
    }); 


  });