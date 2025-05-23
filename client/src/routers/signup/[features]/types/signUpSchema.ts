import { z } from 'zod';
import SignInSchema from '../../../signin/[features]/types/signInSchema';

const Input = z.object({
  email: z.string().email('이메일 형식이 아닙니다.'),
  confirmPwd: z.string().min(4, '비밀번호는 4글자 이상이어야 합니다.'),
}).merge(
  SignInSchema.Input,
).refine((data) => {
    return data.pwd === data.confirmPwd;
  }, {
    message: '비밀번호가 일치하지 않습니다.',
    path: ['confirmPwd'],
  },
);

const signUpSchema = {
  Input,
};

export default signUpSchema;

