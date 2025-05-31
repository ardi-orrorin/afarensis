import styles from './index.module.css';
import Item from './[features]/components/item';
import webhookQuery from './[features]/stores/query';
import { useQuery } from '@tanstack/react-query';


const Index = () => {
  const { queryOp } = webhookQuery.webhook();
  const { data } = useQuery(queryOp);
  if (!data) return <></>;

  return (
    <div className={styles['container']}>
      {
        data.length > 0
        && data.map((webhook, i) => {
          return <Item key={`${webhook.id}-${i}`} {...{ ...webhook, addMode: false }} />;
        })
      }
      <div>
        <Item {...{ addMode: true }} />
      </div>
    </div>
  );
};

export default Index;