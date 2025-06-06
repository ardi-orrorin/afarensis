import SettingItemTemplate from '../../../[features]/components/settingItemTemplate';
import systemSettingQuery from '../../../[features]/stores/query';
import { useEffect, useState } from 'react';
import { CommonType } from '../../../../../../commons/types/commonType';
import { SystemSetting } from '../../../[features]/types/systemSetting';
import systemSettingServiceApi from '../../../[features]/service/api';
import { AxiosError, AxiosResponse } from 'axios';
import Toggle from '../../../[features]/components/toggle';
import systemSettingFunc from '../../../[features]/service/func';
import commonFunc from '../../../../../../commons/services/funcs';
import PublicKey = SystemSetting.PublicKey;

const SignUp = () => {
  const { data: publicData, refetch } = systemSettingQuery.publicQuery();
  
  const [value, setValue] = useState(publicData[PublicKey.SIGN_UP].value);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SystemSetting.SignUp>);
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);

  useEffect(() => {
    if (publicData?.[PublicKey.SIGN_UP]?.value) {
      setValue(publicData[PublicKey.SIGN_UP].value);
    }
  }, [publicData]);

  const changeToggle = () => {
    const newValue = {
      ...value,
      enabled: !value.enabled,
    } as SystemSetting.Value[PublicKey.SIGN_UP];

    const result = validHandler(newValue);

    if (result.enabled && result.enabled.length > 0) {
      return;
    }

    setValue(newValue);
  };

  const validHandler = (newValue: SystemSetting.Value[PublicKey.SIGN_UP]) => {
    const subtractRequired = systemSettingFunc.subtractRequiredHandler({ key: 'SignUp', newValue });
    setErrors(subtractRequired);
    setResponse({} as CommonType.ResponseStatus<boolean>);

    return subtractRequired;
  };

  const saveHandle = async () => {
    const oldObj = publicData[PublicKey.SIGN_UP];
    const newObj = {
      ...oldObj,
      value,
    };

    try {
      setLoading(true);
      const res = await systemSettingServiceApi.post<SystemSetting.PublicSystemSetting[PublicKey.SIGN_UP]>(newObj);
      setResponse(res);
      await refetch();
    } catch (e) {
      const err = e as AxiosError;
      const res = err?.response as AxiosResponse;

      setErrors({
        enabled: res.data.message,
      });

      setValue(oldObj.value);
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const buttons = [
    {
      text: '저장',
      onClick: saveHandle,
      disabled: loading,
    },
    {
      text: '초기화',
      onClick: () => systemSettingFunc.initHandle({
        key: PublicKey.SIGN_UP,
        setLoading,
        setResponse,
        refetch,
      }),
      disabled: loading,
    },
  ] as SystemSetting.SettingTemplateBtn[];


  const otherChildren =
    <Toggle  {...{
      checked: value.enabled,
      disabled: loading,
      onChange: changeToggle,
    }} />;

  return (
    <SettingItemTemplate
      {...{
        headline: 'SIGN UP',
        buttons,
        response, errors,
        otherChildren,
        errorFields: Object.keys(errors),
      }}
    />
  );
};


export default SignUp;