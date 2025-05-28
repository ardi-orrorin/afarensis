import { redirect, RouteObject } from 'react-router-dom';
import signOutService from './[features]/services/api';

const SignOutRouter: RouteObject = {
  id: 'root/signout',
  path: 'signout',
  loader: async () => {
    await signOutService.deleteSignOut();
    return redirect('/');
  },
};

export default SignOutRouter;
