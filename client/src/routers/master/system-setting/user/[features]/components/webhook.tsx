import systemSettingQuery from '../../../[features]/stores/query';
import { useEffect, useState } from 'react';
import { CommonType } from '../../../../../../commons/types/commonType';
import { SystemSetting } from '../../../[features]/types/systemSetting';
import SettingItemTemplate from '../../../[features]/components/settingItemTemplate';
import { AxiosError } from 'axios';
import systemSettingServiceApi from '../../../[features]/service/api';
import Toggle from '../../../[features]/components/toggle';
import styles from './webhook.module.css';
import systemSettingFunc from '../../../[features]/service/func';
import commonFunc from '../../../../../../commons/services/funcs';
import PublicKey = SystemSetting.PublicKey;


const Webhook = () => {
  const { data: publicData, refetch } = systemSettingQuery.publicQuery();
  const [value, setValue] = useState(publicData[PublicKey.WEBHOOK].value);
  const [errors, setErrors] = useState({} as CommonType.FormErrors<SystemSetting.Webhook>);
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);


  useEffect(() => {
    if (publicData?.[PublicKey.WEBHOOK].value) {
      setValue(publicData[PublicKey.WEBHOOK].value);
    }
  }, [publicData]);

  const changeToggle = () => {
    const newValue = {
      ...value,
      enabled: !value.enabled,
    } as SystemSetting.Value[PublicKey.WEBHOOK];

    const result = validHandler(newValue);

    if (result.enabled && result.enabled.length > 0) {
      return;
    }

    setValue(newValue);
  };

  const validHandler = (newValue: SystemSetting.Value[PublicKey.WEBHOOK]) => {
    const subtractRequired = systemSettingFunc.subtractRequiredHandler({ key: 'Webhook', newValue });
    setErrors(subtractRequired);
    setResponse({} as CommonType.ResponseStatus<boolean>);

    return subtractRequired;
  };

  const changeItemHandle =
    ({
       key, item, isAdd,
     }: {
      key: 'hasRole' | 'coverage', item: string, isAdd: boolean
    }) => {
      if (isAdd) {
        const newValue = [
          ...value[key],
          item,
        ];
        setValue({ ...value, [key]: newValue });
      } else {
        const newValue = value[key].filter((v) => v !== item);
        setValue({ ...value, [key]: newValue });
      }
    };

  const saveHandle = async () => {
    setLoading(true);
    const oldValue = publicData[PublicKey.WEBHOOK];
    const newValue = {
      ...oldValue,
      value,
    };
    try {
      const res = await systemSettingServiceApi.post(newValue);
      setResponse(res);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const initHandle = async () => {
    setLoading(true);
    try {
      const res = await systemSettingServiceApi.putInit(PublicKey.WEBHOOK);
      await refetch();
      setResponse(res);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const buttons = [
    { text: '저장', onClick: saveHandle, disabled: loading },
    { text: '초기화', onClick: initHandle, disabled: loading },
  ] as SystemSetting.SettingTemplateBtn[];


  const otherChildren = <>
    <SelectArray {...{
      keyName: 'role',
      title: '계정 권한',
      valueArray: value.hasRole,
      selectArray: Object.keys(CommonType.Role),
      onChange: ({ target: { value: item } }) =>
        changeItemHandle({ key: 'hasRole', item, isAdd: true }),
      onClick: (item) => changeItemHandle({ key: 'hasRole', item, isAdd: false }),
    }} />
    <SelectArray {...{
      keyName: 'coverage',
      title: '사용가능한 기능',
      valueArray: value.coverage,
      selectArray: Object.keys(SystemSetting.Coverage),
      onChange: ({ target: { value: item } }) =>
        changeItemHandle({ key: 'coverage', item, isAdd: true }),
      onClick: (item) => changeItemHandle({ key: 'coverage', item, isAdd: false }),
    }} />
    <Toggle  {...{
      checked: value.enabled,
      disabled: loading,
      onChange: changeToggle,
    }} />
  </>;


  return (
    <SettingItemTemplate {...{
      headline: 'WEBHOOK',
      response, errors,
      buttons,
      errorFields: Object.keys(errors),
      otherChildren,
    }} />
  );
};

export default Webhook;

const SelectArray =
  ({
     keyName,
     title,
     valueArray,
     selectArray,
     onChange,
     onClick,
   }: {
    keyName: string,
    title: string,
    valueArray: string[],
    selectArray: string[],
    onChange: (e: React.ChangeEvent<HTMLSelectElement>) => void,
    onClick: (e: string) => void,
  }) => {
    return (
      <div className={styles['role-container']}>
        <label>{title}</label>
        <select onChange={onChange}
        >
          <option>선택</option>
          {
            selectArray.map((item, index) => {
              if (valueArray.includes(item)) return;
              return (
                <option key={`${keyName}-${index}`}>{item}</option>
              );
            })
          }
        </select>
        {
          valueArray.map((item, index) => {
            return (
              <button key={`${keyName}-${index}`}
                      name={item}
                      onClick={() => onClick(item)}
              >
                {item}
              </button>
            );
          })
        }
      </div>
    );
  };