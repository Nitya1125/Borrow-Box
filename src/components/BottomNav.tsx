import React from 'react';
import { HomeIcon, ExploreIcon, AddIcon, ActivityIcon, ProfileIcon } from './Icons';

type Screen = 'home' | 'explore' | 'add' | 'activity' | 'profile';

interface BottomNavProps {
  active: Screen;
  onNavigate: (screen: Screen) => void;
}

const tabs = [
  { id: 'home' as Screen, label: 'Home', Icon: HomeIcon },
  { id: 'explore' as Screen, label: 'Explore', Icon: ExploreIcon },
  { id: 'add' as Screen, label: 'Add', Icon: AddIcon },
  { id: 'activity' as Screen, label: 'Activity', Icon: ActivityIcon },
  { id: 'profile' as Screen, label: 'Profile', Icon: ProfileIcon },
];

export const BottomNav = ({ active, onNavigate }: BottomNavProps) => {
  return (
    <div
      className="flex-shrink-0 flex items-center justify-around px-2"
      style={{
        height: 72,
        background: '#FFFFFF',
        borderTop: '1px solid #E5E8F2',
        boxShadow: '0 -4px 20px rgba(79, 106, 240, 0.06)',
      }}
    >
      {tabs.map(({ id, label, Icon }) => {
        const isActive = active === id;
        const isAdd = id === 'add';

        if (isAdd) {
          return (
            <button
              key={id}
              onClick={() => onNavigate(id)}
              className="flex items-center justify-center ripple"
              style={{
                width: 56,
                height: 56,
                borderRadius: 18,
                background: 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
                boxShadow: '0 4px 16px rgba(79, 106, 240, 0.38)',
                transform: isActive ? 'scale(0.95)' : 'scale(1)',
                transition: 'transform 0.15s ease',
              }}
            >
              <Icon size={24} color="white" />
            </button>
          );
        }

        return (
          <button
            key={id}
            onClick={() => onNavigate(id)}
            className="flex flex-col items-center gap-0.5 py-2 px-3"
            style={{ minWidth: 56 }}
          >
            <div
              className="flex items-center justify-center rounded-2xl transition-all duration-200"
              style={{
                width: 44,
                height: 28,
                background: isActive ? 'var(--bb-primary-light)' : 'transparent',
              }}
            >
              <Icon
                size={22}
                color={isActive ? 'var(--bb-primary)' : 'var(--bb-text3)'}
              />
            </div>
            <span
              style={{
                fontSize: 10,
                fontWeight: isActive ? 700 : 500,
                color: isActive ? 'var(--bb-primary)' : 'var(--bb-text3)',
                transition: 'color 0.2s ease',
              }}
            >
              {label}
            </span>
          </button>
        );
      })}
    </div>
  );
};
