import React from 'react';
import { useForm, SubmitHandler, Controller } from 'react-hook-form';
import { register as registerUser } from '../../../services/authService';
import { RegisterFormData, RegisterPayload } from '../../../types/authentication/registerDetails';
import { Role } from '../../../types/authentication/role';
import styles from './Register.module.css';
import { useDispatch } from 'react-redux';
import { setAuth } from '../../../store/slices/authSlice';
import { AppDispatch } from '../../../store/store';

interface RegisterProps {
    onClose: () => void;
}

const Register: React.FC<RegisterProps> = ({ onClose }) => {
  const dispatch = useDispatch<AppDispatch>();
  const { register, handleSubmit, control, formState: { errors, isValid } } = useForm<RegisterFormData>({
      mode: 'onBlur', 
      defaultValues: {
          firstname: '',
          lastname: '',
          nickname: '',
          dob: '',
          sex: '',
          email: '',
          password: '',
          phone: '',
          role: '',
          profilePicture: null,
          bio: ''
      }
  });

  const onSubmit: SubmitHandler<RegisterFormData> = async (data) => {
    if (!data.role) {
        console.error('Role is required');
        
        return;
    }

    const payload: RegisterPayload = {
        firstname: data.firstname,
        lastname: data.lastname,
        nickname: data.nickname,
        dob: data.dob,
        sex: data.sex,
        email: data.email,
        password: data.password,
        phone: data.phone,
        role: data.role,
        bio: data.bio,
        creationDate: new Date().toISOString().split('T')[0],
        
    };

    
    let submissionData: RegisterPayload | FormData = payload;

    if (data.profilePicture) {
        const formData = new FormData();
        Object.keys(payload).forEach(key => {
             
             
             
             if (key !== 'profilePicture') {
                formData.append(key, (payload as any)[key] as string);
            }
        });
        formData.append('profilePicture', data.profilePicture);
        submissionData = formData;
    }

    try {
      const response = await registerUser(submissionData);
      dispatch(setAuth({ token: response.token })); 
      onClose(); 
      
    } catch (error) { 
      console.error('Registration failed:', error);
      alert('Registration failed. Please try again.'); 
    }
  };

  return (
    <div className={styles.registerContainer}>
      <h2>Create your account</h2>
      <form onSubmit={handleSubmit(onSubmit)} className={styles.registerForm}>
        
        <div className={styles.formGroup}>
          <label htmlFor="firstname">First Name</label>
          <input
            type="text"
            id="firstname"
            {...register('firstname', { required: 'Please enter your first name', minLength: { value: 3, message: 'At least 3 characters please' } })}
            className={errors.firstname ? styles.inputError : ''}
          />
          {errors.firstname && <span className={styles.validationMessage}>{errors.firstname.message}</span>}
        </div>

        
         <div className={styles.formGroup}>
          <label htmlFor="lastname">Last Name</label>
          <input
            type="text"
            id="lastname"
            {...register('lastname', { required: 'Your last name is important too!', minLength: { value: 3, message: '3 characters minimum' } })}
            className={errors.lastname ? styles.inputError : ''}
          />
          {errors.lastname && <span className={styles.validationMessage}>{errors.lastname.message}</span>}
        </div>

        
         <div className={styles.formGroup}>
          <label htmlFor="nickname">Nickname</label>
          <input
            type="text"
            id="nickname"
            {...register('nickname', { required: 'How should we call you?', minLength: { value: 3, message: 'Make it at least 3 characters' } })}
            className={errors.nickname ? styles.inputError : ''}
          />
          {errors.nickname && <span className={styles.validationMessage}>{errors.nickname.message}</span>}
        </div>

        
        <div className={styles.formGroup}>
            <label htmlFor="dob">Date of Birth</label>
            <input 
                type="date" 
                id="dob" 
                {...register('dob', { required: 'We need to know your birthday!' })} 
                className={errors.dob ? styles.inputError : ''}
            />
            {errors.dob && <span className={styles.validationMessage}>{errors.dob.message}</span>}
        </div>

        
        <div className={styles.formGroup}>
            <label htmlFor="sex">Sex</label>
            <select 
                id="sex" 
                {...register('sex', { required: 'Please select your gender' })} 
                className={errors.sex ? styles.inputError : ''}
            >
                <option value="">Select...</option>
                <option value="M">Man</option>
                <option value="F">Woman</option>
                <option value="O">Other</option>
            </select>
            {errors.sex && <span className={styles.validationMessage}>{errors.sex.message}</span>}
        </div>

        
        <div className={styles.formGroup}>
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            {...register('email', { required: 'Email is required', pattern: { value: /^\S+@\S+$/i, message: 'Please enter a valid email' } })}
            className={errors.email ? styles.inputError : ''}
          />
          {errors.email && <span className={styles.validationMessage}>{errors.email.message}</span>}
        </div>

        
         <div className={styles.formGroup}>
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            {...register('password', { required: 'Password is required', minLength: { value: 8, message: 'At least 8 characters please' } })}
            className={errors.password ? styles.inputError : ''}
          />
          {errors.password && <span className={styles.validationMessage}>{errors.password.message}</span>}
        </div>

        
        <div className={styles.formGroup}>
            <label htmlFor="phone">Phone</label>
            <input 
                type="tel" 
                id="phone" 
                {...register('phone', { required: 'Phone number is required', minLength: { value: 10, message: 'At least 10 digits please' } })} 
                className={errors.phone ? styles.inputError : ''}
            />
            {errors.phone && <span className={styles.validationMessage}>{errors.phone.message}</span>}
        </div>

        
        <div className={styles.formGroup}>
            <label htmlFor="role">Role</label>
            <select 
                id="role" 
                {...register('role', { required: 'Please select a role' })} 
                className={errors.role ? styles.inputError : ''}
            >
                <option value="">Select...</option>
                <option value={Role.ADMIN}>Admin</option>
                <option value={Role.USER}>User</option>
                <option value={Role.MANAGER}>Manager</option>
            </select>
            {errors.role && <span className={styles.validationMessage}>{errors.role.message}</span>}
        </div>

        
         <div className={styles.formGroup}>
            <label htmlFor="profilePicture">Profile Image (Optional)</label>
            <Controller
                name="profilePicture"
                control={control}
                render={({ field: { onChange, onBlur, name, ref } }) => (
                    <input 
                        type="file" 
                        id="profilePicture"
                        accept=".jpg, .jpeg, .png"
                        onChange={(e) => onChange(e.target.files ? e.target.files[0] : null)}
                        onBlur={onBlur}
                        name={name}
                        ref={ref}
                        className={styles.fileInput} 
                    />
                )}
             />
        </div>

        
         <div className={styles.formGroup}>
          <label htmlFor="bio">Bio (Optional)</label>
          <textarea
            id="bio"
            {...register('bio', { maxLength: 500 })}
            rows={5}
             placeholder="Tell us a bit about yourself..."
             className={errors.bio ? styles.inputError : ''}
          />
          {errors.bio && <span className={styles.validationMessage}>{errors.bio.message}</span>}
        </div>

        <button type="submit" disabled={!isValid} className={styles.submitBtn}>
          Register
        </button>
      </form>
    </div>
  );
};

export default Register;
