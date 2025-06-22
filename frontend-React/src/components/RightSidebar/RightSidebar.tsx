import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './RightSidebar.module.css';
import { getTrendingHashtags } from '../../services/hashtagService';
import { autocompleteSearch } from '../../services/searchService';
import { TrendingHashtagDetails } from '../../types/hashtag/trendingHashtagDetails';
import { AutocompleteResponse, AutocompleteUser, AutocompleteHashtag } from '../../types/search/autocompleteResponse';
import { HttpConfig } from '../../config/http-config';

const RightSidebar: React.FC = () => {
  const [trendingHashtags, setTrendingHashtags] = useState<TrendingHashtagDetails[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<AutocompleteResponse | null>(null);
  const [isSearchLoading, setIsSearchLoading] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTrending = async () => {
      try {
        const data = await getTrendingHashtags();
        setTrendingHashtags(data);
      } catch (error) {
        console.error('Failed to fetch trending hashtags:', error);
      }
    };
    fetchTrending();
  }, []);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const query = event.target.value;
    console.log("Search Input Change:", query);
    setSearchQuery(query);
    if (query.trim() === '') {
      setSearchResults(null);
      setShowResults(false);
    } else {
      setShowResults(true);
      debouncedSearch(query);
    }
  };

  const performSearch = async (query: string) => {
    console.log("Performing search for:", query);
    if (query.trim() === '') {
      setSearchResults(null);
      return;
    }
    setIsSearchLoading(true);
    try {
      const data = await autocompleteSearch(query);
      console.log("Search results received:", data);
      setSearchResults(data);
    } catch (error) {
      console.error('Autocomplete search failed:', error);
      setSearchResults(null);
    } finally {
      setIsSearchLoading(false);
    }
  };

  const debounce = <F extends (...args: any[]) => any>(
    func: F,
    delay: number
  ) => {
    let timeoutId: ReturnType<typeof setTimeout>;
    return (...args: Parameters<F>): void => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => func(...args), delay);
    };
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debouncedSearch = useCallback(debounce(performSearch, 300), []);

  // Changed from onClick to onMouseDown
  const handleResultClick = useCallback((item: AutocompleteUser | AutocompleteHashtag, type: 'user' | 'hashtag') => {
    console.log(`Autocomplete: Item clicked - Type: ${type}, ID: ${'id' in item ? item.id : 'N/A'}`, item);
    // Note: Using onMouseDown fires before onBlur, helping prevent the element disappearing
    // No need for e.stopPropagation() or e.preventDefault() here, as onMouseDown completes before blur

    setSearchQuery('');
    setSearchResults(null);
    setShowResults(false); // Hide results immediately on click

    if (type === 'user') {
      const userItem = item as AutocompleteUser;
      console.log(`Autocomplete: Navigating to /profile/${userItem.id}`);
      navigate(`/profile/${userItem.id}`);
    } else {
      const hashtagItem = item as AutocompleteHashtag;
      console.log(`Autocomplete: Navigating to /explore/tag/${hashtagItem.id}/${encodeURIComponent(hashtagItem.tag)}`);
      navigate(`/explore/tag/${hashtagItem.id}/${encodeURIComponent(hashtagItem.tag)}`);
    }
  }, [navigate]);

  const formatCount = (count: number): string => {
    if (count >= 1000000) return (count / 1000000).toFixed(1) + 'M posts';
    if (count >= 1000) return (count / 1000).toFixed(0) + 'K posts';
    return count + ' posts';
  };

  return (
    <aside className={styles.sidebarContainer}>
      <div className={styles.searchBarContainer}>
        <input
          type="text"
          placeholder="Search"
          className={styles.searchInput}
          value={searchQuery}
          onChange={handleSearchChange}
          onFocus={() => {
            console.log("Search input focused. Show results if query exists.");
            searchQuery.trim() !== '' && setShowResults(true);
          }}
          onBlur={() => {
            console.log("Search input blurred. Delaying hide.");
            // Delay hiding results to allow click/mousedown event on suggestions to fire
            setTimeout(() => {
              console.log("Search: Hiding results after delay (200ms).");
              setShowResults(false);
            }, 200); // Increased delay to 200ms
          }}
        />
        {showResults && (searchResults || isSearchLoading) && (
          <div className={styles.autocompleteResults}>
            {isSearchLoading && <div className={styles.loadingText}>Loading...</div>}
            {/* Render users */}
            {searchResults?.users?.map(user => (
              <div
                key={`user-${user.id}`}
                className={styles.autocompleteItem}
                // Use onMouseDown instead of onClick - fires before onBlur
                onMouseDown={() => handleResultClick(user, 'user')}
              >
                <img src={user.profilePicture ? `${HttpConfig.baseUrl}${user.profilePicture}` : '/icons/default-avatar.png'} alt={user.nickname} className={styles.itemImage} />
                <span className={styles.itemName}>{user.nickname}</span>
                <span className={styles.itemType}>User</span>
              </div>
            ))}
            {/* Render hashtags */}
            {searchResults?.hashtags?.map(hashtag => (
              <div
                key={`tag-${hashtag.id}`}
                className={styles.autocompleteItem}
                // Use onMouseDown instead of onClick - fires before onBlur
                 onMouseDown={() => handleResultClick(hashtag, 'hashtag')}
              >
                <span className={styles.itemName}>#{hashtag.tag}</span>
                <span className={styles.itemType}>Hashtag</span>
              </div>
            ))}
            {/* No results message */}
            {!isSearchLoading && !searchResults?.users?.length && !searchResults?.hashtags?.length && searchQuery.trim() !== '' && (
                <div className={styles.loadingText}>No results for "{searchQuery}"</div>
            )}
          </div>
        )}
      </div>

      <div className={styles.widget}>
        <h2 className={styles.widgetTitle}>Trends</h2>
        {trendingHashtags.length > 0 ? (
            <ul className={styles.trendList}>
            {trendingHashtags.map(trend => (
                <li key={trend.id} className={styles.trendItem} onClick={() => navigate(`/explore/tag/${trend.id}/${encodeURIComponent(trend.tag)}`)}>
                <div className={styles.trendCategory}>Trending</div>
                <div className={styles.trendName}>#{trend.tag}</div>
                <div className={styles.trendCount}>{formatCount(trend.count)}</div>
                </li>
            ))}
            </ul>
        ) : (
            <div className={styles.loadingText}>Loading trends...</div>
        )}
      </div>
    </aside>
  );
};

export default RightSidebar;
