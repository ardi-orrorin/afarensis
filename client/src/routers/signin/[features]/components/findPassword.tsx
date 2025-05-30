import styles from './findPassword.module.css';
import { useState } from 'react';
import { FindPasswordType } from '../types/findPassword';
import { CommonType } from '../../../../commons/types/commonType';
import commonFunc from '../../../../commons/services/funcs';
import findPasswordSchema from '../types/findPasswordSchema';
import { AxiosError } from 'axios';
import signInService from '../services/api';
import { useModal } from '../../../../commons/hooks/useModal';

const FindPassword = () => {

  const [value, setValue] = useState({} as FindPasswordType.Input);
  const [error, setError] = useState({} as CommonType.FormErrors<FindPasswordType.Input>);
  const [loading, setLoading] = useState(false);
  const { viewModal, onClose } = useModal();

  const onChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValue({
      ...value,
      [e.target.name]: e.target.value,
    });

    const result = findPasswordSchema.Input.safeParse(value);

    const fieldErrors = result.success
      ? {}
      : result.error.flatten().fieldErrors;

    const subtractRequired = commonFunc.subtractRequiredStr(fieldErrors);

    setError(subtractRequired);
  };

  const submit = async () => {
    if (!viewModal?.id) return;
    setLoading(true);
    try {
      const res = await signInService.postResetPassword(value);
      if (res.data) {
        alert('이메일로 비밀번호가 전송되었습니다.');
      }

      onClose(viewModal.id);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  return <div className={styles['container']}>
    <input name={'userId'}
           placeholder={'아이디 입력하세요.'}
           value={value.userId ?? ''}
           onChange={onChange}
           disabled={loading}
           autoFocus
    />

    <input name={'email'}
           placeholder={'이메일을 입력하세요.'}
           value={value.email ?? ''}
           disabled={loading}
           onChange={onChange}
           onKeyUp={async (e) => {
             if (e.key !== 'Enter') return;
             await submit();
           }}
    />
    {
      error.userId
      && <p>{error.userId}</p>
    }
    {
      error.email
      && <p>{error.email}</p>
    }
    <button onClick={submit}
            disabled={loading}
    >
      비밀번호 찾기
    </button>
  </div>;
};


export default FindPassword;