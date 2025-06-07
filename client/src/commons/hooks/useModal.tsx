import React, { createContext, useContext, useMemo, useState } from 'react';
import { ModalTypes } from '../types/modalType';
import ModalContainer from '../components/ModalContainer';
import { v5 as uuidv5 } from 'uuid';

type ModalContextT = {
  addModal: (params: ModalTypes.ModalParams) => void;
  viewModal: ModalTypes.ModalProps | undefined;
  onClose: (id: string) => void;
}

const ModalContext = createContext({} as ModalContextT);

export const ModalProvider = ({ children }: { children: React.ReactNode }) => {

  const [modals, setModals] = useState([] as ModalTypes.ModalProps[]);

  const viewModal = useMemo(() =>
      modals.findLast(modal => modal.isOpen)
    , [modals]);

  const addModal = ({ title, children, isOpen }: ModalTypes.ModalParams) => {
    const id = uuidv5(uuidv5.URL, uuidv5.DNS);

    setModals(prev => [...prev, {
      priority: prev.length + 1,
      id,
      title,
      isOpen,
      onClose: () => onClose(id),
      children: React.isValidElement(children)
        ? React.cloneElement(children, { id } as any)
        : children,
    }]);
  };

  const onClose = (id: string) => {
    setModals(prev =>
      prev.filter(modal => modal.id !== id)
        .sort((a, b) => a.priority - b.priority)
        .map((modal, index) => ({ ...modal, priority: index + 1 })),
    );
  };

  return (
    <ModalContext.Provider value={{
      addModal, viewModal, onClose,
    }}>
      {children}
      {
        viewModal
        && <ModalContainer {...viewModal} />
      }
    </ModalContext.Provider>
  );
};

export const useModal = () => {
  const context = useContext(ModalContext);

  if (!context) {
    throw new Error('useModal must be used within a ModalProvider');
  }

  return context;
};

