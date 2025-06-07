import SettingItemTemplate from '../../../[features]/components/settingItemTemplate';
import systemSettingQuery from '../../../[features]/stores/query';
import { useEffect, useState } from 'react';
import { CommonType } from '../../../../../../commons/types/commonType';
import { SystemSetting } from '../../../[features]/types/systemSetting';
import systemSettingFunc from '../../../[features]/service/func';
import Toggle from '../../../[features]/components/toggle';
import systemSettingServiceApi from '../../../[features]/service/api';
import { AxiosError, AxiosResponse } from 'axios';
import commonFunc from '../../../../../../commons/services/funcs';
import styles from './otp.module.css';
import PublicKey = SystemSetting.PublicKey;

const Otp = () => {
  const { data: publicData, refetch } = systemSettingQuery.publicQuery();
  const [value, setValue] = useState(publicData[PublicKey.OTP].value);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SystemSetting.Otp>);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (publicData) {
      setValue(publicData[PublicKey.OTP].value);
    }
  }, [publicData]);

  const inputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = {
      ...value,
      [event.target.name]: event.target.value,
    } as SystemSetting.Value[PublicKey.OTP];

    validHandler(newValue);

    setValue(newValue);
  };

  const validHandler = (newValue: SystemSetting.Value[PublicKey.OTP]) => {
    const subtractRequired = systemSettingFunc.subtractRequiredHandler({ key: 'Otp', newValue });
    setErrors(subtractRequired);
    setResponse({} as CommonType.ResponseStatus<boolean>);

    return subtractRequired;
  };

  const changeToggle = () => {
    const newValue = {
      ...value,
      enabled: !value.enabled,
    } as SystemSetting.Value[PublicKey.OTP];

    const result = validHandler(newValue);

    if (result.enabled && result.enabled.length > 0) {
      return;
    }

    setValue(newValue);
  };

  const saveHandle = async () => {
    const oldObj = publicData[PublicKey.OTP];
    const newObj = {
      ...oldObj,
      value,
    };

    try {
      setLoading(true);
      const res = await systemSettingServiceApi.post<SystemSetting.PublicSystemSetting[PublicKey.OTP]>(newObj);
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

  const inputs = [
    {
      className: styles['input'],
      name: 'issuer',
      value: value.issuer,
      placeholder: 'issuer 발행자(서비스명)을 입력하세요',
      onChange: inputChange,
      disabled: loading,
    },
  ] as SystemSetting.SettingTemplateInput[];

  const otherChildren =
    <Toggle  {...{
      checked: value.enabled,
      disabled: loading,
      onChange: changeToggle,
    }} />;

  const buttons = [
    {
      text: '저장',
      onClick: saveHandle,
      disabled: loading,
    },
    {
      text: '초기화',
      onClick: () => systemSettingFunc.initHandle({
        key: PublicKey.PASSKEY,
        setLoading,
        setResponse,
        refetch,
      }),
      disabled: loading,
    },
  ] as SystemSetting.SettingTemplateBtn[];


  return (
    <SettingItemTemplate {...{
      headline: 'OTP',
      response, errors,
      buttons, inputs,
      errorFields: Object.keys(errors),
      otherChildren,
    }} />
  );
};

export default Otp;