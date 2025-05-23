import { z } from 'zod';

const Input = z.object({
  userId: z.string().min(4, '아이디는 4글자 이상이어야 합니다.'),
  pwd: z.string().min(4, '비밀번호는 4글자 이상이어야 합니다.'),
});


const signInSchema = {
  Input,
};

export default signInSchema;