import { createBrowserRouter } from 'react-router-dom';
import Root from '.';
import RootLayout from './layout';
import Error from '../commons/components/error';

const router = createBrowserRouter([
  {
    path: '/',
    Component: RootLayout,
    errorElement: <Error />,
    children: [
      {
        index: true,
        Component: Root,
      },
    ],
  },
]);

export default router;
