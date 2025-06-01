import { redirect } from 'react-router-dom';
import signOutService from './[features]/services/api';
import { CommonType } from '../../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;

const SignOutRouter: ExRouteObject = {
  id: 'root/signout',
  path: 'signout',
  name: 'Sign Out',
  requiredRoles: ['USER'],
  loader: async () => {
    await signOutService.deleteSignOut();
    return redirect('/');
  },
};

export default SignOutRouter;
