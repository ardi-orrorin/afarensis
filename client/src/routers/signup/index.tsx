import { z } from 'zod';
import styles from './index.module.css';
import { useNavigate } from 'react-router-dom';
import { useMemo, useState } from 'react';
import { AxiosError } from 'axios';
import signUpService from './[features]/services/api';

const Index = () => {
  const [signUp, setSignUp] = useState({} as SignUpInputType);
  const [errors, setErrors] = useState({} as FormErrors);

  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const isValid = useMemo(() => {
    return SignUpInputSchema.safeParse(signUp).success;
  }, [signUp]);

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSignUp({
      ...signUp,
      [e.target.name]: e.target.value,
    });

    const result = SignUpInputSchema.safeParse(signUp);

    if (!result.success) {
      const filesErrors = result.error.flatten().fieldErrors;

      const subtractRequired = Object.entries(filesErrors)
        .filter(([key, value]) => {
          return value[0] !== 'Required';
        })
        .reduce((acc, [key, value]) => {
          acc[key] = value;
          return acc;
        }, {} as FormErrors);

      setErrors(subtractRequired);
      setLoading(false);
    }
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
    } finally {
      setLoading(false);
    }
  };


  const resetHandler = () => {
    setSignUp({} as z.infer<typeof SignUpInputSchema>);
    setErrors({} as FormErrors);
  };

  return (
    <div className={styles['container']}>
      <div>
        <input name={'userId'}
               value={signUp.userId ?? ''}
               onChange={onChangeHandler}
               disabled={loading}
               placeholder={'아이디를 입력하세요'}
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

const SignUpInputSchema = z.object({
  userId: z.string().min(4, '아이디는 4글자 이상이어야 합니다.'),
  email: z.string().email('이메일 형식이 아닙니다.'),
  pwd: z.string().min(4, '비밀번호는 4글자 이상이어야 합니다.'),
  confirmPwd: z.string().min(4, '비밀번호는 4글자 이상이어야 합니다.'),
}).refine((data) => {
    return data.pwd === data.confirmPwd;
  }, {
    message: '비밀번호가 일치하지 않습니다.',
    path: ['confirmPwd'],
  },
);

type SignUpInputType = z.infer<typeof SignUpInputSchema>;

type FormErrors = {
  [key in keyof SignUpInputType]?: string;
} & {
  [key: string]: string [] | undefined;
}

