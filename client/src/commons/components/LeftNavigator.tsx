import { Link, useLocation } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import { useSignInToken } from '../hooks/useSiginInToken';
import { CommonType } from '../types/commonType';
import { useMemo } from 'react';
import Role = CommonType.Role;

const LeftNavigator = () => {

  const [cookies] = useCookies(['access_token', 'refresh_token']);
  const { getRoles } = useSignInToken();
  const location = useLocation();

  const rootLink = [
    { path: '/example', name: 'Example', isLogin: true },
    { path: '/signup', name: 'SignUp', isLogin: false },
    { path: '/signin', name: 'SignIn', isLogin: false },
    { path: '/master', name: 'Master', isLogin: true },
  ];

  const masterLink = [
    { path: '/master', name: 'Master' },
    { path: '/master/system-setting', name: 'System Setting' },
    { path: '/master/system-setting/smtp', name: 'SMTP' },
  ];

  const links = useMemo(() => {
    if (location.pathname.startsWith('/master')) {
      return masterLink;
    }

    return rootLink.filter((link) => {
      return link.isLogin === (!!cookies.access_token || !!cookies.refresh_token);
    }).filter((link) => {
      return !getRoles().includes(Role.MASTER) ? link.path !== '/master' : true;
    });
  }, [location, cookies.access_token, getRoles()]);

  return (
    <nav>
      <Link to={'/'}>Home</Link>
      {
        links.map((link) => {
          return (
            <Link key={link.path} to={link.path}>{link.name}</Link>
          );
        })
      }
      {
        cookies.access_token
        && <Link to={'/signout'}>SignOut</Link>
      }
    </nav>
  );
};

export default LeftNavigator;