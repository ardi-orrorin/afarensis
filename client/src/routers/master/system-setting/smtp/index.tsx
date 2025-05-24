import systemSettingQuery from '../[features]/stores/query';
import { SystemSetting } from '../[features]/types/systemSetting';
import { useEffect, useState } from 'react';
import styles from './index.module.css';
import SystemSettingSchema from '../[features]/types/systemSettingSchema';
import { CommonType } from '../../../../commons/types/commonType';
import commonFunc from '../../../../commons/services/funcs';
import smtpServiceApi from './[features]/service/api';
import { AxiosError } from 'axios';
import systemSettingServiceApi from '../[features]/service/api';
import Key = SystemSetting.Key;

const Index = () => {
  const { data: privateData, refetch } = systemSettingQuery.privateQuery();
  const [value, setValue] = useState(privateData[Key.SMTP].value);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SystemSetting.Smtp>);
  const [isTested, setIsTested] = useState(false);
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);

  useEffect(() => {
    if (privateData?.[Key.SMTP]?.value) {
      setValue(privateData[Key.SMTP].value);
    }
  }, [privateData]);

  const changeToggle = () => {
    const newValue = {
      ...value,
      enabled: !value.enabled,
    } as SystemSetting.Value[SystemSetting.Key.SMTP];

    const result = updateValue(newValue);

    if (result.enabled && result.enabled.length > 0) {
      return;
    }

    setValue(newValue);
  };

  const inputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = {
      ...value,
      [event.target.name]: event.target.value,
    } as SystemSetting.Value[SystemSetting.Key.SMTP];

    updateValue(newValue);

    setValue(newValue);
  };

  const updateValue = (newValue: SystemSetting.Value[SystemSetting.Key.SMTP]) => {
    const result = SystemSettingSchema.Smtp.safeParse(newValue);
    const fieldErrors = result.success
      ? {}
      : result.error.flatten().fieldErrors;

    const subtractRequired = commonFunc.subtractRequiredStr(fieldErrors);
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

    } finally {
      setLoading(false);
    }
  };

  const saveHandle = async () => {
    const oldSmtp = privateData[Key.SMTP];
    const newSmtp = {
      ...oldSmtp,
      value,
    };

    try {
      setLoading(true);
      const res = await smtpServiceApi.post(newSmtp);
      setResponse(res);
      setIsTested(false);
    } catch (e) {
      const err = e as AxiosError;
    } finally {
      setLoading(false);
    }
  };

  const initHandle = async () => {
    setLoading(true);
    try {
      const res = await systemSettingServiceApi.putInit(Key.SMTP);
      await refetch();
      setResponse(res);
      setIsTested(false);
    } catch (e) {
      const err = e as AxiosError;
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className={styles['container']}>
      <h1>SMTP</h1>
      <div className={styles['item']}>
        <div className={styles['input-container']}>
          <input className={styles['url']}
                 name={'host'}
                 value={value.host}
                 placeholder={'host주소를 입력하세요 (ex: smtp.gmail.com)'}
                 onChange={inputChange}
                 disabled={loading}
          />
          <input className={styles['port']}
                 name={'port'}
                 value={value.port}
                 onChange={inputChange}
                 disabled={loading}
          />
          <input type={'text'}
                 name={'username'}
                 value={value.username}
                 placeholder={'계정 아이디를 입력하세요.'}
                 onChange={inputChange}
                 disabled={loading}
          />
          <input type={'password'}
                 name={'password'}
                 value={value.password}
                 placeholder={'계정 비밀번호를 입력하세요.'}
                 onChange={inputChange}
                 disabled={loading}
          />
        </div>
        <div className={styles['error-container']}>
          {
            errors.host
            && errors.host.length > 0
            && <p>{`host<${errors.host}>`}</p>
          }
          {
            errors.port
            && errors.port.length > 0
            && <p>{`port<${errors.port}>`}</p>
          }
          {
            errors.username
            && errors.username.length > 0
            && <p>{`username<${errors.username}>`}</p>
          }
          {
            errors.password
            && errors.password.length > 0
            && <p>{`password<${errors.password}>`}</p>
          }
          {
            errors.enabled
            && errors.enabled.length > 0
            && <p>{errors.enabled}</p>
          }
        </div>
        <label className={styles['toggle']}>
          <input type="checkbox"
                 checked={value.enabled}
                 onChange={changeToggle}
                 disabled={loading || !isTested}
          />
          <span className={styles['slider']}></span>
        </label>
      </div>
      <div className={styles['button-container']}>
        <button disabled={loading || !isTested}
                onClick={saveHandle}
        >
          저장
        </button>
        <button disabled={loading}
                onClick={submitTest}
        >
          테스트
        </button>
        <button disabled={loading}
                onClick={initHandle}
        > 초기화
        </button>
      </div>
      {
        response.data
        && <p className={styles['success']}>{response.message}</p>
      }
    </div>
  );
};

export default Index;




