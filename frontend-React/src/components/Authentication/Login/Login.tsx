import React from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { LoginDetails } from '../../../types/authentication/loginDetails';
import { login as loginUser } from '../../../services/authService';
import styles from './Login.module.css';
import { useDispatch } from 'react-redux';
import { setAuth } from '../../../store/slices/authSlice';
import { AppDispatch } from '../../../store/store';

interface LoginProps {
    onClose: () => void;
}

const Login: React.FC<LoginProps> = ({ onClose }) => {
  const dispatch = useDispatch<AppDispatch>();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm<LoginDetails>({
      mode: 'onChange'
  });

  const onSubmit: SubmitHandler<LoginDetails> = async (data) => {
    try {
      const response = await loginUser(data);
      dispatch(setAuth({ token: response.token }));
      onClose(); 
      
    } catch (error) {
      console.error('Login failed:', error);
      alert('Login failed. Check credentials and try again.'); 
    }
  };

  return (
    <div className={styles.loginContainer}>
        <h2>Log in to your account</h2>
      <form onSubmit={handleSubmit(onSubmit)} className={styles.loginForm}>
        <div className={styles.formGroup}>
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            {...register('email', { required: 'Email is required', pattern: { value: /^\S+@\S+$/i, message: 'Invalid email format' } })}
             className={errors.email ? styles.inputError : ''}
          />
          {errors.email && <span className={styles.validationMessage}>{errors.email.message}</span>}
        </div>
        <div className={styles.formGroup}>
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            {...register('password', { required: 'Password is required', minLength: { value: 8, message: 'Password must be at least 8 characters' } })}
            className={errors.password ? styles.inputError : ''}
          />
           {errors.password && <span className={styles.validationMessage}>{errors.password.message}</span>}
        </div>

        <button type="submit" disabled={!isValid} className={styles.submitBtn}>
          Login
        </button>
      </form>
    </div>
  );
};

export default Login;
