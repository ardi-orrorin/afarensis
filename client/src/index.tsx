import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import './index.css';
import reactQuery from './commons/services/reractQuery';
import rootRouter from './routers/router';
import { CookiesProvider } from 'react-cookie';

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
  // <React.StrictMode>
  <CookiesProvider>
    <QueryClientProvider client={reactQuery.queryClient}>
      <RouterProvider {...{ router: createBrowserRouter([rootRouter]) }} />
    </QueryClientProvider>
  </CookiesProvider>,
  // </React.StrictMode>,
);

reportWebVitals();

