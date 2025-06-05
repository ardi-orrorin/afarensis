import { SystemSetting } from '../types/systemSetting';
import systemSettingSchema from '../types/systemSettingSchema';
import commonFunc from '../../../../../commons/services/funcs';
import systemSettingServiceApi from './api';
import { AxiosError } from 'axios';
import { CommonType } from '../../../../../commons/types/commonType';

const subtractRequiredHandler = <T extends SystemSetting.PrivateKey | SystemSetting.PublicKey>
({ key, newValue }: {
  key: keyof typeof systemSettingSchema,
  newValue: SystemSetting.Value[T]
}) => {
  const result = systemSettingSchema[key].safeParse(newValue);
  const fieldErrors = result.success
    ? {}
    : result.error.flatten().fieldErrors;

  return commonFunc.subtractRequiredStr(fieldErrors);
};

const initHandle =
  async ({
           key,
           setLoading,
           setResponse,
           refetch,
         }: {
    key: SystemSetting.PrivateKey | SystemSetting.PublicKey,
    setLoading: (value: React.SetStateAction<boolean>) => void
    setResponse: (value: React.SetStateAction<CommonType.ResponseStatus<boolean>>) => void,
    refetch: () => Promise<void>,
  }) => {
    setLoading(true);
    try {
      const res = await systemSettingServiceApi.putInit(key);
      await refetch();
      setResponse(res);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

const systemSettingFunc = {
  subtractRequiredHandler, initHandle,
};


export default systemSettingFunc;

