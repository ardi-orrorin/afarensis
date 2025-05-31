import { WebhookType } from '../types/webhook';
import styles from './item.module.css';
import { useEffect, useRef, useState } from 'react';
import { AxiosError } from 'axios';
import commonFunc from '../../../../../commons/services/funcs';
import webhookServiceApi from '../services/api';
import webhookQuery from '../stores/query';
import { CommonType } from '../../../../../commons/types/commonType';
import webhookSchema from '../types/webhookSchema';
import FormErrors = CommonType.FormErrors;
import ResStatus = CommonType.ResStatus;

type AddModeProps = {
  addMode: true;
};

type NormalProps = (WebhookType.Webhook | WebhookType.Input) & {
  addMode: false;
};

const initValue = { type: WebhookType.WebhookType.DISCORD, url: 'https://' } as WebhookType.Input;

type ItemProps = AddModeProps | NormalProps;

const Item = (props: ItemProps) => {
  const [input, setInput] = useState(initValue);
  const [errors, setErrors] = useState({} as FormErrors<WebhookType.Input>);
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState({} as CommonType.ResponseStatus<boolean>);
  const { refetch } = webhookQuery.webhook();
  const ref = useRef<HTMLInputElement[]>([]);


  useEffect(() => {
    if (props.addMode) return;
    setInput(props);
  }, []);

  const onChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const data = {
      ...input,
      [e.target.name]: e.target.value,
    };

    const result =
      webhookSchema.Input.safeParse(data);

    const fieldErrors = result.success
      ? {}
      : result.error.flatten().fieldErrors;

    const subtractRequired =
      commonFunc.subtractRequiredStr(fieldErrors);

    setErrors(subtractRequired);
    setInput(data);
  };

  const addHandler = async () => {
    if (!props.addMode) return;
    if (!webhookSchema.Input.safeParse(input).success) return;

    setLoading(true);

    try {
      const res = await webhookServiceApi.postWebhook(input);
      if (!res.data) return;
      await refetch();
      setInput(initValue);
      setResponse(res);
      setErrors({} as FormErrors<WebhookType.Input>);
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.setResponseError(err, setResponse);
    } finally {
      setLoading(false);
    }
  };
  const updateHandler = async () => {
    if (props.addMode) return;
    if (!webhookSchema.Input.safeParse(input).success) return;
    setLoading(true);
    try {
      const res = await webhookServiceApi.patchWebhook(input);
      if (!res.data) return;
      await refetch();
    } catch (e) {
      const err = e as AxiosError;
      commonFunc.axiosError(err);
    } finally {
      setLoading(false);
    }
  };

  const removeHandler = async () => {
    if (props.addMode) {
      setInput(initValue);
      return;
    }

    setLoading(true);

    const res = await webhookServiceApi.deleteWebhook(props.id!);

    if (!res.data) return;

    await refetch();

    setLoading(false);
  };

  return (
    <div className={styles['container']}>
      <div>
        <select value={props.addMode ? input.type ?? '' : props.type}
                name={'type'}
                onChange={onChange}
                autoFocus={props.addMode}
        >
          {
            Object.keys(WebhookType.WebhookType).map((type) => {
              return <option key={`type-${type}`} value={type}>{type}</option>;
            })
          }
        </select>
        <input value={input.url ?? ''}
               name={'url'}
               placeholder={'https://example.com/webhook'}
               onChange={onChange}
               disabled={loading}
               ref={e => {
                 ref.current[0] = e!;
               }}
               onKeyUp={e => {
                 if (e.key === 'Enter') {
                   ref.current[1]?.focus();
                 }
               }}

        />
        <input value={input.secret ?? ''}
               name={'secret'}
               placeholder="secret key를 입력하세요."
               onChange={onChange}
               disabled={loading}
               ref={e => {
                 ref.current[1] = e!;
               }}
               onKeyUp={async (e) => {
                 if (e.key === 'Enter') {
                   if (props.addMode) {
                     await addHandler();
                   } else {
                     await updateHandler();
                   }
                 }
               }}
        />
        {
          props.addMode
            ? <button onClick={addHandler}
                      disabled={loading}
            >
              add
            </button>
            : <button onClick={updateHandler}
                      disabled={loading}
            >
              update
            </button>
        }
        <button onClick={removeHandler}
                disabled={loading}
        >
          remove
        </button>
      </div>
      {
        errors
        && <div className={styles['error-container']}>
          {
            Object.keys(errors).map((key) => {
              return <p className={styles['error']} key={`error-${key}`}>{errors[key]}</p>;
            })
          }
        </div>
      }
      {
        response.status
        && <div className={styles['response-container']}>
          <p className={response.status === ResStatus.ERROR ? styles['error'] : ''}>{response.message}</p>
        </div>
      }
    </div>
  );
};


export default Item;