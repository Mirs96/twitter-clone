export interface UpdateProfilePayload {
    bio?: string;
    avatar?: File | null; // Use File for upload, backend handles storage
}
