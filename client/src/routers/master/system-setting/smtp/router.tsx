import { RouteObject } from 'react-router-dom';
import Layout from './layout';
import Index from '.';

const SmtpRouter: RouteObject = {
  id: 'root/master/system-setting/smtp',
  path: 'smtp',
  Component: Layout,
  loader: async () => {
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default SmtpRouter;
