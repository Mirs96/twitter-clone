import React, { useState, useEffect, useCallback } from 'react';
import { useForm, SubmitHandler, Controller } from 'react-hook-form';
import Compressor from 'compressorjs';
import { UpdateProfilePayload } from '../../../types/user/UpdateProfilePayload';
import { updateProfile, getUserProfile } from '../../../services/userProfileService';
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
    watch, // <--- Add watch here
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

  const avatarValue = watch('avatar'); // Watch the avatar field

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
    const originalFile = event.target.files?.[0];
    if (!originalFile) {
      setAvatarPreview(userProfileDetails?.profilePicture ? `${HttpConfig.baseUrl}${userProfileDetails.profilePicture}` : null);
      setValue('avatar', null, { shouldDirty: true, shouldValidate: true });
      return;
    }

    if (!originalFile.type.startsWith('image/')) {
      alert('Please select an image file.');
      return;
    }

    new Compressor(originalFile, {
      quality: 0.6,
      maxWidth: 800,
      maxHeight: 800,
      success: (compressedResult) => {
        const finalFile = new File(
          [compressedResult],
          originalFile.name,
          {
            type: compressedResult.type || originalFile.type,
          }
        );

        const reader = new FileReader();
        reader.onloadend = () => {
          setAvatarPreview(reader.result as string);
        };
        reader.readAsDataURL(finalFile);
        setValue('avatar', finalFile, { shouldDirty: true, shouldValidate: true });
      },
      error: (err) => {
        console.error('Compression error:', err.message);
        alert('Could not process image. Please try another one.');
        setAvatarPreview(userProfileDetails?.profilePicture ? `${HttpConfig.baseUrl}${userProfileDetails.profilePicture}` : null);
        setValue('avatar', null, { shouldDirty: false, shouldValidate: true });
      },
    });
  };

  const onSubmit: SubmitHandler<UpdateProfilePayload> = async (formDataValues) => { // Renamed 'data' to 'formDataValues'
    if (!userId) {
      console.error('User ID not found');
      return;
    }

    const avatarChanged = !!formDataValues.avatar;

    // Check if any field has changed: either 'bio' is dirty or 'avatar' has a new value.
    // 'isDirty' from RHF only tracks registered fields that have changed from their default/reset values.
    // If 'avatar' was initially null and a new file is selected, 'avatarChanged' will be true.
    if (!isDirty && !avatarChanged) {
        onProfileUpdated();
        return;
    }

    setIsProcessing(true);
    const formData = new FormData();
    formData.append('bio', formDataValues.bio || '');

    if (avatarChanged && formDataValues.avatar) {
      formData.append('avatar', formDataValues.avatar);
    }

    try {
      await updateProfile(formData, userId);
      dispatch(fetchUserDetails(userId));
      onProfileUpdated();
    } catch (error) {
      console.error('Failed to update profile:', error);
      alert('Failed to update profile. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  };

  if (isLoadingProfile) {
    return <div>Loading profile data...</div>;
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
              <img src='/icons/default-avatar.png' alt="Default Avatar" className={styles.avatarPreview} />
            )}
          </label>
          <Controller
            name="avatar"
            control={control}
            render={({ field: { onBlur, name, ref } }) => ( // Removed onChange as handleFileChange calls setValue
              <input
                type="file"
                id="avatar"
                accept="image/*"
                onChange={(e) => {
                    handleFileChange(e);
                }}
                onBlur={onBlur}
                name={name}
                ref={ref}
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
            // Corrected disabled logic:
            // Button is disabled if:
            // 1. No fields are dirty (isDirty is false) AND no new avatar has been selected (avatarValue is null/falsy)
            // OR
            // 2. The form is not valid (e.g., bio exceeds max length)
            // OR
            // 3. An update is currently processing
            disabled={(!isDirty && !avatarValue) || !isValid || isProcessing}
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