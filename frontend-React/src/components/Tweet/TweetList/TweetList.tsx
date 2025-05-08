import React, { useState, useEffect, useRef, useCallback } from 'react';
import SingleTweet from '../SingleTweet/SingleTweet';
import { DisplayTweetDetails } from '../../../types/tweet/displayTweetDetails';
import styles from './TweetList.module.css';
import { getFollowedUsersTweets, getTweets, getTweetsByUserId } from '../../../services/tweetService';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../../store/slices/authSlice';

interface TweetListProps {
  isFollowing: boolean;
  userId?: number;
  listKey: string;
}

const TweetList: React.FC<TweetListProps> = ({ isFollowing, userId, listKey }) => {
  const [tweets, setTweets] = useState<DisplayTweetDetails[]>([]);
  const [page, setPage] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMoreTweets, setHasMoreTweets] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const observer = useRef<IntersectionObserver>();
  const size = 10;
  const loggedInUserId = useSelector(selectUserId);

  const loadTweets = useCallback(async (currentPage: number) => {
    
    if (currentPage > 0 && (isLoading || !hasMoreTweets)) {
        return;
    }
    
    if (currentPage === 0 && isLoading) {
        return;
    }

    setIsLoading(true);
    setError(null); 

    try {
      let response;
      if (userId !== undefined) {
        response = await getTweetsByUserId(userId, currentPage, size);
      } else if (isFollowing) {
         if (!loggedInUserId) {
             setIsLoading(false);
             setTweets([]);
             setHasMoreTweets(false);
             setError("Please log in to see tweets from users you follow."); 
             return; 
         }
        response = await getFollowedUsersTweets(currentPage, size);
      } else {
        response = await getTweets(currentPage, size);
      }

      if (!response || !response.content) {
          throw new Error("Invalid API response structure");
      }
      
      const morePagesExist = currentPage + 1 < response.totalPages;
      setHasMoreTweets(morePagesExist);

      if (response.content.length === 0 && currentPage === 0) {
        setTweets([]); 
      } else if (response.content.length > 0) {
        setTweets(prev => {
           const existingIds = new Set(prev.map(t => t.id));
           const newTweets = response.content.filter(t => !existingIds.has(t.id));
           
           return currentPage === 0 ? response.content : [...prev, ...newTweets];
        });
        setPage(currentPage + 1); 
      } 
    } catch (err) {
      console.error('Failed to load tweets:', err);
      setError((err instanceof Error ? err.message : 'Could not load tweets') + '. Please try again later.');
      setHasMoreTweets(false); 
    } finally {
      setIsLoading(false);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isFollowing, userId, loggedInUserId, size, hasMoreTweets, page]);


  useEffect(() => {
    setTweets([]);
    setPage(0);
    setHasMoreTweets(true); 
    setError(null);
    setIsLoading(false); 
    loadTweets(0);

  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [listKey, userId]); 


  const lastTweetElementRef = useCallback((node: HTMLDivElement) => {
    if (isLoading) return; 
    if (observer.current) observer.current.disconnect(); 

    observer.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMoreTweets && !isLoading) {
        loadTweets(page); 
      }
    }, { threshold: 0.5 }); 

    if (node) observer.current.observe(node); 
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading, hasMoreTweets, loadTweets, page]); 


  return (
    <div className={styles.tweetsContainer}>
      {tweets.map((tweet, index) => (
        <div key={tweet.id} ref={index === tweets.length - 1 ? lastTweetElementRef : null}>
             <SingleTweet tweet={tweet} />
        </div>
      ))}
      {isLoading && <div className={styles.loading}>Loading...</div>}
      {!isLoading && !hasMoreTweets && tweets.length > 0 && <div className={styles.endMessage}>End of tweets</div>}
      {!isLoading && !hasMoreTweets && tweets.length === 0 && !error && <div className={styles.endMessage}>No tweets to show</div>} {}
      {error && <div className={styles.error}>{error}</div>}
    </div>
  );
};

export default TweetList;