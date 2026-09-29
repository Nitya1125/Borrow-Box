import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { BellIcon, SearchIcon, BookmarkIcon, LocationIcon } from '../components/Icons';
import { items, categories } from '../data';
import type { Item } from '../data';

interface Props {
  onSearch: () => void;
  onItem: (item: Item) => void;
  onNotifications: () => void;
}

const StatusBadge = ({ available }: { available: boolean }) => (
  <span
    style={{
      fontSize: 10,
      fontWeight: 700,
      color: available ? '#3BB8A2' : '#FF7A7A',
      background: available ? '#E6F7F4' : '#FFF0F0',
      borderRadius: 8,
      padding: '2px 8px',
    }}
  >
    {available ? 'Available' : 'Borrowed'}
  </span>
);

const ItemCard = ({ item, onPress }: { item: Item; onPress: () => void }) => {
  const [saved, setSaved] = useState(false);
  return (
    <div
      className="card-press"
      onClick={onPress}
      style={{
        background: 'white',
        borderRadius: 20,
        overflow: 'hidden',
        boxShadow: 'var(--bb-shadow-sm)',
        border: '1px solid #F0F2FA',
      }}
    >
      <div className="relative" style={{ height: 130, background: '#EEF1FF' }}>
        <img
          src={item.image}
          alt={item.name}
          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
        />
        <button
          onClick={e => { e.stopPropagation(); setSaved(!saved); }}
          className="absolute top-2.5 right-2.5 flex items-center justify-center"
          style={{
            width: 32,
            height: 32,
            borderRadius: 10,
            background: 'rgba(255,255,255,0.9)',
            backdropFilter: 'blur(4px)',
          }}
        >
          <BookmarkIcon size={16} color={saved ? '#4F6AF0' : '#9BA3B8'} filled={saved} />
        </button>
        <div className="absolute bottom-2.5 left-2.5">
          <StatusBadge available={item.available} />
        </div>
      </div>
      <div className="p-3">
        <p style={{ fontSize: 13, fontWeight: 700, color: '#1C2135', marginBottom: 2, lineHeight: 1.3 }}>
          {item.name}
        </p>
        <p style={{ fontSize: 11, color: '#9BA3B8', fontWeight: 500, marginBottom: 6 }}>
          {item.category}
        </p>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1">
            <img
              src={item.ownerAvatar}
              alt={item.owner}
              style={{ width: 18, height: 18, borderRadius: '50%', objectFit: 'cover' }}
            />
            <span style={{ fontSize: 11, color: '#5A607A', fontWeight: 600 }}>{item.owner.split(' ')[0]}</span>
          </div>
          <div className="flex items-center gap-0.5">
            <LocationIcon size={12} color="#9BA3B8" />
            <span style={{ fontSize: 11, color: '#9BA3B8', fontWeight: 500 }}>{item.distance}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export const HomeScreen = ({ onSearch, onItem, onNotifications }: Props) => {
  const [activeCat, setActiveCat] = useState('all');

  const filtered = activeCat === 'all'
    ? items
    : items.filter(i => i.category.toLowerCase() === activeCat.toLowerCase());

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Hero header */}
      <div
        className="px-5 pt-2 pb-6"
        style={{
          background: 'linear-gradient(160deg, #4F6AF0 0%, #6B58E8 100%)',
          borderBottomLeftRadius: 28,
          borderBottomRightRadius: 28,
        }}
      >
        {/* Top row */}
        <div className="flex items-center justify-between mb-5">
          <div>
            <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.75)', fontWeight: 500 }}>Good morning,</p>
            <h2 style={{ fontSize: 22, fontWeight: 800, color: 'white', margin: 0 }}>Nitya 👋</h2>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={onNotifications}
              className="relative flex items-center justify-center"
              style={{ width: 42, height: 42, borderRadius: 14, background: 'rgba(255,255,255,0.18)', backdropFilter: 'blur(8px)' }}
            >
              <BellIcon size={20} color="white" />
              {/* Unread dot */}
              <div style={{ position: 'absolute', top: 9, right: 9, width: 8, height: 8, borderRadius: '50%', background: '#F5C842', border: '2px solid #5B69E8' }} />
            </button>
            <img
              src="https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format"
              alt="Avatar"
              style={{ width: 42, height: 42, borderRadius: 14, objectFit: 'cover', border: '2.5px solid rgba(255,255,255,0.4)' }}
            />
          </div>
        </div>

        {/* Search bar */}
        <button
          onClick={onSearch}
          className="w-full flex items-center gap-3"
          style={{
            height: 50,
            borderRadius: 16,
            background: 'rgba(255,255,255,0.95)',
            padding: '0 16px',
            boxShadow: '0 4px 16px rgba(0,0,0,0.12)',
          }}
        >
          <SearchIcon size={18} color="#9BA3B8" />
          <span style={{ fontSize: 14, color: '#9BA3B8', fontWeight: 500 }}>Find something to borrow...</span>
        </button>
      </div>

      {/* Content */}
      <div className="flex-1 screen-scroll">
        {/* Categories */}
        <div className="pt-5 pb-3">
          <div className="flex items-center justify-between px-5 mb-3">
            <h3 style={{ fontSize: 16, fontWeight: 800, color: '#1C2135', margin: 0 }}>Categories</h3>
          </div>
          <div className="flex gap-2 overflow-x-auto scrollbar-hide px-5 pb-1">
            {categories.map(cat => (
              <button
                key={cat.id}
                onClick={() => setActiveCat(cat.id)}
                className="flex-shrink-0 flex flex-col items-center gap-1.5 px-4 py-2.5"
                style={{
                  borderRadius: 16,
                  background: activeCat === cat.id ? '#4F6AF0' : 'white',
                  border: `1.5px solid ${activeCat === cat.id ? '#4F6AF0' : '#E5E8F2'}`,
                  transition: 'all 0.2s ease',
                  boxShadow: activeCat === cat.id ? '0 4px 12px rgba(79,106,240,0.25)' : 'var(--bb-shadow-sm)',
                }}
              >
                <span style={{ fontSize: 18 }}>{cat.icon}</span>
                <span style={{ fontSize: 11, fontWeight: 700, color: activeCat === cat.id ? 'white' : '#5A607A', whiteSpace: 'nowrap' }}>
                  {cat.label}
                </span>
              </button>
            ))}
          </div>
        </div>

        {/* Stats banner */}
        <div className="px-5 mb-4">
          <div
            className="flex items-center justify-around p-4"
            style={{
              background: 'linear-gradient(135deg, #E6F7F4 0%, #EEF1FF 100%)',
              borderRadius: 20,
              border: '1px solid #E5E8F2',
            }}
          >
            {[
              { label: 'Items Listed', value: '1,248' },
              { label: 'Active Borrows', value: '384' },
              { label: 'Students', value: '2.1k' },
            ].map(({ label, value }) => (
              <div key={label} className="flex flex-col items-center gap-0.5">
                <span style={{ fontSize: 18, fontWeight: 800, color: '#4F6AF0' }}>{value}</span>
                <span style={{ fontSize: 11, color: '#9BA3B8', fontWeight: 500 }}>{label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Recommended */}
        <div className="px-5">
          <div className="flex items-center justify-between mb-3">
            <h3 style={{ fontSize: 16, fontWeight: 800, color: '#1C2135', margin: 0 }}>
              {activeCat === 'all' ? 'Recommended for You' : categories.find(c => c.id === activeCat)?.label}
            </h3>
            <button style={{ fontSize: 13, fontWeight: 700, color: '#4F6AF0' }}>See all</button>
          </div>

          {filtered.length === 0 ? (
            <div className="flex flex-col items-center py-12 gap-3">
              <div style={{ fontSize: 48 }}>📭</div>
              <p style={{ fontSize: 15, fontWeight: 700, color: '#1C2135' }}>Nothing here yet</p>
              <p style={{ fontSize: 13, color: '#9BA3B8', textAlign: 'center' }}>No items in this category. Try another one!</p>
            </div>
          ) : (
            <div className="stagger grid grid-cols-2 gap-3 pb-6">
              {filtered.map(item => (
                <ItemCard key={item.id} item={item} onPress={() => onItem(item)} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
