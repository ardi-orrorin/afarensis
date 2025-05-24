import { SystemSetting } from '../../../[features]/types/systemSetting';
import exAxios from '../../../../../../commons/services/exAxios';
import { CommonType } from '../../../../../../commons/types/commonType';

const postTest = async (body: SystemSetting.Smtp) => {
  return exAxios<CommonType.ResponseStatus<boolean>, SystemSetting.Smtp>({
    method: 'POST',
    url: '/api/v1/private/master/system-setting/smtp/test',
    body: body,
    isReturnData: true,
  });
};

const post = async (body: SystemSetting.PrivateSystemSetting[SystemSetting.Key.SMTP]) => {
  return exAxios<CommonType.ResponseStatus<boolean>, SystemSetting.PrivateSystemSetting[SystemSetting.Key.SMTP]>({
    method: 'PUT',
    url: '/api/v1/private/master/system-setting',
    body,
    isReturnData: true,
  });
};

const smtpServiceApi = {
  postTest, post,
};

export default smtpServiceApi;