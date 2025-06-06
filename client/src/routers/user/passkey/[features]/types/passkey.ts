interface ListI {
  data: PasskeyT[];
}

type PasskeyT = {
  id: string;
  deviceName: string;
  createdAt: string;
  lastUsedAt: string;
  [key: string]: string
}

type FinishAssertionRequestT = {
  userId: string,
  assertion: string
}

export namespace PassKeyType {
  export type PassKey = PasskeyT;
  export type List = ListI;
  export type FinishAssertionRequest = FinishAssertionRequestT;
}