import React, { useRef, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { ReplyDetails } from '../../../types/reply/replyDetails';
import { CreateReplyDetails } from '../../../types/reply/createReplyDetails';
import styles from './CreateReply.module.css';
import { replyToTweet } from '../../../services/replyService';
import { useSelector } from 'react-redux';
import { selectUserId } from '../../../store/slices/authSlice';

interface CreateReplyProps {
  tweetId: number;
  parentReplyId?: number | null;
  onReplyCreated: (newReply: ReplyDetails) => void;
}

interface FormData {
  content: string;
}

const CreateReply: React.FC<CreateReplyProps> = ({ tweetId, parentReplyId = null, onReplyCreated }) => {
  const userId = useSelector(selectUserId);
  const { register, handleSubmit, reset, watch, formState: { isValid } } = useForm<FormData>({
     mode: 'onChange' 
  });
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);
  const contentValue = watch('content');

  const adjustTextareaHeight = () => {
    if (textareaRef.current) {
      textareaRef.current.style.height = '25px'; 
      textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;
    }
  };

  
  useEffect(() => {
    adjustTextareaHeight();
  }, [contentValue]);

  const onSubmit = async (data: FormData) => {
    if (!userId) {
        console.error('User not logged in');
        
        return;
    }

    const payload: CreateReplyDetails = {
      ...data,
      userId: userId,
      tweetId: tweetId,
      parentReplyId: parentReplyId
    };

    try {
      const newReply = await replyToTweet(payload);
      onReplyCreated(newReply);
      reset(); 
      
    } catch (error) {
      console.error('Failed to post reply:', error);
    }
  };

  return (
    <div className={styles.replyContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className={styles.replyForm}>
        <div className={styles.textareaWrapper}>
          <textarea
            {...register('content', { required: true, maxLength: 500 })}
            rows={1}
            maxLength={1000} 
            placeholder="Post your reply"
            className={styles.textarea}
            ref={(e) => {
              register('content').ref(e);
              textareaRef.current = e; 
            }}
          />
           
        </div>
        <div className={styles.buttonContainer}>
          <button type="submit" disabled={!isValid} className={styles.submitButton}>
            Reply
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateReply;
