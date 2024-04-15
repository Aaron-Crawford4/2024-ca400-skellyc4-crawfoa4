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
    it('renders share with me collections page', async () => {
  
      render(<ViewMarkdownFile match={{ params: { view: 'shared-with-me' } }} />);
      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 0));
      });
      
      expect(screen.getByText('Shared With Me')).toBeInTheDocument();
      expect(screen.getByText('Aaron')).toBeInTheDocument();
      expect(screen.getByText('images')).toBeInTheDocument();
      expect(screen.getByText('01-03-2024')).toBeInTheDocument();
    }); 
  });