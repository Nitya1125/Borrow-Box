import React from 'react';

interface IconProps {
  size?: number;
  color?: string;
  className?: string;
}

export const HomeIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M3 9.5L12 3L21 9.5V20C21 20.55 20.55 21 20 21H15V15H9V21H4C3.45 21 3 20.55 3 20V9.5Z" fill={color} />
  </svg>
);

export const ExploreIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <circle cx="11" cy="11" r="7.5" stroke={color} strokeWidth="2" />
    <path d="M16.5 16.5L21 21" stroke={color} strokeWidth="2" strokeLinecap="round" />
  </svg>
);

export const AddIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M12 5V19M5 12H19" stroke={color} strokeWidth="2.5" strokeLinecap="round" />
  </svg>
);

export const ActivityIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <rect x="3" y="3" width="8" height="8" rx="2" stroke={color} strokeWidth="1.8" />
    <rect x="13" y="3" width="8" height="8" rx="2" stroke={color} strokeWidth="1.8" />
    <rect x="3" y="13" width="8" height="8" rx="2" stroke={color} strokeWidth="1.8" />
    <path d="M13 17H21M17 13V21" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const ProfileIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <circle cx="12" cy="8" r="4" stroke={color} strokeWidth="1.8" />
    <path d="M4 20C4 16.686 7.582 14 12 14C16.418 14 20 16.686 20 20" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const BellIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M12 2C8.686 2 6 4.686 6 8V13L4 15V16H20V15L18 13V8C18 4.686 15.314 2 12 2Z" stroke={color} strokeWidth="1.8" strokeLinejoin="round" />
    <path d="M10 16C10 17.105 10.895 18 12 18C13.105 18 14 17.105 14 16" stroke={color} strokeWidth="1.8" />
  </svg>
);

export const SearchIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <circle cx="11" cy="11" r="7" stroke={color} strokeWidth="1.8" />
    <path d="M16 16L21 21" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const BookmarkIcon = ({ size = 24, color = 'currentColor', filled = false, className = '' }: IconProps & { filled?: boolean }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill={filled ? color : 'none'} className={className}>
    <path d="M5 3H19C19.55 3 20 3.45 20 4V21L12 17L4 21V4C4 3.45 4.45 3 5 3Z" stroke={color} strokeWidth="1.8" strokeLinejoin="round" />
  </svg>
);

export const StarIcon = ({ size = 24, color = 'currentColor', filled = false, className = '' }: IconProps & { filled?: boolean }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill={filled ? color : 'none'} className={className}>
    <path d="M12 2L14.85 9.3L22.5 9.3L16.5 13.9L18.85 21.1L12 16.5L5.15 21.1L7.5 13.9L1.5 9.3L9.15 9.3L12 2Z" stroke={color} strokeWidth="1.5" strokeLinejoin="round" />
  </svg>
);

export const BackIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M15 18L9 12L15 6" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const ChevronRightIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M9 6L15 12L9 18" stroke={color} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const FilterIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M4 6H20M7 12H17M10 18H14" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const CheckIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M5 12L10 17L19 8" stroke={color} strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="check-draw" />
  </svg>
);

export const CloseIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M18 6L6 18M6 6L18 18" stroke={color} strokeWidth="2" strokeLinecap="round" />
  </svg>
);

export const CameraIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <rect x="2" y="7" width="20" height="15" rx="2.5" stroke={color} strokeWidth="1.8" />
    <circle cx="12" cy="14" r="4" stroke={color} strokeWidth="1.8" />
    <path d="M8 7L9.5 4H14.5L16 7" stroke={color} strokeWidth="1.8" strokeLinejoin="round" />
  </svg>
);

export const QRIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <rect x="3" y="3" width="7" height="7" rx="1" stroke={color} strokeWidth="1.8" />
    <rect x="14" y="3" width="7" height="7" rx="1" stroke={color} strokeWidth="1.8" />
    <rect x="3" y="14" width="7" height="7" rx="1" stroke={color} strokeWidth="1.8" />
    <rect x="14" y="14" width="3" height="3" fill={color} />
    <rect x="18" y="14" width="3" height="3" fill={color} />
    <rect x="14" y="18" width="3" height="3" fill={color} />
    <rect x="5" y="5" width="3" height="3" fill={color} />
    <rect x="16" y="5" width="3" height="3" fill={color} />
    <rect x="5" y="16" width="3" height="3" fill={color} />
  </svg>
);

export const LocationIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M12 2C8.686 2 6 4.686 6 8C6 13 12 21 12 21C12 21 18 13 18 8C18 4.686 15.314 2 12 2Z" stroke={color} strokeWidth="1.8" />
    <circle cx="12" cy="8" r="2.5" stroke={color} strokeWidth="1.8" />
  </svg>
);

export const CalendarIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <rect x="3" y="5" width="18" height="17" rx="2" stroke={color} strokeWidth="1.8" />
    <path d="M3 10H21M8 3V7M16 3V7" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const ShareIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <circle cx="18" cy="5" r="3" stroke={color} strokeWidth="1.8" />
    <circle cx="6" cy="12" r="3" stroke={color} strokeWidth="1.8" />
    <circle cx="18" cy="19" r="3" stroke={color} strokeWidth="1.8" />
    <path d="M9 10.5L15 6.5M9 13.5L15 17.5" stroke={color} strokeWidth="1.8" />
  </svg>
);

export const ArrowsIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M4 12C4 7.582 7.582 4 12 4C14.761 4 17.197 5.347 18.732 7.414M20 12C20 16.418 16.418 20 12 20C9.239 20 6.803 18.653 5.268 16.586" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
    <path d="M20 8L18.5 7.5L19 4" stroke={color} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
    <path d="M4 16L5.5 16.5L5 20" stroke={color} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const SettingsIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <circle cx="12" cy="12" r="3" stroke={color} strokeWidth="1.8" />
    <path d="M12 2V4M12 20V22M2 12H4M20 12H22M4.929 4.929L6.343 6.343M17.657 17.657L19.071 19.071M19.071 4.929L17.657 6.343M6.343 17.657L4.929 19.071" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const HeartIcon = ({ size = 24, color = 'currentColor', filled = false, className = '' }: IconProps & { filled?: boolean }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill={filled ? color : 'none'} className={className}>
    <path d="M12 21C12 21 3 14.5 3 8.5C3 5.42 5.42 3 8.5 3C10.24 3 11.91 3.81 13 5.08C14.09 3.81 15.76 3 17.5 3C20.58 3 23 5.42 23 8.5C23 14.5 12 21 12 21Z" stroke={color} strokeWidth="1.8" strokeLinejoin="round" />
  </svg>
);

export const LogoutIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M9 21H5C4.47 21 3.96 20.79 3.59 20.41C3.21 20.04 3 19.53 3 19V5C3 4.47 3.21 3.96 3.59 3.59C3.96 3.21 4.47 3 5 3H9" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
    <path d="M16 17L21 12L16 7M21 12H9" stroke={color} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const HelpIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <circle cx="12" cy="12" r="9" stroke={color} strokeWidth="1.8" />
    <path d="M9.5 9.5C9.5 8.12 10.62 7 12 7C13.38 7 14.5 8.12 14.5 9.5C14.5 10.88 13.38 12 12 12V14" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
    <circle cx="12" cy="17" r="0.5" fill={color} stroke={color} strokeWidth="0.5" />
  </svg>
);

export const EyeIcon = ({ size = 24, color = 'currentColor', closed = false, className = '' }: IconProps & { closed?: boolean }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    {closed ? (
      <>
        <path d="M3 3L21 21M10.5 10.5C10.19 10.81 10 11.23 10 11.7C10 12.74 10.895 13.62 12 13.62C12.47 13.62 12.9 13.44 13.22 13.14" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
        <path d="M6 6.5C4.5 7.8 3.5 9.3 3 12C4.5 17 8 20 12 20C14.5 20 16.8 18.8 18.5 17" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
        <path d="M9.5 4.4C10.3 4.15 11.14 4 12 4C16 4 19.5 7 21 12C20.6 13.4 20 14.7 19.2 15.8" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
      </>
    ) : (
      <>
        <path d="M3 12C4.5 7 8 4 12 4C16 4 19.5 7 21 12C19.5 17 16 20 12 20C8 20 4.5 17 3 12Z" stroke={color} strokeWidth="1.8" />
        <circle cx="12" cy="12" r="3" stroke={color} strokeWidth="1.8" />
      </>
    )}
  </svg>
);

export const PackageIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <path d="M12 2L20 7V17L12 22L4 17V7L12 2Z" stroke={color} strokeWidth="1.8" strokeLinejoin="round" />
    <path d="M12 2V22M4 7L12 12L20 7" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
    <path d="M8 9.5L16 4.5" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const TimelineIcon = ({ size = 24, color = 'currentColor', className = '' }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className}>
    <circle cx="12" cy="5" r="2" fill={color} />
    <circle cx="12" cy="12" r="2" fill={color} />
    <circle cx="12" cy="19" r="2" fill={color} />
    <path d="M12 7V10M12 14V17" stroke={color} strokeWidth="1.8" strokeLinecap="round" />
  </svg>
);

export const BorrowBoxLogo = ({ size = 48, className = '' }: { size?: number; className?: string }) => (
  <svg width={size} height={size} viewBox="0 0 48 48" fill="none" className={className}>
    <rect width="48" height="48" rx="14" fill="url(#bb-grad)" />
    <rect x="10" y="16" width="28" height="22" rx="4" fill="white" fillOpacity="0.2" stroke="white" strokeWidth="1.5" />
    <path d="M10 22H38" stroke="white" strokeWidth="1.5" />
    <path d="M24 11C24 11 19 14 24 19C29 14 24 11 24 11Z" fill="white" />
    <path d="M18 30C18 28.9 18.9 28 20 28H28C29.1 28 30 28.9 30 30V33H18V30Z" fill="white" fillOpacity="0.9" />
    <path d="M21 26H27" stroke="white" strokeWidth="1.5" strokeLinecap="round" />
    <defs>
      <linearGradient id="bb-grad" x1="0" y1="0" x2="48" y2="48" gradientUnits="userSpaceOnUse">
        <stop stopColor="#4F6AF0" />
        <stop offset="1" stopColor="#7B61FF" />
      </linearGradient>
    </defs>
  </svg>
);
