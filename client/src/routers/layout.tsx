import styles from './layout.module.css';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

import { Outlet } from 'react-router-dom';
import { HistoryPageProvider } from '../commons/hooks/usehisotryPage';
import HistoryPage from '../commons/components/HistoryPage';
import LeftNavigator from '../commons/components/LeftNavigator';
import { SignInTokenProvider } from '../commons/hooks/useSiginInToken';

dayjs.extend(utc);
dayjs.extend(timezone);

const RootLayout = () => {
  return (
    <div className={`${styles['container']}`}>
      <SignInTokenProvider>
        <HistoryPageProvider>
          <LeftNavigator />
          <main>
            <Outlet />
          </main>
          <footer>
            <HistoryPage />
          </footer>
        </HistoryPageProvider>
      </SignInTokenProvider>
    </div>
  );
};

export default RootLayout;
