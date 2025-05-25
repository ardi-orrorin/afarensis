import styles from './toggle.module.css';
import React from 'react';

const Toggle =
  ({
     checked,
     onChange,
     disabled,
   }: {
    checked: boolean,
    disabled: boolean,
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
  }) => {
    return (
      <label className={styles['toggle']}>
        <input type="checkbox"
               {...{ checked, onChange, disabled }}
        />
        <span className={styles['slider']}></span>
      </label>
    );
  };

export default React.memo(Toggle, (prevProps, nextProps) => {
  return prevProps.checked === nextProps.checked
    && prevProps.disabled === nextProps.disabled
    && prevProps.onChange === nextProps.onChange
    && prevProps.disabled === nextProps.disabled;
});