import React, { useState, useRef, useEffect } from 'react';
import CreateTweet from '../../components/Tweet/CreateTweet/CreateTweet';
import TweetList from '../../components/Tweet/TweetList/TweetList';
import styles from './Home.module.css';

const Home: React.FC = () => {
  const [isFollowing, setIsFollowing] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const [listKey, setListKey] = useState<'forYou' | 'following'>('forYou');

  const handleForYou = () => {
    if (!isFollowing) return; // Avoid unnecessary state change if already selected
    setIsFollowing(false);
    setListKey('forYou');
  };

  const handleFollowing = () => {
    if (isFollowing) return; // Avoid unnecessary state change if already selected
    setIsFollowing(true);
    setListKey('following');
  };

   useEffect(() => {
     // Scroll to top when the tab changes
     if (containerRef.current) {
       containerRef.current.scrollTop = 0;
     }
   }, [listKey]); // Depend on listKey which changes only when tabs are switched

   const handleTweetCreated = () => {
      console.log('New tweet created, list should update.');
   }

  return (
    <div className={styles.homeContainer} ref={containerRef}>
      <div className={styles.stickyHeader}> 
        <div className={styles.switchBtn}>
          <button
            className={`${styles.btn} ${!isFollowing ? styles.active : ''}`}
            onClick={handleForYou}
          >
            For you
          </button>
          <button
            className={`${styles.btn} ${isFollowing ? styles.active : ''}`}
            onClick={handleFollowing}
          >
            Following
          </button>
        </div>
        <div className={styles.createTweetContainer}>
           <CreateTweet onTweetCreated={handleTweetCreated} />
        </div>
      </div>

      <div className={styles.tweetListContainer}>
          <TweetList isFollowing={isFollowing} listKey={listKey} />
      </div>
    </div>
  );
};

export default Home;
