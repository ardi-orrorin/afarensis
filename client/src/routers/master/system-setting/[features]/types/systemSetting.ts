import { z } from 'zod';
import systemSettingSchema from './systemSettingSchema';


interface PublicSystemSettingI {
  [SystemSetting.PublicKey.SIGN_UP]: SystemSettingValueI<SystemSetting.PublicKey.SIGN_UP>;
  [SystemSetting.PublicKey.INIT]: SystemSettingValueI<SystemSetting.PublicKey.INIT>;
  [SystemSetting.PublicKey.WEBHOOK]: SystemSettingValueI<SystemSetting.PublicKey.WEBHOOK>;

  [key: string]: SystemSettingValueI<SystemSetting.PublicKey>;
}

interface PrivateSystemSettingI {
  [SystemSetting.PrivateKey.SMTP]: SystemSettingValueI<SystemSetting.PrivateKey.SMTP>;

  [key: string]: SystemSettingValueI<SystemSetting.PrivateKey>;
}

interface SystemSettingValueI<T extends SystemSetting.PrivateKey | SystemSetting.PublicKey> {
  key: T;
  value: ValueI[T];
  initValue: ValueI[T];
  public: boolean;
}

interface ValueI {
  [SystemSetting.PublicKey.SIGN_UP]: SystemSetting.SignUp;
  [SystemSetting.PublicKey.INIT]: SystemSetting.Init;
  [SystemSetting.PublicKey.WEBHOOK]: SystemSetting.Webhook;

  [SystemSetting.PrivateKey.SMTP]: SystemSetting.Smtp;

  [key: string]: InitT | SignUpT | SmtpT | WebhookT;
}

type InitT = z.infer<typeof systemSettingSchema.Init>;
type SignUpT = z.infer<typeof systemSettingSchema.SignUp>;
type SmtpT = z.infer<typeof systemSettingSchema.Smtp>;
type WebhookT = z.infer<typeof systemSettingSchema.Webhook>;


type SettingTemplateBtnT = {
  text: string,
  disabled: boolean,
  onClick: () => void
}

type SettingTemplateInputT = {
  className?: string,
  name: string,
  type: React.HTMLInputTypeAttribute | undefined,
  value: string,
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void
  disabled: boolean
  placeholder?: string
}

export namespace SystemSetting {
  export type Init = InitT;
  export type SignUp = SignUpT;
  export type Smtp = SmtpT;
  export type Webhook = WebhookT;
  export type PublicSystemSetting = PublicSystemSettingI;
  export type PrivateSystemSetting = PrivateSystemSettingI;
  export type SystemSettingValue<T extends SystemSetting.PublicKey | SystemSetting.PrivateKey> = SystemSettingValueI<T>
  export type Value = ValueI;
  export type SettingTemplateBtn = SettingTemplateBtnT;
  export type SettingTemplateInput = SettingTemplateInputT;

  export enum PublicKey {
    SIGN_UP = 'SIGN_UP',
    INIT = 'INIT',
    WEBHOOK = 'WEBHOOK',
  }

  export enum PrivateKey {
    SMTP = 'SMTP',
  }

  export enum Coverage {
    SIGNIN = 'SIGNIN',
    SIGNOUT = 'SIGNOUT',
    SIGNUP = 'SIGNUP',
    PASSWORD = 'PASSWORD',
    ROLE = 'ROLE'
  }
}