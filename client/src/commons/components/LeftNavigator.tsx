import { Link } from 'react-router-dom';
import { useState } from 'react';
import { CommonType } from '../types/commonType';
import commonFunc from '../services/funcs';
import rootRouter from '../../routers/router';
import styles from './leftNavigator.module.css';
import { useCookies } from 'react-cookie';
import systemSettingQuery from '../../routers/master/system-setting/[features]/stores/query';
import { SystemSetting } from '../../routers/master/system-setting/[features]/types/systemSetting';
import RoutePathObject = CommonType.RoutePathObject;
import PublicKey = SystemSetting.PublicKey;

interface NavigationItemProps {
  link: RoutePathObject;
  cookies: {
    access_token?: string;
    refresh_token?: string;
    roles?: string[];
    user_id?: string;
  };
}

const ROLE_RESTRICTED_ROUTES = {
  master: 'MASTER',
  admin: 'ADMIN',
  user: 'USER',
} as const;

const AUTH_ROUTES = ['signin', 'signup'] as const;

const LeftNavigator = () => {
  const [cookies] = useCookies(['access_token', 'roles', 'user_id']);
  const linkObj = [{ path: '/', name: 'Home' }, ...commonFunc.getAllRoutePaths(rootRouter)] as RoutePathObject[];

  return (
    <nav className={styles['nav']}>
      <ul className={styles['nav-container']}>
        {linkObj.map((link) => (
          <NavigationItem key={link.path} {...{ link, cookies }} />
        ))}
      </ul>
    </nav>
  );
};

const NavigationItem = ({ link, cookies }: NavigationItemProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const { data: publicData } = systemSettingQuery.publicQuery();
  const signUp = publicData[PublicKey.SIGN_UP].value;

  const isAuthenticated = Boolean(cookies.access_token ?? cookies.user_id);

  const hasRequiredRole = (routeName: string): boolean => {
    const requiredRole = ROLE_RESTRICTED_ROUTES[routeName as keyof typeof ROLE_RESTRICTED_ROUTES];
    return !requiredRole || cookies.roles?.includes(requiredRole) || false;
  };

  const shouldHideLink = (): boolean => {
    const pathName = link.path.replace('/', '');
    if (AUTH_ROUTES.includes(pathName as any) && isAuthenticated) return true;
    if (pathName === 'signout' && !isAuthenticated) return true;
    if (pathName === 'signup' && !signUp.enabled) return true;
    if (!hasRequiredRole(pathName)) return true;
    return false;
  };

  if (shouldHideLink()) {
    return null;
  }

  const toggleSubmenu = () =>
    setIsOpen((prev) => !prev);

  return (
    <li className={styles['nav-item']}>
      <div className={styles['nav-link-container']}>
        {
          link.children
            ? <button className={styles['nav-btn']}
                      onClick={toggleSubmenu}
            >
              {link.name}
            </button>
            : <Link to={link.path}
                    className={styles['nav-link']}
            >
              {link.name}
            </Link>
        }
        {
          link.children
          && <button className={styles['toggle-button']}
                     onClick={toggleSubmenu}
                     aria-expanded={isOpen}
                     aria-label={`Toggle ${link.name} submenu`}
          >
            <span className={`${styles['arrow']} ${isOpen ? styles['arrow-up'] : styles['arrow-down']}`}
            >
              ▼
            </span>
          </button>
        }
      </div>
      {
        link.children
        && <ul className={`${styles['submenu']} ${isOpen ? styles['submenu-open'] : ''}`}>
          {
            link.children.map((child) => (
              <NavigationItem key={child.path} link={child} cookies={cookies} />
            ))
          }
        </ul>
      }
    </li>
  );
};

export default LeftNavigator;
