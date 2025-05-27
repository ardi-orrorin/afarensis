import { Link } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import { useState } from 'react';
import { CommonType } from '../types/commonType';
import commonFunc from '../services/funcs';
import rootRouter from '../../routers/router';
import styles from './leftNavigator.module.css';
import RoutePathObject = CommonType.RoutePathObject;

interface NavigationItemProps {
  link: RoutePathObject;
  cookies: {
    access_token?: string;
    refresh_token?: string;
    roles?: string[];
  };
}

const ROLE_RESTRICTED_ROUTES = {
  master: 'MASTER',
  admin: 'ADMIN',
} as const;

const AUTH_ROUTES = ['signin', 'signup'] as const;

const LeftNavigator = () => {
  const [cookies] = useCookies(['access_token', 'refresh_token', 'roles']);
  const linkObj = [{ path: '/', name: 'Home' }, ...commonFunc.getAllRoutePaths(rootRouter)] as RoutePathObject[];
  return (
    <nav className={styles['nav']}>
      <ul className={styles['nav-container']}>
        {linkObj.map((link) => (
          <NavigationItem key={link.path} link={link} cookies={cookies} />
        ))}
      </ul>
    </nav>
  );
};

const NavigationItem = ({ link, cookies }: NavigationItemProps) => {
  const [isOpen, setIsOpen] = useState(false);

  const isAuthenticated = Boolean(cookies.access_token || cookies.refresh_token);
  const hasRequiredRole = (routeName: string): boolean => {
    const requiredRole = ROLE_RESTRICTED_ROUTES[routeName as keyof typeof ROLE_RESTRICTED_ROUTES];
    return !requiredRole || cookies.roles?.includes(requiredRole) || false;
  };

  const shouldHideLink = (): boolean => {
    if (AUTH_ROUTES.includes(link.name as any) && isAuthenticated) return true;
    if (link.name === 'signout' && !isAuthenticated) return true;
    if (!hasRequiredRole(link.name)) return true;
    return false;
  };

  if (shouldHideLink()) {
    return null;
  }

  const toggleSubmenu = () => setIsOpen((prev) => !prev);

  return (
    <li className={styles['nav-item']}>
      <div className={styles['nav-link-container']}>
        <Link to={link.path} className={styles['nav-link']}>
          {link.name.toLocaleUpperCase()}
        </Link>
        {link.children && (
          <button
            className={styles['toggle-button']}
            onClick={toggleSubmenu}
            aria-expanded={isOpen}
            aria-label={`Toggle ${link.name} submenu`}
          >
            <span className={`${styles['arrow']} ${isOpen ? styles['arrow-up'] : styles['arrow-down']}`}>▼</span>
          </button>
        )}
      </div>
      {link.children && (
        <ul className={`${styles['submenu']} ${isOpen ? styles['submenu-open'] : ''}`}>
          {link.children.map((child) => (
            <NavigationItem key={child.path} link={child} cookies={cookies} />
          ))}
        </ul>
      )}
    </li>
  );
};

export default LeftNavigator;
