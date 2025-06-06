import systemSettingQuery from '../[features]/stores/query';
import { SystemSetting } from '../[features]/types/systemSetting';
import { useEffect, useState } from 'react';
import { CommonType } from '../../../../commons/types/commonType';
import systemSettingFunc from '../[features]/service/func';
import styles from './index.module.css';
import { AxiosError } from 'axios';
import commonFunc from '../../../../commons/services/funcs';
import systemSettingServiceApi from '../[features]/service/api';
import PublicKey = SystemSetting.PublicKey;


const Index = () => {
  const { data } = systemSettingQuery.publicQuery();

  const [value, setValue] = useState(data[PublicKey.INIT].value);

  const [errors, setErrors] = useState({} as CommonType.FormErrors<SystemSetting.Init>);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!data) return;

    setValue(data[PublicKey.INIT].value);

  }, [data]);

  const changeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value: v } = e.target;
    const newValue = { ...value, [name]: v };


    const subtractRequired = systemSettingFunc.subtractRequiredHandler({ key: 'Init', newValue });
    setErrors(subtractRequired);
    setResponse({} as CommonType.ResponseStatus<boolean>);

    setValue(newValue);
  };

  const submitHandler = async () => {
    setLoading(true);
    const oldInit = data[PublicKey.INIT];
    const newInit = { ...oldInit, value };

    try {
      const res = await systemSettingServiceApi.post(newInit);
      setResponse(res);

    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
      commonFunc.setResponseError(err, setResponse);
    } finally {
      setLoading(false);
    }
  };


  return (
    <div className={styles['container']}>
      <div className={styles['input-container']}>
        <input name={'homeUrl'}
               placeholder={'사이트 주소를 입력하세요.'}
               value={value.homeUrl ?? ''}
               onChange={changeHandler}
               disabled={loading}
        />
        <button onClick={submitHandler}>
          submit
        </button>
      </div>
      <div className={styles['error-container']}>
        {
          errors
          && Object.keys(errors).map((key) => {
            return (
              <p key={`error-${key}`}>
                {errors[key]}
              </p>
            );
          })
        }
      </div>
      {
        response.status
        && <p
          className={styles['response-container'] + ' ' + ((response.status === 'SUCCESS') ? styles['success'] : styles['error'])}
        >
          {
            response.message
          }
        </p>
      }
    </div>
  );
};

export default Index;




