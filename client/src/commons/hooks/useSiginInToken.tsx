import React, { createContext } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { SignIn } from '../../routers/signin/[features]/types/signin';
import { CommonType } from '../types/commonType';
import Role = CommonType.Role;


interface SignInTokenContextI {
  token: SignIn.Token;
  setToken: (token: SignIn.Token) => void;
  getRoles: () => Role[];
}

const SignInTokenContext = createContext({} as SignInTokenContextI);


export const SignInTokenProvider = ({ children }: { children: React.ReactNode }) => {
  const queryClient = useQueryClient();
  const { data: token } = useQuery<SignIn.Token>({
    queryKey: ['token'],
    queryFn: async (): Promise<SignIn.Token> => ({} as SignIn.Token),
    initialData: {} as SignIn.Token,
    staleTime: Infinity,
  });

  const setToken = (token: SignIn.Token) => {
    queryClient.setQueryData<SignIn.Token>(['token'], (old) => ({
      ...old, ...token,
    }));
  };

  const getRoles = () => {
    return token?.roles ?? [];
  };

  return <SignInTokenContext.Provider value={{
    token, setToken, getRoles,
  }}>
    {children}
  </SignInTokenContext.Provider>;
};

export const useSignInToken = () => {
  const context = React.useContext(SignInTokenContext);
  if (!context) {
    throw new Error('useSignInToken must be used within a SignInTokenProvider');
  }
  return context;
};