import React from 'react';
import { NavLink } from 'react-router-dom';
import styles from './Navbar.module.css';
import { useSelector, useDispatch } from 'react-redux';
import { selectUserId, clearAuth } from '../../store/slices/authSlice';
import { AppDispatch } from '../../store/store';
import { clearUserState } from '../../store/slices/userSlice';
import bookmarkIcon from '../../assets/icons/navbar/bookmark.svg';
import profileIcon from '../../assets/icons/navbar/profile.svg';
import exploreIcon from '../../assets/icons/navbar/explore.svg';
import notificationIcon from '../../assets/icons/navbar/notification.svg';
import homeIcon from '../../assets/icons/navbar/home.svg';

const Navbar: React.FC = () => {
  const userId = useSelector(selectUserId);
  const dispatch = useDispatch<AppDispatch>();

  const handleLogout = () => {
    dispatch(clearAuth());
    dispatch(clearUserState());
  };

  const navLinkClass = ({ isActive }: { isActive: boolean }) => 
    isActive ? `${styles.navLink} ${styles.active}` : styles.navLink;

  return (
    <nav className={styles.navContainer}>
      <NavLink to="/home" className={navLinkClass}>
        <img src={homeIcon} alt="Home Icon" className={styles.menuIcon} />
        Home
      </NavLink>
      <NavLink to="/explore" className={navLinkClass}>
        <img src={exploreIcon} alt="Explore Icon" className={styles.menuIcon} />
        Explore
      </NavLink>
      <NavLink to="/notifications" className={navLinkClass}>
        <img src={notificationIcon} alt="Notifications Icon" className={styles.menuIcon} />
        Notifications
      </NavLink>
      {userId && (
          <NavLink to={`/profile/${userId}`} className={navLinkClass}>
            <img src={profileIcon} alt="Profile Icon" className={styles.menuIcon} />
            Profile
          </NavLink>
      )}
      <NavLink to="/bookmarks" className={navLinkClass}>
        <img src={bookmarkIcon} alt="Bookmark" className={styles.menuIcon} />
          Bookmarks
      </NavLink>
      <button onClick={handleLogout} className={styles.logoutButton}>Logout</button>
    </nav>
  );
};

export default Navbar;
