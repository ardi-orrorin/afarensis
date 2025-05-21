import styles from './layout.module.css';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

import { Link, Outlet } from 'react-router-dom';
import { HistoryPageProvider } from '../commons/hooks/usehisotryPage';
import HistoryPage from '../commons/components/HistoryPage';

dayjs.extend(utc);
dayjs.extend(timezone);

const RootLayout = () => {
  return (
    <div className={`${styles['container']}`}>
      <HistoryPageProvider>
        <nav>
          <Link to={`/`}>Home</Link>
        </nav>
        <main>
          <Outlet />
        </main>
        <footer>
          <HistoryPage />
        </footer>
      </HistoryPageProvider>
    </div>
  );
};

export default RootLayout;
