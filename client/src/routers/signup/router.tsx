import Layout from './layout';
import Index from '.';
import { CommonType } from '../../commons/types/commonType';
import systemSettingQuery from '../master/system-setting/[features]/stores/query';
import { SystemSetting } from '../master/system-setting/[features]/types/systemSetting';
import ExRouteObject = CommonType.ExRouteObject;
import PublicKey = SystemSetting.PublicKey;

const SignupRouter: ExRouteObject = {
  id: 'root/signup',
  path: 'signup',
  name: 'Sign Up',
  Component: Layout,
  loader: async () => {
    const enabled = await systemSettingQuery.publicQuery().data[PublicKey.SIGN_UP].value.enabled;
    if (!enabled) throw new Error('Sign Up is not enabled');
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default SignupRouter;
