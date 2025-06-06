import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { SignIn } from './[features]/types/signin';
import { Link, useNavigate } from 'react-router-dom';
import styles from './index.module.css';
import { AxiosError } from 'axios';
import signInService from './[features]/services/api';
import { useSignInToken } from '../../commons/hooks/useSiginInToken';
import { CommonType } from '../../commons/types/commonType';
import signInSchema from './[features]/types/signInSchema';
import commonFunc from '../../commons/services/funcs';
import { useModal } from '../../commons/hooks/useModal';
import FindPassword from './[features]/components/findPassword';
import systemSettingQuery from '../master/system-setting/[features]/stores/query';
import { SystemSetting } from '../master/system-setting/[features]/types/systemSetting';
import passkeyServiceApi from '../user/passkey/[features]/services/api';
import * as webauthnJson from '@github/webauthn-json';
import { CredentialRequestOptionsJSON } from '@github/webauthn-json';
import { PassKeyType } from '../user/passkey/[features]/types/passkey';
import PublicKey = SystemSetting.PublicKey;
import ResStatus = CommonType.ResStatus;


const Index = () => {

  const [login, setLogin] = useState({} as SignIn.Input);
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SignIn.Input>);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);
  const [loading, setLoading] = useState(false);
  const pwdRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const { setToken } = useSignInToken();
  const { addModal } = useModal();
  const { value: signUp } = systemSettingQuery.publicQuery().data[PublicKey.SIGN_UP];
  const { value: passkey } = systemSettingQuery.publicQuery().data[PublicKey.PASSKEY];

  const [timeoutFunc, setTimeoutFunc] = useState<NodeJS.Timeout>();

  useEffect(() => {
    return () => {
      if (timeoutFunc) {
        clearTimeout(timeoutFunc);
      }
    };
  }, []);

  const isValid = useMemo(() => {
    return signInSchema.Input.safeParse(login).success;
  }, [login]);

  const activePassKey = useCallback(async () => {
    const notSupportedPasskey = !window.PublicKeyCredential
      || !navigator.credentials
      || !navigator.credentials.create
      || !passkey.enabled;

    if (notSupportedPasskey || !login.userId) {
      return goPasswordInput();

    }


    try {
      setLoading(true);
      const assertion = await passkeyServiceApi.getAssertionOption(login.userId);

      if (assertion.status === ResStatus.SKIP) {
        return goPasswordInput();
      }
      const options = JSON.parse(assertion.data) as CredentialRequestOptionsJSON;
      const credentail = await webauthnJson.get(options);

      const body = {
        userId: login.userId,
        assertion: JSON.stringify(credentail),
      } as PassKeyType.FinishAssertionRequest;

      const res = await passkeyServiceApi.postFinishAssertion(body);
      if (res.status === ResStatus.SUCCESS) {
        navigate('/');
      }

      // todo 실패 처리 로직

    } catch (e) {
      return goPasswordInput();
    } finally {
      setLoading(false);
    }
  }, [login.userId]);

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    if (name === 'userId' && showPassword) {
      setShowPassword(false);
      setLogin({
        ...login, [name]: value, pwd: '',
      });
    } else {
      setLogin({ ...login, [name]: value });
    }

    const result = signInSchema.Input.safeParse(login);

    const fieldErrors = result.success
      ? {}
      : result.error.flatten().fieldErrors;


    const subtractRequired = commonFunc.subtractRequiredStr(fieldErrors);
    setErrors(subtractRequired);
  };

  const onClickSubmitHandler = async () => {
    setLoading(true);
    try {
      const data = await signInService.postSignIn(login);

      setToken(data);

      navigate('/');
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.setResponseError(err, setResponse);
    } finally {
      setLoading(false);
    }
  };

  const onClickResetHandler = useCallback(() => {
    setLogin({} as SignIn.Request);
  }, []);

  const findPasswordHandler = useCallback(() => {
    addModal({
      title: 'Find Password',
      isOpen: true,
      children: <FindPassword />,
    });
  }, []);

  const goPasswordInput = useCallback(() => {
    setShowPassword(true);
    const timeout = setTimeout(() => {
      pwdRef.current?.focus();
    }, 50);
    setTimeoutFunc(timeout);
  }, [pwdRef.current]);

  return (
    <div className={styles['container']}>
      <div className={styles['login-container']}>
        <h1 className={styles['headline']}>SIGN IN</h1>
        <div className={styles['login-input']}>
          <div>
            <input name={'userId'}
                   value={login.userId ?? ''}
                   onChange={onChangeHandler}
                   placeholder={'아이디를 입력하세요'}
                   disabled={loading}
                   type={'text'}
                   onKeyDown={async (e) => {
                     if (e.key === 'Enter' || e.key === 'Tab') {
                       await activePassKey();
                       goPasswordInput();
                     }
                   }}
                   autoFocus
            />
            {
              errors?.userId
              && errors.userId.length > 0
              && <p>{errors.userId}</p>
            }
            {
              showPassword
              && <input name={'pwd'}
                        ref={pwdRef}
                        value={login.pwd ?? ''}
                        onChange={onChangeHandler}
                        placeholder={'비밀번호를 입력하세요'}
                        disabled={loading}
                        type={'password'}
                        onKeyDown={(e) => {
                          if (e.key !== 'Enter' || !isValid) return;
                          onClickSubmitHandler();
                        }}
              />
            }
            {
              errors.pwd
              && errors.pwd.length > 0
              && <p>{errors.pwd}</p>
            }
          </div>
          <button onClick={onClickSubmitHandler}
                  disabled={loading || !isValid}
          >
            {'>'}
          </button>
        </div>
        {
          response.status
          && <p className={styles[response.status.toLowerCase()]}>{response.message}</p>
        }
        <div>
          <button onClick={onClickResetHandler}
                  disabled={loading}
                  className={styles['reset']}
          >
            Reset
          </button>
          {
            signUp.enabled
            && <>
              <Link to={'/signup'}> Sign Up </Link>
              <button onClick={findPasswordHandler}
                      disabled={loading}
                      className={styles['find-password']}
              >
                Find Password
              </button>
            </>
          }
        </div>
      </div>
    </div>
  );
};

export default Index;

