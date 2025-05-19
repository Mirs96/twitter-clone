import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { UserProfileDetails } from '../../types/user/userProfileDetails';
import ReplyList from '../../components/Reply/ReplyList/ReplyList';
import TweetList from '../../components/Tweet/TweetList/TweetList';
import UpdateProfile from '../../components/User/UpdateProfile/UpdateProfile';
import { HttpConfig } from '../../config/http-config';
import styles from './UserProfile.module.css';
import { follow, getUserProfile, unfollow } from '../../services/userProfileService';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../store/slices/authSlice';
import backIcon from '../../assets/icons/back-arrow.svg';


const UserProfile: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const currentUserId = useSelector(selectUserId);

  const [profile, setProfile] = useState<UserProfileDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'tweets' | 'replies'>('tweets');
  const [showSetupProfileModal, setShowSetupProfileModal] = useState(false);
  const [listKey, setListKey] = useState<string>(`user-${id}-tweets`); // Key for TweetList/ReplyList

  const profileUserId = Number(id);

  const fetchProfile = useCallback(async () => {
    if (!profileUserId) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getUserProfile(profileUserId); 
      setProfile(data);
    } catch (err) {
      console.error("Failed to fetch profile:", err);
      setError('Failed to load profile.');
    } finally {
      setIsLoading(false);
    }
  }, [profileUserId]); 

  useEffect(() => {
    fetchProfile();
    // Reset tab to tweets when profile changes
    setActiveTab('tweets');
    setListKey(`user-${id}-tweets`); // Update list key when ID changes
  }, [fetchProfile, id]); // Depend on id from useParams as well

  const handleToggleFollow = async () => {
    if (!profile || currentUserId === null || currentUserId === profileUserId) return;

    const wasFollowing = profile.isFollowing;
    // Optimistic update
    setProfile(prev => prev ? { 
        ...prev, 
        isFollowing: !wasFollowing,
        followersCount: wasFollowing ? prev.followersCount - 1 : prev.followersCount + 1
    } : null);

    try {
      if (wasFollowing) {
        await unfollow(profileUserId);
      } else {
        await follow(profileUserId);
      }
      // Optional: Refetch profile for consistency, though optimistic update might suffice
      // fetchProfile(); 
    } catch (err) {
      console.error('Failed to toggle follow:', err);
      // Revert optimistic update on error
      setProfile(prev => prev ? { 
        ...prev, 
        isFollowing: wasFollowing,
        followersCount: wasFollowing ? prev.followersCount + 1 : prev.followersCount - 1 
       } : null);
      alert('Could not update follow status.');
    }
  };

  const handleProfileUpdated = () => {
     setShowSetupProfileModal(false);
     fetchProfile(); // Refetch profile data after update
  }

  const switchTab = (tab: 'tweets' | 'replies') => {
      setActiveTab(tab);
      setListKey(`user-${profileUserId}-${tab}-${Date.now()}`); // Force remount/refresh of list
  }

  const getFullImageUrl = (profilePicturePath: string | null | undefined) => {
      if (!profilePicturePath) {
          return '/icons/default-avatar.png'; 
      }
      return `${HttpConfig.baseUrl}${profilePicturePath}`;
  };

  const goBack = () => {
    navigate('/home'); 
  };

  if (isLoading) {
    return <div>Loading profile...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (!profile) {
    return <div>Profile not found.</div>;
  }

  return (
    <div className={styles.profileContainer}>
      <div className={styles.headerBar}>
        <button onClick={goBack} className={styles.backButton}>
          <img src={backIcon} alt="Go Back" className={styles.arrowIcon} />
        </button>
        <span className={styles.headerNickname}>{profile.nickname}</span>
      </div>

      {showSetupProfileModal && (
          <div className="overlay" onClick={() => setShowSetupProfileModal(false)}>
              <div className={`modal ${styles.updateProfileModal}`} onClick={e => e.stopPropagation()}> 
                 <button className={`close-btn ${styles.customCloseBtn}`} onClick={() => setShowSetupProfileModal(false)}>&times;</button>
                 <UpdateProfile onProfileUpdated={handleProfileUpdated} />
              </div>
           </div>
      )}

      <div className={styles.profileHeader}>
        <div className={styles.headerTopSection}>
            <img 
                src={getFullImageUrl(profile.profilePicture)}
                alt={`${profile.nickname}'s profile picture`} 
                className={styles.avatarImg}
            />
           {currentUserId === profileUserId ? (
             <button className={styles.setupProfile} onClick={() => setShowSetupProfileModal(true)}>
               Set up profile
             </button>
           ) : (
             <button 
                onClick={handleToggleFollow} 
                className={`${styles.followBtn} ${profile.isFollowing ? styles.following : ''}`}
             >
               {profile.isFollowing ? 'Unfollow' : 'Follow'}
             </button>
           )}
        </div>

        <div className={styles.userInfoSection}>
            <h2 className={styles.userNickname}>{profile.nickname}</h2>
            
            <p className={styles.bioText}>{profile.bio || ''}</p>
            <div className={styles.followCounts}>
                <span className={styles.countItem}><b>{profile.followingCount}</b> Following</span>
                <span className={styles.countItem}><b>{profile.followersCount}</b> Followers</span>
             </div>
        </div>
        
      </div>

      <div className={styles.profileTabs}>
        <button 
            onClick={() => switchTab('tweets')} 
            className={`${styles.tabButton} ${activeTab === 'tweets' ? styles.active : ''}`}
        >
            Posts
        </button>
        <button 
            onClick={() => switchTab('replies')} 
            className={`${styles.tabButton} ${activeTab === 'replies' ? styles.active : ''}`}
        >
            Replies
        </button>
      </div>

      <div className={styles.profileContent}>
        {activeTab === 'tweets' && (
          <TweetList 
            userId={profileUserId} 
            isFollowing={false} // Not relevant when fetching by specific user ID
            listKey={listKey} // Use state key
          />
        )}
        {activeTab === 'replies' && (
          <ReplyList 
            userId={profileUserId} 
            onOpenReplyPopup={() => { /* Define or pass reply popup logic if needed here */ }}
            listKey={listKey} // Use state key
          />
        )}
      </div>
    </div>
  );
};

export default UserProfile;
