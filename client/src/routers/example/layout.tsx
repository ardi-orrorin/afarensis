import { Outlet } from 'react-router-dom';
import { ExampleProvider } from './[features]/hooks/useExample';

const Layout = () => {
  return (
    <ExampleProvider>
      <Outlet />
    </ExampleProvider>
  );
};

export default Layout;
