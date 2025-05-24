import { createBrowserRouter } from 'react-router-dom';
import Root from '.';
import RootLayout from './layout';
import ErrorComponent from '../commons/components/errorComponent';
import ExampleRouter from './example/router';
import SignupRouter from './signup/router';
import SignInRouter from './signin/router';
import SignOutRouter from './signout/router';
import MasterRouter from './master/router';

const router = createBrowserRouter([
  {
    path: '/',
    Component: RootLayout,
    errorElement: <ErrorComponent />,
    loader: async () => {
      
    },
    children: [
      {
        index: true,
        Component: Root,
      },
      ExampleRouter,
      SignupRouter,
      SignInRouter,
      SignOutRouter,
      MasterRouter,
    ],
  },
]);

export default router;
