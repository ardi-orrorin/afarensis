import styles from './layout.module.css';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

import { Outlet } from 'react-router-dom';
import { HistoryPageProvider } from '../commons/hooks/usehisotryPage';
import HistoryPage from '../commons/components/HistoryPage';
import LeftNavigator from '../commons/components/LeftNavigator';
import { SignInTokenProvider } from '../commons/hooks/useSiginInToken';
import systemSettingQuery from './master/system-setting/[features]/stores/query';
import { SystemSetting } from './master/system-setting/[features]/types/systemSetting';
import { useMemo } from 'react';
import InitMaster from './[features]/components/initMaster';

dayjs.extend(utc);
dayjs.extend(timezone);

const RootLayout = () => {

  const { data } = systemSettingQuery.publicQuery();
  
  const isUpdatedMaster = useMemo(() => {
    const init = data[SystemSetting.PublicKey.INIT].value;
    return init.isUpdatedMasterPwd && init.isUpdatedMasterPwd;
  }, [data]);

  if (!isUpdatedMaster) return <InitMaster />;

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
