import React, { useState, useRef, useEffect } from 'react';
import CreateTweet from '../../components/Tweet/CreateTweet/CreateTweet';
import TweetList from '../../components/Tweet/TweetList/TweetList';
import styles from './Home.module.css';

const Home: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'forYou' | 'following'>('forYou');
  const containerRef = useRef<HTMLDivElement>(null);

  const handleTabChange = (tab: 'forYou' | 'following') => {
    if (activeTab === tab) return;
    setActiveTab(tab);
  };

   useEffect(() => {
     if (containerRef.current) {
       containerRef.current.scrollTop = 0;
     }
   }, [activeTab]);

   const handleTweetCreated = () => {
      // Potentially force a re-fetch or update of the 'For you' list if on that tab
      // Or simply let the optimistic UI update from CreateTweet handle it visually
      console.log('New tweet created, Home page notified.');
      if (activeTab === 'forYou') {
         // Consider a mechanism to refresh the 'forYou' list
         // For now, changing listKey to force re-render, though more sophisticated state management might be better
         setActiveTab('forYou'); // This will trigger useEffect if we add a new key based on this
      }
   }

  return (
    <div className={styles.homeContainer} ref={containerRef}>
      <div className={styles.stickyHeader}> 
        <div className={styles.switchBtn}>
          <button
            className={`${styles.btn} ${activeTab === 'forYou' ? styles.active : ''}`}
            onClick={() => handleTabChange('forYou')}
          >
            For you
          </button>
          <button
            className={`${styles.btn} ${activeTab === 'following' ? styles.active : ''}`}
            onClick={() => handleTabChange('following')}
          >
            Following
          </button>
        </div>
        <div className={styles.createTweetContainer}>
           <CreateTweet onTweetCreated={handleTweetCreated} />
        </div>
      </div>

      <div className={styles.tweetListContainer}>
          <TweetList 
            fetchType={activeTab === 'forYou' ? 'trending' : 'following'} 
            listKey={activeTab} 
          />
      </div>
    </div>
  );
};

export default Home;
