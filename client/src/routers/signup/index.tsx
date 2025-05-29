import styles from './index.module.css';
import { useNavigate } from 'react-router-dom';
import { useMemo, useState } from 'react';
import { AxiosError } from 'axios';
import signUpService from './[features]/services/api';
import { SignUp } from './[features]/types/signUp';
import signUpSchema from './[features]/types/signUpSchema';
import { CommonType } from '../../commons/types/commonType';
import commonFunc from '../../commons/services/funcs';
import FormErrors = CommonType.FormErrors;

const Index = () => {
  const [signUp, setSignUp] = useState({} as SignUp.SignUpInput);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SignUp.SignUpInput>);

  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const isValid = useMemo(() => {
    return signUpSchema.Input.safeParse(signUp).success;
  }, [signUp]);

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const name = e.target.name;

    setSignUp({
      ...signUp,
      [name]: e.target.value,
    });

    const result =
      signUpSchema.Input.safeParse(signUp);

    const fieldErrors = result.success
      ? {}
      : result.error.flatten().fieldErrors;

    const subtractRequired =
      commonFunc.subtractRequiredStr(fieldErrors);

    if (name !== 'userId') {
      setErrors(prevState => ({
        ...subtractRequired,
        userId: prevState.userId,
      } as FormErrors<SignUp.SignUpInput>));
    } else {
      setErrors(subtractRequired);
    }

    setLoading(false);

  };

  const onClickHandler = async () => {
    setLoading(true);

    try {
      const res = await signUpService.postSingUp(signUp);

      if (res.status === 200) {
        alert('회원가입이 완료되었습니다.');
        navigate('/signin');
      }

    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const checkExistByUserId = async () => {
    try {
      const res = await signUpService.getExistByUserId(signUp.userId);

      setErrors(prevState => ({
        ...prevState,
        userId: res.data ? prevState.userId : '이미 존재하는 아이디입니다.',
      } as FormErrors<SignUp.SignUpInput>));

    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    }
  };


  const resetHandler = () => {
    setSignUp({} as SignUp.SignUpInput);
    setErrors({} as CommonType.FormErrors<SignUp.SignUpInput>);
  };

  return (
    <div className={styles['container']}>
      <div>
        <input name={'userId'}
               value={signUp.userId ?? ''}
               onChange={onChangeHandler}
               disabled={loading}
               placeholder={'아이디를 입력하세요'}
               onBlur={checkExistByUserId}
               autoFocus
        />
        {
          errors?.userId
          && errors.userId.length > 0
          && <p>{errors.userId}</p>
        }
        <input name={'pwd'}
               type={'password'}
               value={signUp.pwd ?? ''}
               onChange={onChangeHandler}
               disabled={loading}
               placeholder={'비밀번호를 입력하세요'}
        />
        {
          errors?.pwd
          && errors.pwd.length > 0
          && <p>{errors.pwd}</p>
        }
        <input name={'confirmPwd'}
               type={'password'}
               value={signUp.confirmPwd ?? ''}
               onChange={onChangeHandler}
               disabled={loading}
               placeholder={'비밀번호를 다시 입력하세요'}
        />
        {
          errors?.confirmPwd
          && errors.confirmPwd.length > 0
          && <p>{errors.confirmPwd}</p>
        }
        <input name={'email'}
               value={signUp.email ?? ''}
               onChange={onChangeHandler}
               disabled={loading}
               placeholder={'이메일을 입력하세요'}
        />
        {
          errors?.email
          && errors.email.length > 0
          && <p>{errors.email}</p>
        }
      </div>
      <div>
        <button onClick={onClickHandler}
                disabled={loading || !isValid}
        >
          Sign Up
        </button>
        <button onClick={resetHandler}
                disabled={loading || !isValid}
        >
          Reset
        </button>
      </div>
    </div>
  );
};

export default Index;




