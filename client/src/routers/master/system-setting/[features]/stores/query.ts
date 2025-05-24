import reactQuery from '../../../../../commons/services/reractQuery';
import systemSettingServiceApi from '../service/api';
import { SystemSetting } from '../types/systemSetting';


const publicQuery = () => {
  type Data = SystemSetting.PublicSystemSetting;

  const queryKey = ['systemSetting', 'public'];

  return reactQuery.baseFetchQueryFn<Data>({
    queryKey,
    fetchApi: systemSettingServiceApi.getPublic,
    initialData: {} as Data,
  });
};

const privateQuery = () => {
  type Data = SystemSetting.PrivateSystemSetting;
  const queryKey = ['systemSetting', 'private'];
  return reactQuery.baseFetchQueryFn<Data>({
    queryKey,
    fetchApi: systemSettingServiceApi.getPrivate,
    initialData: {} as Data,
  });
};


const systemSettingQuery = {
  publicQuery,
  privateQuery,
};

export default systemSettingQuery;