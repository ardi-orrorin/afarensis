import { useExample } from './[features]/hooks/useExample';
import ExampleTest from './[features]/components/exampleTest';
import dayjs from 'dayjs';

const Index = () => {
  const { example, getData } = useExample();
  const { data, isPending, refetch } = getData;

  const onClick = async () => {
    await refetch();
  };

  const onChangeTimezone = () => {
    dayjs.tz.setDefault();
  };

  if (isPending) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>{example}</h1>
      <p>{data.text}</p>
      <p>{data.date}</p>
      <p>{dayjs.tz(data.date).format('YYYY-MM-DD HH:mm:ss')}</p>
      <button onClick={onClick} disabled={isPending}>
        refetch
      </button>
      <button onClick={onChangeTimezone}>change US Tz</button>
      <ExampleTest />
    </div>
  );
};

export default Index;
