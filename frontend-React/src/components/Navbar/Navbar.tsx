import React from 'react';
import { NavLink } from 'react-router-dom';
import styles from './Navbar.module.css';
import { useSelector, useDispatch } from 'react-redux';
import { selectUserId, clearAuth } from '../../store/slices/authSlice';
import { selectUserDetails } from '../../store/slices/userSlice'; 
import { AppDispatch } from '../../store/store';
import { clearUserState } from '../../store/slices/userSlice';
import { HttpConfig } from '../../config/http-config';

import homeIcon from '../../assets/icons/navbar/home.svg';
import exploreIcon from '../../assets/icons/navbar/explore.svg';
import notificationIcon from '../../assets/icons/navbar/notification.svg';
import bookmarkIcon from '../../assets/icons/navbar/bookmark.svg';
import profileIcon from '../../assets/icons/navbar/profile.svg';
import logo from '../../assets/icons/logo.svg'; // Assuming you have a logo

const Navbar: React.FC = () => {
  const userId = useSelector(selectUserId);
  const userDetails = useSelector(selectUserDetails);
  const dispatch = useDispatch<AppDispatch>();

  const handleLogout = () => {
    dispatch(clearAuth());
    dispatch(clearUserState());
  };

  const navLinkClass = ({ isActive }: { isActive: boolean }) => 
    isActive ? `${styles.navLink} ${styles.active}` : styles.navLink;

  const profilePictureUrl = userDetails?.profilePicture
    ? `${HttpConfig.baseUrl}${userDetails.profilePicture}`
    : '/icons/default-avatar.png'; // Fallback to a default avatar

  return (
    <div className={styles.navContainerWrapper}>
      <div className={styles.logoContainer}>
        <img src={logo} alt="Logo" className={styles.logo} />
      </div>

      <NavLink to="/home" className={navLinkClass}>
        <img src={homeIcon} alt="" className={styles.menuIcon} />
        <span className={styles.menuText}>Home</span>
      </NavLink>
      <NavLink to="/explore" className={navLinkClass}>
        <img src={exploreIcon} alt="" className={styles.menuIcon} />
        <span className={styles.menuText}>Explore</span>
      </NavLink>
      <NavLink to="/notifications" className={navLinkClass}>
        <img src={notificationIcon} alt="" className={styles.menuIcon} />
        <span className={styles.menuText}>Notifications</span>
      </NavLink>
      {userId && (
          <NavLink to={`/profile/${userId}`} className={navLinkClass}>
            <img src={profileIcon} alt="" className={styles.menuIcon} />
            <span className={styles.menuText}>Profile</span>
          </NavLink>
      )}
      <NavLink to="/bookmarks" className={navLinkClass}>
        <img src={bookmarkIcon} alt="" className={styles.menuIcon} />
        <span className={styles.menuText}>Bookmarks</span>
      </NavLink>

      {userDetails && (
        <div className={styles.userAccountControl} onClick={handleLogout} title="Logout">
          <img src={profilePictureUrl} alt="Your profile" className={styles.userProfileImage} />
          <div className={styles.userInfo}>
            <span className={styles.userNickname}>{userDetails.nickname}</span>
          </div>
          <button className={styles.logoutButton} aria-label="Logout">Logout</button> 
        </div>
      )}
    </div>
  );
};

export default Navbar;
