import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './initMaster.module.css';
import { RootType } from '../types/rootType';
import { CommonType } from '../../../commons/types/commonType';
import commonFunc from '../../../commons/services/funcs';
import rootSchema from '../types/rootSchema';
import systemSettingQuery from '../../master/system-setting/[features]/stores/query';
import { AxiosError } from 'axios';
import rootServiceApi from '../services/api';
import { SystemSetting } from '../../master/system-setting/[features]/types/systemSetting';
import PublicKey = SystemSetting.PublicKey;

const InitMaster = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { data: publicData, refetch } = systemSettingQuery.publicQuery();

  const [input, setInput] = useState({ homeUrl: publicData[PublicKey.INIT].value.homeUrl } as RootType.Master);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<RootType.Master>);
  const [isLoading, setIsLoading] = useState(false);
  const inputRefs = useRef([] as HTMLInputElement[]);

  useEffect(() => {
    if (location.pathname !== '/') {
      navigate('/');
    }
  }, [location]);

  const onChangeInputHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    const newValue = { ...input, [name]: value };
    validHandler(newValue);
    setInput(newValue);
  };

  const validHandler = (newValue: RootType.Master) => {
    const result = rootSchema.Master.safeParse(newValue);
    const fieldErrors = result.success ? {} : result.error.flatten().fieldErrors;

    const subtractRequired = commonFunc.subtractRequiredStr(fieldErrors);
    setErrors(subtractRequired);

    return subtractRequired;
  };

  const submitHandler = async () => {
    setIsLoading(true);

    try {
      const res = await rootServiceApi.patchUpdateMaster({ body: input });

      if (!res.data) return;

      await refetch();

      window.location.reload();
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setIsLoading(false);
    }

  };


  return (
    <div className={styles['container']}>
      <div className={styles['box']}>
        <h1>Master Account Setting</h1>
        <div className={styles['input-box']}>
          <input className={errors.homeUrl && styles['error-input']}
                 ref={(el) => {
                   inputRefs.current[0] = el!;
                 }}
                 value={input.homeUrl}
                 name={'homeUrl'}
                 disabled={isLoading}
                 placeholder={'홈페이지 주소를 입력해주세요'}
                 onChange={onChangeInputHandler}
                 onKeyUp={e => {
                   if (e.key === 'Enter') {
                     inputRefs.current[1]?.focus();
                   }
                 }}
          />
          <input className={errors.email && styles['error-input']}
                 ref={(el) => {
                   inputRefs.current[1] = el!;
                 }}
                 value={input.email}
                 name={'email'}
                 disabled={isLoading}
                 placeholder={'이메일을 입력해주세요'}
                 onChange={onChangeInputHandler}
                 onKeyUp={e => {
                   if (e.key === 'Enter') {
                     inputRefs.current[2]?.focus();
                   }
                 }}
          />
          <input className={errors.pwd && styles['error-input']}
                 ref={(el) => {
                   inputRefs.current[2] = el!;
                 }}
                 value={input.pwd}
                 name={'pwd'}
                 type={'password'}
                 disabled={isLoading}
                 placeholder={'비밀번호를 입력해주세요'}
                 onChange={onChangeInputHandler}
                 onKeyUp={e => {
                   if (e.key === 'Enter') {
                     inputRefs.current[3]?.focus();
                   }
                 }}
          />
          <input className={errors.pwdConfirm && styles['error-input']}
                 ref={(el) => {
                   inputRefs.current[3] = el!;
                 }}
                 value={input.pwdConfirm}
                 name={'pwdConfirm'}
                 type={'password'}
                 disabled={isLoading}
                 placeholder={'비밀번호를 다시 입력해주세요'}
                 onChange={onChangeInputHandler}
                 onKeyUp={e => {
                   if (e.key === 'Enter') {
                     submitHandler();
                   }
                 }}
          />
        </div>
        {
          errors
          && Object.keys(errors).length > 0
          && <ul className={styles['error-box']}>
            {
              Object.keys(errors).map((key) => (
                <li key={key}>{`<${key}> ${errors[key]}`}</li>
              ))
            }
          </ul>
        }
        <div>
          <button className={styles['submit-btn']}
                  disabled={isLoading}
                  onClick={submitHandler}
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  );
};

export default InitMaster;