import { z } from 'zod';

const Input = z.object({
  userId: z.string().min(4, { message: '아이디는 4자 이상이어야 합니다.' }),
  email: z.string().min(1, { message: '이메일을 입력해주세요.' }).email({ message: '이메일 형식이 아닙니다.' }),
});

const findPasswordSchema = {
  Input,
};

export default findPasswordSchema;