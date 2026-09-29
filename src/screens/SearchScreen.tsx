import React, { useState } from 'react';
import { StatusBar } from '../components/PhoneShell';
import { SearchIcon, BackIcon, CloseIcon } from '../components/Icons';
import { items, categories } from '../data';
import type { Item } from '../data';

const recents = ['Scientific Calculator', 'Physics Textbook', 'USB-C Charger', 'Lab Coat'];

interface Props {
  onBack: () => void;
  onItem: (item: Item) => void;
}

export const SearchScreen = ({ onBack, onItem }: Props) => {
  const [query, setQuery] = useState('');
  const [recentsList, setRecentsList] = useState(recents);

  const results = query.length > 1
    ? items.filter(i =>
        i.name.toLowerCase().includes(query.toLowerCase()) ||
        i.category.toLowerCase().includes(query.toLowerCase()) ||
        i.owner.toLowerCase().includes(query.toLowerCase())
      )
    : [];

  const removeRecent = (term: string) => setRecentsList(r => r.filter(x => x !== term));

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Search bar */}
      <div className="flex items-center gap-3 px-5 pt-3 pb-4">
        <button onClick={onBack} className="flex items-center justify-center flex-shrink-0" style={{ width: 40, height: 40, borderRadius: 12, background: 'white', boxShadow: 'var(--bb-shadow-sm)' }}>
          <BackIcon size={20} color="#1C2135" />
        </button>
        <div className="relative flex-1">
          <div className="absolute left-4 top-1/2 -translate-y-1/2">
            <SearchIcon size={18} color="#4F6AF0" />
          </div>
          <input
            type="text"
            placeholder="Search items, categories..."
            value={query}
            onChange={e => setQuery(e.target.value)}
            autoFocus
            style={{
              width: '100%',
              height: 50,
              borderRadius: 16,
              border: '2px solid #4F6AF0',
              background: 'white',
              paddingLeft: 44,
              paddingRight: 16,
              fontSize: 15,
              fontWeight: 500,
              color: '#1C2135',
              boxShadow: '0 0 0 4px rgba(79,106,240,0.10)',
            }}
          />
        </div>
      </div>

      <div className="flex-1 screen-scroll px-5">
        {query.length === 0 ? (
          <>
            {/* Recent searches */}
            {recentsList.length > 0 && (
              <div className="mb-6">
                <div className="flex items-center justify-between mb-3">
                  <h3 style={{ fontSize: 14, fontWeight: 700, color: '#1C2135' }}>Recent Searches</h3>
                  <button onClick={() => setRecentsList([])} style={{ fontSize: 12, fontWeight: 600, color: '#4F6AF0' }}>
                    Clear all
                  </button>
                </div>
                <div className="flex flex-col gap-1">
                  {recentsList.map(term => (
                    <div key={term} className="flex items-center justify-between py-3 border-b" style={{ borderColor: '#F0F2FA' }}>
                      <div className="flex items-center gap-3">
                        <div style={{ width: 32, height: 32, borderRadius: 10, background: '#EEF1FF', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                          <SearchIcon size={15} color="#4F6AF0" />
                        </div>
                        <span style={{ fontSize: 14, fontWeight: 500, color: '#1C2135' }}>{term}</span>
                      </div>
                      <button onClick={() => removeRecent(term)}>
                        <CloseIcon size={16} color="#9BA3B8" />
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Popular categories */}
            <div>
              <h3 style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', marginBottom: 12 }}>Popular Categories</h3>
              <div className="grid grid-cols-2 gap-3">
                {categories.filter(c => c.id !== 'all').map(cat => (
                  <button
                    key={cat.id}
                    onClick={() => setQuery(cat.label)}
                    className="flex items-center gap-3 p-3"
                    style={{
                      background: 'white',
                      borderRadius: 16,
                      border: '1px solid #F0F2FA',
                      boxShadow: 'var(--bb-shadow-sm)',
                    }}
                  >
                    <span style={{ fontSize: 24 }}>{cat.icon}</span>
                    <span style={{ fontSize: 13, fontWeight: 700, color: '#1C2135' }}>{cat.label}</span>
                  </button>
                ))}
              </div>
            </div>
          </>
        ) : results.length === 0 ? (
          <div className="flex flex-col items-center py-16 gap-4">
            <svg width="100" height="100" viewBox="0 0 100 100" fill="none">
              <circle cx="50" cy="50" r="46" fill="#EEF1FF" />
              <circle cx="44" cy="44" r="18" stroke="#9BA3B8" strokeWidth="3" />
              <path d="M58 58L74 74" stroke="#9BA3B8" strokeWidth="3" strokeLinecap="round" />
              <path d="M38 44H50M44 38V50" stroke="#C5C9D8" strokeWidth="2.5" strokeLinecap="round" />
            </svg>
            <div className="text-center">
              <p style={{ fontSize: 18, fontWeight: 800, color: '#1C2135' }}>No items found</p>
              <p style={{ fontSize: 14, color: '#9BA3B8', marginTop: 4 }}>Try searching for something else</p>
            </div>
          </div>
        ) : (
          <div className="stagger flex flex-col gap-3 pb-6">
            <p style={{ fontSize: 13, color: '#9BA3B8', fontWeight: 500 }}>{results.length} results for "{query}"</p>
            {results.map(item => (
              <div
                key={item.id}
                className="card-press flex gap-3 p-3"
                onClick={() => onItem(item)}
                style={{
                  background: 'white',
                  borderRadius: 18,
                  border: '1px solid #F0F2FA',
                  boxShadow: 'var(--bb-shadow-sm)',
                }}
              >
                <img
                  src={item.image}
                  alt={item.name}
                  style={{ width: 72, height: 72, borderRadius: 12, objectFit: 'cover', flexShrink: 0 }}
                />
                <div className="flex-1 min-w-0">
                  <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', marginBottom: 2 }}>{item.name}</p>
                  <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, marginBottom: 4 }}>{item.category}</p>
                  <div className="flex items-center justify-between">
                    <span
                      style={{
                        fontSize: 10,
                        fontWeight: 700,
                        color: item.available ? '#3BB8A2' : '#FF7A7A',
                        background: item.available ? '#E6F7F4' : '#FFF0F0',
                        borderRadius: 6,
                        padding: '2px 8px',
                      }}
                    >
                      {item.available ? 'Available' : 'Borrowed'}
                    </span>
                    <span style={{ fontSize: 11, color: '#9BA3B8' }}>{item.distance}</span>
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
