import Root from '.';
import RootLayout from './layout';
import ErrorComponent from '../commons/components/errorComponent';
import ExampleRouter from './example/router';
import SignupRouter from './signup/router';
import SignInRouter from './signin/router';
import SignOutRouter from './signout/router';
import MasterRouter from './master/router';
import systemSettingQuery from './master/system-setting/[features]/stores/query';
import authMiddleware from '../commons/services/middleware';
import UserRouter from './user/router';
import { CommonType } from '../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;

const rootRouter: ExRouteObject =
  {
    path: '/',
    name: 'Home',
    Component: RootLayout,
    errorElement: <ErrorComponent />,
    unstable_middleware: [authMiddleware],
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
      UserRouter,
    ],
  };

export default rootRouter;
