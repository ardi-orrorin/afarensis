import ExAxios from './exAxios';
import { AxiosError } from 'axios';
import { SignIn } from '../../routers/signin/[features]/types/signin';
import dayjs from 'dayjs';
import { CommonType } from '../types/commonType';
import { Cookies } from 'react-cookie';


const authMiddleware = async () => {

  const cookies = document.cookie.split(';');
  const accessToken = cookies.find((cookie) => cookie.startsWith('access_token'));

  if (accessToken) return true;

  const refreshToken = cookies.find((cookie) => cookie.startsWith('refresh_token'));
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
    cookie.set('access_token', res.accessToken, {
      path: '/',
      expires: dayjs().add(res.accessTokenExpiresIn, 'second').toDate(),
      sameSite: 'strict',
      secure: true,
    });

    return true;
  } catch (e) {
    const err = e as AxiosError;
    return false;
  }


};

export default authMiddleware;
