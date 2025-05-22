import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import TweetList from '../../components/Tweet/TweetList/TweetList';
import { getPaginatedTrendingHashtags } from '../../services/hashtagService';
import { TrendingHashtagDetails } from '../../types/hashtag/trendingHashtagDetails';
import { Page } from '../../types/page';
import styles from './ExplorePage.module.css';
import backIcon from '../../assets/icons/back-arrow.svg';
import { selectIsLoggedIn } from '../../store/slices/authSlice'; 

const ExplorePage: React.FC = () => {
  const { hashtagId: hashtagIdParam, hashtagName: hashtagNameParam } = useParams<{ hashtagId?: string; hashtagName?: string }>();
  const navigate = useNavigate();
  const isLoggedIn = useSelector(selectIsLoggedIn); // Get login status

  const [trendingHashtags, setTrendingHashtags] = useState<TrendingHashtagDetails[]>([]);
  const [hashtagPage, setHashtagPage] = useState(0);
  const [isLoadingHashtags, setIsLoadingHashtags] = useState(false);
  const [hasMoreHashtags, setHasMoreHashtags] = useState(true);
  const [errorHashtags, setErrorHashtags] = useState<string | null>(null);

  const hashtagListContainerRef = useRef<HTMLDivElement>(null);
  const hashtagObserver = useRef<IntersectionObserver>();
  const hashtagSize = 20;

  const hashtagId = hashtagIdParam ? Number(hashtagIdParam) : undefined;
  const hashtagName = hashtagNameParam ? decodeURIComponent(hashtagNameParam) : undefined;

  const loadTrendingHashtags = useCallback(async (currentPage: number) => {
    if (!isLoggedIn && !hashtagId) {
      setErrorHashtags("Please log in to explore trends.");
      setTrendingHashtags([]);
      setHasMoreHashtags(false);
      setIsLoadingHashtags(false);
      return;
    }

    if (isLoadingHashtags || (!hasMoreHashtags && currentPage !== 0)) return;
    setIsLoadingHashtags(true);
    setErrorHashtags(null);

    try {
      const response: Page<TrendingHashtagDetails> = await getPaginatedTrendingHashtags(currentPage, hashtagSize);
      if (!response || !response.content) {
        throw new Error("Invalid API response structure for trending hashtags");
      }

      setTrendingHashtags(prev => currentPage === 0 ? response.content : [...prev, ...response.content.filter(newTag => !prev.find(oldTag => oldTag.id === newTag.id))]);
      setHashtagPage(currentPage + 1);
      setHasMoreHashtags(currentPage + 1 < response.totalPages);

    } catch (err: any) {
      console.error('Failed to load trending hashtags:', err);
      if (err.response && err.response.status === 403) {
        setErrorHashtags('Access denied. You might need to log in or have specific permissions.');
      } else {
        setErrorHashtags(err instanceof Error ? err.message : 'Could not load trending hashtags');
      }
      setHasMoreHashtags(false);
    } finally {
      setIsLoadingHashtags(false);
    }
  }, [isLoadingHashtags, hasMoreHashtags, hashtagSize, isLoggedIn, hashtagId]);

  useEffect(() => {
    if (!hashtagId) {
      setTrendingHashtags([]);
      setHashtagPage(0);
      setHasMoreHashtags(true);
      setErrorHashtags(null);
      if (isLoggedIn) {
        loadTrendingHashtags(0);
      } else {
        setErrorHashtags("Please log in to explore trends.");
        setHasMoreHashtags(false);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [hashtagId, isLoggedIn]);

  const lastHashtagElementRef = useCallback((node: HTMLAnchorElement | null) => { // <--- Changed HTMLDivElement to HTMLAnchorElement
    if (isLoadingHashtags || !isLoggedIn) return;
    if (hashtagObserver.current) hashtagObserver.current.disconnect();

    hashtagObserver.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMoreHashtags && !isLoadingHashtags) {
        loadTrendingHashtags(hashtagPage);
      }
    }, { threshold: 0.9 });

    if (node) hashtagObserver.current.observe(node);
  }, [isLoadingHashtags, hasMoreHashtags, loadTrendingHashtags, hashtagPage, isLoggedIn]);

  const formatCount = (count: number): string => {
    if (count >= 1000000) return (count / 1000000).toFixed(1) + 'M posts';
    if (count >= 1000) return (count / 1000).toFixed(0) + 'K posts';
    return count + ' posts';
  };

  const goBack = () => {
    if (hashtagIdParam) {
      navigate('/explore');
    } else {
      navigate('/home');
    }
  };

  return (
    <div className={styles.exploreContainer} ref={hashtagListContainerRef}>
      <div className={styles.header}>
        <button onClick={goBack} className={styles.backButton}>
          <img src={backIcon} alt="Go Back" className={styles.arrowIcon} />
        </button>
        <h1 className={styles.title}>
          {hashtagId && hashtagName ? `#${hashtagName}` : 'Explore Trends'}
        </h1>
      </div>

      {hashtagId && hashtagName ? (
        <div className={styles.tweetListWrapper}>
          <TweetList
            hashtagId={hashtagId}
            fetchType='hashtag'
            listKey={`hashtag-${hashtagId}-tweets`}
          />
        </div>
      ) : (
        <>
          {!isLoggedIn && errorHashtags && (
            <div className={styles.errorMessage}>{errorHashtags}</div>
          )}
          {isLoggedIn && (
            <div className={styles.hashtagList}>
              {trendingHashtags.map((hashtag, index) => (
                <Link
                  to={`/explore/tag/${hashtag.id}/${encodeURIComponent(hashtag.tag)}`}
                  key={hashtag.id}
                  className={styles.hashtagItem}
                  ref={index === trendingHashtags.length - 1 ? lastHashtagElementRef : null}
                >
                  <div className={styles.hashtagInfo}>
                    <span className={styles.hashtagName}>#{hashtag.tag}</span>
                    <span className={styles.hashtagCount}>{formatCount(hashtag.count)}</span>
                  </div>
                </Link>
              ))}
              {isLoadingHashtags && <div className={styles.loadingMessage}>Loading more trends...</div>}
              {!isLoadingHashtags && !hasMoreHashtags && trendingHashtags.length > 0 && <div className={styles.endMessage}>No more trends</div>}
              {!isLoadingHashtags && trendingHashtags.length === 0 && !errorHashtags && <div className={styles.endMessage}>No trends to show</div>}
              {isLoggedIn && errorHashtags && <div className={styles.errorMessage}>{errorHashtags}</div>}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ExplorePage;