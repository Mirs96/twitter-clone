import React, { useState } from 'react';
import Register from '../../components/Authentication/Register/Register';
import Login from '../../components/Authentication/Login/Login';
import styles from './AuthHome.module.css';
import logoIcon from '../../assets/icons/logo.svg';

const AuthHome: React.FC = () => {
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);
  const [isLoginOpen, setIsLoginOpen] = useState(false);

  const openRegister = () => setIsRegisterOpen(true);
  const closeRegister = () => setIsRegisterOpen(false);

  const openLogin = () => setIsLoginOpen(true);
  const closeLogin = () => setIsLoginOpen(false);

  return (
    <div className={styles.authContainer}>
      <img src={logoIcon} alt="logo" className={styles.authImage} />
      <div className={styles.authContent}>
        <h1 className={styles.mainHeading}>Happening now</h1>
        <h2 className={styles.subHeading}>Join today.</h2>
        <button className={styles.registerBtn} onClick={openRegister}>Create Account</button>
        <div className={styles.separator}>
            <span className={styles.separatorLine}></span>
            <span className={styles.separatorText}>or</span>
            <span className={styles.separatorLine}></span>
        </div>
        <h3 className={styles.loginPrompt}>Already have an account?</h3>
        <button className={styles.loginBtn} onClick={openLogin}>Sign in</button>
      </div>

      
      {isRegisterOpen && (
        <div className="overlay" onClick={closeRegister}>
          <div className="modal" onClick={(e) => e.stopPropagation()}> 
            <button className="close-btn" onClick={closeRegister}>&times;</button>
            <Register onClose={closeRegister} />
          </div>
        </div>
      )}

      
      {isLoginOpen && (
        <div className="overlay" onClick={closeLogin}>
          <div className="modal" onClick={(e) => e.stopPropagation()}> 
            <button className="close-btn" onClick={closeLogin}>&times;</button>
            <Login onClose={closeLogin} />
          </div>
        </div>
      )}
    </div>
  );
};

export default AuthHome;
