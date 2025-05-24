import { z } from 'zod';
import { SystemSetting } from './systemSetting';

const Smtp = z.object({
  host: z.string().regex(
    /^[a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?(\.[a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?)*$/,
    '올바른 호스트명 형식이 아닙니다',
  ),
  port: z.coerce.number().max(65535, '최대값은 65535 입니다.').min(1),
  enabled: z.boolean(),
  username: z.string().regex(/^[a-zA-Z0-9]+$/, '영문자와 숫자만 입력 가능합니다'),
  password: z.string().min(1, '비밀번호를 입력해주세요'),
})
  .refine((data) =>
    data.enabled ? data.host && data.port && data.username && data.password : true, {
    message: '모든 필드를 정확히 입력해주세요',
    path: ['enabled'],
  });

const SignUp = z.object({
  enabled: z.boolean(),
});

const Init = z.object({
  initialized: z.boolean(),
  isUpdatedMasterPwd: z.boolean(),
});

const Webhook = z.object({
  enabled: z.boolean(),
  hasRole: z.enum(Object.values(SystemSetting.Key) as [string, ...string[]]),
  coverage: z.enum(Object.values(SystemSetting.Coverage) as [string, ...string[]]),
});

const systemSettingSchema = {
  Init, SignUp, Smtp, Webhook,
};

export default systemSettingSchema;