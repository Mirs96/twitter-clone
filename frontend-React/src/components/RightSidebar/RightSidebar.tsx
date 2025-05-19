import React from 'react';
import styles from './RightSidebar.module.css';

const RightSidebar: React.FC = () => {
  
  

  return (
    <aside className={styles.sidebarContainer}>
      <div className={styles.searchPlaceholder}>
        <input type="text" placeholder="Search" className={styles.searchInput} />
      </div>

      
      <div className={styles.widget}>
        <h2 className={styles.widgetTitle}>Trends for you</h2>
        <ul className={styles.trendList}>
          <li className={styles.trendItem}>#ReactJS</li>
          <li className={styles.trendItem}>#TypeScript</li>
          <li className={styles.trendItem}>#Vite</li>
          <li className={styles.trendItem}>#ReduxToolkit</li>
          <li className={styles.trendItem}>#ScrollbarsBeGone</li>
          
        </ul>
      </div>

       
      {/* <div className={styles.widget}>
        <h2 className={styles.widgetTitle}>Who to follow</h2>
         <ul className={styles.followList}>
          
          <li className={styles.followItem}>Suggested User 1</li>
          <li className={styles.followItem}>Suggested User 2</li>
         </ul>
      </div> */}

    </aside>
  );
};

export default RightSidebar;
