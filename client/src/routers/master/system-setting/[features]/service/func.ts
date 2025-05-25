import { SystemSetting } from '../types/systemSetting';
import systemSettingSchema from '../types/systemSettingSchema';
import commonFunc from '../../../../../commons/services/funcs';

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

const systemSettingFunc = {
  subtractRequiredHandler,
};


export default systemSettingFunc;

