import React, { useRef, useEffect } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { CreateTweetDetails } from '../../../types/tweet/createTweetDetails';
import styles from './CreateTweet.module.css';
import { HttpConfig } from '../../../config/http-config';
import { createTweet } from '../../../services/tweetService';
import { useSelector } from 'react-redux';
import { selectUserDetails } from '../../../store/slices/userSlice';
import { selectUserId } from '../../../store/slices/authSlice';

interface CreateTweetProps {
   onTweetCreated?: () => void; 
}

interface FormData {
  content: string;
}

const CreateTweet: React.FC<CreateTweetProps> = ({ onTweetCreated }) => {
  const userId = useSelector(selectUserId);
  const userDetails = useSelector(selectUserDetails);
  const { register, handleSubmit, reset, watch, formState: { isValid } } = useForm<FormData>({ mode: 'onChange' });
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);
  const contentValue = watch('content'); 

  const adjustTextareaHeight = () => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto'; 
      textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;
    }
  };
  
  useEffect(() => {
    adjustTextareaHeight();
  }, [contentValue]);

  const onSubmit: SubmitHandler<FormData> = async (data) => {
    if (!userId) {
      console.error('User not logged in');
      return;
    }

    const payload: CreateTweetDetails = {
      ...data,
      userId: userId
    };

    try {
      await createTweet(payload);
      reset(); 
      if (onTweetCreated) onTweetCreated(); 
      
    } catch (error) {
      console.error('Failed to post tweet:', error);
      alert('Could not post tweet. Please try again.');
    }
  };

  // Use userDetails from Redux for the profile picture
  const profilePictureUrl = userDetails?.profilePicture
      ? `${HttpConfig.baseUrl}${userDetails.profilePicture}`
      : '/icons/default-avatar.png';

  return (
    <div className={styles.createTweetContainer}>
        <div className={styles.inputArea}>
            <img 
                src={profilePictureUrl}
                alt="Your profile picture"
                className={styles.profilePicture}
            />
            <form onSubmit={handleSubmit(onSubmit)} className={styles.tweetForm}>
                <textarea
                    {...register('content', { required: true, maxLength: 1000 })}
                    rows={1}
                    placeholder="What is happening?!"
                    className={styles.textarea}
                    ref={(e) => {
                    register('content').ref(e);
                    textareaRef.current = e; 
                    }}
                />
                 
                <div className={styles.actionsContainer}>            
                    <div className={styles.iconsPlaceholder}></div>
                    <button type="submit" disabled={!isValid} className={styles.submitButton}>
                        Post
                    </button>
                 </div>
            </form>
        </div>
    </div>
  );
};


export default CreateTweet;
