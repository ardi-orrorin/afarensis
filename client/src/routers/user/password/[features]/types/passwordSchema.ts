import { z } from 'zod';


const Input = z.object({
  pwd: z.string().min(4, { message: 'must be at least 4 characters long' }),
  newPwd: z.string().min(4, { message: 'must be at least 4 characters long' }),
  newCheckPwd: z.string().min(4, { message: 'must be at least 4 characters long' }),
})
  .refine((data) => data.newPwd === data.newCheckPwd, {
    message: 'Passwords do not match',
    path: ['newCheckPwd'],
  });


const passwordSchema = {
  Input,
};

export default passwordSchema;