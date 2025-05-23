interface RequestI {
  userId: string;
  pwd: string;
  email: string;
}

interface SignUpInputI extends RequestI {
  confirmPwd: string;
}

export namespace SignUp {
  export type Request = RequestI;
  export type SignUpInput = SignUpInputI;
}