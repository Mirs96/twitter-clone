import React, { useState, useEffect, useCallback } from 'react';
import { useForm, SubmitHandler, Controller } from 'react-hook-form';
import Compressor from 'compressorjs';
import { UpdateProfilePayload } from '../../../types/user/UpdateProfilePayload';
import { updateProfile, getUserProfile } from '../../../services/userProfileService'; // Import getUserProfile
import { HttpConfig } from '../../../config/http-config';
import styles from './UpdateProfile.module.css';
import { UserProfileDetails } from '../../../types/user/userProfileDetails';
import { useSelector, useDispatch } from 'react-redux';
import { selectUserId } from '../../../store/slices/authSlice';
import { fetchUserDetails } from '../../../store/slices/userSlice';
import { AppDispatch } from '../../../store/store';

interface UpdateProfileProps {
  onProfileUpdated: () => void;
}

const UpdateProfile: React.FC<UpdateProfileProps> = ({ onProfileUpdated }) => {
  const userId = useSelector(selectUserId);
  const dispatch = useDispatch<AppDispatch>();
  const [userProfileDetails, setUserProfileDetails] = useState<UserProfileDetails | null>(null);
  const [isLoadingProfile, setIsLoadingProfile] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    setValue,
    reset,
    formState: { errors, isDirty, isValid }
  } = useForm<UpdateProfilePayload>({
    mode: 'onChange',
    defaultValues: {
      bio: '',
      avatar: null
    }
  });

  const [avatarPreview, setAvatarPreview] = useState<string | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);

  // Fetch current profile details to populate the form
  const fetchCurrentProfile = useCallback(async () => {
    if (!userId) return;
    setIsLoadingProfile(true);
    try {
      const profileData = await getUserProfile(userId);
      setUserProfileDetails(profileData);
      // Reset form with fetched data
      reset({
        bio: profileData.bio || '',
        avatar: null // Don't pre-fill file input
      });
      if (profileData.profilePicture) {
        setAvatarPreview(`${HttpConfig.baseUrl}${profileData.profilePicture}`);
      } else {
        setAvatarPreview(null); // Ensure preview is cleared if no picture
      }
    } catch (error) {
      console.error('Failed to fetch profile details:', error);
    } finally {
      setIsLoadingProfile(false);
    }
  }, [userId, reset]);

  useEffect(() => {
    fetchCurrentProfile();
  }, [fetchCurrentProfile]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) {
      // If user cancels file selection, revert to original or clear preview if desired
      // Revert preview to original fetched profile picture
      setAvatarPreview(userProfileDetails?.profilePicture ? `${HttpConfig.baseUrl}${userProfileDetails.profilePicture}` : null);
      setValue('avatar', null, { shouldDirty: true }); // Mark as dirty if they clear it
      return;
    }

    if (!file.type.startsWith('image/')) {
      alert('Please select an image file.');
      return;
    }

    new Compressor(file, {
      quality: 0.6,
      maxWidth: 800,
      maxHeight: 800,
      success: (compressedResult) => {
        const compressedFile = compressedResult as File;
        const reader = new FileReader();
        reader.onloadend = () => {
          setAvatarPreview(reader.result as string);
        };
        reader.readAsDataURL(compressedFile);
        setValue('avatar', compressedFile, { shouldDirty: true, shouldValidate: true });
      },
      error: (err) => {
        console.error('Compression error:', err.message);
        alert('Could not process image. Please try another one.');
      },
    });
  };

  const onSubmit: SubmitHandler<UpdateProfilePayload> = async (data) => {
    if (!userId) {
      console.error('User ID not found');
      return;
    }

    const avatarChanged = !!data.avatar; // True if a new file is selected

    if (!isDirty && !avatarChanged) { // Use isDirty for bio, explicit check for avatar
        // console.log('No changes detected.');
        onProfileUpdated(); // Close modal even if no changes
        return;
    }

    setIsProcessing(true);
    const formData = new FormData();

    // Only append fields that are part of the change
    // Backend should handle null/empty bio correctly if needed
    formData.append('bio', data.bio || '');

    if (avatarChanged) {
      formData.append('avatar', data.avatar as Blob);
    }

    try {
      await updateProfile(formData, userId);
      // Refresh user details in the Redux store after successful update
      dispatch(fetchUserDetails(userId));
      onProfileUpdated(); // Callback to close modal/refresh profile page
    } catch (error) {
      console.error('Failed to update profile:', error);
      alert('Failed to update profile. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  };

  if (isLoadingProfile) {
    return <div>Loading profile data...</div>; // Or a spinner
  }

  return (
    <div className={styles.profileUpdateContainer}>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className={styles.avatarContainer}>
          <p className={styles.chooseAvatarText}>Pick a profile picture</p>
          <label htmlFor="avatar" className={styles.avatarLabel}>
            {avatarPreview ? (
              <img src={avatarPreview} alt="Avatar Preview" className={styles.avatarPreview} />
            ) : (
              // Better placeholder or use a default avatar icon
              <img src='/icons/default-avatar.png' alt="Default Avatar" className={styles.avatarPreview} />
            )}
          </label>
          <Controller
            name="avatar"
            control={control}
            render={() => (
              <input
                type="file"
                id="avatar"
                accept="image/*"
                onChange={handleFileChange}
                className={styles.fileInput}
              />
            )}
          />
        </div>

        <div className={styles.bioContainer}>
          <label htmlFor="bio">Describe yourself</label>
          <textarea
            id="bio"
            {...register('bio', {
              maxLength: { value: 500, message: 'Bio cannot exceed 500 characters' }
            })}
            rows={4}
            placeholder="What makes you special? Don't think too hard, just have fun with it."
            className={errors.bio ? styles.inputError : ''}
          />
          {errors.bio && <span className={styles.validationMessage}>{errors.bio.message}</span>}
        </div>

        <div className={styles.formActions}>
          <button
            type="submit"
            disabled={!isDirty || !isValid || isProcessing} // Keep !isValid check
            className={styles.submitButton}
          >
            {isProcessing ? 'Saving...' : 'Save Changes'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default UpdateProfile;
