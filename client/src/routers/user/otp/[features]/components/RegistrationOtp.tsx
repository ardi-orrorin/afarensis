import { useModal } from '../../../../../commons/hooks/useModal';
import { QRCodeCanvas } from 'qrcode.react';
import { AxiosError } from 'axios';
import commonFunc from '../../../../../commons/services/funcs';
import { useRef, useState } from 'react';
import styles from './registrationOtp.module.css';
import otpServiceApi from '../services/api';
import { CommonType } from '../../../../../commons/types/commonType';
import ResStatus = CommonType.ResStatus;

const OTP_LENGTH = 6;

const RegistrationOtp = (props: { id?: string, qrcode: string }) => {
  if (!props.id) return;

  const { onClose } = useModal();
  const [otp, setOtp] = useState<string[]>(Array(OTP_LENGTH).fill(''));
  const inputsRef = useRef<Array<HTMLInputElement | null>>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');


  const handleChange = async (value: string, idx: number) => {
    if (!/^\d?$/.test(value)) return; // 숫자 1자리만 허용

    const newOtp = [...otp];
    newOtp[idx] = value;
    setOtp(newOtp);

    if (value && idx < OTP_LENGTH - 1) {
      inputsRef.current[idx + 1]?.focus();
    }

    if (newOtp[5] && newOtp.every((v) => v !== '') && (idx === OTP_LENGTH - 1)) {
      await verifyOtp(newOtp.join(''));
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, idx: number) => {
    if (e.key === 'Backspace' && !otp[idx] && idx > 0) {
      inputsRef.current[idx - 1]?.focus();
      setError('');
    }
  };


  const verifyOtp = async (code: string) => {
    try {
      setIsLoading(true);
      const res = await otpServiceApi.postVerify(code);

      if (res.status === ResStatus.FAILED) {
        setError(res.message);
        return;
      }

      onClose(props.id!!);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setIsLoading(false);
    }
  };


  return (
    <div className={styles['container']}>
      <QRCodeCanvas
        value={props.qrcode}
        size={200}
        bgColor="#fff"
        fgColor="#000"
      />
      <p>QR코드를 스캔하여 OTP를 등록하세요.</p>
      <p>화면에 표시된 OTP 코드 6자리를 입력하세요.</p>
      <div className={styles['input-box']}>
        {otp.map((digit, idx) => (
          <input
            key={idx}
            ref={el => {
              inputsRef.current[idx] = el;
            }}
            autoFocus={idx === 0}
            type="text"
            inputMode="numeric"
            maxLength={1}
            value={digit}
            onChange={e => handleChange(e.target.value, idx)}
            onKeyDown={e => handleKeyDown(e, idx)}
            disabled={isLoading}
          />
        ))}
      </div>
      {
        error.length > 0
        && <p>
          <span className={styles['error']}>{error}</span>
        </p>
      }
    </div>
  );
};

export default RegistrationOtp;
