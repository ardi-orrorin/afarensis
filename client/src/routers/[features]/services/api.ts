import { RootType } from '../types/rootType';
import exAxios from '../../../commons/services/exAxios';
import { CommonType } from '../../../commons/types/commonType';

const patchUpdateMaster = async ({ body }: { body: RootType.Master }) => {
  return exAxios<CommonType.ResponseStatus<boolean>>({
    method: 'PATCH',
    url: '/api/v1/public/users/master',
    body,
    isReturnData: true,
  });
};

const rootServiceApi = { patchUpdateMaster };

export default rootServiceApi;