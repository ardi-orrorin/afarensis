import reactQuery from '../../../../../commons/services/reractQuery';
import { PassKeyType } from '../types/passkey';
import passkeyServiceApi from '../services/api';

const passkey = () => {
  const queryKey = ['passkey'];

  return reactQuery.baseFetchQueryFn<PassKeyType.PassKey[]>({
    queryKey,
    fetchApi: passkeyServiceApi.getPasskeys,
    initialData: {} as PassKeyType.PassKey[],
  });
};


const passkeyQuery = {
  passkey,
};


export default passkeyQuery;