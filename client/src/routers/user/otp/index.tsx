import otpServiceApi from './[features]/services/api';
import styles from './index.module.css';
import { useModal } from '../../../commons/hooks/useModal';
import RegistrationOtp from './[features]/components/RegistrationOtp';

const Index = () => {

  const { addModal } = useModal();

  const getQrCode = async () => {
    const res = await otpServiceApi.getOtpQrcode();

    addModal({
      title: 'OTP 등록',
      isOpen: true,
      children: <RegistrationOtp qrcode={res.data} />,
    });
  };

  // todo: 등록된 otp 표시
  return (
    <div className={styles['container']}>
      <button onClick={getQrCode}
      >
        Get QR Code
      </button>
    </div>
  );
};

export default Index;




