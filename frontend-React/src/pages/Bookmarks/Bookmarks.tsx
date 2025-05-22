/* import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import TweetList from '../../components/Tweet/TweetList/TweetList';
import styles from './Bookmarks.module.css';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../store/slices/authSlice';
import backIcon from '../../assets/icons/back-arrow.svg';

const Bookmarks: React.FC = () => {
  const userId = useSelector(selectUserId);
  const navigate = useNavigate();

  useEffect(() => {
    // Optional: Any specific logic for when the Bookmarks page mounts or userId changes
  }, [userId]);

  const goBack = () => {
    navigate('/home');
  };

  if (!userId) {
    // Handle case where user is not logged in or userId is not available yet
    return (
        <div className={styles.bookmarksContainer}>
            <div className={styles.header}>
                <h1 className={styles.title}>Bookmarks</h1>
            </div>
            <div className={styles.content} style={{ textAlign: 'center', paddingTop: '50px' }}>
                Please log in to see your bookmarks.
            </div>
        </div>
    );
  }

  return (
    <div className={styles.bookmarksContainer}>
        <div className={styles.header}>
          <button onClick={goBack} className={styles.backButton}>
           <img src={backIcon} alt="Go Back" className={styles.arrowIcon} />
           <span className={styles.arrowIcon}></span>
          </button>
            <h1 className={styles.title}>Bookmarks</h1>
        </div>
        <div className={styles.content}>
            <TweetList 
                fetchType='bookmarks'
                userId={userId} 
                listKey={`bookmarks-user-${userId}`}
            />
        </div>
    </div>
  );
};

export default Bookmarks;
 */

import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import TweetList from '../../components/Tweet/TweetList/TweetList';
import styles from './Bookmarks.module.css';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../store/slices/authSlice';
import backIcon from '../../assets/icons/back-arrow.svg';

const Bookmarks: React.FC = () => {
  const userId = useSelector(selectUserId);
  const navigate = useNavigate();

  useEffect(() => {
    // Optional: Any specific logic for when the Bookmarks page mounts or userId changes
  }, [userId]);

  const goBack = () => {
    navigate('/home'); // Or navigate(-1) for a generic back behavior
  };

  // Optional: Handler if Bookmarks.tsx itself needs to react to an unbookmark
  const handleTweetUnbookmarked = (tweetId: number) => {
    console.log(`Tweet ${tweetId} was unbookmarked from the Bookmarks page.`);
    // You could add a toast notification here, for example.
    // The visual removal from the list is handled by TweetList.
  };

  if (!userId) {
    return (
        <div className={styles.bookmarksContainer}>
            <div className={styles.header}>
                {/* It's good practice to have a back button even if not logged in,
                    though its utility might be limited. Or you could hide it. */}
                <button onClick={() => navigate(-1)} className={styles.backButton} aria-label="Go back">
                   <img src={backIcon} alt="Go Back" className={styles.arrowIcon} />
                </button>
                <h1 className={styles.title}>Bookmarks</h1>
            </div>
            <div className={styles.content} style={{ textAlign: 'center', paddingTop: '50px' }}>
                Please log in to see your bookmarks.
            </div>
        </div>
    );
  }

  return (
    <div className={styles.bookmarksContainer}>
        <div className={styles.header}>
          <button onClick={goBack} className={styles.backButton} aria-label="Go back">
           <img src={backIcon} alt="Go Back" className={styles.arrowIcon} />
           {/* The span inside the button was redundant if only using the image, removed it for cleanliness */}
          </button>
            <h1 className={styles.title}>Bookmarks</h1>
        </div>
        <div className={styles.content}>
            <TweetList
                fetchType='bookmarks'
                userId={userId}
                listKey={`bookmarks-user-${userId}`}
                onTweetUnbookmarked={handleTweetUnbookmarked} // Pass the handler
            />
        </div>
    </div>
  );
};

export default Bookmarks;
