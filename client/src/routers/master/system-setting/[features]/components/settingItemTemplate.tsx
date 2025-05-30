import styles from './settingItemTemplate.module.css';
import { CommonType } from '../../../../../commons/types/commonType';
import React, { JSX } from 'react';
import { SystemSetting } from '../types/systemSetting';

const SettingItemTemplate =
  ({
     headline,
     response,
     inputs,
     errors,
     errorFields,
     buttons,
     otherChildren,
   }: {
    headline: string
    inputs?: SystemSetting.SettingTemplateInput[];
    errors?: CommonType.FormErrors<object>
    errorFields?: string[],
    otherChildren?: JSX.Element,
    buttons: SystemSetting.SettingTemplateBtn[]
    response: CommonType.ResponseStatus<boolean>
  }) => {
    return (
      <div className={styles['item']}>
        <h2>{headline}</h2>
        {
          inputs
          && inputs.length > 0
          && <div className={styles['input-container']}>
            {
              inputs.map(input => (
                <input
                  key={input.name}
                  type={input.type}
                  name={input.name}
                  value={input.value}
                  onChange={input.onChange}
                  placeholder={input.placeholder}
                />
              ))
            }
          </div>
        }
        <div className={styles['other-container']}>
          {otherChildren}
        </div>
        <div className={styles['button-container']}>
          {
            buttons.map(button => (
              <button
                key={button.text}
                onClick={button.onClick}
                disabled={button.disabled}
              >
                {button.text}
              </button>
            ))
          }
        </div>
        {
          errors
          && errorFields
          && errorFields.length > 0
          && <div className={styles['error-container']}>
            {
              errorFields.map(field => (
                errors[field] && errors[field]!.length > 0 && (
                  <p key={field}>{`${field}<${errors[field]}>`}</p>
                )
              ))
            }
          </div>
        }
        {
          response.status
          && <p className={styles[response.data ? 'success' : 'error']}>
            {response.message}
          </p>
        }
      </div>
    );
  };

export default React.memo(SettingItemTemplate, (
  prev, next) => (
  prev.response === next.response
  && prev.headline === next.headline
  && prev.inputs === next.inputs
  && prev.otherChildren === next.otherChildren
  && prev.buttons === next.buttons
  && prev.errorFields === next.errorFields
  && prev.errors === next.errors
));