import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import SingleTweet from '../../components/Tweet/SingleTweet/SingleTweet';
import CreateReply from '../../components/Reply/CreateReply/CreateReply';
import ReplyList from '../../components/Reply/ReplyList/ReplyList';
import { DisplayTweetDetails } from '../../types/tweet/displayTweetDetails';
import { ReplyDetails } from '../../types/reply/replyDetails';
import styles from './TweetDetail.module.css';
import { findTweetById } from '../../services/tweetService';
import backIcon from '../../assets/icons/back-arrow.svg';

const TweetDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [tweet, setTweet] = useState<DisplayTweetDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [newReply, setNewReply] = useState<ReplyDetails | null>(null);
  const [showReplyModal, setShowReplyModal] = useState(false);
  const [parentReplyId, setParentReplyId] = useState<number | null>(null);

  const tweetId = Number(id);

  const loadTweetDetails = useCallback(async () => {
    if (!tweetId) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await findTweetById(tweetId);
      setTweet(data);
    } catch (err) {
      console.error("Failed to load tweet details:", err);
      setError('Could not load tweet.');
    } finally {
      setIsLoading(false);
    }
  }, [tweetId]);

  useEffect(() => {
    loadTweetDetails();
  }, [loadTweetDetails]);

  const openReplyPopup = (replyId: number) => {
    setParentReplyId(replyId);
    setShowReplyModal(true);
  };

  const closeReplyPopup = () => {
    setShowReplyModal(false);
    setParentReplyId(null);
  };

  const handleReplyCreated = (createdReply: ReplyDetails) => {
    // Update reply count on the main tweet
    setTweet(prev => prev ? { ...prev, replyCount: prev.replyCount + 1 } : null);
    // Pass the new reply to the ReplyList component to prepend it (if it's a main reply)
    if (!createdReply.parentReplyId) {
        setNewReply(createdReply);
    }
    // Close the modal if it was open for a nested reply
    if (showReplyModal) {
       closeReplyPopup(); 
    }
  };

  const goBack = () => {
    navigate('/home'); // Go back to the previous page in history
  };

  if (isLoading) {
    return <div>Loading tweet...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (!tweet) {
    return <div>Tweet not found.</div>;
  }

  return (
    <div className={styles.pageContainer}>
       <div className={styles.headerBar}>
          <button onClick={goBack} className={styles.backButton}>
             <img src={backIcon} alt="Go Back" className={styles.arrowIcon} />
          </button>
          <span className={styles.headerTitle}>Post</span>
       </div>
      
       <div className={styles.tweetContainer}>
         <SingleTweet tweet={tweet} isDetailView={true}/>
       </div>

      
      {showReplyModal && (
        <div className="overlay" onClick={closeReplyPopup}>
          <div className={`modal ${styles.replyModal}`} onClick={e => e.stopPropagation()}> 
             <button className={`close-btn ${styles.customCloseBtn}`} onClick={closeReplyPopup}>&times;</button>
            <CreateReply 
              tweetId={tweetId}
              parentReplyId={parentReplyId} // Pass the ID of the reply being replied to
              onReplyCreated={handleReplyCreated}
            />
          </div>
        </div>
      )}

      
      <div className={styles.createReplyContainer}>
        <CreateReply 
            tweetId={tweetId} 
            parentReplyId={null} // Main reply creator at the bottom
            onReplyCreated={handleReplyCreated} 
        />
      </div>

      <div className={styles.repliesContainer}>
        <ReplyList 
          tweetId={tweetId} 
          newReply={newReply} // Pass the newly created main reply
          onOpenReplyPopup={openReplyPopup} // Function to open the nested reply modal
          listKey={`tweet-${tweetId}-replies`}
        />
      </div>
    </div>
  );
};

export default TweetDetail;
