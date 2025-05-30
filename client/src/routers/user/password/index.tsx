import styles from './index.module.css';
import { useRef, useState } from 'react';
import { PasswordType } from './[features]/types/password';
import { CommonType } from '../../../commons/types/commonType';
import commonFunc from '../../../commons/services/funcs';
import passwordSchema from './[features]/types/passwordSchema';
import { AxiosError } from 'axios';
import passwordServiceApi from './[features]/services/api';
import { useNavigate } from 'react-router-dom';

const Index = () => {
  const [input, setInput] = useState({} as PasswordType.Password);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<PasswordType.Password>);
  const [loading, setLoading] = useState(false);
  const inputRef = useRef([] as HTMLInputElement[]);
  const [resResponse, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);
  const navigate = useNavigate();

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    const newInput = { ...input, [name]: value };

    setInput(newInput);

    const result =
      passwordSchema.Input.safeParse(newInput);

    const fieldErrors = result.success
      ? {}
      : result.error.flatten().fieldErrors;

    const subtractRequired =
      commonFunc.subtractRequiredStr(fieldErrors);

    setErrors(subtractRequired);
  };

  const submitHandler = async () => {
    setLoading(true);

    try {
      const res = await passwordServiceApi.patchUpdatePassword(input);
      setResponse(res);
      alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.');
      navigate('/signout');

    } catch (e) {
      const err = e as AxiosError;
      commonFunc.setResponseError(err, setResponse);

    } finally {
      setLoading(false);
    }
  };

  const cancelHandler = () => {
    setInput({} as PasswordType.Password);
    setErrors({} as CommonType.FormErrors<PasswordType.Password>);
  };

  return (
    <div className={styles['container']}>
      <div>
        <h1>Update Password</h1>
      </div>
      <div className={styles['input-container']}>
        <input name={'pwd'}
               placeholder={'비밀번호를 입력하세요.'}
               type={'password'}
               value={input.pwd ?? ''}
               onChange={onChangeHandler}
               ref={(e) => {
                 inputRef.current[0] = e!;
               }}
               onKeyUp={(e) => {
                 if (e.key === 'Enter') {
                   inputRef.current[1]?.focus();
                 }
               }}
               disabled={loading}
        />
        {
          errors.pwd
          && errors.pwd.length > 0
          && <p>{errors.pwd}</p>
        }
        <input name={'newPwd'}
               placeholder={'새로운 비밀번호를 입력하세요.'}
               type={'password'}
               value={input.newPwd ?? ''}
               onChange={onChangeHandler}
               ref={(e) => {
                 inputRef.current[1] = e!;
               }}
               onKeyUp={(e) => {
                 if (e.key === 'Enter') {
                   inputRef.current[2]?.focus();
                 }
               }}
               disabled={loading}
        />
        {
          errors.newPwd
          && errors.newPwd.length > 0
          && <p>{errors.newPwd}</p>
        }
        <input name={'newCheckPwd'}
               placeholder={'새로운 비밀번호를 확인하세요.'}
               type={'password'}
               value={input.newCheckPwd ?? ''}
               onChange={onChangeHandler}
               ref={(e) => {
                 inputRef.current[2] = e!;
               }}
               onKeyUp={async (e) => {
                 if (e.key === 'Enter') {
                   await submitHandler();
                 }
               }}
               disabled={loading}
        />
        {
          errors.newCheckPwd
          && errors.newCheckPwd.length > 0
          && (errors.newCheckPwd as string[])
            .map((error, index) =>
              <p key={`error-${index}`}>{error}</p>,
            )
        }
      </div>
      <div className={styles['button-container']}>
        <button onClick={submitHandler}
                disabled={loading}
        >
          Submit
        </button>
        <button onClick={cancelHandler}
                disabled={loading}
        >
          Cancel
        </button>
      </div>
      {
        resResponse.status
      }
    </div>
  );
};

export default Index;




