import systemSettingQuery from '../../../[features]/stores/query';
import { useEffect, useState } from 'react';
import { SystemSetting } from '../../../[features]/types/systemSetting';
import SettingItemTemplate from '../../../[features]/components/settingItemTemplate';
import { CommonType } from '../../../../../../commons/types/commonType';
import systemSettingServiceApi from '../../../[features]/service/api';
import { AxiosError, AxiosResponse } from 'axios';
import commonFunc from '../../../../../../commons/services/funcs';
import systemSettingFunc from '../../../[features]/service/func';
import Toggle from '../../../[features]/components/toggle';
import styles from './passkey.module.css';
import PublicKey = SystemSetting.PublicKey;

const Passkey = () => {
  const { data: publicData, refetch } = systemSettingQuery.publicQuery();
  const [value, setValue] = useState(publicData[PublicKey.PASSKEY].value);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SystemSetting.PassKey>);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (publicData) {
      setValue(publicData[PublicKey.PASSKEY].value);
    }
  }, [publicData]);

  const saveHandle = async () => {
    const oldObj = publicData[PublicKey.PASSKEY];
    const newObj = {
      ...oldObj,
      value,
    };

    try {
      setLoading(true);
      const res = await systemSettingServiceApi.post<SystemSetting.PublicSystemSetting[PublicKey.PASSKEY]>(newObj);
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

  const inputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = {
      ...value,
      [event.target.name]: event.target.value,
    } as SystemSetting.Value[PublicKey.PASSKEY];

    validHandler(newValue);

    setValue(newValue);
  };

  const validHandler = (newValue: SystemSetting.Value[PublicKey.PASSKEY]) => {
    const subtractRequired = systemSettingFunc.subtractRequiredHandler({ key: 'Passkey', newValue });
    setErrors(subtractRequired);
    setResponse({} as CommonType.ResponseStatus<boolean>);

    return subtractRequired;
  };

  const changeToggle = () => {
    const newValue = {
      ...value,
      enabled: !value.enabled,
    } as SystemSetting.Value[PublicKey.PASSKEY];

    const result = validHandler(newValue);

    if (result.enabled && result.enabled.length > 0) {
      return;
    }

    setValue(newValue);
  };


  const inputs = [
    {
      className: styles['url'],
      name: 'domain',
      value: value.domain,
      placeholder: 'http(s)://를 제외하고 도메인만 입려하세요.',
      onChange: inputChange,
      disabled: loading,
    },
    {
      className: styles['port'],
      name: 'port',
      value: value.port,
      placeholder: '포트번호를 입력하세요.',
      onChange: inputChange,
      disabled: loading,
    },
    {
      className: '',
      name: 'displayName',
      value: value.displayName,
      placeholder: '표시할 이름을 입력하세요.',
      onChange: inputChange,
      disabled: loading,
    },

  ] as SystemSetting.SettingTemplateInput[];

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

  const otherChildren =
    <Toggle  {...{
      checked: value.enabled,
      disabled: loading,
      onChange: changeToggle,
    }} />;

  return (
    <SettingItemTemplate {...{
      headline: 'PASSKEY',
      response, errors,
      buttons, inputs,
      errorFields: Object.keys(errors),
      otherChildren,
    }} />
  );
};


export default Passkey;