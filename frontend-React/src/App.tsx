import { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar/Navbar';
import AuthHome from './pages/AuthHome/AuthHome';
import RightSidebar from './components/RightSidebar/RightSidebar';
import Home from './pages/Home/Home';
import TweetDetail from './pages/TweetDetail/TweetDetail';
import UserProfile from './pages/UserProfile/UserProfile';
import styles from './App.module.css';
import { useSelector, useDispatch } from 'react-redux';
import { selectIsLoggedIn, selectUserId, checkAuthStatus } from './store/slices/authSlice';
import { AppDispatch } from './store/store';
import { fetchUserDetails, selectUserDetails, selectUserStatus, clearUserState } from './store/slices/userSlice';

function App() {
  const isLoggedIn = useSelector(selectIsLoggedIn);
  const userId = useSelector(selectUserId);
  const userDetails = useSelector(selectUserDetails);
  const userStatus = useSelector(selectUserStatus);
  const dispatch = useDispatch<AppDispatch>();

  // Check auth status on initial load (covers case where token might expire while app is open)
  useEffect(() => {
    dispatch(checkAuthStatus());
  }, [dispatch]);

  // Fetch user details when logged in and userId is available
  useEffect(() => {
    if (isLoggedIn && userId) {
        // Fetch only if not already fetched/loading for this user, or if user changed
        if ((!userDetails || userDetails.id !== userId) && userStatus !== 'loading' && userStatus !== 'succeeded') {
             dispatch(fetchUserDetails(userId));
        }
    } else if (!isLoggedIn) {
        // If logged out, clear user details state
        dispatch(clearUserState());
    }
  }, [isLoggedIn, userId, userDetails, userStatus, dispatch]);

  return (
    <>
      {isLoggedIn ? (
        <div className={styles.mainContainer}>
          <div className={styles.nav}>
            <Navbar />
          </div>
          <div className={styles.scrollableMain}>
            <div className={styles.content}>
              <Routes>
                <Route path="/" element={<Navigate to="/home" replace />} />
                <Route path="/home" element={<Home />} />
                <Route path="/tweet/:id" element={<TweetDetail />} />
                <Route path="/profile/:id" element={<UserProfile />} />
                <Route path="/explore" element={<div>Explore Page</div>} />
                <Route path="/notifications" element={<div>Notifications Page</div>} />
                <Route path="/bookmarks" element={<div>Bookmarks Page</div>} />
                <Route path="*" element={<Navigate to="/home" replace />} />
              </Routes>
            </div>
            <div className={styles.rightSidebar}>
              <RightSidebar />
            </div>
          </div>
        </div>
      ) : (
        <Routes>
           <Route path="*" element={<AuthHome />} />
        </Routes>
      )}
    </>
  );
}

export default App;
