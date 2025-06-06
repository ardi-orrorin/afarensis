import exAxios from '../../../../../commons/services/exAxios';
import { PassKeyType } from '../types/passkey';
import { CommonType } from '../../../../../commons/types/commonType';

const getPasskeys = async () => {
  return exAxios<PassKeyType.List>({
    method: 'GET',
    url: '/api/v1/private/user/passkey',
    isReturnData: true,
  }).then(res => res.data);
};

const getCredentialOption = async () => {
  return exAxios<CommonType.ResponseStatus<string>>({
    method: 'GET',
    url: '/api/v1/private/user/passkey/credential',
    isReturnData: true,
  });
};

const postRegistration = async ({ json }: { json: string }) => {
  return exAxios<CommonType.ResponseStatus<boolean>, { json: string }>({
    method: 'POST',
    url: '/api/v1/private/user/passkey/registration',
    body: { json },
    isReturnData: true,
  });
};

const getAssertionOption = async (userId: string) => {
  return exAxios<CommonType.ResponseStatus<string>>({
    method: 'GET',
    url: '/api/v1/public/users/signin/passkey-start',
    params: { userId },
    isReturnData: true,
  });
};

const deletePasskey = async (id: string) => {
  return exAxios<CommonType.ResponseStatus<boolean>>({
    method: 'DELETE',
    url: '/api/v1/private/user/passkey',
    params: { id },
    isReturnData: true,
  });
};

const postFinishAssertion = async (body: PassKeyType.FinishAssertionRequest) => {
  return exAxios<CommonType.ResponseStatus<boolean>>({
    method: 'POST',
    url: '/api/v1/public/users/signin/passkey-finish',
    body,
    isReturnData: true,
  });
};

const passkeyServiceApi = {
  getPasskeys, getCredentialOption,
  postRegistration, getAssertionOption, postFinishAssertion,
  deletePasskey,
};

export default passkeyServiceApi;