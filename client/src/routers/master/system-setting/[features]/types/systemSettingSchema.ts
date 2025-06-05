import { z } from 'zod';
import { SystemSetting } from './systemSetting';
import { CommonType } from '../../../../../commons/types/commonType';

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
  homeUrl: z.string().min(1, '홈 URL을 입력해주세요').url('올바른 URL 형식이 아닙니다'),
});

const Webhook = z.object({
  enabled: z.boolean(),
  hasRole: z.array(z.nativeEnum(CommonType.Role)),
  coverage: z.array(z.nativeEnum(SystemSetting.Coverage)),
});

const Passkey = z.object({
  enabled: z.boolean(),
  domain: z.string().min(1, '도메인을 입력해주세요').regex(/^(?!https?:\/\/).+$/, '도메인만 입력해주세요'),
  port: z.number().min(1, '포트 번호를 입력해주세요').max(65535, '포트 번호는 65535 이하로 입력해주세요'),
  displayName: z.string().min(1, '표시 이름을 입력해주세요'),
});

const systemSettingSchema = {
  Init, SignUp, Smtp, Webhook, Passkey,
};

export default systemSettingSchema;