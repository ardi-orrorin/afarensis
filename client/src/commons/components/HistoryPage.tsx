import React, { useState } from 'react';
import styles from './historyPage.module.css';
import { useNavigate } from 'react-router-dom';
import { useHistoryPage } from '../hooks/usehisotryPage';

const HistoryPage = () => {
  const navigate = useNavigate();

  const { pages, addPage, removePage } = useHistoryPage();
  const [more, setMore] = useState(true);

  const onClickChangeMore = () => {
    setMore((prev) => !prev);
  };

  return (
    <div className={`${styles['container']}`}>
      <div>
        <button onClick={onClickChangeMore}> {more ? '<<' : '>>'}</button>
      </div>
      <div>
        {more &&
          pages.map((page, index) => {
            return (
              <div key={`history-page-${page.id}`} onClick={() => navigate(`${page.path}`)}>
                <div className={`${page.isOpen && styles['active']}`}>
                  <p>{index + 1}</p>
                  <p>{page.title}</p>
                </div>
                <button
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    removePage({ id: page.id });
                  }}
                >
                  X
                </button>
              </div>
            );
          })}
      </div>
    </div>
  );
};

export default React.memo(HistoryPage);
