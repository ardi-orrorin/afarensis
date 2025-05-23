import { createBrowserRouter } from 'react-router-dom';
import Root from '.';
import RootLayout from './layout';
import ErrorComponent from '../commons/components/errorComponent';
import ExampleRouter from './example/router';
import SignupRouter from './signup/router';
import SignInRouter from './signin/router';
import SignOutRouter from './signout/router';

const router = createBrowserRouter([
  {
    path: '/',
    Component: RootLayout,
    errorElement: <ErrorComponent />,
    children: [
      {
        index: true,
        Component: Root,
      },
      ExampleRouter,
      SignupRouter,
      SignInRouter,
      SignOutRouter,
    ],
  },
]);

export default router;
