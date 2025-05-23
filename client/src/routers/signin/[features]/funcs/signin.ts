import { CookieSetOptions } from 'universal-cookie';
import dayjs from 'dayjs';

const createCookieOption =
  ({ expiresIn }: { expiresIn: number }) => ({
    path: '/',
    expires: dayjs().add(expiresIn, 'second').toDate(),
    sameSite: 'strict',
    secure: true,
  } as CookieSetOptions);

const signInFunc = {
  createCookieOption,
};

export default signInFunc;