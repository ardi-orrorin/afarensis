import { PasswordType } from '../types/password';
import ExAxios from '../../../../../commons/services/exAxios';
import { CommonType } from '../../../../../commons/types/commonType';
import ResponseStatus = CommonType.ResponseStatus;

const patchUpdatePassword = async (req: PasswordType.Password) => {
  return ExAxios<ResponseStatus<boolean>, PasswordType.Password>({
    method: 'PATCH',
    url: '/api/v1/private/user/users/update-password',
    body: req,
    isReturnData: true,
  });
};

const passwordServiceApi = {
  patchUpdatePassword,
};

export default passwordServiceApi;