import ExAxios from './exAxios';
import { AxiosError } from 'axios';
import { SignIn } from '../../routers/signin/[features]/types/signin';
import { CommonType } from '../types/commonType';
import commonFunc from './funcs';


const authMiddleware = async () => {

  const cookies = document.cookie.split(';');
  const accessToken = cookies.find((cookie) => cookie.startsWith('access_token'));
  const roles = cookies.find((cookie) => cookie.startsWith('roles'));

  if (accessToken && roles) return true;

  const userId = cookies.find((cookie) => cookie.includes('user_id'));

  if (!userId) {
    throw new Error('로그인이 필요합니다.');
  }

  try {
    await ExAxios<SignIn.Token, CommonType.PublishRefreshToken>({
      method: 'GET',
      url: '/api/v1/public/users/refresh',
      isReturnData: true,
    });

    return true;
  } catch (e) {
    const err = e as AxiosError;
    commonFunc.axiosError(err);
    return false;
  }


};

export default authMiddleware;
