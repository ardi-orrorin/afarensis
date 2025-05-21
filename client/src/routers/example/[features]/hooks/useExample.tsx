import { createContext, useContext } from 'react';
import { ExampleType } from '../types/example';
import exampleQuery from '../stores/query';
import { CommonType } from '../../../../commons/types/commonType';

type ExampleContextT = ExampleType.Example & {
  getData: CommonType.CreateQueryActions<ExampleType.ExampleApi>;
};

const ExampleContext = createContext({} as ExampleContextT);

export const ExampleProvider = ({ children }: { children: React.ReactNode }) => {
  const example = 'Example!!!!';
  const getData = exampleQuery.getExample({ params: { id: 1, sort: 'asc', query: { id: 1, sort: 'asc' } } });

  return <ExampleContext.Provider value={{ example, getData }}>{children}</ExampleContext.Provider>;
};

export const useExample = () => {
  const context = useContext(ExampleContext);
  if (!context) throw new Error('useExample must be used within a ExampleProvider');

  return context;
};
