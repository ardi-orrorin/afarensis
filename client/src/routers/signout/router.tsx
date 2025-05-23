import { redirect, RouteObject } from 'react-router-dom';
import signOutService from './[features]/services/api';
import { Cookies } from 'react-cookie';

const SignOutRouter: RouteObject = {
  id: 'root/signout',
  path: '/signout',
  loader: async () => {
    const cookie = new Cookies();
    const refreshToken = cookie.get('refresh_token');
    if (!refreshToken) return redirect('/');
    
    const userId = cookie.get('user_id');
    if (!userId) return redirect('/');


    try {
      await signOutService.deleteSignOut();
    } catch (e) {
      return redirect('/');
    } finally {
      cookie.remove('access_token');
      cookie.remove('refresh_token');
      cookie.remove('user_id');
      return redirect('/');
    }
  },
};

export default SignOutRouter;
