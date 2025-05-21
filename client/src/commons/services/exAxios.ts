import axios, { AxiosHeaders, AxiosRequestConfig, AxiosResponse } from 'axios';

export type Method = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
export type ContentType = 'application/json' | 'multipart/form-data' | '' | string;
export type Headers = Record<string, string | boolean>;

export type ExAxoisProps = {
  path: string;
  method: Method;
  body?: any;
  params?: any;
  setAuthorization?: boolean;
  contentType?: ContentType;
  headers?: Headers;
  cache?: boolean;
  cookie?: string;
  isReturnData?: boolean;
  timeout?: number;
};

async function exAxios<R = any, I = any>(props: ExAxoisProps & { isReturnData: true }): Promise<R>;
async function exAxios<R = any, I = any>(props: ExAxoisProps & { isReturnData: false }): Promise<AxiosResponse<R>>;
async function exAxios<R = any, I = any>(props: ExAxoisProps): Promise<AxiosResponse<R>>;

async function exAxios<R = any, I = any>(props: ExAxoisProps): Promise<R | AxiosResponse<R>> {
  const { path, method, body, params, setAuthorization, contentType, cookie, isReturnData, timeout, headers, cache } =
    props;

  if (!path) Error('path is required');
  if (!method) Error('method is required');

  const url = process.env.REACT_APP_REST_SERVER + path;

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

  // fixme: add authentication
  // if (setAuthorization) {
  //   let token = cookie;
  //   if (typeof window === 'undefined') {
  //     const { cookies } = require('next/headers');
  //     const cookie = await cookies();
  //     token = (cookie.get('next.access.token') || cookie.get('next.refresh.token'))?.value;
  //   }

  //   if (token) {
  //     newHeaders.setAuthorization('Bearer ' + token);
  //   } else {
  //     Error('token is required');
  //   }
  // }

  const Axios = axios.create();
  const config: AxiosRequestConfig<I> = {
    headers: newHeaders,
    method,
    url,
  };

  if (method === 'POST' || method === 'PUT' || method === 'PATCH') {
    if (!body) Error('path body is required');
    config.data = body;
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
