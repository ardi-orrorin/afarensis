import React from 'react';


type ModalParamsT = {
  children: React.ReactNode;
  isOpen: boolean;
  onClose?: (id: string) => void;
}

type ModalPropsT = ModalParamsT & {
  id: string,
  priority: number;
  onClose: () => void;
}

export namespace ModalTypes {
  export type ModalParams = ModalParamsT
  export type ModalProps = ModalPropsT
}