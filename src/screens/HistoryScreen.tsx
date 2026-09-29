import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { BackIcon, StarIcon } from '../components/Icons';
import { transactions } from '../data';

interface Props {
  onBack: () => void;
}

const filters = ['All', 'Borrowed', 'Lent'];

export const HistoryScreen = ({ onBack }: Props) => {
  const [filter, setFilter] = useState('All');

  const completed = transactions.filter(t => t.status === 'returned');
  const filtered = filter === 'All' ? completed : completed.filter(t => t.type === filter.toLowerCase());

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Header */}
      <div
        className="flex items-center gap-4 px-5 pt-3 pb-5"
        style={{
          background: 'linear-gradient(160deg, #4F6AF0 0%, #7B61FF 100%)',
          borderBottomLeftRadius: 28,
          borderBottomRightRadius: 28,
        }}
      >
        <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none', cursor: 'pointer' }}>
          <BackIcon size={20} color="white" />
        </button>
        <div>
          <h1 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>Borrowing History</h1>
          <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.75)', margin: 0 }}>{completed.length} completed transactions</p>
        </div>
      </div>

      {/* Filters */}
      <div className="px-5 pt-4 pb-3">
        <div className="flex gap-2">
          {filters.map(f => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              style={{
                height: 36,
                paddingLeft: 16,
                paddingRight: 16,
                borderRadius: 10,
                background: filter === f ? '#4F6AF0' : 'white',
                border: `1.5px solid ${filter === f ? '#4F6AF0' : '#E5E8F2'}`,
                fontSize: 13,
                fontWeight: 700,
                color: filter === f ? 'white' : '#5A607A',
                transition: 'all 0.2s ease',
                cursor: 'pointer',
              }}
            >
              {f}
            </button>
          ))}
        </div>
      </div>

      <div className="flex-1 screen-scroll px-5 pb-4">
        {filtered.length === 0 ? (
          <div className="flex flex-col items-center py-16 gap-4">
            <div style={{ fontSize: 56 }}>📭</div>
            <div className="text-center">
              <p style={{ fontSize: 16, fontWeight: 700, color: '#1C2135' }}>No history yet</p>
              <p style={{ fontSize: 13, color: '#9BA3B8', marginTop: 4 }}>Your completed transactions will appear here.</p>
            </div>
          </div>
        ) : (
          <div className="stagger flex flex-col gap-3">
            {filtered.map(tx => (
              <div
                key={tx.id}
                className="p-4"
                style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA', boxShadow: 'var(--bb-shadow-sm)' }}
              >
                <div className="flex gap-3 mb-3">
                  <img
                    src={tx.item.image}
                    alt={tx.item.name}
                    style={{ width: 64, height: 64, borderRadius: 14, objectFit: 'cover', flexShrink: 0 }}
                  />
                  <div className="flex-1">
                    <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: '0 0 2px' }}>{tx.item.name}</p>
                    <div className="flex items-center gap-2 mb-2">
                      <img src={tx.personAvatar} alt={tx.person} style={{ width: 18, height: 18, borderRadius: '50%', objectFit: 'cover' }} />
                      <span style={{ fontSize: 12, color: '#5A607A', fontWeight: 600 }}>{tx.person}</span>
                    </div>
                    <span style={{ fontSize: 11, fontWeight: 700, color: '#3BB8A2', background: '#E6F7F4', borderRadius: 6, padding: '2px 8px' }}>
                      ✓ Returned
                    </span>
                  </div>
                </div>

                <div
                  className="flex items-center justify-between p-3"
                  style={{ background: '#F4F6FF', borderRadius: 12 }}
                >
                  <div>
                    <p style={{ fontSize: 11, color: '#9BA3B8', margin: 0 }}>Borrowed</p>
                    <p style={{ fontSize: 13, fontWeight: 700, color: '#1C2135', margin: 0 }}>{tx.borrowDate}</p>
                  </div>
                  <div style={{ width: 1, height: 32, background: '#E5E8F2' }} />
                  <div>
                    <p style={{ fontSize: 11, color: '#9BA3B8', margin: 0 }}>Returned</p>
                    <p style={{ fontSize: 13, fontWeight: 700, color: '#1C2135', margin: 0 }}>{tx.returnDate}</p>
                  </div>
                  <div style={{ width: 1, height: 32, background: '#E5E8F2' }} />
                  <div className="flex items-center gap-1">
                    {[1, 2, 3, 4, 5].map(i => (
                      <StarIcon key={i} size={12} color="#F5C842" filled={i <= 5} />
                    ))}
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
