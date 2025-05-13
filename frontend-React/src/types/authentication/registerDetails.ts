import { Role } from "./role";

export interface RegisterFormData {
    firstname: string;
    lastname: string;
    nickname: string;
    dob: string;
    sex: string;
    email: string;
    password: string;
    phone: string;
    role: Role | '';
    profilePicture?: File | null;
    bio?: string;
}

export interface RegisterPayload {
    firstname: string;
    lastname: string;
    nickname: string;
    dob: string;
    sex: string;
    email: string;
    password: string;
    phone: string;
    role: Role;
    profilePicture?: string | null; // Backend might handle this from FormData
    bio?: string;
    createdAt: string;
}