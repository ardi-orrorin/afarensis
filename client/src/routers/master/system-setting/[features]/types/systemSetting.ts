import { z } from 'zod';
import systemSettingSchema from './systemSettingSchema';


type PublicSystemSettingI = {
  [key in keyof typeof SystemSetting.PublicKey]: SystemSettingValueI<SystemSetting.PublicKey>;
} & {
  [SystemSetting.PublicKey.SIGN_UP]: SystemSettingValueI<SystemSetting.PublicKey.SIGN_UP>;
  [SystemSetting.PublicKey.INIT]: SystemSettingValueI<SystemSetting.PublicKey.INIT>;
  [SystemSetting.PublicKey.WEBHOOK]: SystemSettingValueI<SystemSetting.PublicKey.WEBHOOK>;
  [SystemSetting.PublicKey.PASSKEY]: SystemSettingValueI<SystemSetting.PublicKey.PASSKEY>;
  [SystemSetting.PublicKey.OTP]: SystemSettingValueI<SystemSetting.PublicKey.OTP>;
}

type PrivateSystemSettingI = {
  [key in keyof typeof SystemSetting.PrivateKey]: SystemSettingValueI<SystemSetting.PrivateKey>;
} & {
  [SystemSetting.PrivateKey.SMTP]: SystemSettingValueI<SystemSetting.PrivateKey.SMTP>;
};

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
  [SystemSetting.PublicKey.PASSKEY]: SystemSetting.PassKey;
  [SystemSetting.PublicKey.OTP]: SystemSetting.Otp;

  [SystemSetting.PrivateKey.SMTP]: SystemSetting.Smtp;

  [key: string]: InitT | SignUpT | SmtpT | WebhookT;
}

type InitT = z.infer<typeof systemSettingSchema.Init>;
type SignUpT = z.infer<typeof systemSettingSchema.SignUp>;
type SmtpT = z.infer<typeof systemSettingSchema.Smtp>;
type WebhookT = z.infer<typeof systemSettingSchema.Webhook>;
type PassKeyT = z.infer<typeof systemSettingSchema.Passkey>;
type OtpT = z.infer<typeof systemSettingSchema.Otp>;


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
  export type PassKey = PassKeyT;
  export type Otp = OtpT;
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
    PASSKEY = 'PASSKEY',
    OTP = 'OTP'
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