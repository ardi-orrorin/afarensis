import ExAxios from './exAxios';
import { AxiosError } from 'axios';
import { SignIn } from '../../routers/signin/[features]/types/signin';
import { CommonType } from '../types/commonType';
import { Cookies } from 'react-cookie';
import commonFunc from './funcs';
import signInFunc from '../../routers/signin/[features]/funcs/signin';


const authMiddleware = async () => {

  const cookies = document.cookie.split(';');
  const accessToken = cookies.find((cookie) => cookie.startsWith('access_token'));
  const roles = cookies.find((cookie) => cookie.startsWith('roles'));


  if (accessToken && roles) return true;

  const refreshToken = cookies.find((cookie) => cookie.includes('refresh_token'));
  
  if (!refreshToken) {
    throw new Error('로그인이 필요합니다.');
  }

  const userId = cookies.find((cookie) => cookie.includes('user_id'));
  if (!userId) {
    throw new Error('로그인이 필요합니다.');
  }

  const body = {
    refreshToken: refreshToken.split('=')[1],
    userId: userId.split('=')[1],
  } as CommonType.PublishRefreshToken;

  try {
    const res = await ExAxios<SignIn.Token, CommonType.PublishRefreshToken>({
      method: 'POST',
      url: '/api/v1/public/users/refresh',
      body,
      isReturnData: true,
    });

    const cookie = new Cookies();
    cookie.set(
      'access_token',
      res.accessToken,
      signInFunc.createCookieOption({ expiresIn: res.accessTokenExpiresIn }),
    );

    cookie.set(
      'roles',
      res.roles,
      signInFunc.createCookieOption({ expiresIn: res.accessTokenExpiresIn }),
    );

    return true;
  } catch (e) {
    const err = e as AxiosError;
    commonFunc.axiosError(err);
    return false;
  }


};

export default authMiddleware;
