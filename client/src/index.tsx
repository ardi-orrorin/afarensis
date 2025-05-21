import React from 'react';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import { RouterProvider } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import './index.css';
import reactQuery from './commons/services/reractQuery';
import router from './routers/router';

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
  <React.StrictMode>
    <QueryClientProvider client={reactQuery.queryClient}>
      <RouterProvider {...{ router }} />
    </QueryClientProvider>
  </React.StrictMode>,
);

reportWebVitals();
