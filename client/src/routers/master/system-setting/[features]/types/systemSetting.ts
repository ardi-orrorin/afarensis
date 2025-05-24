import { z } from 'zod';
import systemSettingSchema from './systemSettingSchema';


interface PublicSystemSettingI {
  [SystemSetting.Key.INIT]: SystemSettingValueI<SystemSetting.Key.INIT>;
  [SystemSetting.Key.SIGN_UP]: SystemSettingValueI<SystemSetting.Key.SIGN_UP>;

  [key: string]: SystemSettingValueI<SystemSetting.Key>;
}

interface PrivateSystemSettingI {
  [SystemSetting.Key.SMTP]: SystemSettingValueI<SystemSetting.Key.SMTP>;
  [SystemSetting.Key.WEBHOOK]: SystemSettingValueI<SystemSetting.Key.WEBHOOK>;

  [key: string]: SystemSettingValueI<SystemSetting.Key>;
}

interface SystemSettingValueI<T extends SystemSetting.Key> {
  key: T;
  value: ValueI[T];
  initValue: ValueI[T];
  public: boolean;
}

interface ValueI {
  [SystemSetting.Key.INIT]: SystemSetting.Init;
  [SystemSetting.Key.SIGN_UP]: SystemSetting.SignUp;
  [SystemSetting.Key.SMTP]: SystemSetting.Smtp;
  [SystemSetting.Key.WEBHOOK]: SystemSetting.Webhook;

  [key: string]: InitT | SignUpT | SmtpT;
}

type InitT = z.infer<typeof systemSettingSchema.Init>;
type SignUpT = z.infer<typeof systemSettingSchema.SignUp>;
type SmtpT = z.infer<typeof systemSettingSchema.Smtp>;
type WebhookT = z.infer<typeof systemSettingSchema.Webhook>;

export namespace SystemSetting {
  export type Init = InitT;
  export type SignUp = SignUpT;
  export type Smtp = SmtpT;
  export type Webhook = WebhookT;
  export type PublicSystemSetting = PublicSystemSettingI;
  export type PrivateSystemSetting = PrivateSystemSettingI;
  export type SystemSettingValue<T extends SystemSetting.Key> = SystemSettingValueI<T>
  export type Value = ValueI;

  export enum Key {
    INIT = 'INIT',
    SMTP = 'SMTP',
    SIGN_UP = 'SIGN_UP',
    WEBHOOK = 'WEBHOOK',
  }

  export enum Coverage {
    SIGNIN = 'SIGNIN',
    SIGNOUT = 'SIGNOUT',
    SIGNUP = 'SIGNUP',
    PASSWORD = 'PASSWORD',
    ROLE = 'ROLE'
  }
}