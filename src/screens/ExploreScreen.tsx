import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { SearchIcon, FilterIcon, BookmarkIcon, LocationIcon } from '../components/Icons';
import { items } from '../data';
import type { Item } from '../data';

const sorts = ['Nearest', 'Recent', 'Popular', 'Available'];

interface Props {
  onItem: (item: Item) => void;
  onSearch: () => void;
}

export const ExploreScreen = ({ onItem, onSearch }: Props) => {
  const [activeSort, setActiveSort] = useState('Nearest');
  const [query, setQuery] = useState('');
  const [saved, setSaved] = useState<Set<string>>(new Set());

  const filtered = items.filter(i =>
    i.name.toLowerCase().includes(query.toLowerCase()) ||
    i.category.toLowerCase().includes(query.toLowerCase())
  );

  const toggle = (id: string) => {
    setSaved(prev => {
      const n = new Set(prev);
      n.has(id) ? n.delete(id) : n.add(id);
      return n;
    });
  };

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Header */}
      <div className="px-5 pt-3 pb-4" style={{ background: '#F4F6FF' }}>
        <h1 style={{ fontSize: 24, fontWeight: 800, color: '#1C2135', marginBottom: 12 }}>Explore Items</h1>

        {/* Search + Filter row */}
        <div className="flex gap-2">
          <div className="flex-1 relative">
            <div className="absolute left-4 top-1/2 -translate-y-1/2">
              <SearchIcon size={18} color="#9BA3B8" />
            </div>
            <input
              type="text"
              placeholder="Search items..."
              value={query}
              onChange={e => setQuery(e.target.value)}
              onFocus={onSearch}
              style={{
                width: '100%',
                height: 48,
                borderRadius: 14,
                border: '1.5px solid #E5E8F2',
                background: 'white',
                paddingLeft: 44,
                paddingRight: 16,
                fontSize: 14,
                fontWeight: 500,
                color: '#1C2135',
                boxShadow: 'var(--bb-shadow-sm)',
              }}
            />
          </div>
          <button
            style={{
              width: 48,
              height: 48,
              borderRadius: 14,
              background: '#4F6AF0',
              border: 'none',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: '0 4px 12px rgba(79,106,240,0.3)',
              flexShrink: 0,
            }}
          >
            <FilterIcon size={20} color="white" />
          </button>
        </div>

        {/* Sort chips */}
        <div className="flex gap-2 mt-3 overflow-x-auto scrollbar-hide">
          {sorts.map(s => (
            <button
              key={s}
              onClick={() => setActiveSort(s)}
              className="flex-shrink-0"
              style={{
                height: 34,
                paddingLeft: 14,
                paddingRight: 14,
                borderRadius: 10,
                background: activeSort === s ? '#4F6AF0' : 'white',
                border: `1.5px solid ${activeSort === s ? '#4F6AF0' : '#E5E8F2'}`,
                fontSize: 13,
                fontWeight: 700,
                color: activeSort === s ? 'white' : '#5A607A',
                transition: 'all 0.2s ease',
              }}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      {/* Grid */}
      <div className="flex-1 screen-scroll px-5 pb-4">
        {filtered.length === 0 ? (
          <div className="flex flex-col items-center py-16 gap-4">
            <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
              <circle cx="40" cy="40" r="36" fill="#EEF1FF" />
              <circle cx="37" cy="37" r="16" stroke="#9BA3B8" strokeWidth="2.5" />
              <path d="M49 49L60 60" stroke="#9BA3B8" strokeWidth="2.5" strokeLinecap="round" />
              <path d="M31 37H43M37 31V43" stroke="#9BA3B8" strokeWidth="2" strokeLinecap="round" />
            </svg>
            <div className="text-center">
              <p style={{ fontSize: 16, fontWeight: 700, color: '#1C2135' }}>No items found</p>
              <p style={{ fontSize: 13, color: '#9BA3B8', marginTop: 4 }}>Try searching for something else.</p>
            </div>
          </div>
        ) : (
          <div className="stagger grid grid-cols-2 gap-3">
            {filtered.map(item => (
              <div
                key={item.id}
                className="card-press"
                onClick={() => onItem(item)}
                style={{
                  background: 'white',
                  borderRadius: 20,
                  overflow: 'hidden',
                  boxShadow: 'var(--bb-shadow-sm)',
                  border: '1px solid #F0F2FA',
                }}
              >
                <div className="relative" style={{ height: 120, background: '#EEF1FF' }}>
                  <img src={item.image} alt={item.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  <button
                    onClick={e => { e.stopPropagation(); toggle(item.id); }}
                    className="absolute top-2 right-2 flex items-center justify-center"
                    style={{ width: 28, height: 28, borderRadius: 8, background: 'rgba(255,255,255,0.9)' }}
                  >
                    <BookmarkIcon size={14} color={saved.has(item.id) ? '#4F6AF0' : '#9BA3B8'} filled={saved.has(item.id)} />
                  </button>
                  <div className="absolute bottom-2 left-2">
                    <span
                      style={{
                        fontSize: 9,
                        fontWeight: 700,
                        color: item.available ? '#3BB8A2' : '#FF7A7A',
                        background: item.available ? '#E6F7F4' : '#FFF0F0',
                        borderRadius: 6,
                        padding: '2px 6px',
                      }}
                    >
                      {item.available ? 'Available' : 'Borrowed'}
                    </span>
                  </div>
                </div>
                <div className="p-3">
                  <p style={{ fontSize: 12, fontWeight: 700, color: '#1C2135', marginBottom: 2, lineHeight: 1.3 }}>
                    {item.name}
                  </p>
                  <p style={{ fontSize: 10, color: '#9BA3B8', fontWeight: 500, marginBottom: 6 }}>{item.category}</p>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-1">
                      <img src={item.ownerAvatar} alt={item.owner} style={{ width: 16, height: 16, borderRadius: '50%', objectFit: 'cover' }} />
                      <span style={{ fontSize: 10, color: '#5A607A', fontWeight: 600 }}>{item.owner.split(' ')[0]}</span>
                    </div>
                    <div className="flex items-center gap-0.5">
                      <LocationIcon size={10} color="#9BA3B8" />
                      <span style={{ fontSize: 10, color: '#9BA3B8' }}>{item.distance}</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
