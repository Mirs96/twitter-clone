import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { DisplayTweetDetails } from '../../../types/tweet/displayTweetDetails';
import { HttpConfig } from '../../../config/http-config';
import styles from './SingleTweet.module.css';
import { addBookmarkToTweet, addLikeToTweet, removeBookmarkFromTweet, removeLikeFromTweet } from '../../../services/tweetService';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../../store/slices/authSlice';
import replyIcon from '../../../assets/icons/tweet-buttons/reply.svg';
import likeIcon from '../../../assets/icons/tweet-buttons/like.svg';
import bookmarkIcon from '../../../assets/icons/tweet-buttons/bookmark.svg';
import { format, parseISO } from 'date-fns';
import { it } from 'date-fns/locale/it';

interface SingleTweetProps {
  tweet: DisplayTweetDetails; // Prop from TweetList
  isDetailView?: boolean;
  // CHANGED: Prop to notify parent with the *entire* updated tweet data
  onBookmarkChange?: (updatedTweet: DisplayTweetDetails) => void;
}

const SingleTweet: React.FC<SingleTweetProps> = ({ tweet: initialTweetProp, isDetailView = false, onBookmarkChange }) => {
  const loggedInUserId = useSelector(selectUserId);
  const navigate = useNavigate();
  const [localTweet, setLocalTweet] = useState<DisplayTweetDetails>(initialTweetProp);

  useEffect(() => {
    // Sync local state if the incoming prop from parent changes
    // This is crucial for when TweetList updates its data based on this component's actions
    if (JSON.stringify(initialTweetProp) !== JSON.stringify(localTweet)) {
        console.log(`SingleTweet (ID: ${initialTweetProp.id}) EFFECT: Prop changed. Updating localTweet.`,
            { prop: initialTweetProp, local: localTweet });
        setLocalTweet(initialTweetProp);
    }
  }, [initialTweetProp]); // Re-run if the initialTweetProp object reference changes

  const handleLikeToggle = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (!loggedInUserId) {
        alert("Please log in to like.");
        return;
    }

    const stateBeforeApiCall = { ...localTweet };

    setLocalTweet(prev => ({
      ...prev,
      liked: !prev.liked,
      likeCount: prev.liked ? Math.max(0, prev.likeCount - 1) : prev.likeCount + 1,
      likeId: prev.liked ? undefined : -1
    }));

    try {
      let finalUpdatedTweetStateForLike: DisplayTweetDetails;
      if (!stateBeforeApiCall.liked) {
        const likeData = { userId: loggedInUserId, tweetId: stateBeforeApiCall.id };
        const serverResponse = await addLikeToTweet(likeData); // Expects { likeCount, likeId }
        finalUpdatedTweetStateForLike = {
            ...localTweet, // Start with optimistically updated localTweet
            likeCount: serverResponse.likeCount,
            likeId: serverResponse.likeId,
            liked: true
        };
      } else if (stateBeforeApiCall.likeId) {
        const serverResponse = await removeLikeFromTweet(stateBeforeApiCall.likeId); // Expects { likeCount }
        finalUpdatedTweetStateForLike = {
            ...localTweet, // Start with optimistically updated localTweet
            likeCount: serverResponse.likeCount,
            likeId: undefined,
            liked: false
        };
      } else {
        console.warn("Like toggle: likeId missing when trying to unlike.");
        throw new Error("Cannot unlike, like ID missing.");
      }
      setLocalTweet(finalUpdatedTweetStateForLike);
      // TODO: If TweetList needs to know about like changes, implement onLikeChange similarly to onBookmarkChange
      // if (onLikeChange) { onLikeChange(finalUpdatedTweetStateForLike); }
    } catch (error) {
      console.error("Failed to toggle like:", error);
      setLocalTweet(stateBeforeApiCall);
      alert("Could not update like status.");
    }
  };

  const handleBookmarkToggle = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (!loggedInUserId) {
        alert("Please log in to bookmark.");
        return;
    }

    const stateBeforeApiCall = { ...localTweet };
    console.log(`HANDLE BOOKMARK (Tweet ID: ${stateBeforeApiCall.id}): Action based on: bookmarked=${stateBeforeApiCall.bookmarked}, count=${stateBeforeApiCall.bookmarkCount}, bookmarkId=${stateBeforeApiCall.bookmarkId}`);

    // Optimistic UI update for immediate visual feedback
    setLocalTweet(prev => ({
      ...prev,
      bookmarked: !prev.bookmarked,
      bookmarkCount: prev.bookmarked ? Math.max(0, prev.bookmarkCount - 1) : prev.bookmarkCount + 1,
      bookmarkId: prev.bookmarked ? undefined : -1 // Optimistic: undefined if removing, -1 if adding
    }));

    try {
      let finalUpdatedTweetState: DisplayTweetDetails;

      if (!stateBeforeApiCall.bookmarked) {
        // ---- User wants to ADD a bookmark ----
        const bookmarkData = { userId: loggedInUserId, tweetId: stateBeforeApiCall.id };
        console.log('  Calling addBookmarkToTweet with:', bookmarkData);
        const serverResponse = await addBookmarkToTweet(bookmarkData); // Expects { bookmarkCount, bookmarkId }
        console.log('  Response from addBookmarkToTweet:', serverResponse);

        finalUpdatedTweetState = {
            ...localTweet, // Current localTweet already has optimistic bookmarked:true and count
            bookmarkId: serverResponse.bookmarkId,       // Use server's actual bookmark ID
            bookmarkCount: serverResponse.bookmarkCount, // Use server's actual count
            bookmarked: true                             // Confirm status
        };
      } else {
        // ---- User wants to REMOVE a bookmark ----
        if (stateBeforeApiCall.bookmarkId === undefined || stateBeforeApiCall.bookmarkId === null || stateBeforeApiCall.bookmarkId === -1) {
          console.error('  ERROR: Cannot remove bookmark. stateBeforeApiCall.bookmarkId is invalid!', stateBeforeApiCall);
          alert("Error: Bookmark ID is missing or invalid. Please refresh.");
          setLocalTweet(stateBeforeApiCall); // Revert optimistic UI
          return;
        }
        console.log('  Calling removeBookmarkFromTweet with bookmarkId:', stateBeforeApiCall.bookmarkId);
        const serverResponse = await removeBookmarkFromTweet(stateBeforeApiCall.bookmarkId); // Expects { bookmarkCount }
        console.log('  Response from removeBookmarkFromTweet:', serverResponse);

        finalUpdatedTweetState = {
            ...localTweet, // Current localTweet already has optimistic bookmarked:false and count
            bookmarkId: undefined,                       // Bookmark ID is now gone
            bookmarkCount: serverResponse.bookmarkCount, // Use server's actual count
            bookmarked: false                            // Confirm status
        };
      }

      // Set the final, accurate state
      setLocalTweet(finalUpdatedTweetState);
      console.log(`  Bookmark toggle success. Final localTweet (ID: ${finalUpdatedTweetState.id}): bookmarked=${finalUpdatedTweetState.bookmarked}, count=${finalUpdatedTweetState.bookmarkCount}, bookmarkId=${finalUpdatedTweetState.bookmarkId}`);

      // Propagate the fully updated state to the parent
      if (onBookmarkChange) {
        onBookmarkChange(finalUpdatedTweetState);
      }

    } catch (error) {
      console.error("Failed to toggle bookmark API:", error);
      setLocalTweet(stateBeforeApiCall); // Revert to state before this action
      alert("Could not update bookmark status. Check console.");
    }
  };

  const handleReplyClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (!isDetailView) navigate(`/tweet/${localTweet.id}`);
  };

  const navigateToTweet = () => {
    if (!isDetailView) navigate(`/tweet/${localTweet.id}`);
  };

  const getFullImageUrl = (path: string | null | undefined) => {
    if (!path) return '/icons/default-avatar.png';
    return `${HttpConfig.baseUrl}${path}`;
  };

  const formatDateTime = (dateTimeStr: string | undefined) => {
    if (!dateTimeStr) return '';
    try {
      const date = parseISO(dateTimeStr);
      return `${format(date, 'yyyy-MM-dd', { locale: it })} · ${format(date, 'HH:mm', { locale: it })}`;
    } catch (e) { return dateTimeStr; }
  };

  return (
    <div className={styles.tweetContainer} onClick={navigateToTweet} role="article" tabIndex={0}>
      <div className={styles.tweetHeader}>
        <Link to={`/profile/${localTweet.userId}`} onClick={(e) => e.stopPropagation()} className={styles.profilePicLink}>
          <img className={styles.profilePicture} src={getFullImageUrl(localTweet.userProfilePicture)} alt={`${localTweet.userNickname}'s profile`} />
        </Link>
        <div className={styles.userInfoContainer}>
          <div className={styles.userInfo}>
            <Link to={`/profile/${localTweet.userId}`} className={styles.nicknameLink} onClick={(e) => e.stopPropagation()}>
              <h3 className={styles.nickname}>{localTweet.userNickname}</h3>
            </Link>
            <p className={styles.time}>{formatDateTime(localTweet.createdAt)}</p>
          </div>
          <div className={styles.tweetContent}><p>{localTweet.content}</p></div>
        </div>
      </div>
      <div className={styles.actions}>
        <div className={styles.interactionButtons}>
          <button className={`${styles.interactionButton} ${styles.likeButton} ${localTweet.liked ? styles.active : ''}`} onClick={handleLikeToggle} aria-label={localTweet.liked ? 'Unlike' : 'Like'} aria-pressed={localTweet.liked}>
            <img className={styles.interactionIcon} src={likeIcon} alt="Like" />
            <span className={styles.interactionCount}>{localTweet.likeCount ?? 0}</span>
          </button>
          <button className={`${styles.interactionButton} ${styles.replyButton}`} onClick={handleReplyClick} aria-label="Reply">
            <img className={styles.interactionIcon} src={replyIcon} alt="Reply" />
            <span className={styles.interactionCount}>{localTweet.replyCount ?? 0}</span>
          </button>
          <button className={`${styles.interactionButton} ${styles.bookmarkButton} ${localTweet.bookmarked ? styles.active : ''}`} onClick={handleBookmarkToggle} aria-label={localTweet.bookmarked ? 'Remove bookmark' : 'Bookmark'} aria-pressed={localTweet.bookmarked}>
            <img className={styles.interactionIcon} src={bookmarkIcon} alt="Bookmark" />
            <span className={styles.interactionCount}>{Math.max(0, localTweet.bookmarkCount ?? 0)}</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default SingleTweet;