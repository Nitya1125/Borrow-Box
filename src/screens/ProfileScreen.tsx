import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { ChevronRightIcon, StarIcon, PackageIcon, SettingsIcon, HelpIcon, LogoutIcon, HeartIcon, TimelineIcon } from '../components/Icons';
import { myItems } from '../data';

interface Props {
  onLogout: () => void;
  onHistory: () => void;
  onMyItems: () => void;
  onRating: () => void;
}

const menuSections = [
  {
    title: 'My Account',
    items: [
      { icon: '📦', label: 'My Items', color: '#4F6AF0', bg: '#EEF1FF' },
      { icon: '📋', label: 'Borrowing History', color: '#3BB8A2', bg: '#E6F7F4' },
      { icon: '❤️', label: 'Wishlist', color: '#FF7A7A', bg: '#FFF0F0' },
      { icon: '⭐', label: 'Reviews', color: '#F5C842', bg: '#FEF8E7' },
    ],
  },
  {
    title: 'Support',
    items: [
      { icon: '⚙️', label: 'Settings', color: '#9BA3B8', bg: '#F4F6FF' },
      { icon: '❓', label: 'Help & Support', color: '#9585E0', bg: '#F0EDFC' },
    ],
  },
];

export const ProfileScreen = ({ onLogout, onHistory, onMyItems, onRating }: Props) => {
  const [activeTab, setActiveTab] = useState('available');
  const filteredItems = myItems.filter(i => i.status === activeTab);

  const handleMenuPress = (label: string) => {
    if (label === 'Borrowing History') onHistory();
    else if (label === 'My Items') onMyItems();
    else if (label === 'Reviews') onRating();
  };

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      <div className="flex-1 screen-scroll pb-4">
        {/* Profile header */}
        <div
          className="px-5 pt-4 pb-8"
          style={{
            background: 'linear-gradient(160deg, #4F6AF0 0%, #7B61FF 100%)',
            borderBottomLeftRadius: 32,
            borderBottomRightRadius: 32,
          }}
        >
          <div className="flex items-center gap-4 mb-6">
            <div className="relative">
              <img
                src="https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=120&h=120&fit=crop&auto=format"
                alt="Profile"
                style={{ width: 72, height: 72, borderRadius: 22, objectFit: 'cover', border: '3px solid rgba(255,255,255,0.5)' }}
              />
              <div
                style={{
                  position: 'absolute',
                  bottom: -2,
                  right: -2,
                  width: 22,
                  height: 22,
                  borderRadius: '50%',
                  background: '#3BB8A2',
                  border: '2px solid white',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <span style={{ fontSize: 10 }}>✓</span>
              </div>
            </div>
            <div>
              <h2 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>Nitya Kapoor</h2>
              <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.75)', margin: '2px 0 4px' }}>BITS Pilani, 3rd Year</p>
              <div className="flex items-center gap-1">
                {[1, 2, 3, 4, 5].map(i => (
                  <StarIcon key={i} size={12} color="#F5C842" filled={i <= 4} />
                ))}
                <span style={{ fontSize: 12, fontWeight: 700, color: 'white', marginLeft: 4 }}>4.9</span>
              </div>
            </div>
          </div>

          {/* Stats */}
          <div
            className="flex items-center justify-around p-4"
            style={{
              background: 'rgba(255,255,255,0.15)',
              borderRadius: 20,
              backdropFilter: 'blur(8px)',
              border: '1px solid rgba(255,255,255,0.25)',
            }}
          >
            {[
              { value: '12', label: 'Items Lent' },
              { value: '8', label: 'Borrowed' },
              { value: '19', label: 'Returns' },
            ].map(({ value, label }) => (
              <div key={label} className="flex flex-col items-center gap-0.5">
                <span style={{ fontSize: 22, fontWeight: 800, color: 'white' }}>{value}</span>
                <span style={{ fontSize: 11, color: 'rgba(255,255,255,0.7)', fontWeight: 500 }}>{label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Menu sections */}
        <div className="px-5 pt-5">
          {menuSections.map(section => (
            <div key={section.title} className="mb-5">
              <h3 style={{ fontSize: 12, fontWeight: 700, color: '#9BA3B8', letterSpacing: 1, marginBottom: 10, textTransform: 'uppercase' }}>
                {section.title}
              </h3>
              <div
                style={{
                  background: 'white',
                  borderRadius: 20,
                  border: '1px solid #F0F2FA',
                  overflow: 'hidden',
                }}
              >
                {section.items.map((item, i) => (
                  <button
                    key={item.label}
                    onClick={() => handleMenuPress(item.label)}
                    className="w-full flex items-center gap-4 px-4 py-4 card-press"
                    style={{
                      background: 'white',
                      border: 'none',
                      cursor: 'pointer',
                      borderBottom: i < section.items.length - 1 ? '1px solid #F0F2FA' : 'none',
                    }}
                  >
                    <div
                      style={{
                        width: 40,
                        height: 40,
                        borderRadius: 12,
                        background: item.bg,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: 18,
                        flexShrink: 0,
                      }}
                    >
                      {item.icon}
                    </div>
                    <span style={{ flex: 1, fontSize: 15, fontWeight: 600, color: '#1C2135', textAlign: 'left' }}>
                      {item.label}
                    </span>
                    <ChevronRightIcon size={18} color="#C5C9D8" />
                  </button>
                ))}
              </div>
            </div>
          ))}

          {/* Logout */}
          <button
            onClick={onLogout}
            className="w-full flex items-center justify-center gap-3"
            style={{
              height: 52,
              borderRadius: 16,
              background: '#FFF0F0',
              border: '1.5px solid #FFCECE',
              cursor: 'pointer',
              marginBottom: 8,
            }}
          >
            <LogoutIcon size={20} color="#FF7A7A" />
            <span style={{ fontSize: 15, fontWeight: 700, color: '#FF7A7A' }}>Log Out</span>
          </button>

          <p style={{ textAlign: 'center', fontSize: 12, color: '#9BA3B8', marginBottom: 8 }}>
            BorrowBox v1.0.0 · Made with 💙 for students
          </p>
        </div>
      </div>
    </div>
  );
};
