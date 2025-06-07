import exAxios from '../../../../../commons/services/exAxios';
import { CommonType } from '../../../../../commons/types/commonType';

const getOtpQrcode = async () => {
  return exAxios<CommonType.ResponseStatus<string>>({
    method: 'GET',
    url: '/api/v1/private/user/otp/qrcode',
    isReturnData: true,
  });
};

const postVerify = async (code: string) => {
  return exAxios<CommonType.ResponseStatus<boolean>>({
    method: 'POST',
    url: '/api/v1/private/user/otp/verify',
    body: { code },
    isReturnData: true,
  });
};

const otpServiceApi = {
  getOtpQrcode, postVerify,
};

export default otpServiceApi;