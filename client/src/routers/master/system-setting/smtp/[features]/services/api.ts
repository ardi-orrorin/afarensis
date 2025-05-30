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


const smtpServiceApi = {
  postTest,
};

export default smtpServiceApi;