import styles from './item.module.css';
import { PassKeyType } from '../types/passkey';
import dayjs from 'dayjs';
import passkeyServiceApi from '../services/api';
import { CommonType } from '../../../../../commons/types/commonType';
import passkeyQuery from '../stores/query';
import ResStatus = CommonType.ResStatus;

const Item =
  ({
     id, deviceName, lastUsedAt, createdAt,
   }: PassKeyType.PassKey) => {
    const { refetch } = passkeyQuery.passkey();

    const removeClick = async () => {
      if (!confirm(`${deviceName} 패스키를 삭제 하시겠습니까?`)) return;
      const res = await passkeyServiceApi.deletePasskey(id);

      if (res.status === ResStatus.SUCCESS) {
        alert('삭제되었습니다.');
        await refetch();
      }
    };

    return (
      <div className={styles['container']}
           onClick={removeClick}
      >
        <h3>{deviceName}</h3>
        <div className={styles['date']}>
          <span>최근사용일</span>
          <p>{dayjs(lastUsedAt).format('YYYY-MM-DD HH:mm:ss')}</p>
        </div>
        <div className={styles['date']}>
          <span>생성일</span>
          <p>{dayjs(createdAt).format('YYYY-MM-DD HH:mm:ss')}</p>
        </div>
      </div>
    );
  };


export default Item;