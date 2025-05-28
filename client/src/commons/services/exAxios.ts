import axios, { AxiosHeaders, AxiosRequestConfig, AxiosResponse } from 'axios';

export type Method = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
export type ContentType = 'application/json' | 'multipart/form-data' | '' | string;
export type Headers = Record<string, string | boolean>;

export type ExAxiosProps<I> = {
  url: string;
  method: Method;
  body?: I;
  params?: any;
  setAuthorization?: boolean;
  contentType?: ContentType;
  headers?: Headers;
  cache?: boolean;
  cookie?: string;
  isReturnData?: boolean;
  timeout?: number;
};

async function exAxios<R = any, I = any>(props: ExAxiosProps<I> & { isReturnData: true }): Promise<R>;
async function exAxios<R = any, I = any>(props: ExAxiosProps<I> & { isReturnData: false }): Promise<AxiosResponse<R>>;
async function exAxios<R = any, I = any>(props: ExAxiosProps<I>): Promise<AxiosResponse<R>>;

async function exAxios<R = any, I = any>(props: ExAxiosProps<I>): Promise<R | AxiosResponse<R>> {
  const { url, method, body, params, contentType, isReturnData, timeout, headers, cache } =
    props;

  if (!url) Error('url is required');
  if (!method) Error('method is required');

  const newHeaders = new AxiosHeaders();

  if (contentType) {
    newHeaders.setContentType(contentType);
  } else if (method !== 'DELETE') {
    newHeaders.setContentType('application/json');
  }

  if (headers) {
    newHeaders.set(headers);
  }

  if (cache) {
    newHeaders.set('Cache-Data', true);
  }

  const Axios = axios.create();
  const config: AxiosRequestConfig<I> = {
    headers: newHeaders,
    method,
    url,
    withCredentials: true,
  };

  if (method === 'POST' || method === 'PUT' || method === 'PATCH') {
    if (!body) Error('path body is required');
    config.data = body!;
  }
  if (method === 'GET' || method === 'DELETE') {
    config.params = params;
  }

  if (timeout) {
    config.timeout = timeout;
  }

  const res = await Axios.request<I, AxiosResponse<R>, I>(config);
  if (isReturnData) return res.data;
  return res;
}

export default exAxios;
