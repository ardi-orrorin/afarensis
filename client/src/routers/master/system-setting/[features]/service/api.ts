import ExAxios from '../../../../../commons/services/exAxios';
import { SystemSetting } from '../types/systemSetting';
import { CommonType } from '../../../../../commons/types/commonType';

const getPublic = async () => {
  return ExAxios<SystemSetting.PublicSystemSetting>({
    url: '/api/v1/public/system-setting',
    method: 'GET',
    isReturnData: true,
  });
};

const getPrivate = async () => {
  return ExAxios<SystemSetting.PrivateSystemSetting>({
    url: '/api/v1/private/master/system-setting',
    method: 'GET',
    isReturnData: true,
  });
};

const putInit = async (key: SystemSetting.Key) => {
  return ExAxios<CommonType.ResponseStatus<boolean>, { key: SystemSetting.Key }>({
    method: 'PUT',
    url: '/api/v1/private/master/system-setting/init',
    body: { key },
    isReturnData: true,
  });
};


const systemSettingServiceApi = {
  getPublic,
  getPrivate,
  putInit,
};

export default systemSettingServiceApi;