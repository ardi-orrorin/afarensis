import ExAxios from '../../../../../commons/services/exAxios';
import exAxios from '../../../../../commons/services/exAxios';
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

const putInit = async (key: SystemSetting.PrivateKey | SystemSetting.PublicKey) => {
  return ExAxios<CommonType.ResponseStatus<boolean>, { key: SystemSetting.PrivateKey | SystemSetting.PublicKey }>({
    method: 'PUT',
    url: '/api/v1/private/master/system-setting/init',
    body: { key },
    isReturnData: true,
  });
};


const post =
  async <T extends SystemSetting.PrivateSystemSetting[keyof SystemSetting.PrivateSystemSetting] | SystemSetting.PublicSystemSetting[keyof SystemSetting.PublicSystemSetting]>(body: T) => {
    return exAxios<CommonType.ResponseStatus<boolean>, T>({
      method: 'PUT',
      url: '/api/v1/private/master/system-setting',
      body,
      isReturnData: true,
    });
  };

const systemSettingServiceApi = {
  getPublic,
  getPrivate,
  putInit,
  post,
};

export default systemSettingServiceApi;