import { createContext, useContext, useMemo, useState } from 'react';
import { ModalTypes } from '../types/modalType';
import ModalContainer from '../components/ModalContainer';
import { v5 as uuidv5 } from 'uuid';

type ModalContextT = {
  addModal: (params: ModalTypes.ModalParams) => void;
}

const ModalContext = createContext({} as ModalContextT);

export const ModalProvider = ({ children }: { children: React.ReactNode }) => {

  const [modals, setModals] = useState([] as ModalTypes.ModalProps[]);

  const viewModal = useMemo(() =>
      modals.find(modal => modal.isOpen)
    , [modals]);

  const addModal = ({ children, isOpen }: ModalTypes.ModalParams) => {
    const id = uuidv5(uuidv5.URL, uuidv5.DNS);

    setModals(prev => [...prev, { priority: prev.length + 1, id, children, isOpen, onClose: () => onClose(id) }]);
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
      addModal,
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

