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

interface SingleTweetProps {
  tweet: DisplayTweetDetails;
  isDetailView?: boolean;
}

const SingleTweet: React.FC<SingleTweetProps> = ({ tweet, isDetailView = false }) => {
  const userId = useSelector(selectUserId);
  const navigate = useNavigate();
  const [localTweet, setLocalTweet] = useState<DisplayTweetDetails>(tweet);

  useEffect(() => {
    setLocalTweet(tweet);
  }, [tweet]);

  const handleLikeToggle = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (!userId) return;

    const currentLikeId = localTweet.likeId;
    const currentlyLiked = localTweet.liked;
    const currentLikeCount = localTweet.likeCount;


    setLocalTweet(prev => ({
      ...prev,
      liked: !currentlyLiked,
      likeCount: currentlyLiked ? currentLikeCount - 1 : currentLikeCount + 1,
      likeId: currentlyLiked ? undefined : -1
    }));

    try {
      if (!currentlyLiked) {
        const likeData = { userId: userId, tweetId: localTweet.id };
        const result = await addLikeToTweet(likeData);

        setLocalTweet(prev => ({ ...prev, likeCount: result.likeCount, likeId: result.likeId, liked: true }));
      } else if (currentLikeId) {
        const result = await removeLikeFromTweet(currentLikeId);

        setLocalTweet(prev => ({ ...prev, likeCount: result.likeCount, likeId: undefined, liked: false }));
      }
    } catch (error) {
      console.error("Failed to toggle like:", error);

      setLocalTweet(prev => ({ ...prev, liked: currentlyLiked, likeCount: currentLikeCount, likeId: currentLikeId }));
      alert("Could not update like status.");
    }
  };

  const handleBookmarkToggle = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (!userId) return;

    const currentBookmarkId = localTweet.bookmarkId;
    const currentlyBookmarked = localTweet.bookmarked;
    const currentBookmarkCount = localTweet.bookmarkCount;


    setLocalTweet(prev => ({
      ...prev,
      bookmarked: !currentlyBookmarked,
      bookmarkCount: currentlyBookmarked ? currentBookmarkCount - 1 : currentBookmarkCount + 1,
      bookmarkId: currentlyBookmarked ? undefined : -1
    }));

    try {
      const currentDate = new Date();
      const creationDate = currentDate.toISOString().split('T')[0];
      const creationTime = currentDate.toISOString().split('T')[1].split('.')[0];

      if (!currentlyBookmarked) {
        const bookmarkData = {
          userId: userId,
          tweetId: localTweet.id,
          creationDate: creationDate,
          creationTime: creationTime
        };
        const result = await addBookmarkToTweet(bookmarkData);
        setLocalTweet(prev => ({
          ...prev,
          bookmarkCount: result.bookmarkCount,
          bookmarkId: result.bookmarkId,
          bookmarked: true
        }));
      } else if (currentBookmarkId !== undefined) {
        const result = await removeBookmarkFromTweet(currentBookmarkId);
        setLocalTweet(prev => ({
          ...prev,
          bookmarkCount: result.bookmarkCount,
          bookmarkId: undefined,
          bookmarked: false
        }));
      }
    } catch (error) {
      console.error("Failed to toggle bookmark:", error);
      setLocalTweet(prev => ({
        ...prev,
        bookmarked: currentlyBookmarked,
        bookmarkCount: currentBookmarkCount,
        bookmarkId: currentBookmarkId
      }));
      alert("Could not update bookmark status.");
    }
  };

  const handleReplyClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();

    if (!isDetailView) {
      navigate(`/tweet/${localTweet.id}`);
    }
  }

  const navigateToTweet = () => {
    if (!isDetailView) {
      navigate(`/tweet/${localTweet.id}`);
    }
  };

  const getFullImageUrl = (profilePicturePath: string | null | undefined) => {
    if (!profilePicturePath) {
      return '/icons/default-avatar.png';
    }
    return `${HttpConfig.baseUrl}${profilePicturePath}`;
  };

  return (
    <div className={styles.tweetContainer} onClick={navigateToTweet} role="article" tabIndex={0}>
      <div className={styles.tweetHeader}>
        <Link
          to={`/profile/${localTweet.userId}`}
          onClick={(e) => e.stopPropagation()}
          className={styles.profilePicLink}
        >
          <img
            className={styles.profilePicture}
            src={getFullImageUrl(localTweet.userProfilePicture)}
            alt={`Profile picture of ${localTweet.userNickname}`}
          />
        </Link>
        <div className={styles.userInfoContainer}>
          <div className={styles.userInfo}>
            <Link
              to={`/profile/${localTweet.userId}`}
              className={styles.nicknameLink}
              onClick={(e) => e.stopPropagation()}
            >
              <h3 className={styles.nickname}>{localTweet.userNickname}</h3>
            </Link>

            <p className={styles.time}>&middot; {localTweet.creationDate} {isDetailView ? ` \u00b7 ${localTweet.creationTime}` : ''}</p>
          </div>
          <div className={styles.tweetContent}>
            <p>{localTweet.content}</p>
          </div>
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
            <span className={styles.interactionCount}>{localTweet.bookmarkCount ?? 0}</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default SingleTweet;
