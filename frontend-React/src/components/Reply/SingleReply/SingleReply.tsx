import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ReplyDetails } from '../../../types/reply/replyDetails';
import { HttpConfig } from '../../../config/http-config';
import styles from './SingleReply.module.css';
import { addLikeToReply, removeLikeFromReply } from '../../../services/replyService';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../../store/slices/authSlice';
import replyIcon from '../../../assets/icons/tweet-buttons/reply.svg';
import likeIcon from '../../../assets/icons/tweet-buttons/like.svg';

interface SingleReplyProps {
  reply: ReplyDetails;
  onOpenReplyPopup: (replyId: number) => void;
  onToggleNested?: (replyId: number) => void;
  isNestedVisible?: boolean;
}

const SingleReply: React.FC<SingleReplyProps> = ({ reply, onOpenReplyPopup, onToggleNested, isNestedVisible }) => {
  const userId = useSelector(selectUserId);
  const [localReply, setLocalReply] = useState<ReplyDetails>(reply);

  
  useEffect(() => {
    setLocalReply(reply);
  }, [reply]);

  const handleLikeToggle = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation(); 
    if (!userId) return;

    const currentLikeId = localReply.likeId;
    const currentlyLiked = localReply.liked;
    const currentLikeCount = localReply.likeCount;

    // Optimistic UI update
    setLocalReply(prev => ({
      ...prev,
      liked: !currentlyLiked,
      likeCount: currentlyLiked ? currentLikeCount - 1 : currentLikeCount + 1,
      likeId: currentlyLiked ? undefined : -1 // Placeholder or temporary ID
    }));

    try {
      if (!currentlyLiked) {
         const likeData = { userId: userId, replyId: localReply.id };
         const result = await addLikeToReply(likeData);
         // Update with actual data from backend
         setLocalReply(prev => ({ 
             ...prev, 
             liked: true, 
             likeCount: result.likeCount, 
             likeId: result.likeId 
            }));
      } else if (currentLikeId) {
         const result = await removeLikeFromReply(currentLikeId);
         // Update with actual data from backend
          setLocalReply(prev => ({ 
             ...prev, 
             liked: false, 
             likeCount: result.likeCount, 
             likeId: undefined 
            }));
      }
    } catch (error) {
        console.error("Failed to toggle like:", error);
        // Revert optimistic update on failure
        setLocalReply(prev => ({ ...prev, liked: currentlyLiked, likeCount: currentLikeCount, likeId: currentLikeId }));
        alert("Could not update like status.");
    }
  };

  const handleReplyClick = (event: React.MouseEvent<HTMLButtonElement>) => {
      event.stopPropagation();
      onOpenReplyPopup(localReply.id);
  }

  const handleToggleNestedClick = (event: React.MouseEvent<HTMLButtonElement>) => {
      event.stopPropagation();
      if (onToggleNested) {
        onToggleNested(localReply.id);
      }
  }

  const getFullImageUrl = (profilePicturePath: string | null | undefined) => {
      if (!profilePicturePath) {
          return '/icons/default-avatar.png'; 
      }
      return `${HttpConfig.baseUrl}${profilePicturePath}`;
  };

  return (
    <div className={styles.replyContainer}>
      <div className={styles.replyHeader}>
         <Link 
            to={`/profile/${localReply.userId}`} 
            onClick={(e) => e.stopPropagation()} 
            className={styles.profilePicLink}
          >
          <img 
            className={styles.profilePicture}
            src={getFullImageUrl(localReply.userProfilePicture)}
            alt={`Profile picture of ${localReply.userNickname}`}
          />
        </Link>
        <div className={styles.userInfoContainer}>
            <div className={styles.userInfo}>
              <Link 
                to={`/profile/${localReply.userId}`} 
                className={styles.nicknameLink} 
                onClick={(e) => e.stopPropagation()}
               >
                <h3 className={styles.nickname}>{localReply.userNickname}</h3>
              </Link>
              <p className={styles.time}>{localReply.creationDate} &middot; {localReply.creationTime}</p>
            </div>
             <div className={styles.replyContent}>
                <p>{localReply.content}</p>
            </div>
        </div>
      </div>
     
      <div className={styles.actions}>
        <div className={styles.interactionButtons}>
          <button 
            className={`${styles.interactionButton} ${styles.likeButton} ${localReply.liked ? styles.active : ''}`}
            onClick={handleLikeToggle}
          >
            <img className={styles.interactionIcon} src={likeIcon} alt="Like" />
            <span className={styles.interactionCount}>{localReply.likeCount ?? 0}</span>
          </button>
          <button className={`${styles.interactionButton} ${styles.replyButton}`} onClick={handleReplyClick}>
            <img className={styles.interactionIcon} src={replyIcon} alt="Reply" />
          </button>
        </div>
         {localReply.hasNestedReplies && onToggleNested && (
            <button className={styles.toggleNested} onClick={handleToggleNestedClick}>
                {isNestedVisible ? 'Hide replies' : 'Show replies'}
            </button>
        )}
      </div>
    </div>
  );
};

export default SingleReply;
