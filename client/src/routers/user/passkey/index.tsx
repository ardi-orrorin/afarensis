import passkeyQuery from './[features]/stores/query';
import Item from './[features]/components/Item';
import styles from './index.module.css';
import passkeyServiceApi from './[features]/services/api';
import { AxiosError } from 'axios';
import * as webauthnJson from '@github/webauthn-json';
import { CredentialCreationOptionsJSON } from '@github/webauthn-json';
import { useQuery } from '@tanstack/react-query';


const Index = () => {
  const { queryOp } = passkeyQuery.passkey();

  const { data, refetch } = useQuery(queryOp);

  const addPasskey = async () => {
    try {
      const getCredentialOption = await passkeyServiceApi.getCredentialOption();
      const json = JSON.parse(getCredentialOption.data);

      const credentials = await webauthnJson.create(json as CredentialCreationOptionsJSON);
      await passkeyServiceApi.postRegistration({ json: JSON.stringify(credentials) });
      await refetch();
    } catch (e) {
      const err = e as AxiosError;
      console.log(err);
    }
  };


  return (
    <div className={styles['container']}>
      <div className={styles['buttons']}>
        <button onClick={addPasskey}>패스키 추가</button>
      </div>
      {
        data
        && data.length > 0
          ? <div className={styles['list-container']}>
            <h3>패스키 목록</h3>
            <div className={styles['list-box']}>
              {
                data.map((item) => (
                  <Item key={item.id} {...item} />
                ))
              }
            </div>
          </div>
          : <div className={styles['empty']}>
            <p>등록된 패스키가 없습니다.</p>
          </div>
      }
    </div>
  );
};

export default Index;




