import { useMemo, useRef, useState } from 'react';
import { SignIn } from './[features]/types/signin';
import { Link, useNavigate } from 'react-router-dom';
import styles from './index.module.css';
import { AxiosError } from 'axios';
import signInService from './[features]/services/api';
import { useSignInToken } from '../../commons/hooks/useSiginInToken';
import { CommonType } from '../../commons/types/commonType';
import signInSchema from './[features]/types/signInSchema';
import commonFunc from '../../commons/services/funcs';


const Index = () => {

  const [login, setLogin] = useState({} as SignIn.Input);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SignIn.Input>);
  const [loading, setLoading] = useState(false);
  const pwdRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const { setToken } = useSignInToken();


  const isValid = useMemo(() => {
    return signInSchema.Input.safeParse(login).success;
  }, [login]);

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLogin({ ...login, [e.target.name]: e.target.value });

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
      console.log(err);
    } finally {
      setLoading(false);
    }

  };

  const onClickResetHandler = () => {
    setLogin({} as SignIn.Request);
  };

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
                   onKeyDown={(e) => {
                     if (e.key !== 'Enter') return;
                     pwdRef.current?.focus();
                   }}
            />
            {
              errors?.userId
              && errors.userId.length > 0
              && <p>{errors.userId}</p>
            }
            <input name={'pwd'}
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
        <div>
          <button onClick={onClickResetHandler}
                  disabled={loading}
          >
            Reset
          </button>
          <Link to={'/signup'}> Sign Up </Link>
        </div>
      </div>
    </div>
  );
};

export default Index;

