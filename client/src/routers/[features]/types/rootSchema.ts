import { z } from 'zod';

const Master = z.object({
  email: z.string().email('이메일 형식이 아닙니다.'),
  pwd: z.string().min(8, '비밀번호 길이는 최소 8자 이상이어야 합니다.'),
  pwdConfirm: z.string().min(8, '비밀번호 길이는 최소 8자 이상이어야 합니다.'),
}).refine((data) =>
    data.pwd === data.pwdConfirm
  , {
    message: '비밀번호가 일치하지 않습니다.',
    path: ['pwdConfirm'],
  });

const rootSchema = {
  Master,
};

export default rootSchema;