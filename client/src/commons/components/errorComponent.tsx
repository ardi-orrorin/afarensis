import { useNavigate, useRouteError } from 'react-router-dom';
import { useEffect, useState } from 'react';
import styles from './ErrorComponent.module.css';

const ErrorComponent = () => {
  const err = useRouteError();

  console.log(err);

  if (err instanceof Error) {
    return <BasicErrorComponent {...{ err }} />;
  }

  return <div>Error</div>;
};

const BasicErrorComponent = ({ err }: { err: Error }) => {
  const navigate = useNavigate();
  const [count, setCount] = useState(5);

  useEffect(() => {
    if (count === 0) {
      navigate('/');
    }

    const interval = setInterval(() => {
      setCount((prev) => prev - 1);
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, [count]);

  return (
    <div className={styles['basic-error-container']}>
      <div>{err.message}</div>
      <div>Redirecting to home page...</div>
      <div>{count}초</div>
    </div>
  );
};

export default ErrorComponent;
