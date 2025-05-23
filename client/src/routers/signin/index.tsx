import { useRef, useState } from 'react';
import { SignIn } from './[features]/types/signin';
import { Link, useNavigate } from 'react-router-dom';
import styles from './index.module.css';
import { AxiosError } from 'axios';
import signInService from './[features]/services/api';
import { useSignInToken } from '../../commons/hooks/useSiginInToken';
import { useCookies } from 'react-cookie';
import signInFunc from './[features]/funcs/signin';


const Index = () => {

  const [login, setLogin] = useState({} as SignIn.Request);
  const [loading, setLoading] = useState(false);
  const pwdRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const { setToken } = useSignInToken();
  const [cookies, setCookie, removeCookie] = useCookies(['access_token', 'refresh_token', 'user_id']);


  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLogin({ ...login, [e.target.name]: e.target.value });
  };

  const onClickSubmitHandler = async () => {
    setLoading(true);
    try {
      const data = await signInService.postSignIn(login);
      setCookie(
        'access_token',
        data.accessToken,
        signInFunc.createCookieOption({ expiresIn: data.accessTokenExpiresIn }),
      );

      setCookie(
        'refresh_token',
        data.refreshToken,
        signInFunc.createCookieOption({ expiresIn: data.refreshTokenExpiresIn }),
      );

      setCookie(
        'user_id',
        data.userId,
        signInFunc.createCookieOption({ expiresIn: data.refreshTokenExpiresIn }),
      );
      
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
            <input name={'pwd'}
                   ref={pwdRef}
                   value={login.pwd ?? ''}
                   onChange={onChangeHandler}
                   placeholder={'비밀번호를 입력하세요'}
                   disabled={loading}
                   type={'password'}
                   onKeyDown={(e) => {
                     if (e.key !== 'Enter') return;
                     onClickSubmitHandler();
                   }}
            />
          </div>
          <button onClick={onClickSubmitHandler}
                  disabled={loading}
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
