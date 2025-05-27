import { RouteObject } from 'react-router-dom';
import Root from '.';
import RootLayout from './layout';
import ErrorComponent from '../commons/components/errorComponent';
import ExampleRouter from './example/router';
import SignupRouter from './signup/router';
import SignInRouter from './signin/router';
import SignOutRouter from './signout/router';
import MasterRouter from './master/router';
import systemSettingQuery from './master/system-setting/[features]/stores/query';
import commonFunc from '../commons/services/funcs';

const rootRouter: RouteObject =
  {
    path: '/',
    Component: RootLayout,
    errorElement: <ErrorComponent />,
    loader: async () => {
      await systemSettingQuery.publicQuery().prefetch();
    },
    children: [
      {
        index: true,
        Component: Root,
      },
      ExampleRouter,
      SignupRouter,
      SignInRouter,
      SignOutRouter,
      MasterRouter,
    ],
  };

console.log(commonFunc.getAllRoutePaths(rootRouter));


export default rootRouter;
