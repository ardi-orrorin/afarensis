import React, { useMemo } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import styles from './pageNavigator.module.css';

const PageNavigator = ({ page, size, total }: { page: number; size: number; total: number }) => {
  const lastPage = useMemo(() => Math.ceil(total / size), [total, size]);

  const startPAge = useMemo(() => Math.max(1, page - 3), [page]);

  const curEndPage = useMemo(() => Math.min(lastPage, page + 3), [lastPage, page]);

  const pageNumbers = useMemo(
    () => Array.from({ length: curEndPage - startPAge + 1 }, (_, i) => startPAge + i),
    [curEndPage, startPAge, page],
  );

  const skipNextPage = useMemo(() => (page + 5 > lastPage ? lastPage : page + 5), [page, lastPage]);
  const skipPrevPage = useMemo(() => (page - 5 < 1 ? 1 : page - 5), [page]);

  const createQueryStr = (page: number) => {
    const newSearchParams = new URLSearchParams(window.location.search);
    newSearchParams.set('page', page.toString());
    return `?${newSearchParams.toString()}`;
  };

  const pages = useMemo(
    () =>
      pageNumbers.map((item, index) => {
        return (
          <Link
            className={`${Number(page) === Number(item) ? `${styles['active']}` : ''}`}
            to={{
              search: createQueryStr(item),
            }}
            key={`navi-${index}`}
          >
            {item}
          </Link>
        );
      }),
    [window.location.search, pageNumbers],
  );

  return (
    <div className={`${styles['page-navigator']}`}>
      {page !== 1 && (
        <Link className={`${styles['arrow']}`} to={{ search: createQueryStr(skipPrevPage) }}>
          {'<'}
        </Link>
      )}
      {pages}
      {page !== lastPage && lastPage !== 0 && total !== 0 && (
        <Link className={`${styles['arrow']}`} to={{ search: createQueryStr(skipNextPage) }}>
          {'>'}
        </Link>
      )}
    </div>
  );
};

export default React.memo(PageNavigator, (prev, next) => {
  return prev.page === next.page && prev.size === next.size && prev.total === next.total;
});
