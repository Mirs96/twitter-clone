import React, { useState, useEffect, useRef, useCallback } from 'react';
import SingleTweet from '../SingleTweet/SingleTweet';
import { DisplayTweetDetails } from '../../../types/tweet/displayTweetDetails';
import styles from './TweetList.module.css';
import { getFollowedUsersTweets, getTweets, getTweetsByUserId, getBookmarkedTweets, getTweetsByHashtagId } from '../../../services/tweetService';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../../store/slices/authSlice';

interface TweetListProps {
  // propUserId is the userId passed for viewing a specific user's tweets/bookmarks (e.g. on a profile page)
  userId?: number;
  hashtagId?: number;
  listKey: string;
  fetchType: 'trending' | 'following' | 'user' | 'bookmarks' | 'hashtag';
  onTweetUnbookmarked?: (tweetId: number) => void;
}

const TweetList: React.FC<TweetListProps> = ({ userId: propUserId, hashtagId, listKey, fetchType, onTweetUnbookmarked }) => {
  const [tweets, setTweets] = useState<DisplayTweetDetails[]>([]);
  const [page, setPage] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMoreTweets, setHasMoreTweets] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const observer = useRef<IntersectionObserver>();
  const size = 10;
  const loggedInUserId = useSelector(selectUserId); // This can be number | null

  const listKeyRef = useRef(listKey);

  const loadTweets = useCallback(async (currentPage: number) => {
    if (currentPage > 0 && (isLoading || !hasMoreTweets)) return;
    if (currentPage === 0 && isLoading && listKey === listKeyRef.current) return;

    listKeyRef.current = listKey;
    setIsLoading(true);
    setError(null);

    try {
      let response;

      // Determine the effective user ID for 'user' or 'bookmarks' fetch types
      let effectiveUserIdForFetch: number | null = null;
      if (fetchType === 'user') {
        effectiveUserIdForFetch = propUserId !== undefined ? propUserId : loggedInUserId;
      } else if (fetchType === 'bookmarks') {
        effectiveUserIdForFetch = propUserId !== undefined ? propUserId : loggedInUserId;
      }


      switch (fetchType) {
        case 'trending':
          response = await getTweets(currentPage, size);
          break;
        case 'following':
          if (!loggedInUserId) {
            setError("Please log in to see tweets from users you follow.");
            setHasMoreTweets(false); setIsLoading(false); setTweets([]); return;
          }
          response = await getFollowedUsersTweets(currentPage, size);
          break;
        case 'user':
          if (effectiveUserIdForFetch === null) { // Check if still null after logic above
            // This case implies viewing a generic user profile without a specific ID,
            // or the loggedInUserId is null when it's expected.
            // Or propUserId was undefined and loggedInUserId is null
            setError("User ID is required to fetch user tweets but was not available.");
            setHasMoreTweets(false); setIsLoading(false); setTweets([]); return;
          }
          response = await getTweetsByUserId(effectiveUserIdForFetch, currentPage, size);
          break;
        case 'bookmarks':
          if (effectiveUserIdForFetch === null) { // Check if still null
            setError("Please log in to see bookmarks.");
            setHasMoreTweets(false); setIsLoading(false); setTweets([]); return;
          }
          response = await getBookmarkedTweets(effectiveUserIdForFetch, currentPage, size);
          break;
        case 'hashtag':
          if (hashtagId === undefined) {
            setError('Hashtag ID is required for hashtag tweets.');
            setHasMoreTweets(false); setIsLoading(false); setTweets([]); return;
          }
          response = await getTweetsByHashtagId(hashtagId, currentPage, size);
          break;
        default:
          // Should not happen with TypeScript, but good for safety
          const exhaustiveCheck: never = fetchType;
          throw new Error(`Invalid fetch type: ${exhaustiveCheck}`);
      }

      if (!response || !response.content) {
        throw new Error("Invalid API response structure");
      }

      setHasMoreTweets(currentPage + 1 < response.totalPages);
      setTweets(prev => {
          const newTweets = response.content;
          if (currentPage === 0) return newTweets;
          const existingIds = new Set(prev.map(t => t.id));
          return [...prev, ...newTweets.filter(t => !existingIds.has(t.id))];
      });
      setPage(currentPage + 1);

    } catch (err) {
      console.error(`Failed to load tweets (fetchType: ${fetchType}):`, err);
      setError((err instanceof Error ? err.message : 'Could not load tweets') + '. Please try again later.');
      setHasMoreTweets(false);
    } finally {
      setIsLoading(false);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fetchType, propUserId, hashtagId, loggedInUserId, size, listKey]); // listKey added here for safety, though useEffect below primarily handles it

  useEffect(() => {
    console.log(`TweetList EFFECT: listKey changed or initial mount. listKey: ${listKey}. FetchType: ${fetchType}. PropUserID: ${propUserId}`);
    setTweets([]);
    setPage(0);
    setHasMoreTweets(true);
    setError(null);
    loadTweets(0);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [listKey]); // loadTweets is memoized and will use the latest propUserId, loggedInUserId etc.

  const lastTweetElementRef = useCallback((node: HTMLDivElement) => {
    if (isLoading || !hasMoreTweets) return;
    if (observer.current) observer.current.disconnect();

    observer.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting) {
        console.log("TweetList: Last element visible, loading more.");
        loadTweets(page);
      }
    }, { threshold: 0.5 });

    if (node) observer.current.observe(node);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading, hasMoreTweets, page, loadTweets]); // Added loadTweets here


  const handleLocalBookmarkChange = (updatedTweetFromServer: DisplayTweetDetails) => {
    console.log(`TweetList: Received bookmark change for tweet ID ${updatedTweetFromServer.id}. New state:`, updatedTweetFromServer);

    if (fetchType === 'bookmarks' && !updatedTweetFromServer.bookmarked) {
      setTweets(prevTweets => prevTweets.filter(t => t.id !== updatedTweetFromServer.id));
      if (onTweetUnbookmarked) {
          onTweetUnbookmarked(updatedTweetFromServer.id);
      }
    } else {
      setTweets(prevTweets =>
        prevTweets.map(t =>
          t.id === updatedTweetFromServer.id ? updatedTweetFromServer : t
        )
      );
    }
  };

  if (isLoading && page === 0) {
    return <div className={styles.loading}>Loading tweets...</div>;
  }
  if (error) {
    return <div className={styles.error}>{error}</div>;
  }
  if (!isLoading && tweets.length === 0 && !hasMoreTweets) { // This condition means loading finished, no tweets, and no more pages.
    return <div className={styles.endMessage}>No tweets to show.</div>;
  }

  return (
    <div className={styles.tweetsContainer}>
      {tweets.map((tweetItem, index) => (
        <div key={tweetItem.id} ref={index === tweets.length - 1 ? lastTweetElementRef : null}>
          <SingleTweet
            tweet={tweetItem}
            onBookmarkChange={handleLocalBookmarkChange}
          />
        </div>
      ))}
      {/* Show loading indicator for subsequent pages only if there are already tweets */}
      {isLoading && tweets.length > 0 && <div className={styles.loading}>Loading more tweets...</div>}
      {/* Show end message only if not loading, no more tweets, and some tweets were actually loaded */}
      {!isLoading && !hasMoreTweets && tweets.length > 0 && <div className={styles.endMessage}>End of tweets.</div>}
    </div>
  );
};

export default TweetList;