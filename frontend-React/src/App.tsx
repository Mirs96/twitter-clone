import { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar/Navbar';
import AuthHome from './pages/AuthHome/AuthHome';
import RightSidebar from './components/RightSidebar/RightSidebar';
import Home from './pages/Home/Home';
import Bookmarks from './pages/Bookmarks/Bookmarks';
import TweetDetail from './pages/TweetDetail/TweetDetail';
import UserProfile from './pages/UserProfile/UserProfile';
import ExplorePage from './pages/ExplorePage/ExplorePage';
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

  useEffect(() => {
    dispatch(checkAuthStatus());
  }, [dispatch]);

  useEffect(() => {
    if (isLoggedIn && userId) {
        if ((!userDetails || userDetails.id !== userId) && userStatus !== 'loading' && userStatus !== 'succeeded') {
             dispatch(fetchUserDetails(userId));
        }
    } else if (!isLoggedIn) {
        dispatch(clearUserState());
    }
  }, [isLoggedIn, userId, userDetails, userStatus, dispatch]);

  return (
    <>
      {isLoggedIn ? (
        <div className={styles.mainContainer}>
          <div className={styles.layoutWrapper}>
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
                  <Route path="/explore" element={<ExplorePage />} />
                  <Route path="/explore/tag/:hashtagId/:hashtagName" element={<ExplorePage />} />
                  <Route path="/notifications" element={<div>Notifications Page</div>} />
                  <Route path="/bookmarks" element={<Bookmarks />} />
                  <Route path="*" element={<Navigate to="/home" replace />} />
                </Routes>
              </div>
              <div className={styles.rightSidebar}>
                <RightSidebar />
              </div>
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
