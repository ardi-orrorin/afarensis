import { ModalTypes } from '../types/modalType';
import styles from './modalContainer.module.css';

const ModalContainer = ({ title, children, onClose, id, priority }: ModalTypes.ModalProps) => {
  console.log(onClose, id, priority);
  return (
    <div className={styles['modal-container']}>
      <div className={styles['modal']}>
        <div className={styles['modal-header']}>
          <h2>{title}</h2>
          <button onClick={() => onClose()}>Close</button>
        </div>
        <div className={styles['modal-content']}>
          {children}
        </div>
      </div>
    </div>
  );
};

export default ModalContainer;