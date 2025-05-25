import systemSettingQuery from '../[features]/stores/query';
import { SystemSetting } from '../[features]/types/systemSetting';
import { useEffect, useState } from 'react';
import styles from './index.module.css';
import { CommonType } from '../../../../commons/types/commonType';
import smtpServiceApi from './[features]/service/api';
import { AxiosError } from 'axios';
import systemSettingServiceApi from '../[features]/service/api';
import SettingItemTemplate from '../[features]/components/settingItemTemplate';
import Toggle from '../[features]/components/toggle';
import systemSettingFunc from '../[features]/service/func';
import commonFunc from '../../../../commons/services/funcs';
import PrivateKey = SystemSetting.PrivateKey;

const Index = () => {
  const { data: privateData, refetch } = systemSettingQuery.privateQuery();
  const [value, setValue] = useState(privateData[PrivateKey.SMTP].value);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SystemSetting.Smtp>);
  const [isTested, setIsTested] = useState(false);
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);

  useEffect(() => {
    if (privateData?.[PrivateKey.SMTP]?.value) {
      setValue(privateData[PrivateKey.SMTP].value);
    }
  }, [privateData]);

  const changeToggle = () => {
    const newValue = {
      ...value,
      enabled: !value.enabled,
    } as SystemSetting.Value[PrivateKey.SMTP];

    const result = validHandler(newValue);

    if (result.enabled && result.enabled.length > 0) {
      return;
    }

    setValue(newValue);
  };

  const inputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = {
      ...value,
      [event.target.name]: event.target.value,
    } as SystemSetting.Value[PrivateKey.SMTP];

    validHandler(newValue);

    setValue(newValue);
  };

  const validHandler = (newValue: SystemSetting.Value[PrivateKey.SMTP]) => {
    const subtractRequired = systemSettingFunc.subtractRequiredHandler({ key: 'Smtp', newValue });
    setErrors(subtractRequired);
    setResponse({} as CommonType.ResponseStatus<boolean>);

    return subtractRequired;
  };

  const submitTest = async () => {
    try {
      setLoading(true);
      const res = await smtpServiceApi.postTest(value);
      setIsTested(res.data);
      setResponse(res);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const saveHandle = async () => {
    const oldSmtp = privateData[PrivateKey.SMTP];
    const newSmtp = {
      ...oldSmtp,
      value,
    };

    try {
      setLoading(true);
      const res = await systemSettingServiceApi.post<SystemSetting.PrivateSystemSetting[PrivateKey.SMTP]>(newSmtp);
      setResponse(res);
      setIsTested(false);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const initHandle = async () => {
    setLoading(true);
    try {
      const res = await systemSettingServiceApi.putInit(PrivateKey.SMTP);
      await refetch();
      setResponse(res);
      setIsTested(false);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const inputs = [
    {
      className: styles['url'],
      name: 'host',
      value: value.host,
      placeholder: 'host주소를 입력하세요 (ex: smtp.gmail.com)',
      onChange: inputChange,
      disabled: loading,
    },
    {
      className: styles['port'],
      name: 'port',
      value: value.port,
      onChange: inputChange,
      disabled: loading,
    },
    {
      type: 'text',
      name: 'username',
      value: value.username,
      placeholder: '계정 아이디를 입력하세요.',
      onChange: inputChange,
      disabled: loading,
    },
    {
      type: 'password',
      name: 'password',
      value: value.password,
      placeholder: '계정 비밀번호를 입력하세요.',
      onChange: inputChange,
      disabled: loading,
    },
  ] as SystemSetting.SettingTemplateInput[];

  const buttons = [
    { text: '저장', onClick: saveHandle, disabled: loading || !isTested },
    { text: '테스트', onClick: submitTest, disabled: loading },
    { text: '초기화', onClick: initHandle, disabled: loading },
  ] as SystemSetting.SettingTemplateBtn[];

  const otherChildren =
    <Toggle  {...{
      checked: value.enabled,
      disabled: loading || isTested,
      onChange: changeToggle,
    }} />;

  return (
    <SettingItemTemplate
      {...{
        headline: 'SMTP',
        inputs, buttons,
        response, errors,
        otherChildren,
        errorFields: Object.keys(errors),
      }}
    />
  );
};

export default Index;




