import { createContext, useCallback, useContext, useState } from 'react';
import { CommonType } from '../types/commonType';

type HistoryPageContextT = {
  pages: CommonType.HistoryPages;
  addPage: (page: CommonType.HistoryPage) => void;
  removePage: ({ id }: { id: string }) => void;
  setOpen: ({ id }: { id: string }) => void;
};

const HistoryPageContext = createContext({} as HistoryPageContextT);

export const HistoryPageProvider = ({ children }: { children: React.ReactNode }) => {
  const [pages, setPages] = useState([] as CommonType.HistoryPages);

  const addPage = useCallback(
    (page: CommonType.HistoryPage) => {
      setPages((prev) => {
        if (prev?.some((pageI) => pageI.id === page.id)) {
          return prev.map((pageI) => {
            if (pageI.id === page.id) {
              return { ...pageI, isOpen: true };
            }
            return { ...pageI, isOpen: false };
          });
        }
        if (prev.length > 9) {
          const arr = prev.slice(1);
          return [...arr, page];
        }
        return [...prev, page];
      });
    },
    [pages],
  );

  const removePage = useCallback(
    ({ id }: { id: string }) => {
      setPages((prev) => {
        const arr = prev.filter((page) => page.id !== id);
        return [...arr];
      });
    },
    [pages],
  );

  const setOpen = useCallback(
    ({ id }: { id: string }) => {
      setPages((prev) => {
        const arr = prev.map((page) => {
          if (page.id === id) {
            return { ...page, isOpen: !page.isOpen };
          }
          return page;
        });

        return [...arr];
      });
    },
    [pages],
  );

  return (
    <HistoryPageContext.Provider value={{ pages, addPage, removePage, setOpen }}>
      {children}
    </HistoryPageContext.Provider>
  );
};

export const useHistoryPage = () => {
  const context = useContext(HistoryPageContext);
  if (!context) throw new Error('useHistoryPage must be used within a HistoryPageProvider');

  return context;
};
